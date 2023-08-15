package skywolf46.localwatchdog.ui.impl.payday


import skywolf46.localwatchdog.ui.StagedDrawableOverlay
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.font.FontRenderContext
import java.awt.font.TextLayout
import java.awt.image.BufferedImage
import javax.imageio.ImageIO


class SlidingLettersStage() : PayDayStage() {
    private var currentWidthBoundary = lineWeight * 2
    private val preGeneratedTextImage =
        preGenerateTextImage("NEUTRAL ASSAULT IN PROGRESS    ///    ", Color(240, 229, 1))
    private var currentStartIndex = preGeneratedTextImage.width

    override fun onInit(overlay: StagedDrawableOverlay) {
        overlay.doSchedule(5L)

    }



    override fun onClean(overlay: StagedDrawableOverlay) {
//       runBlocking {
//           soundChannel.fadeOut(TimeSpan(1000.0), Easing.EASE_OUT)
//       }
    }

    override fun tick(g: Graphics2D, overlay: StagedDrawableOverlay) {
        if (currentWidthBoundary < mainWidth) {
            currentWidthBoundary = mainWidth.coerceAtMost(currentWidthBoundary + 15)
            drawIcon(g)
            paintBackground(g)
            drawBorder(g)
            if (currentWidthBoundary > 60f) {
                drawTextInBoundary(g, 20 + (mainWidth - currentWidthBoundary), 6, mainWidth - 40, mainHeight - 10)
            }

        } else {
            drawIcon(g)
            paintBackground(g)
            drawBorder(g)
            drawTextInBoundary(g, 20, 6, mainWidth - 40, mainHeight - 10)
            currentStartIndex += 3
            if (currentStartIndex > preGeneratedTextImage.width) {
                currentStartIndex %= preGeneratedTextImage.width
            }
        }

    }

    private fun drawTextInBoundary(g: Graphics2D, startX: Int, startY: Int, endX: Int, endY: Int) {
        val currentWidth = (endX - startX)
        val overflowWidth = (preGeneratedTextImage.width - currentStartIndex) + 2
        g.setClip(startX, startY, (endX - startX), (endY - startY))
        g.drawImage(
            preGeneratedTextImage,
            -currentStartIndex,
            3,
            null
        )
        g.drawImage(
            preGeneratedTextImage,
            overflowWidth,
            3,
            null
        )
    }

    private fun paintBackground(g: Graphics2D) {
        g.paint = Color(82, 82, 82, 125)
        g.fillRect(mainWidth - currentWidthBoundary, 0, mainWidth, mainHeight)
    }

    private fun drawBorder(g: Graphics2D) {
        g.paint = Color(240, 229, 1)
        // Top left
        g.fillRect(mainWidth - currentWidthBoundary, 0, lineWeight, lineWidth)
        g.fillRect(mainWidth - currentWidthBoundary, 0, lineWidth, lineWeight)
        // Bottom left
        g.fillRect(mainWidth - currentWidthBoundary, mainHeight - lineWeight, lineWidth, lineWeight)
        g.fillRect(mainWidth - currentWidthBoundary, mainHeight - lineWidth, lineWeight, lineWidth)
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


    private fun preGenerateTextImage(text: String, color: Color): BufferedImage {
        val font = Font("Bahnschrift", Font.PLAIN, 25)
        val frc = FontRenderContext(font.transform, true, true)
        val image = BufferedImage(
            font.getStringBounds(text, frc).width.toInt(),
            font.getStringBounds(text, frc).height.toInt(),
            BufferedImage.TYPE_INT_ARGB
        )
        println("Text - $text")
        image.createGraphics().apply {
            paint = color
            val layout = TextLayout(
                text,
                Font("Bahnschrift", Font.PLAIN, 24),
                fontRenderContext
            )
            layout.draw(this, 15f, font.getStringBounds(text, frc).height.toFloat())
        }
        ImageIO.write(image, "png", java.io.File("test3.png"))
        return image
    }
}