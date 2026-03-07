package tj.rsdevteam.inmuslim.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tj.rsdevteam.inmuslim.core.router.theme.InmuslimTypo
import tj.rsdevteam.inmuslim.data.models.ErrorBottomSheetConfig
import tj.rsdevteam.inmuslim.res.R

/**
 * Created by Rustam Safarov on 8/13/23.
 * github.com/rustamsafarovrs
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorBottomSheet(
    sheetState: SheetState,
    errorBottomSheetConfig: ErrorBottomSheetConfig,
    dismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = dismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.secondaryContainer
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(errorBottomSheetConfig.icon ?: R.drawable.ic_brightness_alert_24),
                    contentDescription = null,
                    modifier = Modifier.size(38.dp),
                    tint = Color.Unspecified
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorBottomSheetConfig.title ?: stringResource(id = R.string.error_title),
                style = InmuslimTypo.titleLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
            if (!errorBottomSheetConfig.msg.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorBottomSheetConfig.msg,
                    style = InmuslimTypo.bodyMedium.copy(fontSize = 14.sp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            PrimaryButton(text = "OK", modifier = Modifier.fillMaxWidth(), onClick = dismiss)
        }
    }
}
