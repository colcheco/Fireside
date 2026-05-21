package io.github.colcheco.fireside.entity;

import io.github.colcheco.fireside.Fireside;
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
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
    }

    @Override
    protected void removePassenger(Entity passenger) {
        if (passenger instanceof Sleeper sleeper) {
            sleeper.setSleeping(Sleeper.WakeUpTime.NOT_SLEEPING);
        }
        super.removePassenger(passenger);
        if (level() instanceof ServerLevel level) {
            kill(level);
        }
    }
}
