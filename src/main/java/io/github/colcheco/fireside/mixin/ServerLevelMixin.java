package io.github.colcheco.fireside.mixin;

import io.github.colcheco.fireside.entity.Sleeper;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.SleepStatus;
import net.minecraft.world.clock.ClockTimeMarker;
import net.minecraft.world.clock.ServerClockManager;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.WritableLevelData;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;

@NullMarked
@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level {
    protected ServerLevelMixin(
            WritableLevelData levelData,
            ResourceKey<Level> dimension,
            RegistryAccess registryAccess,
            Holder<DimensionType> dimensionTypeRegistration,
            boolean isClientSide,
            boolean isDebug,
            long biomeZoomSeed,
            int maxChainedNeighborUpdates
    ) {
        super(
                levelData,
                dimension,
                registryAccess,
                dimensionTypeRegistration,
                isClientSide,
                isDebug,
                biomeZoomSeed,
                maxChainedNeighborUpdates
        );
    }
    @Shadow
    private @Final SleepStatus sleepStatus;
    @Inject(method = "tick", at = @At("TAIL"))
    public void onTick(BooleanSupplier haveTime, CallbackInfo ci) {
        ServerLevel level = (ServerLevel) (Level) this;
        List<ServerPlayer> players = level.getPlayers(player -> !player.isSpectator());
        GameRules rules = level.getGameRules();
        if (!this.sleepStatus.areEnoughDeepSleeping(rules.get(GameRules.PLAYERS_SLEEPING_PERCENTAGE), players)) {
            Optional<Holder<WorldClock>> clock = level.dimensionType().defaultClock();
            if (rules.get(GameRules.ADVANCE_TIME) && clock.isPresent()) {
                Holder<WorldClock> holder = clock.get();
                ServerClockManager manager = level.clockManager();
                Sleeper.WakeUpTime target = Sleeper.WakeUpTime.NOT_SLEEPING;
                long timeToSoonest = Long.MAX_VALUE;
                for (ServerPlayer sleeper : players) {
                    Sleeper.WakeUpTime sleeperTarget = ((Sleeper) sleeper).sleepingUntil();
                    if (sleeperTarget == Sleeper.WakeUpTime.NOT_SLEEPING) {
                        target = sleeperTarget;
                        break;
                    }
                    long distance = manager.getInstance(holder).timeMarkers.get(sleeperTarget.getMarker())
                            .resolveTimeToMoveTo(manager.getTotalTicks(holder));
                    if (distance < timeToSoonest) {
                        target = sleeperTarget;
                        timeToSoonest = distance;
                    }
                }
                ResourceKey<ClockTimeMarker> marker = target.getMarker();
                if (marker != null) {
                    manager.moveToTimeMarker(holder, marker);

                }
            }
        }
    }
}
