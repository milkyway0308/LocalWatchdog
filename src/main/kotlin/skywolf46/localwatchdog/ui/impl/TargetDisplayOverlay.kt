package skywolf46.localwatchdog.ui.impl

import com.sun.jna.platform.win32.WinDef
import skywolf46.localwatchdog.ui.DrawableOverlay
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.Toolkit
import java.awt.font.TextLayout
import java.awt.image.BufferedImage

object TargetDisplayOverlay : DrawableOverlay() {
    init {
        val screen = Toolkit.getDefaultToolkit().screenSize
        setSize(400, 225)
        background = Color(82, 82, 82, 125)
        display()
    }

    var displayedImage: BufferedImage? = null

    override fun draw(g: Graphics2D) {

        if (displayedImage != null)
            g.drawImage(displayedImage, 0, 0, width, height, null)
        else  {
            super.paintComponents(g)
            g.paint = Color(255, 255, 255)

            val layout = TextLayout(
                "-- No Signal --",
                Font("Bahnschrift", Font.PLAIN, 25),
                g.fontRenderContext
            )
            layout.draw(g, ((width - layout.bounds.width) / 2).toFloat(), ((height - layout.bounds.height) / 2).toFloat() + 25f)
        }
    }

    public override fun findScreen(): WinDef.HWND {
        return super.findScreen()
    }
}