package pro.ikili.vtuberescape

import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.scheduler.BukkitRunnable

class TimerBar(plugin: VTuberEscape, private val sec: Long, private val name: String) : BukkitRunnable() {
    private val bar = plugin.server.createBossBar("time", BarColor.PURPLE, BarStyle.SOLID)
    private var limit = sec

    init {
        bar.removeAll()
        bar.isVisible = true
    }

    override fun run() {
        if (limit <= 0) {
            bar.removeAll()
            this.cancel()
            Bukkit.getPluginManager().callEvent(TimerEndEvent(name))
            return
        }

        Bukkit.getOnlinePlayers().forEach { bar.addPlayer(it) }
        val h = limit / 3600
        val m = String.format("%02d", limit % 3600/ 60)
        val s = String.format("%02d", limit % 60)
        bar.title = "$h:$m:$s"
        bar.progress = limit / sec.toDouble()
        limit--
    }
}