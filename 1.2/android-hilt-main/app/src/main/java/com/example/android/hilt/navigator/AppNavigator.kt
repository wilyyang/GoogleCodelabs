package com.example.android.hilt.navigator

enum class Screens {
    BUTTONS,
    LOGS
}

interface AppNavigator {
    fun navigateTo(screen: Screens)
}
