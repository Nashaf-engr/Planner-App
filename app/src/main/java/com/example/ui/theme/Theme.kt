package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = BentoLavender,
    onPrimary = BentoLavenderText,
    primaryContainer = BentoNavy,
    onPrimaryContainer = BentoLightBlue,
    secondary = BentoPurple,
    onSecondary = BentoPurpleText,
    tertiary = Purple80,
    background = Color(0xFF111318),
    surface = Color(0xFF1A1C1E),
    onBackground = Color(0xFFE2E2E6),
    onSurface = Color(0xFFE2E2E6)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = BentoNavy,
    onPrimary = Color.White,
    primaryContainer = BentoLightBlue,
    onPrimaryContainer = BentoNavy,
    secondary = BentoLavenderText,
    onSecondary = Color.White,
    secondaryContainer = BentoLavender,
    onSecondaryContainer = BentoLavenderText,
    tertiary = Color(0xFF6750A4),
    onTertiary = Color.White,
    tertiaryContainer = BentoPurple,
    onTertiaryContainer = BentoPurpleText,
    background = BentoBg,
    onBackground = Color(0xFF1A1C1E),
    surface = Color.White,
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = BentoPurple,
    onSurfaceVariant = BentoGrayText,
    outline = BentoGrayBorder,
    error = BentoRed
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disable dynamicColor to guarantee that the brand's beautiful Bento color scheme is rendered
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
