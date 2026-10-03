package devs.grupo5.sportpro.ui.screens

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import devs.grupo5.sportpro.ui.theme.BackgroundDark
import devs.grupo5.sportpro.ui.theme.ErrorRed
import devs.grupo5.sportpro.ui.theme.NeonGreen
import devs.grupo5.sportpro.ui.theme.NeonGreenDark
import devs.grupo5.sportpro.ui.theme.OutlineDark
import devs.grupo5.sportpro.ui.theme.SportProTheme
import devs.grupo5.sportpro.ui.theme.SurfaceElevatedDark
import devs.grupo5.sportpro.ui.theme.TextSecondaryDark
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.Period
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private const val EDAD_MINIMA = 18
private const val LONGITUD_MINIMA_CONTRASENA = 8

private val FORMATO_FECHA: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

/** Datos válidos del paso 1, entregadas al selector de perfil del paso 2. */
data class RegisterFormData(
    val nombre: String,
    val apellidos: String,
    val correo: String,
    val contrasena: String,
    val fechaNacimiento: LocalDate,
    val esMenorDeEdad: Boolean
)

/** Errores de validación por campo. Un `null` significa que el campo es válido. */
private data class RegisterFormErrors(
    val nombre: String? = null,
    val apellidos: String? = null,
    val correo: String? = null,
    val contrasena: String? = null,
    val confirmarContrasena: String? = null,
    val fechaNacimiento: String? = null,
    val terminos: String? = null
) {
    val esValido: Boolean
        get() = listOf(
            nombre, apellidos, correo, contrasena, confirmarContrasena,
            fechaNacimiento, terminos
        ).all { it == null }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onContinue: (RegisterFormData) -> Unit = {},
    onLogin: () -> Unit = {},
    correoYaRegistrado: (String) -> Boolean = { false }
) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var apellidos by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var contrasena by rememberSaveable { mutableStateOf("") }
    var confirmarContrasena by rememberSaveable { mutableStateOf("") }
    var fechaNacimientoEpochDay by rememberSaveable { mutableStateOf<Long?>(null) }
    var terminosAceptados by rememberSaveable { mutableStateOf(false) }
    var mostrarErrores by rememberSaveable { mutableStateOf(false) }
    var mostrarSelectorFecha by rememberSaveable { mutableStateOf(false) }

    val fechaNacimiento = remember(fechaNacimientoEpochDay) {
        fechaNacimientoEpochDay?.let(LocalDate::ofEpochDay)
    }

    val errores = remember(
        nombre, apellidos, correo, contrasena, confirmarContrasena,
        fechaNacimiento, terminosAceptados, mostrarErrores, correoYaRegistrado
    ) {
        if (mostrarErrores) {
            validarFormulario(
                nombre = nombre,
                apellidos = apellidos,
                correo = correo,
                contrasena = contrasena,
                confirmarContrasena = confirmarContrasena,
                fechaNacimiento = fechaNacimiento,
                terminosAceptados = terminosAceptados,
                correoYaRegistrado = correoYaRegistrado
            )
        } else {
            RegisterFormErrors()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .systemBarsPadding()
            .imePadding()
    ) {
        RegisterTopBar(onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            Text(
                text = "Crear cuenta",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Ingresa tus datos personales",
                fontSize = 14.sp,
                color = TextSecondaryDark
            )

            Spacer(Modifier.height(4.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CampoRegistro(
                    label = "NOMBRE",
                    valor = nombre,
                    onValueChange = { nombre = it },
                    placeholder = "Carlos",
                    error = errores.nombre,
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                )
                CampoRegistro(
                    label = "APELLIDOS",
                    valor = apellidos,
                    onValueChange = { apellidos = it },
                    placeholder = "Mendoza",
                    error = errores.apellidos,
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                )
            }

            CampoRegistro(
                label = "CORREO ELECTRÓNICO",
                valor = correo,
                onValueChange = { correo = it },
                placeholder = "correo@ejemplo.com",
                error = errores.correo,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            CampoRegistro(
                label = "CONTRASEÑA",
                valor = contrasena,
                onValueChange = { contrasena = it },
                placeholder = "Mínimo $LONGITUD_MINIMA_CONTRASENA caracteres",
                error = errores.contrasena,
                visualTransformation = PasswordVisualTransformation()
            )

            CampoRegistro(
                label = "CONFIRMAR CONTRASEÑA",
                valor = confirmarContrasena,
                onValueChange = { confirmarContrasena = it },
                placeholder = "Repite la contraseña",
                error = errores.confirmarContrasena,
                visualTransformation = PasswordVisualTransformation()
            )

            CampoFechaNacimiento(
                fecha = fechaNacimiento,
                error = errores.fechaNacimiento,
                onClick = { mostrarSelectorFecha = true }
            )

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = terminosAceptados,
                        onCheckedChange = { terminosAceptados = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = NeonGreen,
                            uncheckedColor = TextSecondaryDark,
                            checkmarkColor = BackgroundDark
                        )
                    )
                    Text(
                        text = buildAnnotatedString {
                            append("Acepto los ")
                            withStyle(SpanStyle(color = NeonGreen, fontWeight = FontWeight.SemiBold)) {
                                append("Términos de Servicio")
                            }
                            append(" y la ")
                            withStyle(SpanStyle(color = NeonGreen, fontWeight = FontWeight.SemiBold)) {
                                append("Política de Privacidad de SportPro")
                            }
                        },
                        fontSize = 13.sp,
                        color = TextSecondaryDark,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                    )
                }
                if (errores.terminos != null) {
                    Text(
                        text = errores.terminos,
                        fontSize = 12.sp,
                        color = ErrorRed,
                        modifier = Modifier.padding(start = 44.dp, top = 2.dp)
                    )
                }
            }

            Spacer(Modifier.height(4.dp))
        }

        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
            Button(
                onClick = {
                    mostrarErrores = true
                    val erroresActuales = validarFormulario(
                        nombre = nombre,
                        apellidos = apellidos,
                        correo = correo,
                        contrasena = contrasena,
                        confirmarContrasena = confirmarContrasena,
                        fechaNacimiento = fechaNacimiento,
                        terminosAceptados = terminosAceptados,
                        correoYaRegistrado = correoYaRegistrado
                    )
                    val fecha = fechaNacimiento
                    if (erroresActuales.esValido && fecha != null) {
                        onContinue(
                            RegisterFormData(
                                nombre = nombre.trim(),
                                apellidos = apellidos.trim(),
                                correo = correo.trim(),
                                contrasena = contrasena,
                                fechaNacimiento = fecha,
                                esMenorDeEdad = esMenorDeEdad(fecha)
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(percent = 50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonGreenDark,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = "Continuar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "¿Ya tienes cuenta? ",
                    fontSize = 13.sp,
                    color = TextSecondaryDark
                )
                Text(
                    text = "Iniciar sesión",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NeonGreen,
                    modifier = Modifier.clickable(onClick = onLogin)
                )
            }
        }
    }

    if (mostrarSelectorFecha) {
        val hoy = remember { LocalDate.now() }
        val limiteFecha = remember(hoy) {
            hoy.atTime(LocalTime.MAX).toInstant(ZoneOffset.UTC).toEpochMilli()
        }
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = fechaNacimiento
                ?.atStartOfDay(ZoneOffset.UTC)
                ?.toInstant()
                ?.toEpochMilli(),
            selectableDates = remember(limiteFecha) {
                object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis <= limiteFecha
                    override fun isSelectableYear(year: Int) = year <= hoy.year
                }
            }
        )

        DatePickerDialog(
            onDismissRequest = { mostrarSelectorFecha = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            fechaNacimientoEpochDay = Instant.ofEpochMilli(millis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                                .toEpochDay()
                        }
                        mostrarSelectorFecha = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = NeonGreen)
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarSelectorFecha = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = TextSecondaryDark)
                ) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = BackgroundDark,
                    titleContentColor = Color.White,
                    headlineContentColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun RegisterTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(40.dp),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = SurfaceElevatedDark,
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver"
            )
        }

        Spacer(Modifier.width(16.dp))

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BarraPaso(modifier = Modifier.weight(1f), activo = true)
            BarraPaso(modifier = Modifier.weight(1f), activo = false)
        }

        Spacer(Modifier.width(16.dp))

        Text(
            text = "Paso 1 de 2",
            fontSize = 12.sp,
            color = TextSecondaryDark
        )
    }
}

@Composable
private fun BarraPaso(modifier: Modifier = Modifier, activo: Boolean) {
    Box(
        modifier = modifier
            .height(4.dp)
            .background(
                color = if (activo) NeonGreen else OutlineDark,
                shape = RoundedCornerShape(percent = 50)
            )
    )
}

@Composable
private fun CampoRegistro(
    label: String,
    valor: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            color = NeonGreen
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = valor,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = error != null,
            placeholder = {
                Text(text = placeholder, fontSize = 14.sp, color = TextSecondaryDark)
            },
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = NeonGreen,
                unfocusedBorderColor = OutlineDark,
                focusedContainerColor = SurfaceElevatedDark,
                unfocusedContainerColor = SurfaceElevatedDark,
                cursorColor = NeonGreen,
                errorBorderColor = ErrorRed
            )
        )
        if (error != null) {
            Spacer(Modifier.height(4.dp))
            Text(text = error, fontSize = 12.sp, color = ErrorRed)
        }
    }
}

@Composable
private fun CampoFechaNacimiento(
    fecha: LocalDate?,
    error: String?,
    onClick: () -> Unit
) {
    Column {
        Text(
            text = "FECHA DE NACIMIENTO",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            color = NeonGreen
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = fecha?.format(FORMATO_FECHA).orEmpty(),
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            singleLine = true,
            isError = error != null,
            placeholder = {
                Text(text = "dd/mm/aaaa", fontSize = 14.sp, color = TextSecondaryDark)
            },
            trailingIcon = {
                IconButton(onClick = onClick) {
                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = "Seleccionar fecha de nacimiento",
                        tint = NeonGreen
                    )
                }
            },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = NeonGreen,
                unfocusedBorderColor = OutlineDark,
                focusedContainerColor = SurfaceElevatedDark,
                unfocusedContainerColor = SurfaceElevatedDark,
                cursorColor = NeonGreen,
                errorBorderColor = ErrorRed
            )
        )
        if (error != null) {
            Spacer(Modifier.height(4.dp))
            Text(text = error, fontSize = 12.sp, color = ErrorRed)
        }
    }
}

private fun esMenorDeEdad(fechaNacimiento: LocalDate): Boolean =
    Period.between(fechaNacimiento, LocalDate.now()).years < EDAD_MINIMA

private fun validarFormulario(
    nombre: String,
    apellidos: String,
    correo: String,
    contrasena: String,
    confirmarContrasena: String,
    fechaNacimiento: LocalDate?,
    terminosAceptados: Boolean,
    correoYaRegistrado: (String) -> Boolean
): RegisterFormErrors {
    val hoy = LocalDate.now()
    val correoNormalizado = correo.trim()

    val errorFecha = when {
        fechaNacimiento == null -> "Selecciona tu fecha de nacimiento"
        fechaNacimiento.isAfter(hoy) -> "La fecha de nacimiento no puede ser futura"
        esMenorDeEdad(fechaNacimiento) ->
            "Si eres menor de $EDAD_MINIMA años, tu apoderado debe crear la cuenta y vincularte"
        else -> null
    }

    val errorCorreo = when {
        correoNormalizado.isEmpty() -> "Ingresa tu correo electrónico"
        !Patterns.EMAIL_ADDRESS.matcher(correoNormalizado).matches() ->
            "Ingresa un correo electrónico válido"
        correoYaRegistrado(correoNormalizado) -> "Este correo ya está registrado"
        else -> null
    }

    return RegisterFormErrors(
        nombre = when {
            nombre.isBlank() -> "Ingresa tu nombre"
            nombre.trim().length < 2 -> "El nombre es demasiado corto"
            else -> null
        },
        apellidos = when {
            apellidos.isBlank() -> "Ingresa tus apellidos"
            apellidos.trim().length < 2 -> "Los apellidos son demasiado cortos"
            else -> null
        },
        correo = errorCorreo,
        contrasena = when {
            contrasena.isEmpty() -> "Ingresa una contraseña"
            contrasena.length < LONGITUD_MINIMA_CONTRASENA ->
                "La contraseña debe tener al menos $LONGITUD_MINIMA_CONTRASENA caracteres"
            else -> null
        },
        confirmarContrasena = when {
            confirmarContrasena.isEmpty() -> "Repite la contraseña"
            confirmarContrasena != contrasena -> "Las contraseñas no coinciden"
            else -> null
        },
        fechaNacimiento = errorFecha,
        terminos = if (terminosAceptados) {
            null
        } else {
            "Debes aceptar los términos y la política de privacidad"
        }
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF101413, heightDp = 780)
@Composable
private fun RegisterScreenPreview() {
    SportProTheme(darkTheme = true, dynamicColor = false) {
        RegisterScreen()
    }
}
