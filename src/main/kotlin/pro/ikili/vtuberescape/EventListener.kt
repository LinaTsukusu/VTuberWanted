package pro.ikili.vtuberescape

import org.bukkit.*
import org.bukkit.entity.EntityType
import org.bukkit.entity.Firework
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.ItemStack


class EventListener(private val plugin: VTuberEscape) : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val p = event.player
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
        if (team.hasEntry(p.name)) {
            p.inventory.addItem(VTuberRadar(0))
        } else {
            team.addEntry(p.name)
            p.gameMode = GameMode.SPECTATOR
        }
    }

    @EventHandler
    fun deathMessage(event: PlayerDeathEvent) {
        val timer = plugin.timers["main"]
        if (timer !== null) {
            val vtuberTeam = Bukkit.getScoreboardManager().mainScoreboard.getTeam("VTuber")
            val listenerTeam = Bukkit.getScoreboardManager().mainScoreboard.getTeam("Listener")
            val player = event.entity
            val killer = player.killer
            if (vtuberTeam.hasEntry(player.name) && vtuberTeam.hasEntry(killer.name)
                || listenerTeam.hasEntry(player.name) && listenerTeam.hasEntry(killer.name)) {
                killer.sendTitle("${ChatColor.RED}あら､仲間をキルしましたね...", "", 10, 4 * 20, 10)
            }

            if (vtuberTeam.hasEntry(player.name)) {
                player.gameMode = GameMode.SPECTATOR
                listenerTeam.addEntry(player.name)
                val deathScore = Bukkit.getScoreboardManager().mainScoreboard.getObjective("death").getScore(player.name)
                if (deathScore.score >= 2) {
                    deathScore.score = 0
                }
                if (vtuberTeam.entries.isEmpty()) {
                    timer.cancel()
                }
            }
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
            val level = VTuberRadar.ITEM_RADARS.indexOfFirst { it.isSimilar(event.item) }
            val p = event.player
            if (level >= 2 && !ItemCooldownUtil.inCooldown(p, VTuberRadar.ITEM_RADARS[level].type)) {
                val color = arrayOf("白", "緑", "${ChatColor.YELLOW}黄", "${ChatColor.RED}赤")
                val loc = p.location
                p.chat("${color[level]}${ChatColor.RESET} at X:${loc.blockX} / Y:${loc.blockY} / Z:${loc.blockZ}")
                ItemCooldownUtil.setCooldown(p, VTuberRadar.ITEM_RADARS[level].type, 15 * 20)

                // 花火
                val fw = p.world.spawnEntity(Location(p.world, p.location.x, 100.0, p.location.z), EntityType.FIREWORK) as Firework
                val fwm = fw.fireworkMeta
                val fwColor = if (level == 2) {
                    Color.YELLOW
                } else {
                    Color.RED
                }
                fwm.addEffect(FireworkEffect.builder()
                        .withColor(fwColor).with(FireworkEffect.Type.BALL_LARGE).withFlicker().build()
                )
                fwm.power = 1

                fw.fireworkMeta = fwm
//                fw.detonate()
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
                if (event.remaining == 0L) {
                    Bukkit.getOnlinePlayers().forEach { it.sendTitle("${ChatColor.RED}VTuber${ChatColor.RESET} WIN!", "やった!やった!今夜はドン勝だ!", 20, 2 * 20, 20) }
                } else {
                    Bukkit.getOnlinePlayers().forEach { it.sendTitle("${ChatColor.BLUE}Listener${ChatColor.RESET} WIN!", "やった!やった!今夜はドン勝だ!", 20, 2 * 20, 20) }
                }
            }

            "ready" -> {
                if (event.remaining == 0L) {
                    Bukkit.setWhitelist(false)
                    plugin.server.worlds.forEach { it.time = 0 }
                    Bukkit.getOnlinePlayers().forEach {
                        it.sendTitle("Whitelist 解除", "", 20, 2 * 20, 20)
                }
                }
            }

            "interval" -> {
                if (event.remaining == 0L) {
                    val team = Bukkit.getScoreboardManager().mainScoreboard.getTeam("Listener")
                    Bukkit.getOnlinePlayers().filter { team.hasEntry(it.name) }.forEach {
                        it.inventory.addItem(
                                ItemStack(Material.DIAMOND_PICKAXE, 1), ItemStack(Material.IRON_AXE, 1),
                                ItemStack(Material.IRON_SWORD, 1), ItemStack(Material.IRON_SHOVEL, 1),
                                ItemStack(Material.COOKED_BEEF, 12), ItemStack(Material.OAK_LOG, 64),
                                ItemStack(Material.DIAMOND_HELMET, 1)
                        )
                    }
                    Bukkit.getOnlinePlayers().forEach { it.sendTitle("VTuber Wanted", "Start", 20, 2 * 20, 20) }
                }
            }
        }
    }

}