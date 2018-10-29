package pro.ikili.vtuberescape

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
import java.util.*

class VTuberRadar(level: Int) : ItemStack(Material.WHITE_CONCRETE) {
//    private val levels = arrayOf(Material.WHITE_CONCRETE, Material.YELLOW_CONCRETE, Material.RED_CONCRETE)

    init {
        this.type = levels[level]
        val meta = this.itemMeta
        meta.displayName = "VTuberRadar"
        meta.localizedName = "VTuberRadar"
        meta.lore = listOf(
                ChatColor.RESET.toString() + "VTuberを感知するレーダー",
                ChatColor.RESET.toString() + "距離: 白 > " + ChatColor.YELLOW + "黄" + ChatColor.RESET + " > " + ChatColor.RED + "赤"
        )

        this.itemMeta = meta
    }


    companion object {
        private val levels = arrayOf(Material.WHITE_CONCRETE, Material.YELLOW_CONCRETE, Material.RED_CONCRETE)
        private val ITEM_RADARS = arrayOf(VTuberRadar(0), VTuberRadar(1), VTuberRadar(2))

        fun isSimilar(stack: ItemStack?): Boolean {
            return ITEM_RADARS.any {it.isSimilar(stack)}
        }

        fun changeLevel(item: ItemStack, level: Int) {
            item.type = levels[level]
        }
    }
}