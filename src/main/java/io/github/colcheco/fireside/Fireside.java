package io.github.colcheco.fireside;

import io.github.colcheco.fireside.entity.LogEntity;
import net.fabricmc.api.ModInitializer;
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
        LogEntity.registerType();
    }
}
