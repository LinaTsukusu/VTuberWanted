package pro.ikili.vtuberescape

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
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
        } else {
            team.addEntry(p.name)
            p.gameMode = GameMode.SPECTATOR
        }
    }

    @EventHandler
    fun banPlaceVTuberRadar(event: BlockPlaceEvent) {
        event.isCancelled = VTuberRadar.isSimilar(event.itemInHand)
    }

    @EventHandler
    fun onUseVTuberRadar(event: PlayerInteractEvent) {
        val act = event.action
        if (VTuberRadar.isSimilar(event.item) && (act == Action.RIGHT_CLICK_AIR || act == Action.RIGHT_CLICK_BLOCK)) {
            val radar = event.item as VTuberRadar
            if (radar.level >= 2) {
                val p = event.player
                val color = arrayOf("白", "緑", "${ChatColor.YELLOW}黄", "${ChatColor.RED}赤")
                val loc = p.location
                p.chat("${color[radar.level]}${ChatColor.RESET} at X:${loc.blockX} / Y:${loc.blockY} / Z:${loc.blockZ}")
                ItemCooldownUtil.setCooldown(p, radar.type, 15 * 20)
            }
        }
    }

    @EventHandler
    fun banDropVTuberRadar(event: PlayerDropItemEvent) {
        event.isCancelled = VTuberRadar.isSimilar(event.itemDrop.itemStack)
    }

    @EventHandler
    fun onEndTimer(event: TimerEndEvent) {
        when (event.name) {
            "main" -> {
                plugin.server.scheduler.scheduleSyncDelayedTask(plugin, {
                    Bukkit.getOnlinePlayers().forEach { it.sendTitle("VTuber Wanted", "Finish", 20, 2 * 20, 20) }
                }, 0)
            }

            "ready" -> {
                plugin.server.scheduler.scheduleSyncDelayedTask(plugin, {
                    Bukkit.setWhitelist(false)
                }, 0)
                plugin.server.worlds.forEach { it.time = 0 }

            }

            "interval" -> {
                plugin.server.scheduler.scheduleSyncDelayedTask(plugin, {
                    Bukkit.getOnlinePlayers().forEach { it.sendTitle("VTuber Wanted", "Start", 20, 2 * 20, 20) }
                }, 0)
            }
        }
    }

}