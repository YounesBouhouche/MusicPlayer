package younesbouhouche.musicplayer.features.main.presentation.navigation


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.OverlayScene
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import younesbouhouche.musicplayer.di.dialogModule

@OptIn(ExperimentalMaterial3Api::class)
internal class BottomSheetScene<T : Any>(
    override val key: T,
    override val previousEntries: List<NavEntry<T>>,
    override val overlaidEntries: List<NavEntry<T>>,
    private val entry: NavEntry<T>,
    private val modalBottomSheetProperties: ModalBottomSheetProperties,
    private val onBack: () -> Unit,
) : OverlayScene<T> {

    override val entries: List<NavEntry<T>> = listOf(entry)

    override val content: @Composable (() -> Unit) = {
        ModalBottomSheet(
            onDismissRequest = onBack,
            properties = modalBottomSheetProperties,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            entry.Content()
        }
    }
}

internal class DialogScene<T : Any>(
    override val key: T,
    override val previousEntries: List<NavEntry<T>>,
    override val overlaidEntries: List<NavEntry<T>>,
    private val entry: NavEntry<T>,
    private val dialogProperties: DialogProperties,
    private val onBack: () -> Unit,
) : OverlayScene<T> {

    override val entries: List<NavEntry<T>> = listOf(entry)

    @OptIn(ExperimentalMaterial3Api::class)
    override val content: @Composable (() -> Unit) = {
        BasicAlertDialog(
            onDismissRequest = onBack,
            properties = dialogProperties
        ) {
            Surface(
                shadowElevation = AlertDialogDefaults.TonalElevation,
                shape = AlertDialogDefaults.shape,
                color = AlertDialogDefaults.containerColor,
                contentColor = AlertDialogDefaults.textContentColor,
                modifier = Modifier.fillMaxWidth(.9f)
            ) {
                entry.Content()
            }
        }
    }
}


internal class FullScreenDialogScene<T : Any>(
    override val key: T,
    override val previousEntries: List<NavEntry<T>>,
    override val overlaidEntries: List<NavEntry<T>>,
    private val entry: NavEntry<T>,
    private val onBack: () -> Unit,
) : OverlayScene<T> {

    override val entries: List<NavEntry<T>> = listOf(entry)

    @OptIn(ExperimentalMaterial3Api::class)
    override val content: @Composable (() -> Unit) = {
        ModalBottomSheet(
            onDismissRequest = onBack,
            dragHandle = null,
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true,
            ),
            sheetGesturesEnabled = false,
            contentWindowInsets = { WindowInsets() }
        ) {
            entry.Content()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
class SceneStrategy<T : Any> : SceneStrategy<T> {
    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        val lastEntry = entries.lastOrNull()
        val bottomSheetProperties = lastEntry?.metadata?.get(BOTTOM_SHEET_KEY) as? ModalBottomSheetProperties
        val dialogProperties = lastEntry?.metadata?.get(DIALOG_KEY) as? DialogProperties
        val isFullScreenDialog = lastEntry?.metadata?.get(FULL_SCREEN_DIALOG_KEY) != null
        return when {
            bottomSheetProperties != null -> {
                @Suppress("UNCHECKED_CAST")
                BottomSheetScene(
                    key = lastEntry.contentKey as T,
                    previousEntries = entries.dropLast(1),
                    overlaidEntries = entries.dropLast(1),
                    entry = lastEntry,
                    modalBottomSheetProperties = bottomSheetProperties,
                    onBack = onBack
                )
            }
            dialogProperties != null -> {
                @Suppress("UNCHECKED_CAST")
                DialogScene(
                    key = lastEntry.contentKey as T,
                    previousEntries = entries.dropLast(1),
                    overlaidEntries = entries.dropLast(1),
                    entry = lastEntry,
                    dialogProperties = dialogProperties,
                    onBack = onBack
                )
            }
            isFullScreenDialog -> {
                @Suppress("UNCHECKED_CAST")
                FullScreenDialogScene(
                    key = lastEntry.contentKey as T,
                    previousEntries = entries.dropLast(1),
                    overlaidEntries = entries.dropLast(1),
                    entry = lastEntry,
                    onBack = onBack
                )
            }
            else -> null
        }
    }

    companion object {
        @OptIn(ExperimentalMaterial3Api::class)
        fun bottomSheet(
            modalBottomSheetProperties: ModalBottomSheetProperties = ModalBottomSheetProperties()
        ): Map<String, Any> = mapOf(BOTTOM_SHEET_KEY to modalBottomSheetProperties)

        @OptIn(ExperimentalMaterial3Api::class)
        fun dialog(
            dialogProperties: DialogProperties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ): Map<String, Any> = mapOf(DIALOG_KEY to dialogProperties)

        @OptIn(ExperimentalMaterial3Api::class)
        fun fullScreenDialog(): Map<String, Any> = mapOf(FULL_SCREEN_DIALOG_KEY to Any())

        internal const val BOTTOM_SHEET_KEY = "bottomsheet"
        internal const val DIALOG_KEY = "dialog"

        internal const val FULL_SCREEN_DIALOG_KEY = "full_screen_dialog"
    }
}