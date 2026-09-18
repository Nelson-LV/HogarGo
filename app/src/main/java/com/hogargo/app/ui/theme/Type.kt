package com.hogargo.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.hogargo.app.R

// Quicksand and Nunito Sans are variable fonts (weight axis); each FontWeight below
// selects a distinct instance from the same bundled file via FontVariation.

@OptIn(ExperimentalTextApi::class)
private fun variableFont(resId: Int, weight: FontWeight) = Font(
    resId = resId,
    weight = weight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
)

val QuicksandFamily = FontFamily(
    variableFont(R.font.quicksand, FontWeight.Medium),
    variableFont(R.font.quicksand, FontWeight.SemiBold),
    variableFont(R.font.quicksand, FontWeight.Bold),
)

val NunitoSansFamily = FontFamily(
    variableFont(R.font.nunito_sans, FontWeight.Normal),
    variableFont(R.font.nunito_sans, FontWeight.SemiBold),
    variableFont(R.font.nunito_sans, FontWeight.Bold),
)

private val baseline = Typography()

val HogarGoTypography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = QuicksandFamily, fontWeight = FontWeight.Bold),
    displayMedium = baseline.displayMedium.copy(fontFamily = QuicksandFamily, fontWeight = FontWeight.Bold),
    displaySmall = baseline.displaySmall.copy(fontFamily = QuicksandFamily, fontWeight = FontWeight.Bold),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = QuicksandFamily, fontWeight = FontWeight.Bold),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = QuicksandFamily, fontWeight = FontWeight.SemiBold),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = QuicksandFamily, fontWeight = FontWeight.SemiBold),
    titleLarge = baseline.titleLarge.copy(fontFamily = QuicksandFamily, fontWeight = FontWeight.SemiBold),
    titleMedium = baseline.titleMedium.copy(fontFamily = QuicksandFamily, fontWeight = FontWeight.SemiBold),
    titleSmall = baseline.titleSmall.copy(fontFamily = QuicksandFamily, fontWeight = FontWeight.SemiBold),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = NunitoSansFamily, fontWeight = FontWeight.Normal),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = NunitoSansFamily, fontWeight = FontWeight.Normal),
    bodySmall = baseline.bodySmall.copy(fontFamily = NunitoSansFamily, fontWeight = FontWeight.Normal),
    labelLarge = baseline.labelLarge.copy(fontFamily = QuicksandFamily, fontWeight = FontWeight.SemiBold),
    labelMedium = baseline.labelMedium.copy(fontFamily = QuicksandFamily, fontWeight = FontWeight.SemiBold),
    labelSmall = baseline.labelSmall.copy(fontFamily = QuicksandFamily, fontWeight = FontWeight.SemiBold),
)
