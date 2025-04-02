package younesbouhouche.musicplayer.glance.presentation

import androidx.glance.appwidget.GlanceAppWidgetReceiver

class MyAppWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: MyAppWidget = MyAppWidget()
}