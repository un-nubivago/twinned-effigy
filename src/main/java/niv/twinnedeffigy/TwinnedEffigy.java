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

    public static final String MOD_ID = "twinned_effigy";

    public static final String MOD_NAME = "Twinned Effigy";

    @SuppressWarnings("null")
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    @Override
    public void onInitialize() {
        ModBlocks.initialize();
        ModItems.initialize();
        ModBlockEntityTypes.initialize();

        LOGGER.info("({}) Ready", MOD_NAME);
    }
}
