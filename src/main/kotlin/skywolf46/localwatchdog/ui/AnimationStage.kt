package skywolf46.localwatchdog.ui

import java.awt.Graphics2D

interface AnimationStage {
    fun onInit(overlay: StagedDrawableOverlay) {
        // Empty for implementation
    }

    fun onClean(overlay: StagedDrawableOverlay) {
        // Empty for implementation
    }

    fun tick(g: Graphics2D, overlay: StagedDrawableOverlay) {
        // Empty for implementation
    }
}