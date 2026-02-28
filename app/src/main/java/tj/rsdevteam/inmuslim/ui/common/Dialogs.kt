package tj.rsdevteam.inmuslim.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import tj.rsdevteam.inmuslim.core.router.theme.InmuslimShapes
import tj.rsdevteam.inmuslim.data.models.DialogState
import tj.rsdevteam.inmuslim.res.R

/**
 * Created by Rustam Safarov on 8/13/23.
 * github.com/rustamsafarovrs
 */

@Composable
fun DialogContent() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(
                color = Color.White,
                shape = InmuslimShapes.large
            )
    ) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
fun LoadingDialog(
    isShowingDialog: Boolean,
    dismissOnBackPress: Boolean = false,
    dismissOnClickOutside: Boolean = false
) {
    if (isShowingDialog) {
        Dialog(
            onDismissRequest = { },
            DialogProperties(
                dismissOnBackPress = dismissOnBackPress,
                dismissOnClickOutside = dismissOnClickOutside
            )
        ) {
            DialogContent()
        }
    }
}

@Composable
fun ErrorDialog(
    dialogState: DialogState?,
    dismissOnBackPress: Boolean = false,
    dismissOnClickOutside: Boolean = false
) {
    if (dialogState?.isDismissed?.value == false) {
        AlertDialog(
            onDismissRequest = { dialogState.dismissDialog() },
            confirmButton = {
                TextButton(onClick = { dialogState.dismissDialog() }) {
                    Text(text = "OK")
                }
            },
            dismissButton = { TextButton(onClick = {}, Modifier.width(0.dp)) { } },
            title = { Text(text = stringResource(id = R.string.app_name)) },
            text = { Text(text = dialogState.msg ?: "") },
            properties = DialogProperties(
                dismissOnBackPress = dismissOnBackPress,
                dismissOnClickOutside = dismissOnClickOutside
            )
        )
    }
}
