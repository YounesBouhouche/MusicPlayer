package younesbouhouche.musicplayer.features.glance.presentation

import androidx.glance.appwidget.GlanceAppWidgetReceiver

class MyAppWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: MyAppWidget = MyAppWidget()
}