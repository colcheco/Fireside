package io.github.colcheco.fireside.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import org.jspecify.annotations.NullMarked;
import org.lwjgl.glfw.GLFW;

@NullMarked
@Environment(EnvType.CLIENT)
public class ModKeyMappings {
    public static final KeyMapping SUBMIT = register("submit", GLFW.GLFW_KEY_ENTER);
    public static final KeyMapping SELECT = register("select", GLFW.GLFW_KEY_BACKSLASH);
    private static KeyMapping register(String name, int key) {
        return new KeyMapping("key.fireside." + name,
                InputConstants.Type.KEYSYM, key, KeyMapping.Category.GAMEPLAY);
    }
}
