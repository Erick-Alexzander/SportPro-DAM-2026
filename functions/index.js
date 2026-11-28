const { initializeApp } = require("firebase-admin/app");
const { getAuth } = require("firebase-admin/auth");
const { getFirestore, FieldValue } = require("firebase-admin/firestore");
const { HttpsError, onCall } = require("firebase-functions/v2/https");
const logger = require("firebase-functions/logger");

initializeApp();

const db = getFirestore();
const auth = getAuth();

async function assertTrainer(request) {
  if (!request.auth) {
    throw new HttpsError("unauthenticated", "Debes iniciar sesión.");
  }

  const callerUid = request.auth.uid;
  const callerSnap = await db.collection("users").doc(callerUid).get();
  if (!callerSnap.exists || callerSnap.get("role") !== "TRAINER") {
    throw new HttpsError(
      "permission-denied",
      "Solo un entrenador puede gestionar vínculos de apoderados."
    );
  }

  return callerUid;
}

async function resolveGuardianUid(data) {
  const guardianUid = typeof data.guardianUid === "string" ? data.guardianUid.trim() : "";
  const guardianEmail = typeof data.guardianEmail === "string" ? data.guardianEmail.trim().toLowerCase() : "";

  if (guardianUid) return guardianUid;
  if (!guardianEmail) {
    throw new HttpsError(
      "invalid-argument",
      "Debes enviar guardianUid o guardianEmail."
    );
  }

  try {
    const user = await auth.getUserByEmail(guardianEmail);
    return user.uid;
  } catch (error) {
    logger.warn("No se encontró apoderado por email", { guardianEmail, error });
    throw new HttpsError("not-found", "No existe un usuario con ese correo.");
  }
}

async function getOwnedPlayer(playerId, trainerUid) {
  const playerRef = db.collection("players").doc(playerId);
  const playerSnap = await playerRef.get();
  if (!playerSnap.exists) {
    throw new HttpsError("not-found", "El jugador no existe.");
  }

  if (playerSnap.get("ownerUid") !== trainerUid) {
    throw new HttpsError(
      "permission-denied",
      "No puedes vincular jugadores que no te pertenecen."
    );
  }

  return { playerRef, playerSnap };
}

async function assertGuardianUser(guardianUid) {
  const guardianSnap = await db.collection("users").doc(guardianUid).get();
  if (!guardianSnap.exists) {
    throw new HttpsError("not-found", "El apoderado no tiene perfil en users.");
  }
  if (guardianSnap.get("role") !== "PARENT") {
    throw new HttpsError(
      "failed-precondition",
      "El usuario indicado no tiene rol PARENT."
    );
  }
}

exports.linkGuardianToPlayer = onCall({ region: "us-central1" }, async (request) => {
  const trainerUid = await assertTrainer(request);
  const playerId = typeof request.data?.playerId === "string" ? request.data.playerId.trim() : "";
  if (!playerId) {
    throw new HttpsError("invalid-argument", "playerId es obligatorio.");
  }

  const guardianUid = await resolveGuardianUid(request.data || {});
  await assertGuardianUser(guardianUid);
  const { playerRef, playerSnap } = await getOwnedPlayer(playerId, trainerUid);

  const teamId = playerSnap.get("teamId");
  const parentAccessRef = db.collection("parentAccess").doc(guardianUid);

  const batch = db.batch();
  batch.set(playerRef, { guardianUids: FieldValue.arrayUnion(guardianUid) }, { merge: true });
  batch.set(
    parentAccessRef,
    {
      parentUid: guardianUid,
      playerIds: FieldValue.arrayUnion(playerId),
      ...(typeof teamId === "string" && teamId ? { teamIds: FieldValue.arrayUnion(teamId) } : {}),
    },
    { merge: true }
  );

  await batch.commit();
  logger.info("Apoderado vinculado", { trainerUid, guardianUid, playerId, teamId: teamId || null });

  return { ok: true, playerId, guardianUid, teamId: teamId || null };
});

exports.unlinkGuardianFromPlayer = onCall({ region: "us-central1" }, async (request) => {
  const trainerUid = await assertTrainer(request);
  const playerId = typeof request.data?.playerId === "string" ? request.data.playerId.trim() : "";
  if (!playerId) {
    throw new HttpsError("invalid-argument", "playerId es obligatorio.");
  }

  const guardianUid = await resolveGuardianUid(request.data || {});
  const { playerRef, playerSnap } = await getOwnedPlayer(playerId, trainerUid);
  const teamId = playerSnap.get("teamId");
  const parentAccessRef = db.collection("parentAccess").doc(guardianUid);

  const batch = db.batch();
  batch.set(playerRef, { guardianUids: FieldValue.arrayRemove(guardianUid) }, { merge: true });
  batch.set(
    parentAccessRef,
    {
      parentUid: guardianUid,
      playerIds: FieldValue.arrayRemove(playerId),
      ...(typeof teamId === "string" && teamId ? { teamIds: FieldValue.arrayRemove(teamId) } : {}),
    },
    { merge: true }
  );

  await batch.commit();
  logger.info("Apoderado desvinculado", { trainerUid, guardianUid, playerId, teamId: teamId || null });

  return { ok: true, playerId, guardianUid, teamId: teamId || null };
});

exports.createChildAccount = onCall({ region: "us-central1" }, async (request) => {
  if (!request.auth) {
    throw new HttpsError("unauthenticated", "Debes iniciar sesión.");
  }

  const parentUid = request.auth.uid;
  const parentSnap = await db.collection("users").doc(parentUid).get();
  if (!parentSnap.exists || parentSnap.get("role") !== "PARENT") {
    throw new HttpsError("permission-denied", "Solo un apoderado puede crear accesos para menores.");
  }

  const childId = typeof request.data?.childId === "string" ? request.data.childId.trim() : "";
  const email = typeof request.data?.email === "string" ? request.data.email.trim().toLowerCase() : "";
  const password = typeof request.data?.password === "string" ? request.data.password : "";

  if (!childId || !email || !password) {
    throw new HttpsError("invalid-argument", "childId, email y password son obligatorios.");
  }
  if (!email.includes("@")) {
    throw new HttpsError("invalid-argument", "El correo no es válido.");
  }
  if (password.length < 6) {
    throw new HttpsError("invalid-argument", "La contraseña debe tener al menos 6 caracteres.");
  }

  const playerRef = db.collection("players").doc(childId);
  const playerSnap = await playerRef.get();
  if (!playerSnap.exists) {
    throw new HttpsError("not-found", "El menor no existe.");
  }

  const guardianUids = playerSnap.get("guardianUids") || [];
  if (!Array.isArray(guardianUids) || !guardianUids.includes(parentUid)) {
    throw new HttpsError("permission-denied", "El menor no está vinculado a tu cuenta.");
  }

  const existingLinked = playerSnap.get("linkedAuthId");
  if (typeof existingLinked === "string" && existingLinked.trim()) {
    throw new HttpsError("already-exists", "Este menor ya tiene una cuenta de acceso.");
  }

  let childUser;
  try {
    childUser = await auth.createUser({
      email,
      password,
      emailVerified: false,
      disabled: false,
    });
  } catch (error) {
    if (error.code === "auth/email-already-exists") {
      throw new HttpsError("already-exists", "Ese correo ya está registrado.");
    }
    if (error.code === "auth/invalid-email") {
      throw new HttpsError("invalid-argument", "El correo no es válido.");
    }
    if (error.code === "auth/weak-password") {
      throw new HttpsError("invalid-argument", "La contraseña es demasiado débil.");
    }
    logger.error("Error creando usuario de menor", error);
    throw new HttpsError("internal", "No se pudo crear la cuenta del menor.");
  }

  const fullName = String(playerSnap.get("fullName") || "").trim();
  const parts = fullName.split(/\s+/).filter(Boolean);
  const firstName = parts.shift() || "Jugador";
  const lastName = parts.join(" ") || "SportPro";

  try {
    await db.collection("users").doc(childUser.uid).set({
      uid: childUser.uid,
      email,
      firstName,
      lastName,
      role: "PLAYER",
      birthDate: String(playerSnap.get("dateOfBirth") || ""),
      playerProfile: {
        teamId: String(playerSnap.get("teamId") || ""),
        position: String(playerSnap.get("position") || ""),
        jerseyNumber: Number(playerSnap.get("jerseyNumber") || 0),
      },
    });

    await playerRef.set({ linkedAuthId: childUser.uid }, { merge: true });
  } catch (error) {
    logger.error("Error vinculando cuenta de menor, revirtiendo auth", error);
    try {
      await auth.deleteUser(childUser.uid);
    } catch (cleanupError) {
      logger.error("No se pudo revertir usuario de Auth", cleanupError);
    }
    throw new HttpsError("internal", "La cuenta se creó parcialmente y se revirtió. Intenta nuevamente.");
  }

  logger.info("Cuenta de menor creada", { parentUid, childId, childUid: childUser.uid, email });
  return { ok: true, childId, childUid: childUser.uid, email };
});
