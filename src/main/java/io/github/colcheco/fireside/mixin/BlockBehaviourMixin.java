package io.github.colcheco.fireside.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@NullMarked
@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourMixin {
    @Inject(method = "randomTick", at = @At("TAIL"))
    protected void onRandomTick(
            final BlockState state,
            final ServerLevel level,
            final BlockPos pos,
            final RandomSource random,
            final CallbackInfo ci
    ) {
        if (CampfireBlock.isLitCampfire(state)
                && !state.is(Blocks.SOUL_CAMPFIRE)
                && !state.getValueOrElse(CampfireBlock.SIGNAL_FIRE, true)
                && level.isRainingAt(pos.above())
        ) {
            level.playSound(null, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE,
                    SoundSource.BLOCKS, 1.0F, 1.0F);
            for (int j = 0; j < 20; j++) {
                CampfireBlock.makeParticles(level, pos, state.getValue(CampfireBlock.SIGNAL_FIRE), true);
            }
            level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
            level.setBlockAndUpdate(pos, state.setValue(CampfireBlock.LIT, false));
        }
    }
}
