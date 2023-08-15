package skywolf46.localwatchdog.ui

import java.awt.Graphics2D
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.write

class StagedDrawableOverlay(private var stage: AnimationStage) : DrawableOverlay() {
    private val fixedScheduler = Executors.newScheduledThreadPool(1)
    private val lock = ReentrantReadWriteLock()
    private var canceller: ScheduledFuture<*>? = null

    fun doSchedule(tick: Long) {
        lock.write {
            canceller = fixedScheduler.scheduleAtFixedRate({
                super.repaint()
            }, tick, tick, TimeUnit.MILLISECONDS)
        }
    }

    fun removeSchedule() {
        lock.write {
            canceller?.cancel(false)
            canceller = null
        }
    }

    fun updateStage(stage: AnimationStage) {
        lock.write {
            canceller?.cancel(false)
            canceller = null
            this.stage = stage
        }
        stage.onInit(this)
        repaint()
    }

    override fun draw(g: Graphics2D) {
        stage.tick(g, this)
    }

    override fun display() {
        super.display()
        updateStage(stage)
    }
}