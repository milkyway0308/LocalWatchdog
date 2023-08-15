package skywolf46.localwatchdog

import skywolf46.localwatchdog.ui.impl.PaydayOverlay
import skywolf46.localwatchdog.ui.impl.TargetDisplayOverlay
import skywolf46.localwatchdog.util.ScreenCaptureUtil

class LocalWatchdog internal constructor() {
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
        repeat(1000000) {
            if (ScreenCaptureUtil.isLiveScreen(eveClients.first())) {
                TargetDisplayOverlay.displayedImage = ScreenCaptureUtil.capture(eveClients.first())
                TargetDisplayOverlay.repaint()
            } else {
                TargetDisplayOverlay.displayedImage = null
                TargetDisplayOverlay.repaint()
            }
            Thread.sleep(100L)
        }
        PaydayOverlay().display()
        Thread.sleep(10000000)
    }
}

fun main() {
    LocalWatchdog()
}