package io.github.colcheco.fireside.mixin;

import net.minecraft.references.BlockItemId;
import net.minecraft.references.BlockItemIds;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@NullMarked
@Mixin(Blocks.class)
public abstract class BlocksMixin {
    @Unique
    private static final String MIXIN_PATH = "register(Lnet/minecraft/references/BlockItemId;" +
            "Ljava/util/function/Function;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)" +
            "Lnet/minecraft/world/level/block/Block;";

    @Inject(method = MIXIN_PATH, at = @At("HEAD"), cancellable = true)
    private static void OnRegister(
            BlockItemId id,
            Function<BlockBehaviour.Properties, Block> factory,
            BlockBehaviour.Properties properties,
            CallbackInfoReturnable<Block> cir
    ) {
        if (id.equals(BlockItemIds.CAMPFIRE)) {
            cir.setReturnValue(Blocks.register(
                    id.block(),
                    factory,
                    properties.randomTicks()
            ));
        }
    }
}
