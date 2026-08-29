package tj.rsdevteam.inmuslim.analytics

/**
 * Created by Rustam Safarov on 8/29/26.
 * github.com/rustamsafarovrs
 *
 * User property names passed to [AnalyticsTracker.setUserProperty]. Unlike an [AnalyticsEvent] a
 * property is sticky — it is attached to every later event until it is overwritten. Firebase caps
 * a name at 24 characters.
 */
public object AnalyticsProperty {

    /** Region the prayer times are shown for, so reports can be split per city. */
    public const val REGION_ID: String = "region_id"
}
