package com.nomiceu.realbench.logic;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.List;

public interface TileContainerWorkbench {
    TileEntityWorkbench getTile();

    void updateResult();

    List<ItemStack> getOldMatrix();

    void updateOldMatrix(List<ItemStack> matrix);

    void clearResult();

    void addSlot(Slot slot);
}
