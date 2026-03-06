package tj.rsdevteam.inmuslim.ui.launch

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import tj.rsdevteam.inmuslim.core.Const
import tj.rsdevteam.inmuslim.ui.MainActivity

/**
 * Created by Rustam Safarov on 8/20/23.
 * github.com/rustamsafarovrs
 */

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class LaunchActivity : ComponentActivity() {

    private val viewModel by viewModels<LaunchViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collect { event ->
                    when (event) {
                        is LaunchVMEvent.OpenMain -> {
                            openMain(event.openOnboarding)
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun openMain(openOnboarding: Boolean) {
        Intent(this@LaunchActivity, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(Const.OPEN_ONBOARDING, openOnboarding)
            startActivity(this)
        }
    }
}
