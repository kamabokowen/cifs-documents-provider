package com.wa2c.android.cifsdocumentsprovider.presentation.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.DisposableEffect
import androidx.core.util.Consumer
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.wa2c.android.cifsdocumentsprovider.common.utils.mimeType
import com.wa2c.android.cifsdocumentsprovider.presentation.ext.mode
import com.wa2c.android.cifsdocumentsprovider.presentation.ui.common.Theme
import com.wa2c.android.cifsdocumentsprovider.presentation.ui.common.isDark
import com.wa2c.android.cifsdocumentsprovider.presentation.ui.send.SendViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    // NOTE: Use AppCompatActivity (not ComponentActivity) for Language

    /** View Model */
    private val sendViewModel by viewModels<SendViewModel>()
    /** Main View Model */
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(mainViewModel.uiThemeFlow.value.mode) // Set theme

        setContent {
            val isDark = mainViewModel.uiThemeFlow.isDark() // FIXME
            val systemUiController = rememberSystemUiController()
            systemUiController.setStatusBarColor(Theme.Colors.StatusBackground)

            val navController = rememberNavController()

            DisposableEffect(navController) {
                val consumer = Consumer<Intent> {
                    navController.handleDeepLink(it)
                }
                this@MainActivity.addOnNewIntentListener(consumer)
                onDispose {
                    this@MainActivity.removeOnNewIntentListener(consumer)
                }
            }

            Theme.AppTheme(
                darkTheme = isDark
            ) {
                MainNavHost(
                    navController = navController,
                    onOpenFile = { startApp(it) },
                    onCloseApp = { finishApp() }
                )
            }
        }
    }

    private fun startApp(uris: List<Uri>) {
        if (uris.isEmpty()) {
            return
        } else if (uris.size == 1) {
            // Single
            val uri = uris.first()
            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, uri)
                type = uri.toString().mimeType
            }
            startActivity(Intent.createChooser(shareIntent, null))
        } else {
            // Multiple
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND_MULTIPLE
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
                type = "*/*"
            }
            startActivity(Intent.createChooser(shareIntent, null))
        }
    }

    private fun finishApp() {
        finishAffinity()
    }

}
