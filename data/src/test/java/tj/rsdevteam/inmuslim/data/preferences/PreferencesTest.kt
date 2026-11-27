package tj.rsdevteam.inmuslim.data.preferences

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PreferencesTest {

    private val preferences = inMemoryPreferences()

    @Test
    fun `a new install is empty and has nothing set`() {
        assertTrue(preferences.isEmpty)
        assertEquals(-1L, preferences.getRegionId())
        assertEquals(-1L, preferences.getUserId())
        assertEquals("", preferences.getFirebaseToken())
        assertFalse(preferences.isReviewShown())
        assertFalse(preferences.isHapticEnabled())
        assertFalse(preferences.isDefaultLocaleApplied)
        assertEquals("", preferences.visitDate)
        assertEquals(0, preferences.visitDays)
    }

    @Test
    fun `is no longer empty once anything is saved`() {
        preferences.saveRegionId(3)

        assertFalse(preferences.isEmpty)
    }

    @Test
    fun `keeps what was saved`() {
        preferences.saveRegionId(3)
        preferences.saveUserId(42)
        preferences.saveFirebaseToken("token")
        preferences.saveReviewShown()
        preferences.setHapticEnabled(true)
        preferences.isDefaultLocaleApplied = true
        preferences.visitDate = "2026-04-28"
        preferences.visitDays = 2

        assertEquals(3L, preferences.getRegionId())
        assertEquals(42L, preferences.getUserId())
        assertEquals("token", preferences.getFirebaseToken())
        assertTrue(preferences.isReviewShown())
        assertTrue(preferences.isHapticEnabled())
        assertTrue(preferences.isDefaultLocaleApplied)
        assertEquals("2026-04-28", preferences.visitDate)
        assertEquals(2, preferences.visitDays)
    }

    @Test
    fun `turns haptics back off`() {
        preferences.setHapticEnabled(true)
        preferences.setHapticEnabled(false)

        assertFalse(preferences.isHapticEnabled())
    }
}
