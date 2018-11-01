package pro.ikili.vtuberescape

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class VTCommand(val plugin: VTuberEscape) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (command.name == "vtuber-wanted") {
            when (args[0]) {
                "start" -> {
                    return true
                }

                "reset" -> {
                    return true
                }

                else -> {

                }
            }
        }

        return false
    }

}