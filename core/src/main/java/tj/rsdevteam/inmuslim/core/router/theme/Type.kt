package tj.rsdevteam.inmuslim.core.router.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import tj.rsdevteam.inmuslim.res.R

private val light = Font(R.font.sf_pro_text_light)
private val regular = Font(R.font.sf_pro_text_regular)
private val medium = Font(R.font.sf_pro_text_medium)
private val semiBold = Font(R.font.sf_pro_text_semibold)
private val bold = Font(R.font.sf_pro_text_bold)
val FontLight = FontFamily(light)
val FontRegular = FontFamily(regular)
val FontMedium = FontFamily(medium)
val FontSemiBold = FontFamily(semiBold)
val FontBold = FontFamily(bold)
val AppFontFamily = FontFamily(light, regular, medium, semiBold, bold)

val Light12 = TextStyle(fontFamily = FontLight, fontSize = 12.sp)
val Light14 = TextStyle(fontFamily = FontLight, fontSize = 14.sp)
val Light16 = TextStyle(fontFamily = FontLight, fontSize = 16.sp)
val Light18 = TextStyle(fontFamily = FontLight, fontSize = 18.sp)
val Light20 = TextStyle(fontFamily = FontLight, fontSize = 20.sp)
val Light22 = TextStyle(fontFamily = FontLight, fontSize = 22.sp)

val Regular8 = TextStyle(fontFamily = FontRegular, fontSize = 8.sp)
val Regular9 = TextStyle(fontFamily = FontRegular, fontSize = 9.sp)
val Regular10 = TextStyle(fontFamily = FontRegular, fontSize = 10.sp)
val Regular12 = TextStyle(fontFamily = FontRegular, fontSize = 12.sp)
val Regular14 = TextStyle(fontFamily = FontRegular, fontSize = 14.sp)
val Regular16 = TextStyle(fontFamily = FontRegular, fontSize = 16.sp)
val Regular18 = TextStyle(fontFamily = FontRegular, fontSize = 18.sp)
val Regular20 = TextStyle(fontFamily = FontRegular, fontSize = 20.sp)
val Regular22 = TextStyle(fontFamily = FontRegular, fontSize = 22.sp)

val Medium12 = TextStyle(fontFamily = FontMedium, fontSize = 12.sp)
val Medium14 = TextStyle(fontFamily = FontMedium, fontSize = 14.sp)
val Medium16 = TextStyle(fontFamily = FontMedium, fontSize = 16.sp)
val Medium18 = TextStyle(fontFamily = FontMedium, fontSize = 18.sp)
val Medium20 = TextStyle(fontFamily = FontMedium, fontSize = 20.sp)
val Medium22 = TextStyle(fontFamily = FontMedium, fontSize = 22.sp)
val Medium24 = TextStyle(fontFamily = FontMedium, fontSize = 24.sp)
val Medium26 = TextStyle(fontFamily = FontMedium, fontSize = 26.sp)
val Medium28 = TextStyle(fontFamily = FontMedium, fontSize = 28.sp)

val SemiBold8 = TextStyle(fontFamily = FontSemiBold, fontSize = 8.sp)
val SemiBold12 = TextStyle(fontFamily = FontSemiBold, fontSize = 12.sp)
val SemiBold14 = TextStyle(fontFamily = FontSemiBold, fontSize = 14.sp)
val SemiBold16 = TextStyle(fontFamily = FontSemiBold, fontSize = 16.sp)
val SemiBold18 = TextStyle(fontFamily = FontSemiBold, fontSize = 18.sp)
val SemiBold20 = TextStyle(fontFamily = FontSemiBold, fontSize = 20.sp)
val SemiBold22 = TextStyle(fontFamily = FontSemiBold, fontSize = 22.sp)
val SemiBold24 = TextStyle(fontFamily = FontSemiBold, fontSize = 24.sp)
val SemiBold26 = TextStyle(fontFamily = FontSemiBold, fontSize = 26.sp)
val SemiBold28 = TextStyle(fontFamily = FontSemiBold, fontSize = 28.sp)
val SemiBold30 = TextStyle(fontFamily = FontSemiBold, fontSize = 30.sp)
val SemiBold32 = TextStyle(fontFamily = FontSemiBold, fontSize = 32.sp)
val SemiBold36 = TextStyle(fontFamily = FontSemiBold, fontSize = 36.sp)

val Bold12 = TextStyle(fontFamily = FontBold, fontSize = 12.sp)
val Bold14 = TextStyle(fontFamily = FontBold, fontSize = 14.sp)
val Bold16 = TextStyle(fontFamily = FontBold, fontSize = 16.sp)
val Bold18 = TextStyle(fontFamily = FontBold, fontSize = 18.sp)
val Bold20 = TextStyle(fontFamily = FontBold, fontSize = 20.sp)
val Bold22 = TextStyle(fontFamily = FontBold, fontSize = 22.sp)
val Bold24 = TextStyle(fontFamily = FontBold, fontSize = 24.sp)
val Bold26 = TextStyle(fontFamily = FontBold, fontSize = 26.sp)
val Bold28 = TextStyle(fontFamily = FontBold, fontSize = 28.sp)
val Bold30 = TextStyle(fontFamily = FontBold, fontSize = 30.sp)
val Bold32 = TextStyle(fontFamily = FontBold, fontSize = 32.sp)
val Bold36 = TextStyle(fontFamily = FontBold, fontSize = 36.sp)
val Bold40 = TextStyle(fontFamily = FontBold, fontSize = 40.sp)
val Bold44 = TextStyle(fontFamily = FontBold, fontSize = 44.sp)
val Bold48 = TextStyle(fontFamily = FontBold, fontSize = 48.sp)
val Bold54 = TextStyle(fontFamily = FontBold, fontSize = 54.sp)
val Bold60 = TextStyle(fontFamily = FontBold, fontSize = 60.sp)

val defaultTextStyle = TextStyle(
    fontFamily = AppFontFamily,
    platformStyle = PlatformTextStyle(
        includeFontPadding = false,
    ),
    lineHeightStyle = LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = LineHeightStyle.Trim.None,
    ),
)

val InmuslimTypo = Typography(
    displayLarge = defaultTextStyle.copy(
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp,
    ),
    displayMedium = defaultTextStyle.copy(
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp,
    ),
    displaySmall = defaultTextStyle.copy(
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp,
    ),
    headlineLarge = defaultTextStyle.copy(
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
        lineBreak = LineBreak.Heading,
    ),
    headlineMedium = defaultTextStyle.copy(
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
        lineBreak = LineBreak.Heading,
    ),
    headlineSmall = defaultTextStyle.copy(
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
        lineBreak = LineBreak.Heading,
    ),
    titleLarge = defaultTextStyle.copy(
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
        lineBreak = LineBreak.Heading,
    ),
    titleMedium = defaultTextStyle.copy(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
        fontWeight = FontWeight.Medium,
        lineBreak = LineBreak.Heading,
    ),
    titleSmall = defaultTextStyle.copy(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
        fontWeight = FontWeight.Medium,
        lineBreak = LineBreak.Heading,
    ),
    labelLarge = defaultTextStyle.copy(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
        fontWeight = FontWeight.Medium,
    ),
    labelMedium = defaultTextStyle.copy(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        fontWeight = FontWeight.Medium,
    ),
    labelSmall = defaultTextStyle.copy(
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        fontWeight = FontWeight.Medium,
    ),
    bodyLarge = defaultTextStyle.copy(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        lineBreak = LineBreak.Paragraph,
    ),
    bodyMedium = defaultTextStyle.copy(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
        lineBreak = LineBreak.Paragraph,
    ),
    bodySmall = defaultTextStyle.copy(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
        lineBreak = LineBreak.Paragraph,
    ),
)
