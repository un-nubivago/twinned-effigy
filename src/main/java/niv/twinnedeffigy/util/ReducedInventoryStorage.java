package niv.twinnedeffigy.util;

import static net.minecraft.world.entity.player.Inventory.INVENTORY_SIZE;
import static net.minecraft.world.entity.player.Inventory.SELECTION_SIZE;
import static net.minecraft.world.entity.player.Inventory.SLOT_OFFHAND;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.fabricmc.fabric.impl.transfer.DebugMessages;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@NullMarked
public class ReducedInventoryStorage
        extends CombinedStorage<ItemVariant, SingleSlotStorage<ItemVariant>>
        implements SlottedStorage<ItemVariant> {

    public static final ReducedInventoryStorage of(Player player) {
        var result = new ReducedInventoryStorage(player.getInventory());
        result.resizeSlotList();
        return result;
    }

    private final Inventory inventory;

    private final List<SlotWrapper> slots;

    private final SnapshotParticipant<@NonNull Boolean> callback = new SnapshotParticipant<@NonNull Boolean>() {
        @SuppressWarnings("null")
        @Override
        protected Boolean createSnapshot() {
            return Boolean.TRUE;
        }

        @Override
        protected void readSnapshot(Boolean snapshot) {
            // Do nothing
        }

        @Override
        protected void onFinalCommit() {
            ReducedInventoryStorage.this.inventory.setChanged();
        }
    };

    private ReducedInventoryStorage(Inventory inventory) {
        super(Collections.emptyList());
        this.inventory = inventory;
        this.slots = new ArrayList<>();
    }

    @SuppressWarnings("null")
    private void resizeSlotList() {
        int size = inventory.getContainerSize();
        if (size != this.parts.size()) {
            while (slots.size() < size) {
                slots.add(new SlotWrapper(this, slots.size()));
            }
            this.parts = Collections.unmodifiableList(slots.subList(0, size));
        }
    }

    private SingleSlotStorage<ItemVariant> getHandSlot(InteractionHand hand) {
        if (Objects.requireNonNull(hand) == InteractionHand.MAIN_HAND) {
            if (Inventory.isHotbarSlot(this.inventory.getSelectedSlot())) {
                return getSlot(this.inventory.getSelectedSlot());
            } else {
                throw new IllegalStateException(
                        "Unexpected player selected slot: " + this.inventory.getSelectedSlot());
            }
        } else if (hand == InteractionHand.OFF_HAND) {
            return getSlot(Inventory.SLOT_OFFHAND);
        } else {
            throw new UnsupportedOperationException("Unknown hand: " + hand);
        }
    }

    @Override
    public List<SingleSlotStorage<ItemVariant>> getSlots() {
        return this.parts;
    }

    @Override
    public int getSlotCount() {
        return getSlots().size();
    }

    @SuppressWarnings("null")
    @Override
    public SingleSlotStorage<ItemVariant> getSlot(int slot) {
        return this.parts.get(slot);
    }

    @SuppressWarnings("null")
    @Override
    public long insert(@Nullable ItemVariant resource, long maxAmount, TransactionContext transaction) {
        if (resource == null)
            return 0;

        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        var initialAmount = maxAmount;
        var subSlots = getSlots().subList(0, Inventory.INVENTORY_SIZE);
        for (var hand : InteractionHand.values()) {
            var slot = getHandSlot(hand);
            if (slot.getResource().equals(resource)) {
                maxAmount -= slot.insert(resource, maxAmount, transaction);
                if (maxAmount == 0)
                    return initialAmount;
            }
        }
        maxAmount -= StorageUtil.insertStacking(subSlots, resource, maxAmount, transaction);
        return initialAmount - maxAmount;
    }

    @Override
    public String toString() {
        return "ReducedInventoryStorage[" + DebugMessages.forInventory(this.inventory) + "]";
    }

    private static final class SlotWrapper extends SingleStackStorage {

        private final ReducedInventoryStorage storage;

        private final int slot;

        private @Nullable ItemStack lastReleasedSnapshot = null;

        private SlotWrapper(ReducedInventoryStorage storage, int slot) {
            this.storage = storage;
            this.slot = slot;
        }

        @Override
        protected ItemStack getStack() {
            return this.storage.inventory.getItem(slot);
        }

        @Override
        protected void setStack(ItemStack stack) {
            this.storage.inventory.setItem(slot, stack);
        }

        @Override
        protected boolean canInsert(ItemVariant itemVariant) {
            return super.canInsert(itemVariant) && (this.slot < INVENTORY_SIZE || this.slot == SLOT_OFFHAND);
        }

        @Override
        public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            return canInsert(resource) ? super.insert(resource, maxAmount, transaction) : 0L;
        }

        @Override
        protected boolean canExtract(ItemVariant itemVariant) {
            return super.canExtract(itemVariant) && slot >= SELECTION_SIZE && slot < INVENTORY_SIZE;
        }

        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            return canExtract(resource) ? super.extract(resource, maxAmount, transaction) : 0L;
        }

        @Override
        public int getCapacity(ItemVariant resource) {
            return Math.min(
                    this.storage.inventory.getMaxStackSize(),
                    resource.getItem().getDefaultMaxStackSize());
        }

        @Override
        public void updateSnapshots(TransactionContext transaction) {
            this.storage.callback.updateSnapshots(transaction);
            super.updateSnapshots(transaction);
        }

        @Override
        protected void releaseSnapshot(@Nullable ItemStack snapshot) {
            this.lastReleasedSnapshot = snapshot;
        }

        @Override
        protected void onFinalCommit() {
            var original = this.lastReleasedSnapshot;
            var current = getStack();

            if (original == null)
                return;

            if (original.isEmpty() || original.getItem() != current.getItem()) {
                original.setCount(0);
            } else {
                if (!Objects.equals(original.getComponentsPatch(), current.getComponentsPatch())) {
                    for (var type : original.getComponents().keySet())
                        if (type != null)
                            original.set(type, null);
                    original.applyComponents(current.getComponents());
                }
                original.setCount(current.getCount());
                setStack(original);
            }
        }

        @SuppressWarnings("null")
        @Override
        public String toString() {
            return "SlotWrapper[%s#%d]".formatted(
                    DebugMessages.forInventory(this.storage.inventory),
                    this.slot);
        }
    }
}
