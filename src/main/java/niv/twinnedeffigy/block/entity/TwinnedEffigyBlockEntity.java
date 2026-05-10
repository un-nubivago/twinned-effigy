package niv.twinnedeffigy.block.entity;

import static niv.twinnedeffigy.TwinnedEffigy.MOD_ID;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.LockCode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import niv.twinnedeffigy.block.TwinnedEffigyBlock;
import niv.twinnedeffigy.registry.ModBlockEntityTypes;
import niv.twinnedeffigy.util.ReducedInventoryStorage;

@NullMarked
public class TwinnedEffigyBlockEntity extends BlockEntity {

    public static final String CONTAINER_NAME = "container." + MOD_ID + ".twinned_effigy";

    private static final Component DEFAULT_NAME = Component.translatable(CONTAINER_NAME);
    private static final String TAG_PROFILE = "profile";

    private LockCode lockKey = LockCode.NO_LOCK;

    private @Nullable ResolvableProfile profile = null;

    public TwinnedEffigyBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(ModBlockEntityTypes.TWINNED_EFFIGY, worldPosition, blockState);
    }

    @SuppressWarnings("null")
    public boolean useWithoutItem(BlockState state, Level level, BlockPos pos, Player player) {
        if (canUnlock(this.getBlockPos().getCenter(), player, this.lockKey)) {
            var wasBound = this.profile != null;

            if (this.profile != null && this.profile.partialProfile().id() == player.getGameProfile().id()) {
                this.profile = null;
                level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.BLOCKS);
            } else {
                this.profile = ResolvableProfile.createResolved(player.getGameProfile());
                level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_HIT, SoundSource.BLOCKS);
            }

            var isBound = this.profile != null;
            if (wasBound != isBound)
                level.setBlockAndUpdate(pos, state.setValue(TwinnedEffigyBlock.LIT, isBound));

            return true;
        } else
            return false;
    }

    public @Nullable Storage<ItemVariant> getStorage() {
        var safeProfile = this.profile;
        var safeLevel = this.level;
        if (safeLevel == null || safeProfile == null)
            return null;

        @SuppressWarnings("null")
        var player = safeLevel.getPlayerInAnyDimension(safeProfile.partialProfile().id());

        if (player == null)
            return null;

        return ReducedInventoryStorage.of(player);
    }

    // BlockEntity

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.lockKey = LockCode.fromTag(input);
        this.profile = input.read(TAG_PROFILE, ResolvableProfile.CODEC).orElse(null);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        this.lockKey.addToTag(output);
        output.storeNullable(TAG_PROFILE, ResolvableProfile.CODEC, this.profile);
    }

    @SuppressWarnings("null")
    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);
        this.lockKey = components.getOrDefault(DataComponents.LOCK, LockCode.NO_LOCK);
        this.profile = components.get(DataComponents.PROFILE);
    }

    @SuppressWarnings("null")
    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        if (!this.lockKey.equals(LockCode.NO_LOCK))
            components.set(DataComponents.LOCK, this.lockKey);
        if (this.profile != null)
            components.set(DataComponents.PROFILE, this.profile);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void removeComponentsFromTag(ValueOutput output) {
        super.removeComponentsFromTag(output);
        output.discard(LockCode.TAG_LOCK);
        output.discard(TAG_PROFILE);
    }

    // static

    public static boolean canUnlock(final Vec3 pos, final Player player, final LockCode code) {
        if (code.unlocksWith(player.getMainHandItem()) || code.unlocksWith(player.getOffhandItem())) {
            return true;
        } else {
            BaseContainerBlockEntity.sendChestLockedNotifications(pos, player, DEFAULT_NAME);
            return false;
        }
    }
}
