package devs.grupo5.sportpro

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity // <-- CAMBIO AQUÍ

class ProfileActivity : ComponentActivity() { // <-- CAMBIO AQUÍ

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Resto del código igual...
        // Contenedor principal con ScrollView
        val scrollView = ScrollView(this).apply {
            setBackgroundColor(Color.parseColor("#121414"))
            isFillViewport = true
        }

        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))
        }

        // 1. Tarjeta de Perfil
        val profileCard = createCard().apply {
            addView(createProfileHeaderContent())
        }

        // 2. Tarjeta de Información de Cuenta
        val accountCard = createCard().apply {
            addView(createAccountInfoContent())
        }

        mainLayout.addView(profileCard)
        mainLayout.addView(accountCard)
        scrollView.addView(mainLayout)

        setContentView(scrollView)
    }

    // Método para crear el fondo de tarjeta curva (#1C2220)
    private fun createCard(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val shape = GradientDrawable().apply {
                setColor(Color.parseColor("#1C2220"))
                cornerRadius = dpToPx(16).toFloat()
            }
            background = shape
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, dpToPx(16))
            }
            layoutParams = params
        }
    }

    // Contenido de la Tarjeta 1 (Avatar, Nombre, Rol, Academia)
    private fun createProfileHeaderContent(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dpToPx(24), dpToPx(24), dpToPx(24), dpToPx(24))

            // Avatar Circular "CM"
            val avatar = TextView(context).apply {
                text = "CM"
                setTextColor(Color.WHITE)
                textSize = 22f
                typeface = Typeface.DEFAULT_BOLD
                gravity = Gravity.CENTER
                val circleShape = GradientDrawable().apply {
                    setColor(Color.parseColor("#8C3A1E"))
                    shape = GradientDrawable.OVAL
                }
                background = circleShape
                layoutParams = LinearLayout.LayoutParams(dpToPx(72), dpToPx(72))
            }

            // Nombre del Jugador
            val name = TextView(context).apply {
                text = "Carlos Mendoza"
                setTextColor(Color.WHITE)
                textSize = 20f
                typeface = Typeface.DEFAULT_BOLD
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, dpToPx(12), 0, 0) }
            }

            // Badge/Chip verde "Jugador"
            val roleBadge = TextView(context).apply {
                text = "Jugador"
                setTextColor(Color.parseColor("#00A86B"))
                textSize = 12f
                typeface = Typeface.DEFAULT_BOLD
                setPadding(dpToPx(12), dpToPx(4), dpToPx(12), dpToPx(4))
                val badgeShape = GradientDrawable().apply {
                    setColor(Color.parseColor("#0D533A"))
                    cornerRadius = dpToPx(12).toFloat()
                }
                background = badgeShape
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, dpToPx(6), 0, 0) }
            }

            // Subtítulo Academia
            val academy = TextView(context).apply {
                text = "Academia SportPro · Primera"
                setTextColor(Color.parseColor("#A0A5A3"))
                textSize = 13f
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, dpToPx(8), 0, 0) }
            }

            addView(avatar)
            addView(name)
            addView(roleBadge)
            addView(academy)
        }
    }

    // Contenido de la Tarjeta 2 (Información de cuenta)
    private fun createAccountInfoContent(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))

            val title = TextView(context).apply {
                text = "Información de cuenta"
                setTextColor(Color.WHITE)
                textSize = 15f
                typeface = Typeface.DEFAULT_BOLD
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 0, 0, dpToPx(12)) }
            }

            addView(title)
            addView(createDataRow("Nombre", "Carlos Mendoza"))
            addView(createDataRow("Correo", "carlos.m@sportpro.cl"))
            addView(createDataRow("Rol", "Jugador"))
            addView(createDataRow("Academia", "Academia SportPro · Primera"))
        }
    }

    // Fila individual de clave -> valor
    private fun createDataRow(label: String, value: String): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, dpToPx(6), 0, dpToPx(6))

            val labelTv = TextView(context).apply {
                text = label
                setTextColor(Color.parseColor("#A0A5A3"))
                textSize = 13f
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val valueTv = TextView(context).apply {
                text = value
                setTextColor(Color.WHITE)
                textSize = 13f
                typeface = Typeface.DEFAULT_BOLD
            }

            addView(labelTv)
            addView(valueTv)
        }
    }

    // Conversor auxiliar de DP a Píxeles
    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}