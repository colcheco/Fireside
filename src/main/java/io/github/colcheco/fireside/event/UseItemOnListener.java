package io.github.colcheco.fireside.event;

import io.github.colcheco.fireside.entity.LogEntity;
import net.fabricmc.fabric.api.event.player.BlockEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class UseItemOnListener implements BlockEvents.UseItemOnCallback {
    @Override
    public @Nullable InteractionResult useItemOn(
            ItemStack itemStack,
            BlockState blockState,
            Level level,
            BlockPos blockPos,
            Player player,
            InteractionHand interactionHand,
            BlockHitResult blockHitResult
    ) {
        if (blockState.is(BlockTags.LOGS) && itemStack.isEmpty()) {
            BlockPos above = blockPos.above();
            if (level.getBlockState(above).isAir() && level.getBlockState(above.above()).isAir()) {
                if (level instanceof ServerLevel serverLevel) {
                    LogEntity entity;
                    List<LogEntity> logs = level.getEntities(LogEntity.TYPE, new AABB(blockPos), _ -> true);
                    if (logs.isEmpty()) {
                        entity = LogEntity.TYPE.spawn(serverLevel, blockPos, EntitySpawnReason.TRIGGERED);
                    } else {
                        entity = logs.getFirst();
                    }
                    if (entity != null && !entity.isVehicle()) {
                        player.startRiding(entity);
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }
        return null;
    }
}
