package com.devsnippets.app.ui.theme

import androidx.compose.ui.graphics.Color

// ---- Neutral surfaces (dark first, since that's the default) ----
val DarkBackground = Color(0xFF0E0E14)
val DarkSurface = Color(0xFF16161F)
val DarkSurfaceVariant = Color(0xFF1E1E29)
val DarkOutline = Color(0xFF2C2C3A)

val LightBackground = Color(0xFFFAFAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFEFEFF5)
val LightOutline = Color(0xFFDCDCE6)

// ---- Theme 1: Purple Neon (default) ----
val PurplePrimary = Color(0xFF7C5CFF)
val PurpleSecondary = Color(0xFF38E1C6)
val PurpleTertiary = Color(0xFFFF6FA5)

// ---- Theme 2: Ocean Blue ----
val OceanPrimary = Color(0xFF3D8BFF)
val OceanSecondary = Color(0xFF00C2D1)
val OceanTertiary = Color(0xFF7BE0AD)

// ---- Theme 3: Forest Green ----
val ForestPrimary = Color(0xFF3DDC84)
val ForestSecondary = Color(0xFFA0E426)
val ForestTertiary = Color(0xFF00B4A0)

// ---- Theme 4: Sunset Orange ----
val SunsetPrimary = Color(0xFFFF7A45)
val SunsetSecondary = Color(0xFFFFC24B)
val SunsetTertiary = Color(0xFFFF5C8A)

// ---- Semantic ----
val ErrorRed = Color(0xFFFF5470)
val SuccessGreen = Color(0xFF38E1C6)

// ---- Syntax highlighting palette (dark-friendly) ----
object SyntaxColors {
    val Keyword = Color(0xFF7C5CFF)
    val StringLiteral = Color(0xFF38E1C6)
    val Comment = Color(0xFF6B7280)
    val Number = Color(0xFFFF9F6B)
    val Function = Color(0xFF5CC8FF)
    val Default = Color(0xFFE4E4EC)
    val Annotation = Color(0xFFFF6FA5)
}
