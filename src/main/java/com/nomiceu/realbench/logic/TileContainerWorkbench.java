package com.nomiceu.realbench.logic;

import net.minecraft.item.ItemStack;

import java.util.List;

public interface TileContainerWorkbench {
    TileEntityWorkbench getTile();

    void updateResult();

    List<ItemStack> getOldMatrix();

    void updateOldMatrix(List<ItemStack> matrix);

    void clearResult();
}
