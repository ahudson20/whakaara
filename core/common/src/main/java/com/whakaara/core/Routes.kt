package com.whakaara.core

sealed class RootScreen(val route: String) {
    data object Alarm : RootScreen("alarm_root")
    data object Stopwatch : RootScreen("stopwatch_root")

    data object Timer : RootScreen("timer_root")

    data object Onboarding : RootScreen("onboarding_root")

    // TODO: settings feature module?
    data object Settings: RootScreen("settings_root")
}

sealed class LeafScreen(val route: String) {
    data object Alarm : LeafScreen("alarm")
    data object Stopwatch : LeafScreen("stopwatch")
    data object Timer : LeafScreen("timer")

    data object Onboarding : LeafScreen("onboarding")
}
