package com.nomiceu.realbench.mixin;

import com.nomiceu.realbench.logic.TileEntityWorkbench;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = BlockWorkbench.class)
public class BlockWorkbenchMixin extends Block implements ITileEntityProvider {
    /**
     * Mandatory default ignore constructor
     */
    public BlockWorkbenchMixin(Material blockMaterialIn, MapColor blockMapColorIn) {
        super(blockMaterialIn, blockMapColorIn);
    }

    @Nullable
    @Override
    @Unique
    public TileEntity createTileEntity(@NotNull World world, @NotNull IBlockState state) {
        return createNewTileEntity(world, getMetaFromState(state));
    }


    @Nullable
    @Override
    @Unique
    public TileEntity createNewTileEntity(@NotNull World worldIn, int meta) {
        return new TileEntityWorkbench();
    }
}
