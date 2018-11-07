package pro.ikili.vtuberescape

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class RadarTask(private val player: Player): BukkitRunnable() {

    override fun run() {
        if (!player.isOnline) {
            this.cancel()
            return
        }

        val vtuberTeam = Bukkit.getScoreboardManager().mainScoreboard.getTeam("VTuber")
        if (vtuberTeam.entries.contains(player.name)) {
            val inv = player.inventory
            VTuberRadar.ITEM_RADARS.forEach { inv.removeItem(it) }
            return
        }

        val radars = player.inventory.contents.filter { VTuberRadar.isSimilar(it) }
        val nearby = Bukkit.getOnlinePlayers().filter { vtuberTeam.entries.contains(it.name) }
                .map { player.location.distance(it.location) }
                .min()

        val level = if (nearby is Double) {
            when (nearby) {
                in 0..48 -> 3
                in 48..128 -> 2
                in 128..256 -> 1
                else -> 0
            }
        } else {
            0
        }

        radars.forEach { VTuberRadar.changeLevel(it, level) }
    }
}