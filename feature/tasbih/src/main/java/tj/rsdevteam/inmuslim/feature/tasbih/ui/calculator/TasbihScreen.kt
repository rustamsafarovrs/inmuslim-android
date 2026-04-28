package tj.rsdevteam.inmuslim.feature.tasbih.ui.calculator

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import tj.rsdevteam.inmuslim.core.router.LocalRouter
import tj.rsdevteam.inmuslim.core.router.Screen
import tj.rsdevteam.inmuslim.core.router.theme.InmuslimTheme
import tj.rsdevteam.inmuslim.core.router.theme.InmuslimTypo
import tj.rsdevteam.inmuslim.res.R
import java.text.NumberFormat

@Composable
fun TasbihScreen() {
    val router = LocalRouter.current
    val viewModel: TasbihViewModel = hiltViewModel()
    TasbihScreen(
        state = viewModel.state,
        handleEvent = viewModel::handleEvent,
        didClickBack = router::navigateUp,
        didClickHistory = { router.navigate(Screen.TasbihHistory(router.toRoute<Screen.TasbihCalculator>().tasbihId)) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TasbihScreen(
    state: TasbihScreenState,
    handleEvent: (TasbihUIEvent) -> Unit,
    didClickBack: () -> Unit,
    didClickHistory: () -> Unit,
) {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }

    Scaffold(
        topBar = {
            TasbihTopBar(
                title = state.name,
                hapticEnabled = state.hapticEnabled,
                didClickBack = didClickBack,
                didClickHistory = didClickHistory,
                didClickReset = { handleEvent(TasbihUIEvent.DidClickReset) },
                didToggleHaptic = { handleEvent(TasbihUIEvent.DidToggleHaptic) },
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                            MaterialTheme.colorScheme.background,
                        ),
                    ),
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(color = MaterialTheme.colorScheme.primary),
                ) {
                    performStrongHaptic(context, state)
                    handleEvent(TasbihUIEvent.DidTap)
                },
        ) {
            Count(
                count = state.count,
                modifier = Modifier.align(Alignment.Center),
            )
            HintText(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
            )
        }
    }

    if (state.showResetConfirm) {
        ResetConfirmBottomSheet(
            didDismiss = { handleEvent(TasbihUIEvent.DidDismissResetConfirm) },
            didConfirm = { handleEvent(TasbihUIEvent.DidConfirmReset) },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TasbihTopBar(
    title: String,
    hapticEnabled: Boolean,
    didClickBack: () -> Unit = {},
    didClickHistory: () -> Unit = {},
    didClickReset: () -> Unit = {},
    didToggleHaptic: () -> Unit = {},
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = InmuslimTypo.titleLarge.copy(fontWeight = FontWeight.SemiBold),
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
        actions = { AppBarActions(hapticEnabled, didToggleHaptic, didClickHistory, didClickReset) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
    )
}

@Composable
private fun AppBarActions(
    hapticEnabled: Boolean,
    didToggleHaptic: () -> Unit = {},
    didClickHistory: () -> Unit = {},
    didClickReset: () -> Unit = {},
) {
    val hapticIcon = if (hapticEnabled) {
        R.drawable.outline_mobile_vibrate_24
    } else {
        R.drawable.outline_mobile_vibrate_off_24
    }
    val hapticLabel = if (hapticEnabled) {
        R.string.tasbih_action_haptic_disable
    } else {
        R.string.tasbih_action_haptic_enable
    }

    IconButton(onClick = didToggleHaptic) {
        Icon(
            painter = painterResource(hapticIcon),
            contentDescription = stringResource(hapticLabel),
            tint = if (hapticEnabled) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline
            },
        )
    }
    IconButton(onClick = didClickHistory) {
        Icon(
            painter = painterResource(R.drawable.ic_history_24),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
    }
    IconButton(onClick = didClickReset) {
        Icon(
            painter = painterResource(R.drawable.ic_refresh_24),
            contentDescription = stringResource(R.string.tasbih_action_reset),
            tint = MaterialTheme.colorScheme.error,
        )
    }
}

@Composable
private fun Count(count: Int, modifier: Modifier = Modifier) {
    val scale by animateFloatAsState(
        targetValue = if (count > 0) 1f else 0.95f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 400f),
        label = "scale",
    )
    val borderColor by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.primary.copy(alpha = if (count > 0) 1f else 0.3f),
        label = "borderColor",
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .scale(scale)
                .size(220.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))
                .border(width = 3.dp, color = borderColor, shape = CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = NumberFormat.getNumberInstance().format(count),
                style = InmuslimTypo.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 60.sp,
                ),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun HintText(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.tasbih_description_tap_to_count),
        style = InmuslimTypo.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        modifier = modifier,
    )
}

@Suppress("MagicNumber")
@SuppressLint("DEPRECATION", "MissingPermission")
fun performStrongHaptic(context: Context, state: TasbihScreenState) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
    val nextCount = state.count + 1
    val isHundredCount = nextCount % 100 == 0
    val isSpecialCount = nextCount % 100 == 33 || nextCount % 100 == 66 || nextCount % 100 == 99
    val timings = longArrayOf(0, 200, 100, 200, 100, 400)
    val amplitudes = intArrayOf(0, 255, 0, 255, 0, 255)

    if (state.hapticEnabled || isHundredCount) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = when {
                isHundredCount -> VibrationEffect.createWaveform(timings, amplitudes, -1)
                isSpecialCount -> VibrationEffect.createOneShot(80, 255)
                else -> VibrationEffect.createOneShot(10, 80)
            }
            vibrator.vibrate(effect)
        } else {
            when {
                isHundredCount -> vibrator.vibrate(longArrayOf(0, 150, 80, 150), -1)
                isSpecialCount -> vibrator.vibrate(80)
                else -> vibrator.vibrate(10)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TasbihScreenPreview() {
    InmuslimTheme {
        TasbihScreen(
            state = TasbihScreenState(name = "SubhanAllah", count = 33),
            handleEvent = {},
            didClickBack = {},
            didClickHistory = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TasbihScreenZeroPreview() {
    InmuslimTheme {
        TasbihScreen(
            state = TasbihScreenState(name = "Alhamdulillah", count = 0),
            handleEvent = {},
            didClickBack = {},
            didClickHistory = {},
        )
    }
}
