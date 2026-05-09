package niv.twinnedeffigy.block;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import niv.twinnedeffigy.block.entity.TwinnedEffigyBlockEntity;
import niv.twinnedeffigy.registry.ModBlockEntityTypes;
import niv.twinnedeffigy.registry.ModItems;

@NullMarked
public class TwinnedEffigyBlock extends BaseEntityBlock {

    @SuppressWarnings("null")
    public static final MapCodec<TwinnedEffigyBlock> CODEC = simpleCodec(TwinnedEffigyBlock::new);

    public TwinnedEffigyBlock(Properties properties) {
        super(properties);
    }

    // BaseEntityBlock

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return new TwinnedEffigyBlockEntity(worldPosition, blockState);
    }

    // Block

    @Override
    protected void affectNeighborsAfterRemoval(
            BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        level.updateNeighbourForOutputSignal(pos, state.getBlock());
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        return level.getBlockEntity(pos, ModBlockEntityTypes.TWINNED_EFFIGY)
                .map(entity -> StorageUtil.getRedstoneSignal(entity.getStorage()))
                .orElse(0);
    }

    @Override
    protected InteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide())
            return InteractionResult.SUCCESS;
        if (level.getBlockEntity(pos, ModBlockEntityTypes.TWINNED_EFFIGY)
                .filter(entity -> entity.tryBoundToggle(player)).isPresent()) {
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.FAIL;
        }
    }

    @Override
    public Item asItem() {
        return ModItems.TWINNED_EFFIGY;
    }
}
