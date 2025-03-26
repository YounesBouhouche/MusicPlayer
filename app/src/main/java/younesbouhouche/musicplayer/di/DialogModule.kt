package younesbouhouche.musicplayer.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import younesbouhouche.musicplayer.dialog.data.DialogRepoImpl
import younesbouhouche.musicplayer.dialog.domain.DialogRepo
import younesbouhouche.musicplayer.dialog.presentation.DialogVM

val dialogModule = module {
    viewModelOf(::DialogVM)
    single<DialogRepo> { DialogRepoImpl(get()) }
}