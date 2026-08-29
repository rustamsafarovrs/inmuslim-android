package tj.rsdevteam.inmuslim.ui.region

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tj.rsdevteam.inmuslim.analytics.AnalyticsEvent
import tj.rsdevteam.inmuslim.analytics.AnalyticsProperty
import tj.rsdevteam.inmuslim.analytics.AnalyticsTracker
import tj.rsdevteam.inmuslim.core.Resource
import tj.rsdevteam.inmuslim.data.models.Region
import tj.rsdevteam.inmuslim.data.repositories.RegionRepository
import tj.rstech.uicomponents.bottomsheet.error.ErrorBottomSheetConfig
import javax.inject.Inject

/**
 * Created by Rustam Safarov on 14/08/23.
 * github.com/rustamsafarovrs
 */

@HiltViewModel
class RegionViewModel
@Inject constructor(
    private val analytics: AnalyticsTracker,
    private val regionRepository: RegionRepository,
) : ViewModel() {

    var state by mutableStateOf(RegionScreenState())
        private set

    init {
        getRegions()
    }

    private fun getRegions() {
        viewModelScope.launch {
            regionRepository.getRegions().collect { rs ->
                when (rs) {
                    is Resource.InProgress -> Unit
                    is Resource.Success -> {
                        analytics.log(AnalyticsEvent.RegionsLoaded(rs.data.size))
                        state = state.copy(list = rs.data)
                    }

                    is Resource.Error -> {
                        analytics.log(AnalyticsEvent.RegionsLoadFailed(rs.error?.message))
                        state = state.copy(
                            sheetConfig = ErrorBottomSheetConfig(
                                msg = rs.error?.message,
                                title = "Error",
                            ),
                        )
                    }
                }
                state = state.copy(showLoading = rs is Resource.InProgress)
            }
        }
    }

    fun handleEvent(event: RegionUIEvent) {
        when (event) {
            is RegionUIEvent.GetRegions -> getRegions()
            is RegionUIEvent.DidClickConfirm -> handleDidConfirm()
            is RegionUIEvent.DidSelectRegion -> handleDidSelectRegion(event.region)
            is RegionUIEvent.DismissDialog -> dismissDialog()
        }
    }

    private fun handleDidConfirm() {
        val r = state.list.firstOrNull { it.selected.value }
        if (r != null) {
            analytics.log(AnalyticsEvent.RegionConfirmed(r.id))
            analytics.setUserProperty(AnalyticsProperty.REGION_ID, r.id.toString())
            regionRepository.saveRegionId(r.id)
        }
    }

    private fun handleDidSelectRegion(region: Region) {
        analytics.log(AnalyticsEvent.RegionSelected(region.id))
        state.list.forEach {
            if (it != region) {
                it.selected.value = false
            }
        }
        region.selected.value = true
    }

    private fun dismissDialog() {
        state = state.copy(sheetConfig = null)
    }
}
