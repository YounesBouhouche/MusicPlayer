package younesbouhouche.musicplayer.main.presentation.util

import younesbouhouche.musicplayer.main.domain.models.Routes


val String?.isRouteParent
    get() = this?.let { route ->
        Routes.entries.map { it.destination.javaClass.kotlin.qualifiedName }.contains(route)
    } ?: true

