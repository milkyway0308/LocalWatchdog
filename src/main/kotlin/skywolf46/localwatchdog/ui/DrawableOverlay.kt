package skywolf46.localwatchdog.ui

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinUser
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.util.*
import javax.swing.JFrame

abstract class DrawableOverlay : JFrame() {
    private val uuid = UUID.randomUUID()
    private val screenName = "LocalWatchdog - $uuid"

    init {
        title = screenName
        isUndecorated = true
        background = Color(0, 0, 0, 0)
        rootPane.putClientProperty("apple.awt.draggableWindowBackground", false)
        type = Type.UTILITY
    }

    override fun paint(g: Graphics) {
        super.paint(g)
        draw(g as Graphics2D)
    }

    abstract fun draw(g: Graphics2D)

    open fun display() {
        isAlwaysOnTop = true
        isVisible = true
        val hwnd: WinDef.HWND = User32.INSTANCE.FindWindow("SunAwtFrame", screenName)
        var wl: Int = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE)
        wl = wl or 0x80000 or 0x20
        User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, wl)
    }

    protected open fun findScreen(): HWND {
        return User32.INSTANCE.FindWindow("SunAwtFrame", screenName)
    }


}