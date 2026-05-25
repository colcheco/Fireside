package io.github.colcheco.fireside.entity;

import io.github.colcheco.fireside.Fireside;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class LogEntity extends Entity {
    public static final ResourceKey<EntityType<?>> KEY = ResourceKey.create(Registries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(Fireside.MOD_ID, "log"));
    public static final EntityType<LogEntity> TYPE = EntityType.Builder.of(LogEntity::new, MobCategory.MISC)
            .noLootTable().sized(1F, 1F).build(KEY);

    public LogEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (level() instanceof ServerLevel level) {
            if (!level.getBlockState(getOnPos().above()).is(BlockTags.LOGS)) {
                kill(level);
            }
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        return false;
    }

    public boolean campfire() {
        for (BlockPos offset : EnchantingTableBlock.BOOKSHELF_OFFSETS) {
            BlockState block = level().getBlockState(getOnPos().above().offset(offset));
            if (block.is(BlockTags.CAMPFIRES) && block.getValueOrElse(CampfireBlock.LIT, false)) {
                return true;
            }
        }
        return false;
    }

    public void tickCampfires() {
        if (level() instanceof ServerLevel level) {
            for (BlockPos offset : EnchantingTableBlock.BOOKSHELF_OFFSETS) {
                BlockPos next = getOnPos().above().offset(offset);
                BlockState block = level.getBlockState(next);
                if (block.is(BlockTags.CAMPFIRES)) {
                    block.randomTick(level, next, level.getRandom());
                }
            }
        }
    }

    @Override
    protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
        if (level() instanceof ServerLevel level) {
            kill(level);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
    }
}
