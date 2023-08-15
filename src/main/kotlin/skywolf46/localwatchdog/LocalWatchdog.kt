package skywolf46.localwatchdog

import skywolf46.localwatchdog.ui.StagedDrawableOverlay
import skywolf46.localwatchdog.ui.impl.PaydayOverlay
import skywolf46.localwatchdog.ui.impl.TargetDisplayOverlay
import skywolf46.localwatchdog.ui.impl.payday.FlickingStage
import skywolf46.localwatchdog.util.ScreenCaptureUtil
import java.util.concurrent.Executors

class LocalWatchdog internal constructor() {
    companion object {
        val SHARED_EXECUTOR = Executors.newCachedThreadPool()
    }
    init {
        init()
    }

    @Synchronized
    private fun init() {
        println(
            "-- LocalWatchdog Test --"
        )
        val eveClients = ScreenCaptureUtil.getAllWindowNames().filter { it.startsWith("EVE - ") }.distinct()
        println("Gathering EVE clients... (${eveClients.size})")
        TargetDisplayOverlay.displayedImage
        Thread.sleep(1500L)
//        PaydayOverlay().display()
        StagedDrawableOverlay(FlickingStage()).display()
        repeat(1000000) {
            if (ScreenCaptureUtil.isLiveScreen(eveClients.first())) {
                TargetDisplayOverlay.displayedImage = ScreenCaptureUtil.capture(eveClients.first())
                TargetDisplayOverlay.repaint()
            } else {
                TargetDisplayOverlay.displayedImage = null
                TargetDisplayOverlay.repaint()
            }
            Thread.sleep(10L)
        }

        Thread.sleep(10000000)
    }
}

fun main() {
    LocalWatchdog()
}