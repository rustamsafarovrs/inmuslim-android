package tj.rstech.uicomponents.appbar

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import tj.rsdevteam.inmuslim.core.router.theme.InmuslimTypo
import tj.rsdevteam.inmuslim.res.R

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun LargeTopAppBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior,
    didClickBack: () -> Unit,
) {
    LargeTopAppBar(
        title = {
            Text(
                text = title,
                style = InmuslimTypo.headlineMedium.copy(fontWeight = FontWeight.Bold),
            )
        },
        navigationIcon = {
            IconButton(onClick = didClickBack) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back_24),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        },
        scrollBehavior = scrollBehavior,
    )
}
