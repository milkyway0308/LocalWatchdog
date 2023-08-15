package skywolf46.localwatchdog

import skywolf46.localwatchdog.ui.impl.PaydayOverlay
import skywolf46.localwatchdog.util.ScreenCaptureUtil
import java.io.File
import javax.imageio.ImageIO

class LocalWatchdog internal constructor() {
    init {
        init()
    }

    @Synchronized
    private fun init() {
        println(
            "-- LocalWatchdog ${
                LocalWatchdog::class.java.getResourceAsStream("/version.txt")?.bufferedReader()?.use { it.readLine() }
            } --"
        )
        println(ScreenCaptureUtil.getAllWindowNames().joinToString("\n"))
        val eveClients = ScreenCaptureUtil.getAllWindowNames().filter { it.startsWith("MultiMC") }.distinct()
        println("Gathering EVE clients... (${eveClients.size})")
        for (eveClientName in eveClients) {
            ScreenCaptureUtil.capture(eveClientName)
                .apply {
                    ImageIO.write(this, "png", File("Screenshot - ${eveClientName}.png"))
                }
        }
        PaydayOverlay().display()
        Thread.sleep(10000000)
    }
}

fun main() {
    LocalWatchdog()
}