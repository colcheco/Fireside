package io.github.colcheco.fireside.entity;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.clock.ClockTimeMarker;
import net.minecraft.world.clock.ClockTimeMarkers;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface Sleeper {
    WakeUpTime sleepingUntil();
    void setSleeping(WakeUpTime time);

    enum WakeUpTime {
        NOT_SLEEPING(ClockTimeMarkers.ROLL_VILLAGE_SIEGE),
        MIDNIGHT(ClockTimeMarkers.MIDNIGHT),
        MORNING(ClockTimeMarkers.WAKE_UP_FROM_SLEEP),
        NOON(ClockTimeMarkers.NOON),
        NIGHT(ClockTimeMarkers.NIGHT),
        CLEAR_WEATHER(ClockTimeMarkers.DAY);

        private final ResourceKey<ClockTimeMarker> marker;

        WakeUpTime(ResourceKey<ClockTimeMarker> sleepingUntil) {
            marker = sleepingUntil;
        }

        public @Nullable ResourceKey<ClockTimeMarker> getMarker() {
            return marker == ClockTimeMarkers.ROLL_VILLAGE_SIEGE ? null : marker;
        }
    }
}
