package younesbouhouche.musicplayer.features.main.presentation.util

import younesbouhouche.musicplayer.features.main.domain.models.Routes


val String?.isRouteParent
    get() = this?.let { route ->
        Routes.entries.map { it.destination.javaClass.kotlin.qualifiedName }.contains(route)
    } ?: true

