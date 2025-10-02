package com.example.bolobudur.ui.screen.onboarding

import androidx.annotation.DrawableRes
import com.example.bolobudur.R

data class OnboardingPage(
    @DrawableRes val image: Int,
    val title: String,
    val description: String
)

// List halaman onboarding
val onboardingPages = listOf(
    OnboardingPage(
        image = R.drawable.borobudur_bg,
        title = "BoloBudur App",
        description = "Ceritanya Tak Selalu Terlihat, Kami Baru Agar Lebih Dekat."
    ),
    OnboardingPage(
        image = R.drawable.borobudur_bg,
        title = "BoloMaps",
        description = "Menelusuri Borobudur, Setiap Tempat Tersedia di Ujung Jari."
    ),
    OnboardingPage(
        image = R.drawable.borobudur_bg,
        title = "BoloFind",
        description = "Tidak Ada yang Tersembunyi, Temukan dengan Mudah."
    ),
    OnboardingPage(
        image = R.drawable.borobudur_bg,
        title = "Pairing Bluetooth",
        description = "Cukup Sambungkan Bluetooth, Siap untuk Menjelajah."
    )
)
