package pro.ikili.vtuberescape

import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.scheduler.BukkitRunnable

class TimerBar(private val plugin: VTuberEscape, private val sec: Long, private val name: String) : BukkitRunnable() {
    private val bar = plugin.server.createBossBar("time", BarColor.PURPLE, BarStyle.SOLID)
    private var limit = sec

    init {
        bar.removeAll()
        bar.isVisible = true
        plugin.timers[name] = this
    }

    override fun run() {
        if (limit <= 0) {
            this.cancel()
            return
        }

        Bukkit.getOnlinePlayers().forEach { bar.addPlayer(it) }
        val h = limit / 3600
        val m = String.format("%02d", limit % 3600/ 60)
        val s = String.format("%02d", limit % 60)
        bar.title = "$h:$m:$s"
        bar.progress = limit / sec.toDouble()

        if (limit <= 5) {
            Bukkit.getOnlinePlayers().forEach {
                it.sendTitle("$limit", "", 5, 20, 5)
            }
        }

        limit--
    }

    override fun cancel() {
        super.cancel()
        bar.removeAll()
        plugin.timers.remove(name)
        Bukkit.getPluginManager().callEvent(TimerEndEvent(name, limit))
    }
}