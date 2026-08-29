package tj.rsdevteam.inmuslim.analytics

/**
 * Created by Rustam Safarov on 8/29/26.
 * github.com/rustamsafarovrs
 *
 * Single entry point for product analytics. Inject it wherever something worth measuring happens
 * and report it with one of the [AnalyticsEvent] values — callers never touch a vendor SDK.
 */
public interface AnalyticsTracker {

    /** Reports [event] with its parameters. */
    public fun log(event: AnalyticsEvent)

    /** Reports that [screen] became visible. */
    public fun logScreenView(screen: AnalyticsScreen)

    /** Associates the following events with the backend user id, or clears it when `null`. */
    public fun setUserId(userId: String?)

    /** Sets a user property used to segment reports, or clears it when [value] is `null`. */
    public fun setUserProperty(name: String, value: String?)
}
