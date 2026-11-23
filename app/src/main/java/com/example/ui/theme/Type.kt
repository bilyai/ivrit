package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val AssistantFontFamily = FontFamily(
    Font(R.font.assistant, FontWeight.Normal),
    Font(R.font.assistant, FontWeight.SemiBold),
    Font(R.font.assistant, FontWeight.Bold),
    Font(R.font.assistant, FontWeight.ExtraBold),
)

val FrankRuhlLibreFontFamily = FontFamily(
    Font(R.font.frank_ruhl_libre, FontWeight.Medium),
    Font(R.font.frank_ruhl_libre, FontWeight.Bold),
)

val IvritTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = AssistantFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 32.sp,
        lineHeight = 38.sp,
        letterSpacing = 1.5.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = AssistantFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = AssistantFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = AssistantFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = AssistantFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = AssistantFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = AssistantFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = AssistantFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = AssistantFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
)

object HebrewTextStyles {
    val HeroDisplay = TextStyle(
        fontFamily = FrankRuhlLibreFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 44.sp,
        lineHeight = 52.sp,
    )
    val QuoteDisplay = TextStyle(
        fontFamily = FrankRuhlLibreFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 34.sp,
    )
    val LessonTitle = TextStyle(
        fontFamily = FrankRuhlLibreFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 30.sp,
    )
    val DialogueLine = TextStyle(
        fontFamily = FrankRuhlLibreFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 21.sp,
        lineHeight = 30.sp,
    )
    val VocabWord = TextStyle(
        fontFamily = FrankRuhlLibreFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
    )
    val Motto = TextStyle(
        fontFamily = FrankRuhlLibreFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp,
    )
}
