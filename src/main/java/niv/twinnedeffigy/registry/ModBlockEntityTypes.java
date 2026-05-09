package niv.twinnedeffigy.registry;

import static net.minecraft.resources.Identifier.fromNamespaceAndPath;
import static niv.twinnedeffigy.TwinnedEffigy.MOD_ID;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import niv.twinnedeffigy.block.entity.TwinnedEffigyBlockEntity;

@SuppressWarnings("null")
@NullMarked
public class ModBlockEntityTypes {
    private ModBlockEntityTypes() {
    }

    public static final BlockEntityType<@NonNull TwinnedEffigyBlockEntity> TWINNED_EFFIGY;

    static {
        TWINNED_EFFIGY = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                fromNamespaceAndPath(MOD_ID, "twinned_effigy"),
                FabricBlockEntityTypeBuilder.create(
                        TwinnedEffigyBlockEntity::new,
                        ModBlocks.TWINNED_EFFIGY)
                        .build());

        ItemStorage.SIDED.registerForBlockEntity((entity, side) -> entity.getStorage(), TWINNED_EFFIGY);
    }

    public static final void initialize() {
        // Trigger static initialization
    }
}
