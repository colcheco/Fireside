package io.github.colcheco.fireside;

import io.github.colcheco.fireside.entity.LogEntity;
import io.github.colcheco.fireside.keybind.FiresidePayloadC2S;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NullMarked
public class Fireside implements ModInitializer {
    public static final String MOD_ID = "fireside";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing " + MOD_ID);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, LogEntity.KEY, LogEntity.TYPE);
        PayloadTypeRegistry.serverboundPlay().register(FiresidePayloadC2S.TYPE, FiresidePayloadC2S.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(FiresidePayloadC2S.TYPE, FiresidePayloadC2S::handle);
    }
}
