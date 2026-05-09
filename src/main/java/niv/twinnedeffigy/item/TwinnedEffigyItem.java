package niv.twinnedeffigy.item;

import static niv.twinnedeffigy.TwinnedEffigy.MOD_ID;

import org.jspecify.annotations.NullMarked;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import niv.twinnedeffigy.registry.ModBlocks;

@NullMarked
public class TwinnedEffigyItem extends BlockItem {

    public static final String BOUND_NAME = "item." + MOD_ID +".bound_twinned_effigy";

    public TwinnedEffigyItem(Properties properties) {
        super(ModBlocks.TWINNED_EFFIGY, properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.has(DataComponents.PROFILE) || super.isFoil(stack);
    }

    @Override
    public Component getName(ItemStack stack) {
        var profile = stack.get(DataComponents.PROFILE);
        return profile == null
                ? super.getName(stack)
                : Component.translatable(BOUND_NAME, profile.partialProfile().name());
    }
}
