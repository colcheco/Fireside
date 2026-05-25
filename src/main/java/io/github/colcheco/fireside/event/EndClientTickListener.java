package io.github.colcheco.fireside.event;

import io.github.colcheco.fireside.keybind.ModKeyMappings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Environment(EnvType.CLIENT)
public class EndClientTickListener implements ClientTickEvents.EndTick {
    private static boolean selectPressed = false;
    private static boolean submitPressed = false;

    @Override
    public void onEndTick(Minecraft client) {
        boolean select = ModKeyMappings.SELECT.isDown();
        if (selectPressed != select) {
            selectPressed = select;
            if (select) {
                ModKeyMappings.press(ModKeyMappings.SELECT, client);
            }
        }
        boolean submit = ModKeyMappings.SUBMIT.isDown();
        if (submitPressed != submit) {
            submitPressed = submit;
            if (submit) {
                ModKeyMappings.press(ModKeyMappings.SUBMIT, client);
            }
        }
    }
}
