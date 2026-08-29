package tj.rsdevteam.inmuslim.analytics

/**
 * Created by Rustam Safarov on 8/29/26.
 * github.com/rustamsafarovrs
 *
 * Parameter keys sent along with an [AnalyticsEvent]. Firebase caps a key at 40 characters,
 * so keep new keys short and snake_cased.
 */
public object AnalyticsParam {

    public const val COUNT: String = "count"
    public const val ENABLED: String = "enabled"
    public const val ERROR: String = "error"
    public const val HAS_IMAGE: String = "has_image"
    public const val IS_ONBOARDING: String = "is_onboarding"
    public const val NAME_LENGTH: String = "name_length"
    public const val REGION_ID: String = "region_id"
    public const val RESULT_CODE: String = "result_code"
    public const val TASBIH_ID: String = "tasbih_id"
    public const val UPDATE_TYPE: String = "update_type"
}
