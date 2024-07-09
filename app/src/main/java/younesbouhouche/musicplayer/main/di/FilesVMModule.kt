package younesbouhouche.musicplayer.main.di

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Named

@Module
@InstallIn(ViewModelComponent::class)
object FilesVMModule {
    @ViewModelScoped
    @Provides
    @Named("collection")
    fun provideCollection(): Uri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)

    @ViewModelScoped
    @Provides
    @Named("projection")
    fun provideProjection(): Array<String> =
        arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
        )

    @ViewModelScoped
    @Provides
    @Named("selection")
    fun provideSelection(): String = MediaStore.Audio.Media.IS_MUSIC + "!= 0"

    @ViewModelScoped
    @Provides
    @Named("sortOrder")
    fun provideSortOrder(): String = MediaStore.Audio.Media.IS_MUSIC + "!= 0"

    @ViewModelScoped
    @Provides
    fun providePlayer(app: Application): ExoPlayer =
        ExoPlayer
            .Builder(app)
            .setHandleAudioBecomingNoisy(true)
            .setAudioAttributes(
                AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).build(),
                true,
            )
            .build()
}
