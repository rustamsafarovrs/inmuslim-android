package tj.rsdevteam.inmuslim.data.preferences

import android.content.ContextWrapper
import android.content.SharedPreferences

/** A [Preferences] backed by a map, so it can be built in a JVM test without an Android `Context`. */
fun inMemoryPreferences(): Preferences {
    val prefs = InMemorySharedPreferences()
    return Preferences(
        object : ContextWrapper(null) {
            override fun getSharedPreferences(name: String?, mode: Int): SharedPreferences = prefs
        },
    )
}

@Suppress("UNCHECKED_CAST")
class InMemorySharedPreferences : SharedPreferences {

    private val values = mutableMapOf<String, Any>()

    override fun getAll(): MutableMap<String, *> = values.toMutableMap()

    override fun getString(key: String?, defValue: String?): String? = values[key] as String? ?: defValue

    override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String>? =
        (values[key] as Set<String>?)?.toMutableSet() ?: defValues

    override fun getInt(key: String?, defValue: Int): Int = values[key] as Int? ?: defValue

    override fun getLong(key: String?, defValue: Long): Long = values[key] as Long? ?: defValue

    override fun getFloat(key: String?, defValue: Float): Float = values[key] as Float? ?: defValue

    override fun getBoolean(key: String?, defValue: Boolean): Boolean = values[key] as Boolean? ?: defValue

    override fun contains(key: String?): Boolean = key in values

    override fun edit(): SharedPreferences.Editor = Editor()

    override fun registerOnSharedPreferenceChangeListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener?,
    ) = Unit

    override fun unregisterOnSharedPreferenceChangeListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener?,
    ) = Unit

    // Preferences keeps a single Editor for its whole life, so pending changes are dropped after every apply.
    private inner class Editor : SharedPreferences.Editor {

        private val pending = mutableMapOf<String, Any?>()
        private var clearAll = false

        override fun putString(key: String?, value: String?) = put(key, value)

        override fun putStringSet(key: String?, values: MutableSet<String>?) = put(key, values?.toSet())

        override fun putInt(key: String?, value: Int) = put(key, value)

        override fun putLong(key: String?, value: Long) = put(key, value)

        override fun putFloat(key: String?, value: Float) = put(key, value)

        override fun putBoolean(key: String?, value: Boolean) = put(key, value)

        override fun remove(key: String?) = put(key, null)

        override fun clear(): SharedPreferences.Editor {
            clearAll = true
            return this
        }

        override fun commit(): Boolean {
            apply()
            return true
        }

        override fun apply() {
            if (clearAll) values.clear()
            pending.forEach { (key, value) -> if (value == null) values.remove(key) else values[key] = value }
            pending.clear()
            clearAll = false
        }

        private fun put(key: String?, value: Any?): SharedPreferences.Editor {
            pending[requireNotNull(key)] = value
            return this
        }
    }
}
