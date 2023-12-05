package com.nomiceu.realbench.logic;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class InventoryWorkbench extends InventoryCrafting {
    private final ContainerWorkbench container;
    private final TileEntityWorkbench tile;
    private final int width, height;

    public InventoryWorkbench(ContainerWorkbench container, int width, int height) {
        super(container, width, height);
        this.container = container;
        this.width = width;
        this.height = height;
        tile = ((TileContainerWorkbench) container).getTile();
        tile.ensureCraftMatrixCapacity(width * height);
    }

    @Override
    public int getSizeInventory() {
        return tile.craftMatrix.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : tile.getCraftMatrixSafe())
        {
            if (!itemstack.isEmpty())
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int index) {
        return index >= getSizeInventory() ? ItemStack.EMPTY : tile.getCraftMatrixSafe().get(index);
    }

    @Override
    public @NotNull ItemStack getStackInRowAndColumn(int row, int column) {
        return row >= 0 && row < width && column >= 0 && column <= height ? getStackInSlot(row + column * width) : ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(tile.craftMatrix, index);
    }

    @Override
    public void markDirty() {
        tile.markDirty();
    }

    @Override
    public void setInventorySlotContents(int index, @Nullable ItemStack stack) {
        if (stack == null) stack = ItemStack.EMPTY;
        tile.craftMatrix.set(index, stack);
        ((TileContainerWorkbench) container).updateResult();
        markDirty();
    }

    @MethodsReturnNonnullByDefault
    public @NotNull ItemStack decrStackSize(int index, int count) {
        ItemStack itemstack = ItemStackHelper.getAndSplit(tile.craftMatrix, index, count);

        if (!itemstack.isEmpty()) {
            ((TileContainerWorkbench) container).updateResult();
        }

        markDirty();

        return itemstack;
    }

    @Override
    public void fillStackedContents(@NotNull RecipeItemHelper helper) {
        if (tile == null) return;
        for (ItemStack itemstack : tile.getCraftMatrixSafe())
        {
            helper.accountStack(itemstack);
        }
        markDirty();
    }

    @Override
    public void clear() {
        tile.craftMatrix.clear();
        markDirty();
    }
}
