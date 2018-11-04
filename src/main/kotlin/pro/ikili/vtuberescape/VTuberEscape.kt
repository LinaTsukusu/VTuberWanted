package pro.ikili.vtuberescape

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scoreboard.Team

// TODO 調整: レーダー性能 試合時間 ボーダー範囲
// TODO 治す: スコアボード
class VTuberEscape : JavaPlugin() {

    override fun onEnable() {
        // Plugin startup logic
        this.logger.info("Loaded VTuber-Wanted")
        getCommand("vtuber-wanted").executor = VTCommand(this)
        this.server.pluginManager.registerEvents(EventListener(this), this)

        val board = Bukkit.getScoreboardManager().mainScoreboard
        if (!board.teams.asSequence().map { it.name }.contains("Listener")) {
            val team = board.registerNewTeam("Listener")
//            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OWN_TEAM)
        }

        if (!board.teams.map { it.name }.contains("VTuber")) {
            val team = board.registerNewTeam("VTuber")
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS)
        }

        Bukkit.getWorld("world").setSpawnLocation(0, 70, 0)

        val wb = Bukkit.getWorld("world").worldBorder
        wb.setCenter(0.0, 0.0)
        wb.size = 1000.0

    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
