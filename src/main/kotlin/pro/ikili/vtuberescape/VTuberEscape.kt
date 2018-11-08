package pro.ikili.vtuberescape

import org.bukkit.Bukkit
import org.bukkit.GameRule
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scoreboard.Team


// TODO 全員石ツール一式 -> 準備5分くらいに
class VTuberEscape : JavaPlugin() {
    val timers: MutableMap<String, TimerBar> = mutableMapOf()

    override fun onEnable() {
        // Plugin startup logic
        this.logger.info("Loaded VTuber-Wanted")
        getCommand("vtuber-wanted").executor = VTCommand(this)
        this.server.pluginManager.registerEvents(EventListener(this), this)

        val board = Bukkit.getScoreboardManager().mainScoreboard
        if (!board.teams.asSequence().map { it.name }.contains("Listener")) {
            val team = board.registerNewTeam("Listener")
            team.setOption(Team.Option.DEATH_MESSAGE_VISIBILITY, Team.OptionStatus.NEVER)
        }

        if (!board.teams.map { it.name }.contains("VTuber")) {
            val team = board.registerNewTeam("VTuber")
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS)
        }

        this.server.worlds.forEach { it.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false) }

        Bukkit.getWorld("world").setSpawnLocation(0, 90, 0)
    }

    override fun onDisable() {
        // Plugin shutdown logic
        val board = Bukkit.getScoreboardManager().mainScoreboard
        board.getTeam("Listener").unregister()
        board.getTeam("VTuber").unregister()
    }
}
