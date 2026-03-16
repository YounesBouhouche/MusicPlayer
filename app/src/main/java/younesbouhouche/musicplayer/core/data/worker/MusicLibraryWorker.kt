package younesbouhouche.musicplayer.core.data.worker

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import younesbouhouche.musicplayer.core.domain.repositories.MusicRepository
import java.time.Duration

class MusicLibraryWorker(
    val context: Context,
    workerParams: WorkerParameters,
    val musicRepository: MusicRepository
): CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        return try {
            musicRepository.refreshMusicLibrary()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
    companion object {
        fun launchWorkRequest(context: Context) {
            val worker = OneTimeWorkRequestBuilder<MusicLibraryWorker>()
                .setBackoffCriteria(
                    backoffPolicy = BackoffPolicy.EXPONENTIAL,
                    duration = Duration.ofSeconds(10),
                )
                .build()
            WorkManager.Companion.getInstance(context).enqueue(worker)
        }
    }
}