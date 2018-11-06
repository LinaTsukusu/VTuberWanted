package pro.ikili.vtuberescape

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class TimerEndEvent(val name: String) : Event() {
    companion object {
        val handlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList {
        return TimerEndEvent.handlerList
    }

}