package io.github.colcheco.fireside.client;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.colcheco.fireside.networking.FiresidePayloadC2S;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NullMarked;
import org.lwjgl.glfw.GLFW;

@NullMarked
@Environment(EnvType.CLIENT)
public class ModKeyMappings {
    public static final KeyMapping SUBMIT = register("submit", GLFW.GLFW_KEY_ENTER);
    public static final KeyMapping SELECT = register("select", GLFW.GLFW_KEY_BACKSLASH);
    private static byte selection;
    private static KeyMapping register(String name, int key) {
        return new KeyMapping("key.fireside." + name,
                InputConstants.Type.KEYSYM, key, KeyMapping.Category.GAMEPLAY);
    }

    public static void press(KeyMapping keyMapping, Minecraft client) {
        if (client.player != null) {
            if (keyMapping.equals(ModKeyMappings.SELECT)) {
                if (++selection > 5) {
                    selection = 0;
                }
                client.player.sendOverlayMessage(getMessage());
            }
            if (keyMapping.equals(ModKeyMappings.SUBMIT)) {
                ClientPlayNetworking.send(new FiresidePayloadC2S(selection));
            }
        }
    }

    private static Component getMessage() {
        String message;
        if (selection == 0) {
            message = "Cleared selection";
        } else {
            message = "Selected campfire wake up time: ";
            message = message + switch (selection) {
                case 1 -> "midnight";
                case 2 -> "morning";
                case 3 -> "noon";
                case 4 -> "night";
                case 5 -> "clear weather";
                default -> "invalid";
            };
        }
        return Component.literal(message);
    }
}
