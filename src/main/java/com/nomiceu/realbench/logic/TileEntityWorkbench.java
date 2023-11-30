package com.nomiceu.realbench.logic;

import com.google.common.collect.ImmutableList;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.world.WorldServer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class TileEntityWorkbench extends TileEntity implements ITickable {
    public NonNullList<ItemStack> craftMatrix;
    private List<ItemStack> oldMatrix;
    @Nullable
    private ContainerWorkbench container;

    public TileEntityWorkbench() {
        craftMatrix = NonNullList.withSize(9, ItemStack.EMPTY);
        oldMatrix = NonNullList.withSize(9, ItemStack.EMPTY);
        container = null;
    }

    public TileEntityWorkbench setContainer(@NotNull ContainerWorkbench container) {
        this.container = container;
        return this;
    }

    @Nullable
    public ContainerWorkbench getContainer() {
        return container;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (world != null) {
            world.markBlockRangeForRenderUpdate(pos, pos);
            if (!world.isRemote) {
                WorldServer worldServer = (WorldServer) world;
                worldServer.getPlayerChunkMap().markBlockForUpdate(pos);
            }
        }
    }

    /**
     * Gets the craft matrix making sure nothing is null.
     */
    public List<ItemStack> getCraftMatrixSafe() {
        ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
        for (var stack : craftMatrix) {
            builder.add(stack == null ? ItemStack.EMPTY : stack);
        }
        return builder.build();
    }

    private void writeSlots(NBTTagCompound nbt) {
        nbt.setInteger("Capacity", craftMatrix.size());

        for (int i = 0; i < 9; ++i) {
            if (craftMatrix.get(i) == ItemStack.EMPTY) {
                nbt.removeTag("Slot" + i);
            } else {
                NBTTagCompound slot = new NBTTagCompound();
                (craftMatrix.get(i)).writeToNBT(slot);
                nbt.setTag("Slot" + i, slot);
            }
        }
    }

    private void readSlots(NBTTagCompound nbt) {
        int capacity = 9;
        if (nbt.hasKey("Capacity", 3)) {
            capacity = nbt.getInteger("Capacity");
        }

        updateOldMatrix();

        ensureCraftMatrixCapacity(capacity);

        for (int i = 0; i < 9; ++i) {
            if (!nbt.hasKey("Slot" + i, 10)) {
                craftMatrix.set(i, ItemStack.EMPTY);
            } else {
                craftMatrix.set(i, new ItemStack(nbt.getCompoundTag("Slot" + i)));
            }
        }

        if (!hasMatrixChanged(oldMatrix, craftMatrix)) return;
        markDirty();
    }

    public boolean hasMatrixChanged(List<ItemStack> oldMatrix, List<ItemStack> newMatrix) {
        if (oldMatrix.size() != newMatrix.size()) return true;
        for (int i = 0; i < oldMatrix.size(); i++)
            if (!oldMatrix.get(i).getItem().equals(newMatrix.get(i).getItem()) || oldMatrix.get(i).getMetadata() != newMatrix.get(i).getMetadata())
                return true;
        return false;
    }

    public boolean matrixEmpty(List<ItemStack> matrix) {
        for (var stack : matrix) {
            if (stack.isEmpty()) return false;
        }
        return true;
    }

    public void readFromNBT(@NotNull NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        readSlots(nbt);
    }

    public @NotNull NBTTagCompound writeToNBT(@NotNull NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        writeSlots(nbt);
        return nbt;
    }

    public @NotNull NBTTagCompound getUpdateTag() {
        NBTTagCompound nbt = super.getUpdateTag();
        writeSlots(nbt);
        return nbt;
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
    }

    @Override
    public void handleUpdateTag(@NotNull NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        this.readSlots(tag);
    }

    @Override
    public void onDataPacket(@NotNull NetworkManager net, @NotNull SPacketUpdateTileEntity pkt) {
        handleUpdateTag(pkt.getNbtCompound());
    }

    public void ensureCraftMatrixCapacity(int capacity) {
        if (craftMatrix.size() != capacity) {
            craftMatrix = NonNullList.withSize(capacity, ItemStack.EMPTY);
        }
    }

    @Override
    public void update() {
        if (matrixEmpty(craftMatrix)) {
            if (container != null && !container.craftResult.isEmpty()) {
                container.craftResult.clear();
                updateOldMatrix();
                markDirty();
                return;
            }
            if (!matrixEmpty(oldMatrix)) markDirty();
            return;
        }
        if (hasMatrixChanged(oldMatrix, craftMatrix) || (container != null && ((InventoryWorkbench) container.craftMatrix).needsUpdate())) {
            if (container != null) {
                ((TileContainerWorkbench) container).updateResult();
                ((InventoryWorkbench) container.craftMatrix).setNoUpdate();
                updateOldMatrix();
            }
            markDirty();
        }
    }

    public void updateOldMatrix() {
        oldMatrix = NonNullList.withSize(9, ItemStack.EMPTY);
        for (int i = 0; i < craftMatrix.size(); i++) {
            oldMatrix.set(i, craftMatrix.get(i).copy());
        }
    }
}

