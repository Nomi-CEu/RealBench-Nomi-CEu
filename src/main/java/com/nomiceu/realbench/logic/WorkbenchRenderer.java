package com.nomiceu.realbench.logic;

import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class WorkbenchRenderer extends TileEntitySpecialRenderer<TileEntityWorkbench> {
    Map<TileEntityWorkbench, RenderingState> states = new WeakHashMap<>();
    static final int ANIMATION_DURATION = 1000;

    public WorkbenchRenderer() {
    }

    @Override
    public void render(@NotNull TileEntityWorkbench tile, double xOffset, double yOffset, double zOffset, float partialTicks, int destroyStage, float alpha) {
        super.render(tile, xOffset, yOffset, zOffset, partialTicks, destroyStage, alpha);

        for (int i = 0; i < tile.getCraftMatrixSafe().size(); i++) {
            ItemStack itemStack = tile.getCraftMatrixSafe().get(i);
            if (!itemStack.isEmpty()) {
                RenderingState state = states.computeIfAbsent(tile, (k) -> new RenderingState());
                double playerAngle = (Math.atan2(xOffset + 0.5, zOffset + 0.5) + 3.9269908169872414) % 6.283185307179586;
                byte sector = (byte) ((playerAngle * 2.0 / Math.PI));
                long time = System.currentTimeMillis();
                float shift;
                if (state.sector != sector) {
                    state.animating = true;
                    state.animationAngleStart = state.currentAngle;
                    float delta1 = (float) sector * 90.0F - state.currentAngle;
                    float abs1 = Math.abs(delta1);
                    float delta2 = delta1 + 360.0F;
                    shift = Math.abs(delta2);
                    float delta3 = delta1 - 360.0F;
                    float abs3 = Math.abs(delta3);
                    if (abs3 < abs1 && abs3 < shift) {
                        state.animationAngleEnd = delta3 + state.currentAngle;
                    } else if (shift < abs1 && shift < abs3) {
                        state.animationAngleEnd = delta2 + state.currentAngle;
                    } else {
                        state.animationAngleEnd = delta1 + state.currentAngle;
                    }

                    state.startTime = time;
                    state.sector = sector;
                }

                if (state.animating) {
                    if (time >= state.startTime + 1000L) {
                        state.animating = false;
                        state.currentAngle = (state.animationAngleEnd + 360.0F) % 360.0F;
                    } else {
                        state.currentAngle = (easeOutQuad(time - state.startTime, state.animationAngleStart, state.animationAngleEnd - state.animationAngleStart) + 360.0F) % 360.0F;
                    }
                }

                Item item = itemStack.getItem();
                Block block;
                if (item instanceof ItemBlock itemBlock) {
                    block = itemBlock.getBlock();
                } else if (item instanceof ItemBlockSpecial itemSpecial) {
                    block = itemSpecial.getBlock();
                } else {
                    block = null;
                }

                boolean normalBlock = block != null && block.getDefaultState().getMaterial().isSolid();
                shift = (float) Math.abs((time + (i * 1000L)) % 5000L - 2500L) / 200000.0F;
                GlStateManager.pushMatrix();
                GlStateManager.translate(xOffset + 0.5, yOffset + (double) shift, zOffset + 0.5);
                GlStateManager.rotate(state.currentAngle, 0.0F, 1.0F, 0.0F);
                GlStateManager.translate((double) (i % 3) * 3.0 / 16.0 + 0.3125 - 0.5, 1.09375, (double) (i / 3) * 3.0 / 16.0 + 0.3125 - 0.5);
                if (!normalBlock) {
                    GlStateManager.rotate(-rendererDispatcher.entityPitch, 1.0F, 0.0F, 0.0F);
                }

                GlStateManager.scale(0.125F, 0.125F, 0.125F);
                Minecraft.getMinecraft().getRenderItem().renderItem(itemStack, TransformType.NONE);
                GlStateManager.popMatrix();
            }
        }

    }

    private static float easeOutQuad(long t, float b, float c) {
        float z = (float) t / (float) ANIMATION_DURATION;
        return -c * z * (z - 2.0F) + b;
    }

    static class RenderingState {
        byte sector;
        float currentAngle;
        boolean animating;
        float animationAngleStart;
        float animationAngleEnd;
        long startTime;

        RenderingState() {
        }
    }
}
