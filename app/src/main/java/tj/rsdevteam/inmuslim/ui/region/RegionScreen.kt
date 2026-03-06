package tj.rsdevteam.inmuslim.ui.region

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import tj.rsdevteam.inmuslim.core.router.LocalRouter
import tj.rsdevteam.inmuslim.core.router.Screen
import tj.rsdevteam.inmuslim.core.router.theme.InmuslimTheme
import tj.rsdevteam.inmuslim.core.router.theme.InmuslimTypo
import tj.rsdevteam.inmuslim.data.models.Region
import tj.rsdevteam.inmuslim.res.R
import tj.rsdevteam.inmuslim.ui.common.ErrorBottomSheet
import tj.rsdevteam.inmuslim.ui.common.PrimaryButton
import tj.rsdevteam.inmuslim.ui.common.ProgressIndicator

/**
 * Created by Rustam Safarov on 14/08/23.
 * github.com/rustamsafarovrs
 */

@Composable
private fun Regions(list: List<Region>, modifier: Modifier = Modifier, onClick: (Region) -> Unit) {
    LazyColumn(modifier = modifier) {
        items(list) { region ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { onClick.invoke(region) }
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .defaultMinSize(minHeight = 50.dp)
            ) {
                Text(text = region.name, style = InmuslimTypo.labelLarge)
                Spacer(modifier = Modifier.weight(1f))
                if (region.selected.value) {
                    Icon(painterResource(R.drawable.ic_check_24), contentDescription = null)
                }
            }
        }
    }
}

@Composable
fun RegionScreen() {
    val router = LocalRouter.current
    RegionScreen(isBottomSheet = false) {
        router.navigateAsRoot(Screen.Main)
    }
}

@Composable
fun RegionScreen(
    isBottomSheet: Boolean,
    onSelected: () -> Unit
) {
    val viewModel: RegionViewModel = hiltViewModel()
    RegionScreen(
        state = viewModel.state,
        isBottomSheet = isBottomSheet,
        handleEvent = { viewModel.handleEvent(it) },
        onSelected = onSelected
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegionScreen(
    state: RegionScreenState,
    isBottomSheet: Boolean,
    handleEvent: (RegionUIEvent) -> Unit,
    onSelected: () -> Unit
) {
    val errorState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Column(
        modifier = Modifier
            .then(if (isBottomSheet) Modifier else Modifier.navigationBarsPadding())
            .then(if (isBottomSheet) Modifier else Modifier.statusBarsPadding())
    ) {
        if (state.showLoading) {
            ProgressIndicator()
        } else {
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = stringResource(R.string.select_region_title),
                style = InmuslimTypo.titleLarge,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Regions(
                state.list,
                modifier = Modifier.weight(1f),
            ) {
                handleEvent(RegionUIEvent.DidSelectRegion(it))
            }
            Spacer(modifier = Modifier.height(12.dp))
            PrimaryButton(text = "OK") {
                handleEvent(RegionUIEvent.DidClickConfirm)
                onSelected.invoke()
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    if (state.sheetConfig != null) {
        ErrorBottomSheet(
            sheetState = errorState,
            errorBottomSheetConfig = state.sheetConfig!!,
            dismiss = { handleEvent(RegionUIEvent.DismissDialog) }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RegionScreenPreview() {
    InmuslimTheme {
        RegionScreen(
            state = RegionScreenState(
                list = listOf(
                    Region(0, "Dushanbe"),
                    Region(1, "Khujand"),
                    Region(2, "Bokhtar")
                )
            ),
            isBottomSheet = false,
            handleEvent = {},
            onSelected = {}
        )
    }
}
