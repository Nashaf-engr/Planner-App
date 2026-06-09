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
    primary = LiquidCobalt,
    onPrimary = LiquidCobaltText,
    primaryContainer = LiquidNavy,
    onPrimaryContainer = LiquidLightBlue,
    secondary = LiquidAqua,
    onSecondary = LiquidAquaText,
    secondaryContainer = Color(0xFF1E293B),
    onSecondaryContainer = LiquidLightBlue,
    tertiary = LiquidViolet,
    onTertiary = LiquidVioletText,
    background = LiquidDeepBg,
    onBackground = Color(0xFFF3F4F6),
    surface = GlassSurfaceDark,
    onSurface = Color(0xFFF3F4F6),
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = GlassBorderDark,
    error = ModernRed
  )

private val LightColorScheme =
  lightColorScheme(
    primary = LiquidCobalt,
    onPrimary = Color.White,
    primaryContainer = LiquidLightBlue,
    onPrimaryContainer = LiquidNavy,
    secondary = LiquidAqua,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE2E8F0),
    onSecondaryContainer = Color(0xFF0F172A),
    tertiary = LiquidViolet,
    onTertiary = Color.White,
    background = LiquidIceBg,
    onBackground = Color(0xFF0F172A),
    surface = GlassSurfaceLight,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFEDF2F7),
    onSurfaceVariant = Color(0xFF4A5568),
    outline = GlassBorderLight,
    error = ModernRed
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disable dynamicColor to guarantee that our premium Liquid Glass color scheme is fully rendered
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
