package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Immutable
data class IvritExtendedColors(
    val goldAccent: Color,
    val luminousGold: Color,
    val tanBorder: Color,
    val theorySurface: Color,
    val dialogueSurface: Color,
    val quoteGradientStart: Color,
    val quoteGradientEnd: Color,
    val audioButtonBg: Color,
    val audioButtonContent: Color,
    val mutedBronze: Color,
    val quizCorrectBg: Color,
    val quizCorrectBorder: Color,
    val quizCorrectText: Color,
    val quizWrongBg: Color,
    val quizWrongBorder: Color,
    val quizWrongText: Color,
    val headerNavyDark: Color,
    val headerBlueMid: Color,
    val headerBlueBright: Color,
    val headerSubtitle: Color,
)

val LocalIvritColors = staticCompositionLocalOf {
    IvritExtendedColors(
        goldAccent = AntiqueGold,
        luminousGold = LuminousGold,
        tanBorder = WarmTanBorder,
        theorySurface = SoftParchment,
        dialogueSurface = CreamDialogue,
        quoteGradientStart = SoftSkyTint,
        quoteGradientEnd = SoftGoldTint,
        audioButtonBg = SoftSkyTint,
        audioButtonContent = SapphireBlue,
        mutedBronze = MutedBronze,
        quizCorrectBg = QuizCorrectBg,
        quizCorrectBorder = QuizCorrectBorder,
        quizCorrectText = QuizCorrectText,
        quizWrongBg = QuizWrongBg,
        quizWrongBorder = QuizWrongBorder,
        quizWrongText = QuizWrongText,
        headerNavyDark = DeepRoyalNavy,
        headerBlueMid = SapphireBlue,
        headerBlueBright = BrightRoyalBlue,
        headerSubtitle = WarmCreamHeader,
    )
}

private val LightColorScheme = lightColorScheme(
    primary = SapphireBlue,
    onPrimary = Color.White,
    primaryContainer = SoftSkyTint,
    onPrimaryContainer = DeepRoyalNavy,
    secondary = AntiqueGold,
    onSecondary = Color.White,
    secondaryContainer = SoftGoldTint,
    onSecondaryContainer = DeepRoyalNavy,
    tertiary = DeepRoyalNavy,
    onTertiary = Color.White,
    background = WarmParchment,
    onBackground = InkCharcoal,
    surface = WarmIvoryCard,
    onSurface = InkCharcoal,
    surfaceVariant = SoftParchment,
    onSurfaceVariant = MutedBronze,
    outline = AntiqueGold,
    outlineVariant = WarmTanBorder,
)

private val DarkColorScheme = darkColorScheme(
    primary = NightPrimaryBlue,
    onPrimary = NightNavyBg,
    primaryContainer = NightElevatedSurface,
    onPrimaryContainer = NightTextPrimary,
    secondary = LuminousGold,
    onSecondary = DeepRoyalNavy,
    secondaryContainer = NightElevatedSurface,
    onSecondaryContainer = LuminousGold,
    tertiary = AntiqueGold,
    onTertiary = Color.White,
    background = NightNavyBg,
    onBackground = NightTextPrimary,
    surface = NightCardSurface,
    onSurface = NightTextPrimary,
    surfaceVariant = NightElevatedSurface,
    onSurfaceVariant = NightTextSecondary,
    outline = NightGoldBorder,
    outlineVariant = Color(0xFF2F3E6B),
)

private val LightExtendedColors = IvritExtendedColors(
    goldAccent = AntiqueGold,
    luminousGold = LuminousGold,
    tanBorder = WarmTanBorder,
    theorySurface = SoftParchment,
    dialogueSurface = CreamDialogue,
    quoteGradientStart = SoftSkyTint,
    quoteGradientEnd = SoftGoldTint,
    audioButtonBg = SoftSkyTint,
    audioButtonContent = SapphireBlue,
    mutedBronze = MutedBronze,
    quizCorrectBg = QuizCorrectBg,
    quizCorrectBorder = QuizCorrectBorder,
    quizCorrectText = QuizCorrectText,
    quizWrongBg = QuizWrongBg,
    quizWrongBorder = QuizWrongBorder,
    quizWrongText = QuizWrongText,
    headerNavyDark = DeepRoyalNavy,
    headerBlueMid = SapphireBlue,
    headerBlueBright = BrightRoyalBlue,
    headerSubtitle = WarmCreamHeader,
)

private val DarkExtendedColors = IvritExtendedColors(
    goldAccent = LuminousGold,
    luminousGold = LuminousGold,
    tanBorder = Color(0xFF334270),
    theorySurface = NightElevatedSurface,
    dialogueSurface = NightCardSurface,
    quoteGradientStart = Color(0xFF152550),
    quoteGradientEnd = Color(0xFF282218),
    audioButtonBg = NightElevatedSurface,
    audioButtonContent = LuminousGold,
    mutedBronze = NightTextSecondary,
    quizCorrectBg = NightCorrectBg,
    quizCorrectBorder = QuizCorrectBorder,
    quizCorrectText = NightCorrectText,
    quizWrongBg = NightWrongBg,
    quizWrongBorder = QuizWrongBorder,
    quizWrongText = NightWrongText,
    headerNavyDark = Color(0xFF060C22),
    headerBlueMid = DeepRoyalNavy,
    headerBlueBright = SapphireBlue,
    headerSubtitle = WarmCreamHeader,
)

private val IvritShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
)

@Composable
fun IvritProTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    CompositionLocalProvider(LocalIvritColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = IvritTypography,
            shapes = IvritShapes,
            content = content,
        )
    }
}

object IvritTheme {
    val colors: IvritExtendedColors
        @Composable get() = LocalIvritColors.current
}
