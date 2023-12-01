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
import java.util.ArrayList;
import java.util.List;

public class TileEntityWorkbench extends TileEntity implements ITickable {
    public NonNullList<ItemStack> craftMatrix;
    private final List<ContainerWorkbench> containers;
    private boolean needsClear;

    public TileEntityWorkbench() {
        craftMatrix = NonNullList.withSize(9, ItemStack.EMPTY);
        containers = new ArrayList<>();
        needsClear = false;
    }

    public TileEntityWorkbench addContainer(@NotNull ContainerWorkbench container) {
        containers.add(container);
        return this;
    }

    @Nullable
    public ContainerWorkbench getContainer() {
        return containers.get(0);
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
            if (craftMatrix.get(i).isEmpty()) {
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

        ensureCraftMatrixCapacity(capacity);

        for (int i = 0; i < 9; ++i) {
            if (!nbt.hasKey("Slot" + i, 10)) {
                craftMatrix.set(i, ItemStack.EMPTY);
            } else {
                craftMatrix.set(i, new ItemStack(nbt.getCompoundTag("Slot" + i)));
            }
        }
    }

    public boolean hasMatrixChanged(List<ItemStack> oldMatrix, List<ItemStack> newMatrix) {
        if (oldMatrix.size() != newMatrix.size()) return true;
        for (int i = 0; i < oldMatrix.size(); i++)
            if (!ItemStack.areItemStacksEqual(oldMatrix.get(i), newMatrix.get(i)))
                return true;
        return false;
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
            updateAllOldMatrix();
        }
    }

    public void updateAllOldMatrix() {
        for (var container : containers) {
            if (container == null) {
                containers.remove(null);
                continue;
            }
            ((TileContainerWorkbench) container).updateOldMatrix(craftMatrix);
        }
    }

    @Override
    public void update() {
        boolean anyChange = false;
        if (needsClear) craft();
        for (var container : containers) {
            var tileContainer = (TileContainerWorkbench) container;
            if (container == null) {
                containers.remove(null);
                continue;
            }

            if (!hasMatrixChanged(tileContainer.getOldMatrix(), craftMatrix) &&
                    !((InventoryWorkbench) container.craftMatrix).needsUpdate()) continue;

            tileContainer.updateResult();
            anyChange = true;
            ((InventoryWorkbench) container.craftMatrix).setNoUpdate();
        }
        if (anyChange) markDirty();
    }

    public void craft() {
        markDirty();
        if (world.isRemote) {
            needsClear = true;
            return;
        }
        for (var container : containers) {
            if (container == null) {
                containers.remove(null);
                continue;
            }

            ((TileContainerWorkbench) container).clearResult();
        }
    }
}

