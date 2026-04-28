package tj.rsdevteam.inmuslim.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tj.rsdevteam.inmuslim.core.router.LocalRouter
import tj.rsdevteam.inmuslim.core.router.Screen
import tj.rsdevteam.inmuslim.core.router.theme.InmuslimShapes
import tj.rsdevteam.inmuslim.core.router.theme.InmuslimTheme
import tj.rsdevteam.inmuslim.core.router.theme.InmuslimTypo
import tj.rsdevteam.inmuslim.res.R
import tj.rsdevteam.inmuslim.utils.Utils

/**
 * Created by Rustam Safarov on 8/19/23.
 * github.com/rustamsafarovrs
 */

@Composable
fun SettingsScreen() {
    val router = LocalRouter.current
    val context = LocalContext.current

    SettingsScreen(
        didClickBack = { router.navigateUp() },
        didClickRegion = { router.navigate(Screen.Regions) },
        didClickLanguage = { Utils.openLanguageSettings(context) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    didClickBack: () -> Unit,
    didClickRegion: () -> Unit,
    didClickLanguage: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.common_title_settings),
                        style = InmuslimTypo.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = didClickBack) {
                        Icon(
                            painterResource(R.drawable.ic_arrow_back_24),
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(state = rememberScrollState())
                .fillMaxSize(),
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.common_title_general).uppercase(),
                style = InmuslimTypo.labelLarge.copy(
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp,
                ),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            )

            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                shape = InmuslimShapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Column {
                    SettingItem(
                        icon = ImageVector.vectorResource(R.drawable.ic_location_on_24),
                        title = stringResource(R.string.base_action_change_region),
                        desc = stringResource(R.string.base_description_change_region),
                        didClick = didClickRegion,
                        showDivider = true,
                    )
                    SettingItem(
                        icon = ImageVector.vectorResource(R.drawable.ic_dictionary_24),
                        title = stringResource(R.string.common_action_change_language),
                        desc = stringResource(R.string.common_description_change_language),
                        didClick = didClickLanguage,
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                shape = InmuslimShapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f),
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_brightness_alert_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.base_description_timing_source),
                        style = InmuslimTypo.bodyMedium.copy(
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        ),
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Version ${tj.rsdevteam.inmuslim.BuildConfig.VERSION_NAME}",
                style = InmuslimTypo.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SettingItem(
    didClick: () -> Unit,
    icon: ImageVector,
    title: String,
    desc: String,
    showDivider: Boolean = false,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { didClick.invoke() }
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp),
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = InmuslimTypo.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (desc.isNotEmpty()) {
                    Text(
                        text = desc,
                        style = InmuslimTypo.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Icon(
                painter = painterResource(R.drawable.ic_arrow_back_24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .size(20.dp)
                    .rotate(180f),
            )
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            )
        }
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    InmuslimTheme {
        SettingsScreen(
            didClickBack = {},
            didClickRegion = {},
            didClickLanguage = {},
        )
    }
}
