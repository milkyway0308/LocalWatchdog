package skywolf46.localwatchdog.ui.impl.payday

import skywolf46.localwatchdog.ui.AnimationStage

abstract class PayDayStage : AnimationStage {
    val mainWidth = 300
    val iconWidth = 20
    val margin = 15
    val maxWidth = mainWidth + iconWidth + margin
    val mainHeight = 50
    val maxHeight = 75
    val topMargin = 150
    val lineWeight = 3
    val lineWidth = 10
}