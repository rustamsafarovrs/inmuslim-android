package tj.rsdevteam.inmuslim.analytics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AnalyticsEventTest {

    private val snakeCase = Regex("^[a-z][a-z0-9_]*$")

    // Every event declared in the catalogue, so a new one is checked without touching this test.
    private val allEvents: List<AnalyticsEvent> = AnalyticsEvent::class.java.declaredClasses
        .filter { AnalyticsEvent::class.java.isAssignableFrom(it) }
        .map { type ->
            val instance = runCatching { type.getField("INSTANCE").get(null) }.getOrNull()
                ?: type.constructors.single().let { it.newInstance(*it.parameterTypes.map(::sampleOf).toTypedArray()) }
            instance as AnalyticsEvent
        }

    private fun sampleOf(type: Class<*>): Any = when (type) {
        Boolean::class.javaPrimitiveType -> true
        Int::class.javaPrimitiveType -> 1
        Long::class.javaPrimitiveType -> 1L
        String::class.java -> "value"
        else -> error("Add a sample value for $type")
    }

    @Test
    fun `finds every event in the catalogue`() {
        assertTrue(allEvents.size > 1)
        assertTrue(AnalyticsEvent.UserRegistered in allEvents)
    }

    @Test
    fun `event names follow the Firebase rules`() {
        allEvents.forEach { event ->
            assertTrue("${event.name} is not snake_case", event.name.matches(snakeCase))
            assertTrue("${event.name} is longer than 40 characters", event.name.length <= 40)
            listOf("firebase_", "google_", "ga_").forEach { prefix ->
                assertFalse("${event.name} uses the reserved prefix $prefix", event.name.startsWith(prefix))
            }
        }
    }

    @Test
    fun `event names are unique`() {
        val duplicates = allEvents.groupBy { it.name }.filterValues { it.size > 1 }.keys

        assertEquals(emptySet<String>(), duplicates)
    }

    @Test
    fun `parameter keys follow the Firebase rules`() {
        allEvents.flatMap { it.params.keys }.forEach { key ->
            assertTrue("$key is not snake_case", key.matches(snakeCase))
            assertTrue("$key is longer than 40 characters", key.length <= 40)
        }
    }

    @Test
    fun `reports a missing or blank error as unknown`() {
        assertEquals("unknown", AnalyticsEvent.RegionsLoadFailed(null).params[AnalyticsParam.ERROR])
        assertEquals("unknown", AnalyticsEvent.TimingLoadFailed("  ").params[AnalyticsParam.ERROR])
        assertEquals("timeout", AnalyticsEvent.UserRegisterFailed("timeout").params[AnalyticsParam.ERROR])
    }

    @Test
    fun `reports the length of a new tasbih name, never the name itself`() {
        assertEquals(mapOf(AnalyticsParam.NAME_LENGTH to 11), AnalyticsEvent.TasbihAdded(nameLength = 11).params)
    }

    @Test
    fun `screen names are unique snake_case`() {
        val names = AnalyticsScreen.entries.map { it.screenName }

        names.forEach { assertTrue("$it is not snake_case", it.matches(snakeCase)) }
        assertEquals(names.size, names.toSet().size)
    }

    @Test
    fun `user property names fit the Firebase limit`() {
        assertTrue(AnalyticsProperty.REGION_ID.length <= 24)
    }
}
