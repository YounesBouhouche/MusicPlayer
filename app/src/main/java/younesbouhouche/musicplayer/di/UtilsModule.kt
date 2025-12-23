package younesbouhouche.musicplayer.di

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import younesbouhouche.musicplayer.features.player.presentation.service.NotificationCustomCmdButton
import younesbouhouche.musicplayer.core.domain.player.PlayerStateManager
import younesbouhouche.musicplayer.features.player.presentation.service.MediaSessionManager
import younesbouhouche.musicplayer.core.data.local.MediaStoreScanner
import younesbouhouche.musicplayer.core.data.remote.ArtistsPictureFetcher
import younesbouhouche.musicplayer.core.domain.player.PlayerFactory
import younesbouhouche.musicplayer.core.domain.player.PlayerManager
import younesbouhouche.musicplayer.features.player.domain.controller.PlayerController

val utilsModule = module {
    singleOf(::MediaStoreScanner)
    single {
        ArtistsPictureFetcher(get())
    }
    single<CoroutineScope> {
        CoroutineScope(SupervisorJob() + Dispatchers.Main)
    }
    singleOf(::PlayerManager)
    singleOf(::PlayerStateManager)
    singleOf(::PlayerFactory)
    singleOf(::PlayerController)
    single<MediaSessionManager> {
        val context = androidContext()
        MediaSessionManager(
            context,
            get(),
            PendingIntent.getActivity(
                context,
                0,
                context.packageManager.getLaunchIntentForPackage(context.packageName),
                FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT,
            ),
            NotificationCustomCmdButton.entries.map { it.commandButton }
        )
    }
}