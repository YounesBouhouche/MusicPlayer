package younesbouhouche.musicplayer

import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import younesbouhouche.musicplayer.core.presentation.theme.AppTheme
import younesbouhouche.musicplayer.features.main.presentation.util.composables.SetSystemBarColors
import younesbouhouche.musicplayer.features.player.presentation.service.MediaSessionManager
import younesbouhouche.musicplayer.navigation.MainApp

class MainActivity : ComponentActivity() {
    val sessionManager by inject<MediaSessionManager>()
    private var keepSplashScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        // Keep splash screen visible while animation plays
        splashScreen.setKeepOnScreenCondition { keepSplashScreen }

        // Start animation when icon view is available
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val iconView = splashScreenView.iconView as? ImageView
            val drawable = iconView?.drawable

            if (drawable is AnimatedVectorDrawable) {
                // Start the animation
                drawable.start()

                // Remove splash screen after animation completes
                drawable.registerAnimationCallback(object : Animatable2.AnimationCallback() {
                    override fun onAnimationEnd(drawable: android.graphics.drawable.Drawable?) {
                        splashScreenView.remove()
                    }
                })
            } else {
                // If not an AnimatedVectorDrawable, remove immediately
                splashScreenView.remove()
            }
        }

        super.onCreate(savedInstanceState)

        // Mark splash screen as ready to dismiss after initial setup
        keepSplashScreen = false

        window.decorView.post {
            lifecycleScope.launch {
                sessionManager.initialize()
            }
        }
        setContent {
            AppTheme {
                SetSystemBarColors()
                MainApp()
            }
        }
    }
}