package tj.rsdevteam.inmuslim.feature.tasbih.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import tj.rsdevteam.inmuslim.core.router.LocalRouter
import tj.rsdevteam.inmuslim.core.router.Screen
import tj.rsdevteam.inmuslim.core.router.theme.InmuslimShapes
import tj.rsdevteam.inmuslim.core.router.theme.InmuslimTheme
import tj.rsdevteam.inmuslim.core.router.theme.InmuslimTypo
import tj.rsdevteam.inmuslim.feature.tasbih.data.models.Tasbih
import tj.rsdevteam.inmuslim.res.R
import tj.rstech.uicomponents.appbar.LargeTopAppBar

@Composable
fun TasbihListScreen() {
    val router = LocalRouter.current
    val viewModel: TasbihListViewModel = hiltViewModel()
    TasbihListScreen(
        state = viewModel.state,
        didClickBack = { router.navigateUp() },
        didSelectTasbih = { router.navigate(Screen.TasbihCalculator(it.id)) },
    ) { viewModel.handleEvent(it) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TasbihListScreen(
    state: TasbihListScreenState,
    didClickBack: () -> Unit = {},
    didSelectTasbih: (Tasbih) -> Unit = {},
    eventHandler: (TasbihListUIEvent) -> Unit = {},
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = { LargeTopAppBar(stringResource(R.string.tasbih_title_tasbih), scrollBehavior, didClickBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { eventHandler(TasbihListUIEvent.DidClickShowAddDialog) },
            ) {
                Icon(painter = painterResource(R.drawable.ic_add_24), contentDescription = null)
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { safeArea ->
        LazyColumn(
            modifier = Modifier
                .padding(safeArea)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }
            items(state.tasbihs) { tasbih ->
                TasbihItem(tasbih = tasbih, didClick = { didSelectTasbih(tasbih) })
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    if (state.showAddDialog) {
        AddTasbihBottomSheet(
            didDismiss = { eventHandler(TasbihListUIEvent.DidDismissAddDialog) },
            didConfirm = { eventHandler(TasbihListUIEvent.DidClickAdd(it)) },
        )
    }
}

@Composable
private fun TasbihItem(
    tasbih: Tasbih,
    didClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(InmuslimShapes.large)
            .clickable { didClick() },
        shape = InmuslimShapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TitleSubtitle(modifier = Modifier.weight(1f), tasbih)
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                painter = painterResource(R.drawable.outline_arrow_forward_ios_24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun TitleSubtitle(
    modifier: Modifier = Modifier,
    tasbih: Tasbih,
) {
    Column(modifier = modifier) {
        Text(
            text = tasbih.name,
            style = InmuslimTypo.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (tasbih.todayCount > 0) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = tasbih.todayCount.toString(),
                style = InmuslimTypo.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Suppress("MagicNumber")
@Preview(showBackground = true)
@Composable
private fun TasbihListScreenPreview() {
    InmuslimTheme {
        TasbihListScreen(
            state = TasbihListScreenState(
                tasbihs = listOf(
                    Tasbih(1, "SubhanAllah", 99),
                    Tasbih(2, "Alhamdulillah", 0),
                    Tasbih(3, "Allahu Akbar", 33),
                ),
            ),
        )
    }
}
