package niv.twinnedeffigy.registry;

import static net.minecraft.resources.Identifier.fromNamespaceAndPath;
import static net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy;
import static niv.twinnedeffigy.TwinnedEffigy.MOD_ID;

import org.jspecify.annotations.NullMarked;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import niv.twinnedeffigy.block.TwinnedEffigyBlock;

@SuppressWarnings("null")
@NullMarked
public class ModBlocks {
    private ModBlocks() {
    }

    public static final TwinnedEffigyBlock TWINNED_EFFIGY;

    static {
        TWINNED_EFFIGY = (TwinnedEffigyBlock) Blocks.register(
                ResourceKey.create(Registries.BLOCK, fromNamespaceAndPath(MOD_ID, "twinned_effigy")),
                TwinnedEffigyBlock::new,
                ofFullCopy(Blocks.AMETHYST_BLOCK));
    }

    public static final void initialize() {
        // Trigger static initialization
    }
}
