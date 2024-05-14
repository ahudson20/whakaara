package com.app.whakaara.ui

import com.app.whakaara.ui.onboarding.OnboardingItems
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class OnboardingItemsTest {
    @Test
    fun `enum should`() {
        assertNotNull(OnboardingItems.WELCOME)
        assertNotNull(OnboardingItems.NOTIFICATIONS)
        assertNotNull(OnboardingItems.BATTERY_OPTIMIZATION)
        assertNotNull(OnboardingItems.WIDGET)

        assertNotNull(OnboardingItems.valueOf("WELCOME"))
        assertNotNull(OnboardingItems.valueOf("NOTIFICATIONS"))
        assertNotNull(OnboardingItems.valueOf("BATTERY_OPTIMIZATION"))
        assertNotNull(OnboardingItems.valueOf("WIDGET"))

        assertEquals("WELCOME", OnboardingItems.WELCOME.name)
        assertEquals("NOTIFICATIONS", OnboardingItems.NOTIFICATIONS.name)
        assertEquals("BATTERY_OPTIMIZATION", OnboardingItems.BATTERY_OPTIMIZATION.name)
        assertEquals("WIDGET", OnboardingItems.WIDGET.name)
    }
}
