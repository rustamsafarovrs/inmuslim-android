package tj.rsdevteam.inmuslim.feature.tasbih.ui.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import tj.rsdevteam.inmuslim.core.router.theme.InmuslimShapes
import tj.rsdevteam.inmuslim.core.router.theme.InmuslimTheme
import tj.rsdevteam.inmuslim.core.router.theme.InmuslimTypo
import tj.rsdevteam.inmuslim.res.R
import tj.rstech.uicomponents.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTasbihBottomSheet(
    didDismiss: () -> Unit,
    didConfirm: (String) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = didDismiss,
        sheetState = sheetState,
    ) {
        AddTasbihBottomSheetContent(
            name = name,
            onNameChange = { name = it },
            scope = scope,
            sheetState = sheetState,
            didConfirm = didConfirm,
            didDismiss = didDismiss,
        )
    }
}

@Suppress("LongParameterList")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTasbihBottomSheetContent(
    name: String,
    onNameChange: (String) -> Unit,
    scope: CoroutineScope,
    sheetState: SheetState,
    didConfirm: (String) -> Unit,
    didDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp),
    ) {
        Text(
            text = stringResource(R.string.tasbih_action_add_tasbih),
            style = InmuslimTypo.titleLarge.copy(fontWeight = FontWeight.Bold),
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text(stringResource(R.string.tasbih_hint_name)) },
            singleLine = true,
            shape = InmuslimShapes.medium,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Buttons(name, scope, sheetState, didConfirm, didDismiss)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Buttons(
    name: String,
    scope: CoroutineScope,
    sheetState: SheetState,
    didConfirm: (String) -> Unit,
    didDismiss: () -> Unit,
) {
    PrimaryButton(
        stringResource(R.string.common_action_add),
        clicked = {
            if (name.isNotBlank()) {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        didConfirm(name.trim())
                    }
                }
            }
        },
        enabled = name.isNotBlank(),
        hPadding = 0.dp,
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedButton(
        onClick = {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    didDismiss()
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(stringResource(R.string.common_action_cancel))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun AddTasbihBottomSheetPreview() {
    InmuslimTheme {
        AddTasbihBottomSheetContent(
            name = "",
            onNameChange = {},
            scope = rememberCoroutineScope(),
            sheetState = rememberModalBottomSheetState(),
            didConfirm = {},
            didDismiss = {},
        )
    }
}
