package younesbouhouche.musicplayer.main.domain.use_cases

import younesbouhouche.musicplayer.main.domain.events.UiEvent
import younesbouhouche.musicplayer.main.domain.models.Routes
import younesbouhouche.musicplayer.main.domain.repo.MediaRepository
import younesbouhouche.musicplayer.main.presentation.states.PlaylistViewState
import younesbouhouche.musicplayer.main.presentation.states.UiState
import younesbouhouche.musicplayer.main.presentation.util.Event.SavePlaylist
import younesbouhouche.musicplayer.main.presentation.util.Event.SharePlaylist
import younesbouhouche.musicplayer.main.presentation.util.EventBus.sendEvent
import younesbouhouche.musicplayer.main.presentation.util.ListsSortType
import younesbouhouche.musicplayer.main.presentation.util.SortState
import younesbouhouche.musicplayer.main.presentation.util.SortType

class UiControlUseCase(val mediaRepository: MediaRepository) {
    private suspend fun List<Long>.toMusicCards() = mapNotNull {
        mediaRepository.suspendGetMediaById(it)
    }

    suspend operator fun invoke(
        event: UiEvent,
        sortStateUpdate: ((SortState<SortType>) -> SortState<SortType>) -> Unit,
        albumsSortStateUpdate: ((SortState<ListsSortType>) -> SortState<ListsSortType>) -> Unit,
        artistsSortStateUpdate: ((SortState<ListsSortType>) -> SortState<ListsSortType>) -> Unit,
        playlistsSortStateUpdate: ((SortState<ListsSortType>) -> SortState<ListsSortType>) -> Unit,
        uiStateUpdate: (suspend (UiState) -> UiState) -> Unit,
    ) {
        when (event) {
            UiEvent.ShowSpeedDialog ->
                uiStateUpdate {
                    it.copy(speedDialog = true)
                }
            UiEvent.HideSpeedDialog ->
                uiStateUpdate {
                    it.copy(speedDialog = false)
                }
            UiEvent.HideTimerDialog ->
                uiStateUpdate {
                    it.copy(timerDialog = false)
                }
            UiEvent.ShowTimerDialog ->
                uiStateUpdate {
                    it.copy(timerDialog = true)
                }
            UiEvent.CollapsePlaylist ->
                uiStateUpdate { it.copy(playlistViewState = PlaylistViewState.COLLAPSED) }
            UiEvent.ExpandPlaylist ->
                uiStateUpdate { it.copy(playlistViewState = PlaylistViewState.EXPANDED) }
            UiEvent.HideBottomSheet ->
                uiStateUpdate {
                    it.copy(bottomSheetVisible = false)
                }
            is UiEvent.SetPlaylist ->
                uiStateUpdate {
                    it.copy(playlistId = event.id)
                }
            is UiEvent.ShowBottomSheet ->
                uiStateUpdate {
                    it.copy(bottomSheetItem = event.id, bottomSheetVisible = true)
                }
            UiEvent.HideListBottomSheet ->
                uiStateUpdate {
                    it.copy(listBottomSheetVisible = false)
                }
            is UiEvent.ShowListBottomSheet ->
                uiStateUpdate {
                    it.copy(
                        listBottomSheetList = event.list.toMusicCards(),
                        listBottomSheetTitle = event.title,
                        listBottomSheetImage = event.image,
                        listBottomSheetIcon = event.icon,
                        listBottomSheetVisible = true,
                    )
                }
            UiEvent.HideNewPlaylistDialog ->
                uiStateUpdate {
                    it.copy(newPlaylistDialog = false)
                }
            is UiEvent.ShowCreatePlaylistDialog ->
                uiStateUpdate {
                    it.copy(
                        newPlaylistDialog = true,
                        newPlaylistName = "",
                        newPlaylistItems = event.items,
                        newPlaylistImage = null
                    )
                }

            is UiEvent.UpdateNewPlaylistName ->
                uiStateUpdate {
                    it.copy(newPlaylistName = event.newName)
                }

            is UiEvent.ShowAddToPlaylistDialog ->
                uiStateUpdate {
                    it.copy(
                        addToPlaylistDialog = true,
                        addToPlaylistItems = event.items,
                        addToPlaylistSelected = emptySet()
                    )
                }

            UiEvent.HideAddToPlaylistDialog ->
                uiStateUpdate {
                    it.copy(addToPlaylistDialog = false)
                }

            is UiEvent.UpdateSelectedPlaylist ->
                uiStateUpdate {
                    it.copy(addToPlaylistSelected =
                        if (event.id in it.addToPlaylistSelected)
                            it.addToPlaylistSelected - event.id
                        else
                            it.addToPlaylistSelected + event.id
                    )
                }

            UiEvent.HideMetadataDialog ->
                uiStateUpdate {
                    it.copy(metadataDialog = false)
                }

            is UiEvent.ShowMetadataDialog ->
                uiStateUpdate {
                    it.copy(
                        metadataDialog = true,
                        metadata = event.metadata,
                    )
                }

            UiEvent.ToggleLyrics ->
                uiStateUpdate {
                    it.copy(lyricsVisible = !it.lyricsVisible)
                }

            UiEvent.DisableSyncing ->
                uiStateUpdate {
                    it.copy(syncing = false)
                }

            UiEvent.EnableSyncing ->
                uiStateUpdate {
                    it.copy(syncing = true)
                }

            UiEvent.DismissDetails ->
                uiStateUpdate {
                    it.copy(detailsDialog = false)
                }

            is UiEvent.ShowDetails ->
                uiStateUpdate {
                    it.copy(detailsDialog = true, detailsFile = event.file)
                }

            UiEvent.HideRenamePlaylistDialog ->
                uiStateUpdate {
                    it.copy(renamePlaylistDialogVisible = false)
                }

            is UiEvent.ShowRenamePlaylistDialog ->
                uiStateUpdate {
                    it.copy(
                        renamePlaylistDialogVisible = true,
                        renamePlaylistId = event.id,
                        renamePlaylistName = event.name,
                    )
                }

            is UiEvent.UpdateRenamePlaylistName ->
                uiStateUpdate {
                    it.copy(renamePlaylistName = event.newName)
                }

            UiEvent.HideQueueBottomSheet ->
                uiStateUpdate {
                    it.copy(queueSheetVisible = false)
                }

            UiEvent.ShowQueueBottomSheet ->
                uiStateUpdate {
                    it.copy(queueSheetVisible = true)
                }

            UiEvent.HidePitchDialog ->
                uiStateUpdate {
                    it.copy(pitchDialog = false)
                }

            UiEvent.ShowPitchDialog ->
                uiStateUpdate {
                    it.copy(pitchDialog = true)
                }

            is UiEvent.UpdateNewPlaylistImage ->
                uiStateUpdate {
                    it.copy(newPlaylistImage = event.image)
                }
            is UiEvent.ShowSortSheet -> {
                when(event.route) {
                    Routes.Albums -> albumsSortStateUpdate {
                        it.copy(expanded = true)
                    }
                    Routes.Artists -> artistsSortStateUpdate {
                        it.copy(expanded = true)
                    }
                    Routes.Playlists -> playlistsSortStateUpdate {
                        it.copy(expanded = true)
                    }
                    Routes.Library -> sortStateUpdate {
                        it.copy(expanded = true)
                    }
                    else -> return
                }
            }
            is UiEvent.SavePlaylist -> sendEvent(SavePlaylist(event.playlist))
            is UiEvent.SharePlaylist -> sendEvent(SharePlaylist(event.playlist))
            is UiEvent.UpdateMetadata -> uiStateUpdate { it.copy(metadata = event.metadata) }
        }
    }
}