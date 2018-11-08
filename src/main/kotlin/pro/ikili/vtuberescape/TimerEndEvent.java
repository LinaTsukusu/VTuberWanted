package pro.ikili.vtuberescape;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TimerEndEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private String name;
    private long remaining;

    public TimerEndEvent(String name, long remaining) {
        this.name = name;
        this.remaining = remaining;
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

    public long getRemaining() {
        return remaining;
    }

    public void setRemaining(long remaining) {
        this.remaining = remaining;
    }
}
