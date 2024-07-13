package younesbouhouche.musicplayer.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import younesbouhouche.musicplayer.main.data.PlayerDataStore
import younesbouhouche.musicplayer.main.data.db.AppDatabase
import younesbouhouche.musicplayer.settings.data.SettingsDataStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideDb(
        @ApplicationContext context: Context,
    ): AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "contacts.db").build()

    @Singleton
    @Provides
    fun provideSettingsDS(
        @ApplicationContext context: Context,
    ): SettingsDataStore = SettingsDataStore(context)

    @Singleton
    @Provides
    fun providePlayerDS(
        @ApplicationContext context: Context,
    ): PlayerDataStore = PlayerDataStore(context)
}
