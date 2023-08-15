package skywolf46.localwatchdog.util

import com.sun.jna.Memory
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.Structure
import com.sun.jna.platform.win32.*
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinDef.RECT
import com.sun.jna.platform.win32.WinGDI.BITMAPINFO
import com.sun.jna.platform.win32.WinNT.HRESULT
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.PointerByReference
import skywolf46.localwatchdog.ui.impl.TargetDisplayOverlay
import java.awt.Dimension
import java.awt.image.BufferedImage


object ScreenCaptureUtil {
    var time = 0

    private fun checkError(where: String) {
        println("${where}: ${Kernel32.INSTANCE.GetLastError()}")
    }

    fun capture(windowName: String): BufferedImage {
        return capture(findWindowByName(windowName)!!)
    }

    fun checkSize(window: HWND): Pair<Int, Int> {
        val info = WinUser.WINDOWINFO()
        User32.INSTANCE.GetWindowInfo(window, info)
        val targetRect = info.rcClient
        return targetRect.right - targetRect.left to targetRect.bottom - targetRect.top
    }

    fun test(windowName: String) {
        checkError("#1")
        val window = findWindowByName(windowName) ?: throw IllegalStateException("Window not found")

        val boundary = RECT().apply {
            User32.INSTANCE.GetClientRect(window, this)
        }
        checkError("#2")

        val width = (boundary.right - boundary.left)
        val height = (boundary.bottom - boundary.top)
        println("$width x $height")
    }

    fun findWindowByName(windowName: String): WinDef.HWND? {
        var targetWindow: WinDef.HWND? = null
        User32.INSTANCE.EnumWindows({ window, arg ->
            val windowText = CharArray(512)
            User32.INSTANCE.GetWindowText(window, windowText, 512)
            val title = Native.toString(windowText).trim()
            if (title.isNotEmpty() && title == windowName)
                targetWindow = window
            targetWindow == null
        }, null)
        return targetWindow
    }

    fun getAllWindowNames(): List<String> {
        val list = mutableListOf<String>()
        User32.INSTANCE.EnumWindows({ window, arg ->
            val title = getWindowName(window)
            if (title.isNotEmpty())
                list.add(title)
            true
        }, null)
        return list
    }

    fun getWindowName(window: WinDef.HWND): String {
        val windowText = CharArray(512)
        User32.INSTANCE.GetWindowText(window, windowText, 512)
        return Native.toString(windowText).trim()
    }

    fun capture(hWnd: HWND): BufferedImage {
        val hdcWindow = User32.INSTANCE.GetDC(hWnd)
        val hdcMemDC = GDI32.INSTANCE.CreateCompatibleDC(hdcWindow)
        val bounds = RECT()
        User32.INSTANCE.GetClientRect(hWnd, bounds)
        val width = bounds.right - bounds.left
        val height = bounds.bottom - bounds.top
        val hBitmap = GDI32.INSTANCE.CreateCompatibleBitmap(hdcWindow, width, height)
        val hOld = GDI32.INSTANCE.SelectObject(hdcMemDC, hBitmap)
        GDI32.INSTANCE.BitBlt(hdcMemDC, 0, 0, width, height, hdcWindow, 0, 0, GDI32.SRCCOPY)
        GDI32.INSTANCE.SelectObject(hdcMemDC, hOld)
        GDI32.INSTANCE.DeleteDC(hdcMemDC)
        val bmi = BITMAPINFO()
        bmi.bmiHeader.biWidth = width
        bmi.bmiHeader.biHeight = -height
        bmi.bmiHeader.biPlanes = 1
        bmi.bmiHeader.biBitCount = 32
        bmi.bmiHeader.biCompression = WinGDI.BI_RGB
        val buffer = Memory((width * height * 4).toLong())
        GDI32.INSTANCE.GetDIBits(hdcWindow, hBitmap, 0, height, buffer, bmi, WinGDI.DIB_RGB_COLORS)
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        image.setRGB(0, 0, width, height, buffer.getIntArray(0, width * height), 0, width)
        GDI32.INSTANCE.DeleteObject(hBitmap)
        User32.INSTANCE.ReleaseDC(hWnd, hdcWindow)
        return image
    }

    fun test3(dimension: Dimension, sourceWindow: HWND, targetWindow: HWND = TargetDisplayOverlay.findScreen()) {
        val thumbnailId = PointerByReference()
        val result = DwmApi.INSTANCE.DwmRegisterThumbnail(targetWindow, sourceWindow, thumbnailId)
        if (result.toInt() == 0) {
            // Thumbnail 등록 성공
            // 이제 원하는 작업을 수행하십시오.
            val bitmap = GDI32.INSTANCE.CreateCompatibleBitmap(User32.INSTANCE.GetDC(targetWindow), dimension.width, dimension.height)

            val bufferedImage = BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB)
            bufferedImage.setRGB(0, 0, dimension.width, dimension.height, IntArray(dimension.width * dimension.height), 0, dimension.width)
            // Thumbnail 해제
            println("Success! Unregistering.")
            DwmApi.INSTANCE.DwmUnregisterThumbnail(thumbnailId.value).apply {
                if (toInt() != 0)
                    println("Failed to unregister thumbnail: $this")
                else
                    println("Unregistered thumbnail.")
            }

        } else {
            // Thumbnail 등록 실패
            System.err.println("DwmRegisterThumbnail failed with error: $result")
        }
    }


    fun isLiveScreen(sourceWindowName: String) : Boolean {
        val targetWindow = findWindowByName(sourceWindowName) ?: throw IllegalStateException("Window not found")
        return User32.INSTANCE.GetForegroundWindow() != null && User32.INSTANCE.GetForegroundWindow().pointer == targetWindow.pointer
    }

    interface DwmApi : User32 {
        fun DwmRegisterThumbnail(dest: HWND?, src: HWND?, thumbnailId: PointerByReference?): HRESULT
        fun DwmUnregisterThumbnail(thumbnailId: Pointer): HRESULT
        fun DwmQueryThumbnailSourceSize(hThumbnail: Pointer, width: IntByReference, height: IntByReference): HRESULT

        companion object {
            val INSTANCE = Native.load("dwmapi", DwmApi::class.java)
        }
    }




}