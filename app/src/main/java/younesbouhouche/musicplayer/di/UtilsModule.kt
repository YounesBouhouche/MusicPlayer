package younesbouhouche.musicplayer.di

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.media.MediaMetadataRetriever
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import younesbouhouche.musicplayer.core.domain.NotificationCustomCmdButton
import younesbouhouche.musicplayer.core.domain.player.PlayerFactory
import younesbouhouche.musicplayer.core.domain.player.PlayerManager
import younesbouhouche.musicplayer.core.domain.player.PlayerStateManager
import younesbouhouche.musicplayer.core.domain.player.QueueManager
import younesbouhouche.musicplayer.core.domain.session.MediaSessionManager
import younesbouhouche.musicplayer.features.main.data.dao.AppDao

val utilsModule = module {
    single { MediaMetadataRetriever() }
    single<PlayerStateManager> {
        PlayerStateManager()
    }
    single<QueueManager> {
        QueueManager(get())
    }
    single<PlayerFactory> {
        PlayerFactory(androidContext(), get(), get())
    }
    single<PlayerManager> {
        PlayerManager(
            androidContext(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    single<MediaSessionManager> {
        val context = androidContext()
        MediaSessionManager(
            context,
            get<AppDao>(),
            get<PlayerStateManager>(),
            get(),
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