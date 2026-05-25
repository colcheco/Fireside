package io.github.colcheco.fireside.mixin;

import com.mojang.authlib.GameProfile;
import io.github.colcheco.fireside.entity.LogEntity;
import io.github.colcheco.fireside.entity.Sleeper;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.clock.ClockTimeMarker;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@NullMarked
@Mixin(ServerPlayer.class)
@Implements(@Interface(iface = Sleeper.class, prefix = "fireside$"))
public abstract class ServerPlayerMixin extends Player {
    @Unique
    private Sleeper.WakeUpTime wakeUpTime = Sleeper.WakeUpTime.NOT_SLEEPING;

    protected ServerPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Unique
    public Sleeper.WakeUpTime fireside$sleepingUntil() {
        return this.wakeUpTime;
    }

    @Unique
    public void fireside$setSleeping(Sleeper.WakeUpTime time) {
        this.wakeUpTime = time;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(CallbackInfo ci) {
        ResourceKey<ClockTimeMarker> marker = this.wakeUpTime.getMarker();
        if (this.isSleepingLongEnough()) {
            this.wakeUpTime = Sleeper.WakeUpTime.MORNING;
        } else if (marker != null) {
            if (!(this.getVehicle() instanceof LogEntity log && log.campfire())) {
                this.wakeUpTime = Sleeper.WakeUpTime.NOT_SLEEPING;
            } else if (this.level() instanceof ServerLevel level) {
                if (this.wakeUpTime.equals(Sleeper.WakeUpTime.CLEAR_WEATHER) && !level.isRaining()) {
                    log.kill(level);
                } else {
                    Optional<Holder<WorldClock>> clock = level.dimensionType().defaultClock();
                    if (clock.isPresent() && level.clockManager().isAtTimeMarker(clock.get(), marker)) {
                        log.kill(level);
                    }
                }
            }
        }
    }
}
