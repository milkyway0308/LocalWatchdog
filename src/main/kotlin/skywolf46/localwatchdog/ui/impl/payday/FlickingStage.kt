package skywolf46.localwatchdog.ui.impl.payday

import skywolf46.localwatchdog.ui.StagedDrawableOverlay
import java.awt.*
import java.util.concurrent.atomic.AtomicInteger
import javax.sound.sampled.*
import javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED
import kotlin.math.log10

class FlickingStage : PayDayStage() {

    private val count = AtomicInteger(0)

    override fun onInit(overlay: StagedDrawableOverlay) {
        overlay.setSize(maxWidth, maxHeight)
        overlay.location = Point(Toolkit.getDefaultToolkit().screenSize.width - maxWidth, topMargin)
        overlay.doSchedule(250L)

        AudioSystem.getAudioInputStream(javaClass.getResourceAsStream("/audio/payday_assault.wav")).use { audio ->
            val format = getOutFormat(audio.format)
            val info = DataLine.Info(SourceDataLine::class.java, format)

            (AudioSystem.getLine(info) as? SourceDataLine)?.let {
                it.open()
                it.start()
                it.getControl(FloatControl.Type.MASTER_GAIN).let { ctrl ->
                    ctrl as FloatControl
                    val volume = 0.1f // 0.0f (최소)부터 1.0f (최대)까지의 값을 가질 수 있습니다.

                    ctrl.value = 20f * log10(volume.toDouble()).toFloat()
                }
                stream(audio, it)
                it.drain()
                it.stop()
            } ?: run {
                System.err.println("Line unavailable: $info")
                System.err.println("No sound will be played")
            }
        }
    }

    private fun stream(stream: AudioInputStream, line: SourceDataLine) {
        val buffer = ByteArray(4096)
        var n = 0
        while (n != -1) {
            line.write(buffer, 0, n)
            n = stream.read(buffer, 0, buffer.size)
        }
    }

    private fun getOutFormat(inFormat: AudioFormat): AudioFormat {
        val ch = inFormat.channels
        val rate = inFormat.sampleRate
        return AudioFormat(PCM_SIGNED, rate, 16, ch, ch * 2, rate, false)
    }

    override fun tick(g: Graphics2D, overlay: StagedDrawableOverlay) {
        if (count.incrementAndGet() % 2 == 0) {
            drawIcon(g)
            println(count.get())
            if (count.get() == 8) {
                overlay.updateStage(SlidingLettersStage())
            }
        }
    }


    private fun drawIcon(g: Graphics2D) {
        g.paint = Color(240, 229, 1)
        val iconStart = mainWidth + 4
        g.fillRect(iconStart, 0, iconWidth, iconWidth)
        g.paint = Color(0, 0, 0)
        g.stroke = BasicStroke(2f)
        g.drawPolygon(
            intArrayOf(
                iconStart + iconWidth / 2,
                iconStart + 3,
                iconStart + iconWidth - 3,
            ),
            intArrayOf(
                4,
                iconWidth - 6,
                iconWidth - 6
            ),
            3
        )
    }
}