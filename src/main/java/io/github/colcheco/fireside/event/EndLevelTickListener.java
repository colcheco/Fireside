package io.github.colcheco.fireside.event;

import io.github.colcheco.fireside.Fireside;
import io.github.colcheco.fireside.entity.LogEntity;
import io.github.colcheco.fireside.entity.Sleeper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.clock.ClockTimeMarker;
import net.minecraft.world.clock.ServerClockManager;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.level.gamerules.GameRules;
import org.jspecify.annotations.NullMarked;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@NullMarked
public class EndLevelTickListener implements ServerTickEvents.EndLevelTick {
    private static final Map<ServerLevel, Boolean> processing = new ConcurrentHashMap<>();

    @Override
    public void onEndTick(ServerLevel level) {
        List<ServerPlayer> players = level.getPlayers(player -> !player.isSpectator());
        if (!players.isEmpty()) {
            GameRules rules = level.getGameRules();
            if (level.sleepStatus.areEnoughDeepSleeping(rules.get(GameRules.PLAYERS_SLEEPING_PERCENTAGE), players)) {
                processing.put(level, true);
            } else {
                Optional<Holder<WorldClock>> clock = level.dimensionType().defaultClock();
                boolean rainNap = rules.get(GameRules.ADVANCE_WEATHER) && level.isRaining();
                if (rules.get(GameRules.ADVANCE_TIME) && clock.isPresent()) {
                    Holder<WorldClock> holder = clock.get();
                    ServerClockManager manager = level.clockManager();
                    Sleeper.WakeUpTime target = Sleeper.WakeUpTime.NOT_SLEEPING;
                    long timeToSoonest = Long.MAX_VALUE;
                    int weatherHaters = 0;
                    for (ServerPlayer sleeper : players) {
                        Sleeper.WakeUpTime sleeperTarget;
                        if (sleeper.isSleepingLongEnough()) {
                            if (level.isThundering()) {
                                sleeperTarget = Sleeper.WakeUpTime.CLEAR_WEATHER;
                            } else {
                                sleeperTarget = Sleeper.WakeUpTime.MORNING;
                                weatherHaters++;
                            }
                        } else {
                            sleeperTarget = ((Sleeper) sleeper).sleepingUntil();
                        }
                        if (sleeperTarget.equals(Sleeper.WakeUpTime.NOT_SLEEPING)) {
                            target = sleeperTarget;
                            break;
                        }
                        if (sleeperTarget.equals(Sleeper.WakeUpTime.CLEAR_WEATHER)) {
                            weatherHaters++;
                            continue;
                        }
                        long distance = manager.getInstance(holder).timeMarkers.get(sleeperTarget.getMarker())
                                .resolveTimeToMoveTo(manager.getTotalTicks(holder));
                        if (distance < timeToSoonest) {
                            target = sleeperTarget;
                            timeToSoonest = distance;
                        }
                    }
                    if (rainNap && weatherHaters == players.size()) {
                        target = Sleeper.WakeUpTime.CLEAR_WEATHER;
                    }
                    if (target.equals(Sleeper.WakeUpTime.NOT_SLEEPING)) {
                        processing.put(level, false);
                    }
                    if (processing.getOrDefault(level, false)) {
                        target = Sleeper.WakeUpTime.NOT_SLEEPING;
                    }
                    ResourceKey<ClockTimeMarker> marker = target.getMarker();
                    if (marker != null) {
                        processing.put(level, true);
                        Collections.shuffle(players);
                        for (ServerPlayer sleeper : players) {
                            if (sleeper.getVehicle() instanceof LogEntity log) {
                                log.tickCampfires();
                                if (!log.campfire()) {
                                    return;
                                }
                            }
                        }
                        if (target.equals(Sleeper.WakeUpTime.CLEAR_WEATHER)) {
                            level.resetWeatherCycle();
                        } else {
                            for (ServerPlayer sleeper : players) {
                                if (((Sleeper) sleeper).sleepingUntil().equals(target)
                                        && sleeper.getVehicle() instanceof LogEntity log) {
                                    log.kill(level);
                                }
                            }
                        }
                        manager.moveToTimeMarker(holder, marker);
                        Fireside.LOGGER.info("Skipped time to {}", target);
                    }
                } else if (rainNap) {
                    for (ServerPlayer sleeper : players) {
                        if (!(((Sleeper) sleeper).sleepingUntil().equals(Sleeper.WakeUpTime.CLEAR_WEATHER)
                                || sleeper.isSleepingLongEnough())) {
                            processing.put(level, false);
                            return;
                        }
                    }
                    if (!processing.getOrDefault(level, false)) {
                        processing.put(level, true);
                        Collections.shuffle(players);
                        for (ServerPlayer sleeper : players) {
                            if (sleeper.getVehicle() instanceof LogEntity log) {
                                log.tickCampfires();
                                if (!log.campfire()) {
                                    return;
                                }
                            }
                        }
                        level.resetWeatherCycle();
                        Fireside.LOGGER.info("Cleared weather");
                    }
                }
            }
        }
    }
}
