package younesbouhouche.musicplayer.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import younesbouhouche.musicplayer.features.dialog.data.DialogRepoImpl
import younesbouhouche.musicplayer.features.dialog.domain.DialogRepo
import younesbouhouche.musicplayer.features.dialog.presentation.DialogVM

val dialogModule = module {
    viewModelOf(::DialogVM)
    single<DialogRepo> { DialogRepoImpl(get()) }
}