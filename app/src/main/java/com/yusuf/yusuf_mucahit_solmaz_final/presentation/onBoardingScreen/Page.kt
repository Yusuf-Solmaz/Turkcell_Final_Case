package com.yusuf.paparafinalcase.presentation.onBoardingScreen

import android.content.Context
import com.yusuf.yusuf_mucahit_solmaz_final.R

data class Page(
    val title: String,
    val description: String,
    val lottieFile: Int
)

fun getOnboardingPages(context: Context): List<Page> {
    return listOf(
        Page(
            title = context.getString(R.string.onboarding_title_1),
            description = context.getString(R.string.onboarding_description_1),
            lottieFile = R.raw.onboarding_1
        ),
        Page(
            title = context.getString(R.string.onboarding_title_2),
            description = context.getString(R.string.onboarding_description_2),
            lottieFile = R.raw.onboarding_2
        ),
        Page(
            title = context.getString(R.string.onboarding_title_3),
            description = context.getString(R.string.onboarding_description_3),
            lottieFile = R.raw.onboarding_3
        )
    )
}


//val pager = listOf(
//    Page(
//        "${R.string.onboarding_title1}",
//        "Step into FastBuy to explore and shop, your trusted guide where you can find every kind of product. Discover fashion, technology, home essentials, and more, and don't miss out on exclusive deals.",
//        R.raw.onboarding_1
//    ),
//    Page(
//        "Personalize Your Shopping Experience",
//        "Personalize every shopping experience with FastBuy. Find products tailored to your needs, get recommendations, and keep track of your favorite items.",
//        R.raw.onboarding_2
//    ),
//    Page(
//        "Simplify Your Shopping",
//        "Welcome to FastBuy, the app that simplifies your shopping experience. Browse thousands of options in fashion, electronics, home decor, and more, track discounts and promotions.",
//        R.raw.onboarding_3
//    )
//
//)
