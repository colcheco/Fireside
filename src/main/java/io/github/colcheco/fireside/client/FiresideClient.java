package io.github.colcheco.fireside.client;

import io.github.colcheco.fireside.entity.LogEntity;
import io.github.colcheco.fireside.Fireside;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRenderers;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Environment(EnvType.CLIENT)
public class FiresideClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Fireside.LOGGER.info("Initializing client for " + Fireside.MOD_ID);
        EntityRenderers.register(LogEntity.TYPE, LogEntityRenderer::new);
    }
}
