package skywolf46.localwatchdog.ui.impl.payday

import skywolf46.localwatchdog.ui.StagedDrawableOverlay
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.font.TextLayout

class TemporaryStage : PayDayStage() {
    override fun onInit(overlay: StagedDrawableOverlay) {
        println("Temporary stage initialized")
    }

    override fun tick(g: Graphics2D, overlay: StagedDrawableOverlay) {
        paintBackground(g)
        drawBorder(g)
        drawIcon(g)
        drawDecoratedText(g)
        drawEnemyText(g)
    }

    private fun paintBackground(g: Graphics2D) {
        g.paint = Color(82, 82, 82, 125)
        g.fillRect(0, 0, mainWidth, mainHeight)
    }

    private fun drawBorder(g: Graphics2D) {
        g.paint = Color(240, 229, 1)
        val lineWeight = 3
        val lineWidth = 10
        // Top left
        g.fillRect(0, 0, lineWeight, lineWidth)
        g.fillRect(0, 0, lineWidth, lineWeight)
        // Bottom left
        g.fillRect(0, mainHeight - lineWeight, lineWidth, lineWeight)
        g.fillRect(0, mainHeight - lineWidth, lineWeight, lineWidth)
        // Top right
        g.fillRect(mainWidth - lineWeight, 0, lineWeight, lineWidth)
        g.fillRect(mainWidth - lineWidth, 0, lineWidth, lineWeight)
        // Bottom right
        g.fillRect(mainWidth - lineWeight, mainHeight - lineWidth, lineWeight, lineWidth)
        g.fillRect(mainWidth - lineWidth, mainHeight - lineWeight, lineWidth, lineWeight)
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

    private fun drawDecoratedText(g: Graphics2D) {
        g.paint = Color(240, 229, 1)

        val layout = TextLayout(
            "NEUTRAL ASSAULT IN P",
            Font("Bahnschrift", Font.PLAIN, 25),
            g.fontRenderContext
        )

        layout.draw(g, 20f, 33f)
    }

    private fun drawEnemyText(g: Graphics2D) {
        g.paint = Color(240, 229, 1)


        val layout = TextLayout(
            "16 Neutral Detected [Tama]",
            Font("Bahnschrift", Font.PLAIN, 14),
            g.fontRenderContext
        )

        layout.draw(g, 1.5f, mainHeight + 15f)

    }
}