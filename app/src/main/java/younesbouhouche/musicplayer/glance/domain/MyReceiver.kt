package younesbouhouche.musicplayer.glance.domain

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import younesbouhouche.musicplayer.glance.presentation.MyAppWidget

class MyReceiver(override val glanceAppWidget: GlanceAppWidget = MyAppWidget()) : GlanceAppWidgetReceiver()
