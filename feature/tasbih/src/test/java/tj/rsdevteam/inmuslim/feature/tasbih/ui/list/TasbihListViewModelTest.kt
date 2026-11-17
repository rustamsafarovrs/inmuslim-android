package tj.rsdevteam.inmuslim.feature.tasbih.ui.list

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import tj.rsdevteam.inmuslim.analytics.AnalyticsEvent
import tj.rsdevteam.inmuslim.analytics.FakeAnalyticsTracker
import tj.rsdevteam.inmuslim.core.Resource
import tj.rsdevteam.inmuslim.core.testing.MainDispatcherRule
import tj.rsdevteam.inmuslim.feature.tasbih.data.models.Tasbih
import tj.rsdevteam.inmuslim.feature.tasbih.domain.usecases.AddTasbihUseCase
import tj.rsdevteam.inmuslim.feature.tasbih.domain.usecases.ObserveTasbihsUseCase
import tj.rsdevteam.inmuslim.feature.tasbih.fakes.FakeTasbihRepository

class TasbihListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val analytics = FakeAnalyticsTracker()
    private val tasbihs = MutableStateFlow<Resource<List<Tasbih>>>(Resource.InProgress())
    private val repository = FakeTasbihRepository().also { it.tasbihs = tasbihs }

    private fun viewModel() = TasbihListViewModel(
        analytics,
        ObserveTasbihsUseCase(repository),
        AddTasbihUseCase(repository),
    )

    @Test
    fun `is loading until the tasbihs arrive`() {
        assertTrue(viewModel().state.base.isLoading)
    }

    @Test
    fun `shows the tasbihs`() {
        val viewModel = viewModel()
        val list = listOf(Tasbih(2, "Alhamdulillah", 10), Tasbih(1, "SubhanAllah", 33))

        tasbihs.value = Resource.Success(list)

        assertEquals(list, viewModel.state.tasbihs)
        assertFalse(viewModel.state.base.isLoading)
    }

    @Test
    fun `shows an error when loading fails and clears it once the tasbihs arrive`() {
        val viewModel = viewModel()
        val error = IllegalStateException("boom")

        tasbihs.value = Resource.Error(error = error)
        assertEquals(error, viewModel.state.base.error)
        assertFalse(viewModel.state.base.isLoading)

        tasbihs.value = Resource.Success(emptyList())
        assertNull(viewModel.state.base.error)
    }

    @Test
    fun `opens and closes the add dialog`() {
        val viewModel = viewModel()

        viewModel.handleEvent(TasbihListUIEvent.DidClickShowAddDialog)
        assertTrue(viewModel.state.showAddDialog)
        assertEquals(listOf(AnalyticsEvent.TasbihAddDialogShown), analytics.events)

        viewModel.handleEvent(TasbihListUIEvent.DidDismissAddDialog)
        assertFalse(viewModel.state.showAddDialog)
    }

    @Test
    fun `adds the tasbih and reports only the length of its name`() {
        repository.addResult = flowOf(Resource.InProgress(), Resource.Success(3L))
        val viewModel = viewModel()
        viewModel.handleEvent(TasbihListUIEvent.DidClickShowAddDialog)

        viewModel.handleEvent(TasbihListUIEvent.DidClickAdd("Allahu Akbar"))

        assertEquals(listOf("Allahu Akbar"), repository.addedNames)
        assertFalse(viewModel.state.showAddDialog)
        assertEquals(AnalyticsEvent.TasbihAdded(nameLength = 12), analytics.events.last())
    }

    @Test
    fun `shows the error when adding fails and reports nothing`() {
        val error = IllegalStateException("disk full")
        repository.addResult = flowOf(Resource.Error(error = error))
        val viewModel = viewModel()

        viewModel.handleEvent(TasbihListUIEvent.DidClickAdd("Allahu Akbar"))

        assertEquals(error, viewModel.state.base.error)
        assertTrue(analytics.events.none { it is AnalyticsEvent.TasbihAdded })
    }
}
