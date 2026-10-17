package devs.grupo5.sportpro.presentation.player.calendar

/** Ejercicio individual dentro de una sesión de entrenamiento. */
internal data class TrainingExercise(
    val name: String,
    val type: String,
    val durationMinutes: Int
)

/** Sesión de entrenamiento mostrada en el calendario del jugador. */
internal data class PlayerTrainingSession(
    val id: String,
    val date: String,
    val day: String,
    val month: String,
    val category: String,
    val time: String,
    val durationMinutes: Int,
    val title: String,
    val objective: String,
    val exercises: List<TrainingExercise>,
    val attendeesCount: Int
)

internal val trainingCategories = listOf("Todos", "Primera", "Sub-15", "Sub-10")

internal val sampleSessions = listOf(
    PlayerTrainingSession(
        id = "t1",
        date = "2024-11-04",
        day = "04",
        month = "NOV",
        category = "Primera",
        time = "16:00",
        durationMinutes = 90,
        title = "Trabajo táctico en posición defensiva y salida en juego corto",
        objective = "Trabajo táctico en posición defensiva y salida en juego corto",
        exercises = listOf(
            TrainingExercise(name = "Rondo 5v2", type = "Posesión", durationMinutes = 15),
            TrainingExercise(name = "Posición defensiva", type = "General", durationMinutes = 15),
            TrainingExercise(name = "Juego de posición 8v8", type = "General", durationMinutes = 15),
            TrainingExercise(name = "Partido reducido", type = "General", durationMinutes = 15)
        ),
        attendeesCount = 14
    ),
    PlayerTrainingSession(
        id = "t2",
        date = "2024-11-02",
        day = "02",
        month = "NOV",
        category = "Primera",
        time = "16:00",
        durationMinutes = 75,
        title = "Preparación física previa al partido",
        objective = "Activación física, ritmo de competencia y ajuste de pelota parada antes del encuentro.",
        exercises = listOf(
            TrainingExercise(name = "Calentamiento dinámico", type = "Activación", durationMinutes = 15),
            TrainingExercise(name = "Rondos de activación", type = "Posesión", durationMinutes = 15),
            TrainingExercise(name = "Circuito de velocidad", type = "Físico", durationMinutes = 20),
            TrainingExercise(name = "Pelota parada", type = "Estrategia", durationMinutes = 15)
        ),
        attendeesCount = 16
    ),
    PlayerTrainingSession(
        id = "t3",
        date = "2024-10-30",
        day = "30",
        month = "OCT",
        category = "Sub-15",
        time = "15:30",
        durationMinutes = 90,
        title = "Técnica individual: regate y conducción",
        objective = "Mejorar el control orientado, la conducción en velocidad y el regate en espacios reducidos.",
        exercises = listOf(
            TrainingExercise(name = "Conducción en zigzag", type = "Técnica", durationMinutes = 20),
            TrainingExercise(name = "Regate 1v1", type = "Técnica", durationMinutes = 20),
            TrainingExercise(name = "Slalom con conos", type = "Técnica", durationMinutes = 20),
            TrainingExercise(name = "Juego de aplicación", type = "General", durationMinutes = 20)
        ),
        attendeesCount = 12
    ),
    PlayerTrainingSession(
        id = "t4",
        date = "2024-11-06",
        day = "06",
        month = "NOV",
        category = "Sub-10",
        time = "16:00",
        durationMinutes = 60,
        title = "Habilidades básicas y trabajo lúdico",
        objective = "Desarrollar coordinación, pase y disfrute del juego mediante dinámicas lúdicas.",
        exercises = listOf(
            TrainingExercise(name = "Juego de los pescadores", type = "Lúdico", durationMinutes = 15),
            TrainingExercise(name = "Pases en pareja", type = "Básico", durationMinutes = 15),
            TrainingExercise(name = "Partidito 4v4", type = "General", durationMinutes = 20)
        ),
        attendeesCount = 8
    )
)

internal fun findSessionById(id: String): PlayerTrainingSession =
    sampleSessions.firstOrNull { it.id == id } ?: sampleSessions.first()
