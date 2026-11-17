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

        assertEquals(3L, preferences.getRegionId())
        assertEquals(42L, preferences.getUserId())
        assertEquals("token", preferences.getFirebaseToken())
        assertTrue(preferences.isReviewShown())
        assertTrue(preferences.isHapticEnabled())
        assertTrue(preferences.isDefaultLocaleApplied)
    }

    @Test
    fun `turns haptics back off`() {
        preferences.setHapticEnabled(true)
        preferences.setHapticEnabled(false)

        assertFalse(preferences.isHapticEnabled())
    }
}
