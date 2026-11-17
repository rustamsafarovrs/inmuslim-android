package tj.rsdevteam.inmuslim.analytics

public class FakeAnalyticsTracker : AnalyticsTracker {

    public val events: MutableList<AnalyticsEvent> = mutableListOf()
    public val screens: MutableList<AnalyticsScreen> = mutableListOf()
    public val userIds: MutableList<String?> = mutableListOf()
    public val userProperties: MutableMap<String, String?> = mutableMapOf()

    override fun log(event: AnalyticsEvent) {
        events += event
    }

    override fun logScreenView(screen: AnalyticsScreen) {
        screens += screen
    }

    override fun setUserId(userId: String?) {
        userIds += userId
    }

    override fun setUserProperty(name: String, value: String?) {
        userProperties[name] = value
    }
}
