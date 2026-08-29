package tj.rsdevteam.inmuslim.analytics

/**
 * Created by Rustam Safarov on 8/29/26.
 * github.com/rustamsafarovrs
 *
 * Every screen of the app that is reported as a `screen_view`.
 */
public enum class AnalyticsScreen(public val screenName: String) {

    HOME("home"),
    REGIONS("regions"),
    SETTINGS("settings"),
    TASBIH_LIST("tasbih_list"),
    TASBIH_CALCULATOR("tasbih_calculator"),
    TASBIH_HISTORY("tasbih_history"),
    TASBIH_ENTRY_HISTORY("tasbih_entry_history"),
}
