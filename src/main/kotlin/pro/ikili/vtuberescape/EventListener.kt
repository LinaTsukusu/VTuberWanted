package pro.ikili.vtuberescape

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerRespawnEvent


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

        RadarTask(p).runTaskTimer(plugin, 0, 10)
    }

    @EventHandler
    fun giveRespawnPlayer(event: PlayerRespawnEvent) {
        val p = event.player
        val team = Bukkit.getScoreboardManager().mainScoreboard.getTeam("Listener")
        if (team.entries.contains(p.name)) {
            p.inventory.addItem(VTuberRadar(0))
        }
    }

    @EventHandler
    fun banPlaceVTuberRadar(event: BlockPlaceEvent) {
        event.isCancelled = VTuberRadar.isSimilar(event.itemInHand)
    }

    @EventHandler
    fun banDropVTuberRadar(event: PlayerDropItemEvent) {
        event.isCancelled = VTuberRadar.isSimilar(event.itemDrop.itemStack)
    }

}