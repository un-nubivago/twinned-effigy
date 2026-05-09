package niv.twinnedeffigy;

import net.fabricmc.api.ModInitializer;
import niv.twinnedeffigy.registry.ModBlockEntityTypes;
import niv.twinnedeffigy.registry.ModBlocks;
import niv.twinnedeffigy.registry.ModItems;

import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NullMarked
public class TwinnedEffigy implements ModInitializer {

    public static final String MOD_ID = "twinned-effigy";

    public static final String MOD_NAME = "Twinned Effigy";

    @SuppressWarnings("null")
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        LOGGER.info("[{}] Initialize", MOD_NAME);

        ModBlocks.initialize();
        ModItems.initialize();
        ModBlockEntityTypes.initialize();
    }
}
