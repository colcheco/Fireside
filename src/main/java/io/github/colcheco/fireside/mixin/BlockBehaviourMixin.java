package io.github.colcheco.fireside.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.colcheco.fireside.entity.LogEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@NullMarked
@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourMixin {
    @ModifyReturnValue(method = "useItemOn", at = @At("RETURN"))
    public InteractionResult modifyUseItemOn(
            final InteractionResult original,
            final ItemStack itemStack,
            final BlockState state,
            final Level level,
            final BlockPos pos,
            final Player player,
            final InteractionHand hand,
            final BlockHitResult hitResult
    ) {
        if (state.is(BlockTags.LOGS) && itemStack.isEmpty()) {
            BlockPos above = pos.above();
            if (!level.getBlockState(above).isAir() || !level.getBlockState(above.above()).isAir()) {
                return original;
            }
            if (level instanceof ServerLevel serverLevel) {
                LogEntity entity;
                List<LogEntity> logs = level.getEntities(LogEntity.TYPE, new AABB(pos), _ -> true);
                if (logs.isEmpty()) {
                    entity = LogEntity.TYPE.spawn(serverLevel, pos, EntitySpawnReason.TRIGGERED);
                } else {
                    entity = logs.getFirst();
                }
                if (entity != null && entity.getFirstPassenger() == null) {
                    player.startRiding(entity);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return original;
    }
    @Inject(method = "randomTick", at = @At("TAIL"))
    protected void onRandomTick(
            final BlockState state,
            final ServerLevel level,
            final BlockPos pos,
            final RandomSource random,
            final CallbackInfo ci
    ) {
        if (state.is(BlockTags.CAMPFIRES)
                && !state.getValueOrElse(CampfireBlock.SIGNAL_FIRE, true)
                && state.getValueOrElse(CampfireBlock.LIT, false)
                && level.isRainingAt(pos.above())
        ) {
            level.playSound(null, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE,
                    SoundSource.BLOCKS, 1.0F, 1.0F);
            CampfireBlock.dowse(null, level, pos, state);
            level.setBlockAndUpdate(pos, state.setValue(CampfireBlock.LIT, false));
        }
    }
}
