package io.github.colcheco.fireside.mixin;

import com.mojang.authlib.GameProfile;
import io.github.colcheco.fireside.entity.LogEntity;
import io.github.colcheco.fireside.entity.Sleeper;
import net.minecraft.server.level.ServerPlayer;
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

@NullMarked
@Mixin(ServerPlayer.class)
@Implements(@Interface(iface = Sleeper.class, prefix = "fireside$"))
public abstract class ServerPlayerMixin extends Player {
    public ServerPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }
    @Unique
    private Sleeper.WakeUpTime wakeUpTime = Sleeper.WakeUpTime.NOT_SLEEPING;
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
        if (this.isSleepingLongEnough()) {
            this.wakeUpTime = Sleeper.WakeUpTime.MORNING;
        } else if (this.wakeUpTime != Sleeper.WakeUpTime.NOT_SLEEPING) {
            if (!(this.getVehicle() instanceof LogEntity log && log.campfire())) {
                this.wakeUpTime = Sleeper.WakeUpTime.NOT_SLEEPING;
            }
        }
    }
}
