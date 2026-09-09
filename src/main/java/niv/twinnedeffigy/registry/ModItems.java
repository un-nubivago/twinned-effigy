package niv.twinnedeffigy.registry;

import static net.minecraft.resources.Identifier.fromNamespaceAndPath;
import static niv.twinnedeffigy.TwinnedEffigy.MOD_ID;

import java.util.function.Function;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.LockCode;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import niv.twinnedeffigy.item.TwinnedEffigyItem;

@SuppressWarnings("null")
@NullMarked
public class ModItems {
    private ModItems() {
    }

    public static final String CREATIVE_TAB_NAME;

    public static final TwinnedEffigyItem TWINNED_EFFIGY;

    public static final CreativeModeTab CREATIVE_TAB;

    static {
        CREATIVE_TAB_NAME = "creative." + MOD_ID + ".tab";

        TWINNED_EFFIGY = register(
                "twinned_effigy",
                TwinnedEffigyItem::new,
                new Properties().component(DataComponents.LOCK, LockCode.NO_LOCK));

        CREATIVE_TAB = Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                fromNamespaceAndPath(MOD_ID, "tab"),
                FabricCreativeModeTab.builder()
                        .icon(TWINNED_EFFIGY::getDefaultInstance)
                        .title(Component.translatable(CREATIVE_TAB_NAME))
                        .displayItems((parameters, output) -> output.accept(TWINNED_EFFIGY))
                        .build());
    }

    private static <T extends Item> T register(
            String name,
            Function<@NonNull Properties, T> itemFactory,
            Properties properties) {
        var key = ResourceKey.create(Registries.ITEM, fromNamespaceAndPath(MOD_ID, name));
        var item = itemFactory.apply(properties.setId(key));
        if (item instanceof BlockItem blockItem) {
            blockItem.registerBlocks(Item.BY_BLOCK, item);
        }
        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }

    public static final void initialize() {
        // Trigger static initialization
    }
}
