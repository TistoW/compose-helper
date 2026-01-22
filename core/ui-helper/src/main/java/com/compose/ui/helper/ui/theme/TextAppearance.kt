package com.compose.ui.helper.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight

object TextAppearance {

    // Body
    @Composable
    fun body1() = MaterialTheme.typography.bodyLarge.copy(
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    )

    @Composable
    fun body1Bold() = body1().copy(fontWeight = FontWeight.Bold)

    @Composable
    fun body2() = MaterialTheme.typography.bodyMedium.copy(
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    )

    @Composable
    fun body2Bold() = body2().copy(fontWeight = FontWeight.Bold)

    @Composable
    fun body3() = MaterialTheme.typography.bodySmall.copy(
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    )

    @Composable
    fun body3Bold() = body3().copy(fontWeight = FontWeight.Bold)

    // Label
    @Composable
    fun label1() = MaterialTheme.typography.labelLarge.copy(
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    )

    @Composable
    fun label1Bold() = label1().copy(fontWeight = FontWeight.Bold)

    @Composable
    fun label2() = MaterialTheme.typography.labelMedium.copy(
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    )

    @Composable
    fun label2Bold() = label2().copy(fontWeight = FontWeight.Bold)

    @Composable
    fun label3() = MaterialTheme.typography.labelSmall.copy(
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    )

    @Composable
    fun label3Bold() = label3().copy(fontWeight = FontWeight.Bold)

    // Title
    @Composable
    fun title1() = MaterialTheme.typography.titleLarge.copy(
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    )

    @Composable
    fun title1Bold() = title1().copy(fontWeight = FontWeight.Bold)

    @Composable
    fun title2() = MaterialTheme.typography.titleMedium.copy(
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    )

    @Composable
    fun title2Bold() = title2().copy(fontWeight = FontWeight.Bold)

    @Composable
    fun title3() = MaterialTheme.typography.titleSmall.copy(
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    )

    @Composable
    fun title3Bold() = title3().copy(fontWeight = FontWeight.Bold)

    // Headline
    @Composable
    fun headline1() = MaterialTheme.typography.headlineSmall.copy(
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    )

    @Composable
    fun headline1Bold() = headline1().copy(fontWeight = FontWeight.Bold)

    @Composable
    fun headline2() = MaterialTheme.typography.headlineMedium.copy(
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    )

    @Composable
    fun headline2Bold() = headline2().copy(fontWeight = FontWeight.Bold)

    @Composable
    fun headline3() = MaterialTheme.typography.headlineLarge.copy(
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    )

    @Composable
    fun headline3Bold() = headline3().copy(fontWeight = FontWeight.Bold)

    // Display
    @Composable
    fun display1() = MaterialTheme.typography.displaySmall.copy(
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    )

    @Composable
    fun display1Bold() = display1().copy(fontWeight = FontWeight.Bold)

    @Composable
    fun display2() = MaterialTheme.typography.displayMedium.copy(
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    )

    @Composable
    fun display2Bold() = display2().copy(fontWeight = FontWeight.Bold)

    @Composable
    fun display3() = MaterialTheme.typography.displayLarge.copy(
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    )

    @Composable
    fun display3Bold() = display3().copy(fontWeight = FontWeight.Bold)

}