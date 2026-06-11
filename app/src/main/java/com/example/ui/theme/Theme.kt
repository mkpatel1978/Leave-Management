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
    primary = DarkAmberPrimary,
    primaryContainer = DarkAmberPrimaryContainer,
    onPrimary = DarkAmberOnPrimary,
    onPrimaryContainer = DarkAmberOnPrimaryContainer,
    secondary = DarkAmberSecondary,
    secondaryContainer = DarkAmberSecondaryContainer,
    onSecondaryContainer = DarkAmberOnSecondaryContainer,
    background = DarkAmberBackground,
    surface = DarkAmberSurface,
    onBackground = DarkAmberOnBackground,
    onSurface = DarkAmberOnSurface,
    surfaceVariant = Color(0xFF1E2130),
    onSurfaceVariant = Color(0xFF9CA3AF),
    outline = Color(0xFF2E3245),
    outlineVariant = Color(0xFF232738)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = SunnyGoldPrimary,
    primaryContainer = SunnyGoldPrimaryContainer,
    onPrimary = SunnyGoldOnPrimary,
    onPrimaryContainer = SunnyGoldOnPrimaryContainer,
    secondary = SunnyGoldSecondary,
    secondaryContainer = SunnyGoldSecondaryContainer,
    onSecondaryContainer = SunnyGoldOnSecondaryContainer,
    background = SunnyGoldBackground,
    surface = SunnyGoldSurface,
    onBackground = SunnyGoldOnBackground,
    onSurface = SunnyGoldOnSurface,
    surfaceVariant = SunnyGoldSurfaceVariant,
    onSurfaceVariant = SunnyGoldOnSurfaceVariant,
    outline = SunnyGoldOutline,
    outlineVariant = Color(0xFFF1EDE4)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Set to false by default so our custom gorgeous light yellow theme takes full control immediately
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
