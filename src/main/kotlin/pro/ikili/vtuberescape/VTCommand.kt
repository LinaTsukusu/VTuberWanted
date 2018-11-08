package pro.ikili.vtuberescape

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack
import org.bukkit.scoreboard.Criterias
import org.bukkit.scoreboard.DisplaySlot
import java.lang.NullPointerException
import java.lang.NumberFormatException

class VTCommand(private val plugin: VTuberEscape) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (command.name == "vtuber-wanted") when (args[0]) {
            "start" -> {
                sender.sendMessage("start")

                val wb = Bukkit.getWorld("world").worldBorder
                wb.setCenter(0.0, 0.0)
                wb.size = 500.0

                val board = Bukkit.getScoreboardManager().mainScoreboard
                val vtuberTeam = board.getTeam("VTuber")

                try {
                    board.getObjective("death").unregister()
                } catch (e: NullPointerException) {}

                Bukkit.getOnlinePlayers().filter{ vtuberTeam.hasEntry(it.name) }.forEach {
                    it.inventory.addItem(
                            ItemStack(Material.IRON_PICKAXE, 1), ItemStack(Material.IRON_AXE, 1),
                            ItemStack(Material.IRON_SWORD, 1), ItemStack(Material.IRON_SHOVEL, 1),
                            ItemStack(Material.COOKED_BEEF, 12),
                            ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 1)
                    )
                }

                val death = board.registerNewObjective("death", "main", "VTuberList")
                death.displaySlot = DisplaySlot.SIDEBAR
                death.getScore("----Alive----").score = 3
                death.getScore("----Dead-----").score = 1
                vtuberTeam.entries.forEach {
                    death.getScore(it).score = 2
                }


                val sec = try { args[1].toLong() } catch (e: Exception) { 1200L }
                val ready = try { args[2].toLong() } catch(e: Exception) { 600L }
                val interval = 30L

                Bukkit.getOnlinePlayers().forEach {
                    it.sendTitle("Preparation time", "$ready seconds", 20, 2 * 20, 20)
                }

                TimerBar(plugin, ready, "ready").runTaskTimer(plugin, 0, 20)
                TimerBar(plugin, interval, "interval").runTaskTimer(plugin, ready * 20, 20)
                TimerBar(plugin, sec, "main").runTaskTimer(plugin, interval * 20 + ready * 20, 20)

                return true
            }

            else -> {

            }
        }

        return false
    }

}