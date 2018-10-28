package pro.ikili.vtuberescape

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerJoinEvent


class EventListener(private val plugin: VTuberEscape) : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val p = event.player
        p.sendMessage("よ!")
        plugin.logger.info("join ${p.name}")
        if (!p.inventory.contents.any { VTuberRadar.isSimilar(it) }) {
            p.inventory.addItem(VTuberRadar(0))
        }

        val board = Bukkit.getScoreboardManager().mainScoreboard
        val vtuberTeam = board.getTeam("VTuber")
        val listenerTeam = board.getTeam("Listener")
        if (!vtuberTeam.entries.contains(p.name) && !listenerTeam.entries.contains(p.name)) {
            board.getTeam("Listener").addEntry(p.name)
        }

        plugin.server.scheduler.scheduleSyncRepeatingTask(plugin, {
            if (vtuberTeam.entries.contains(p.name)) {
                return@scheduleSyncRepeatingTask
            }
            val radars = p.inventory.contents.filter { VTuberRadar.isSimilar(it) }.map { it as VTuberRadar }
            val nearby = Bukkit.getOnlinePlayers().filter { vtuberTeam.entries.contains(it.name) }
                    .map { p.location.distance(it.location) }
                    .max()
            if (nearby is Double) {
                when {
                    nearby <= 20.0 -> {
                        radars.forEach { it.changeLevel(2) }
                    }
                    nearby <= 50.0 -> {
                        radars.map { it.changeLevel(1) }
                    }
                    else -> {
                        radars.map { it.changeLevel(0) }
                    }
                }
            }
        }, 0, 10L)

    }

    @EventHandler
    fun banPlaceVTuberRadar(event: BlockPlaceEvent) {
        event.isCancelled = VTuberRadar.isSimilar(event.itemInHand)
    }

    @EventHandler
    fun banDropVTuberRadar(event: PlayerDropItemEvent) {
        val team = Bukkit.getScoreboardManager().mainScoreboard.getTeam("Listener")
        event.isCancelled = VTuberRadar.isSimilar(event.itemDrop.itemStack) and team.entries.contains(event.player.name)
    }

}