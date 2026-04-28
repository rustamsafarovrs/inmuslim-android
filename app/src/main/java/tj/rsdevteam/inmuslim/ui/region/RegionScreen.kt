package tj.rsdevteam.inmuslim.ui.region

import android.content.Intent
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import tj.rsdevteam.inmuslim.ui.MainActivity
import tj.rsdevteam.inmuslim.utils.findActivity
import tj.rstech.uicomponents.PrimaryButton
import tj.rstech.uicomponents.ProgressIndicator
import tj.rstech.uicomponents.bottomsheet.error.ErrorBottomSheet

/**
 * Created by Rustam Safarov on 14/08/23.
 * github.com/rustamsafarovrs
 */

@Composable
fun RegionScreen() {
    val router = LocalRouter.current
    val context = LocalContext.current
    val hasBackStack = router.controller.previousBackStackEntry != null
    val viewModel: RegionViewModel = hiltViewModel()

    RegionScreen(
        state = viewModel.state,
        showBackButton = hasBackStack,
        handleEvent = { viewModel.handleEvent(it) },
        didClickBack = { router.navigateUp() },
        didSelect = {
            if (hasBackStack) {
                context.findActivity().finish()
                context.startActivity(Intent(context, MainActivity::class.java))
            } else {
                router.navigateAsRoot(Screen.Main)
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegionScreen(
    state: RegionScreenState,
    showBackButton: Boolean,
    handleEvent: (RegionUIEvent) -> Unit,
    didClickBack: () -> Unit,
    didSelect: () -> Unit,
) {
    val errorState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        if (showBackButton) {
            IconButton(onClick = didClickBack, modifier = Modifier.padding(4.dp)) {
                Icon(painterResource(R.drawable.ic_arrow_back_24), contentDescription = null)
            }
        }
        if (state.showLoading) {
            ProgressIndicator()
        } else {
            Spacer(modifier = Modifier.height(if (showBackButton) 8.dp else 30.dp))
            Text(
                text = stringResource(R.string.base_title_select_region),
                style = InmuslimTypo.titleLarge,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            Spacer(modifier = Modifier.height(20.dp))
            Regions(
                state.list,
                modifier = Modifier.weight(1f),
            ) {
                handleEvent(RegionUIEvent.DidSelectRegion(it))
            }
            Spacer(modifier = Modifier.height(12.dp))
            PrimaryButton(title = "OK") {
                handleEvent(RegionUIEvent.DidClickConfirm)
                didSelect.invoke()
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    if (state.sheetConfig != null) {
        ErrorBottomSheet(
            sheetState = errorState,
            errorBottomSheetConfig = state.sheetConfig!!,
            dismiss = { handleEvent(RegionUIEvent.DismissDialog) },
        )
    }
}

@Composable
private fun Regions(list: List<Region>, modifier: Modifier = Modifier, didClick: (Region) -> Unit) {
    LazyColumn(modifier = modifier) {
        items(list) { region ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { didClick.invoke(region) }
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .defaultMinSize(minHeight = 50.dp),
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

@Preview(showBackground = true)
@Composable
private fun RegionScreenPreview() {
    InmuslimTheme {
        RegionScreen(
            state = RegionScreenState(
                list = listOf(
                    Region(0, "Dushanbe"),
                    Region(1, "Khujand"),
                    Region(2, "Bokhtar"),
                ),
            ),
            showBackButton = false,
            handleEvent = {},
            didClickBack = {},
            didSelect = {},
        )
    }
}
