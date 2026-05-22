package io.github.colcheco.fireside;

import io.github.colcheco.fireside.entity.LogEntity;
import io.github.colcheco.fireside.entity.LogEntityRenderer;
import io.github.colcheco.fireside.keybind.ModKeyMappings;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.renderer.entity.EntityRenderers;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Environment(EnvType.CLIENT)
public class FiresideClient implements ClientModInitializer {
    public static boolean selectPressed = false;
    public static boolean submitPressed = false;
    @Override
    public void onInitializeClient() {
        Fireside.LOGGER.info("Initializing client for " + Fireside.MOD_ID);
        EntityRenderers.register(LogEntity.TYPE, LogEntityRenderer::new);
        KeyMappingHelper.registerKeyMapping(ModKeyMappings.SELECT);
        KeyMappingHelper.registerKeyMapping(ModKeyMappings.SUBMIT);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (selectPressed != ModKeyMappings.SELECT.isDown()) {
                selectPressed = ModKeyMappings.SELECT.isDown();
                if (selectPressed) {
                    ModKeyMappings.press(ModKeyMappings.SELECT, client);
                }
            }
            if (submitPressed != ModKeyMappings.SUBMIT.isDown()) {
                submitPressed = ModKeyMappings.SUBMIT.isDown();
                if (submitPressed) {
                    ModKeyMappings.press(ModKeyMappings.SUBMIT, client);
                }
            }
        });
    }
}
