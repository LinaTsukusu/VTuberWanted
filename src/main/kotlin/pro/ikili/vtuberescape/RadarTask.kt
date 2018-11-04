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
        // TODO 範囲調整
        if (nearby is Double) {
            when (nearby) {
                in 0..30 -> {
                    radars.forEach { VTuberRadar.changeLevel(it, 2) }
                }
                in 30..80 -> {
                    radars.forEach { VTuberRadar.changeLevel(it, 1) }
                }
                else -> {
                    radars.forEach { VTuberRadar.changeLevel(it, 0) }
                }
            }
        }
    }
}