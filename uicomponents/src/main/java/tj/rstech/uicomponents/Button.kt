package tj.rstech.uicomponents

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import tj.rsdevteam.inmuslim.core.router.theme.InmuslimTypo

/**
 * Created by Rustam Safarov on 8/13/23.
 * github.com/rustamsafarovrs
 */

@Composable
fun PrimaryButton(
    title: String,
    modifier: Modifier = Modifier,
    hPadding: Dp = 20.dp,
    enabled: Boolean = true,
    clicked: () -> Unit = {},
) {
    Button(
        onClick = clicked,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = hPadding),
        enabled = enabled,
    ) {
        Text(text = title, style = InmuslimTypo.titleMedium)
    }
}
