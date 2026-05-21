package io.github.colcheco.fireside.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.colcheco.fireside.entity.LogEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

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
}
