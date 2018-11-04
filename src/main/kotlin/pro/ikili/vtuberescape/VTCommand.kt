package pro.ikili.vtuberescape

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.scoreboard.Criterias
import org.bukkit.scoreboard.DisplaySlot
import java.lang.NullPointerException
import java.lang.NumberFormatException

class VTCommand(private val plugin: VTuberEscape) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (command.name == "vtuber-wanted") {
            when (args[0]) {
                "start" -> {

                    val sec = try {
                        args[1].toInt()
                    } catch (e: NumberFormatException) {
                        3600
                    }
                    sender.sendMessage("start")
                    val board = Bukkit.getScoreboardManager().mainScoreboard
                    val vtuberTeam = board.getTeam("VTuber")
                    try {
                        vtuberTeam.scoreboard.getObjective("death").unregister()
                    } catch (e: NullPointerException) {}

                    val death = vtuberTeam.scoreboard.registerNewObjective("death", Criterias.DEATHS, "VTuberList")
                    death.displaySlot = DisplaySlot.SIDEBAR
                    death.getScore("----Alive----").score = 0
                    vtuberTeam.entries.forEach {
                        death.getScore(it).score = 0
                    }

                    Bukkit.getOnlinePlayers().forEach {
                        it.sendTitle("VTuber Wanted", "Start", 20, 2 * 20, 20)
                    }

                    val ready = 600
                    TimerBar(plugin, ready).runTaskTimer(plugin, 0, 20)
                    plugin.server.scheduler.scheduleSyncDelayedTask(plugin, {
                        Bukkit.setWhitelist(false)
                    }, ready * 20.toLong())

                    plugin.server.scheduler.scheduleSyncDelayedTask(plugin, {
                        Bukkit.getOnlinePlayers().forEach { it.sendTitle("VTuber Wanted", "Start", 20, 2 * 20, 20) }
                    }, 200 + ready * 20.toLong())

                    TimerBar(plugin, sec).runTaskTimer(plugin, ready * 20.toLong(), 20)
                    plugin.server.scheduler.scheduleSyncDelayedTask(plugin, {
                        Bukkit.getOnlinePlayers().forEach { it.sendTitle("VTuber Wanted", "Finish", 20, 2 * 20, 20) }
                    }, ready * 20 + sec * 20.toLong())

                    return true

                }

                "reset" -> {
                    sender.sendMessage("reset")
                    return true
                }

                else -> {

                }
            }
        }

        return false
    }

}