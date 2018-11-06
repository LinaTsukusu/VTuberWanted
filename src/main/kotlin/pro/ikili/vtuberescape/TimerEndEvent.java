package pro.ikili.vtuberescape;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TimerEndEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private String name;

    public TimerEndEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
