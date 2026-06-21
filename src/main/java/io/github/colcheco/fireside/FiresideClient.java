package io.github.colcheco.fireside;

import io.github.colcheco.fireside.entity.LogEntity;
import io.github.colcheco.fireside.event.EndClientTickListener;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Environment(EnvType.CLIENT)
public class FiresideClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Fireside.LOGGER.info("Initializing client for {}", Fireside.MOD_ID);
        EntityRenderers.register(LogEntity.TYPE, context -> new EntityRenderer<>(context) {
            @Override
            public EntityRenderState createRenderState() {
                return new EntityRenderState();
            }
            @Override
            public boolean shouldRender(LogEntity l, Frustum f, double x, double y, double z) {
                return true;
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(new EndClientTickListener());
    }
}
