package zeh.createlowheated.content.processing.charcoal;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CharcoalBurnerRenderer extends SafeBlockEntityRenderer<CharcoalBurnerBlockEntity> {
    public CharcoalBurnerRenderer(BlockEntityRendererProvider.Context context) {}
    @Override
    protected void renderSafe(CharcoalBurnerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        if (be.remainingBurnTime == 0) return;

        ItemStack stack = Items.COAL_BLOCK.getDefaultInstance();
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        float fuel = (float) be.remainingBurnTime / be.MAX_HEAT_CAPACITY;
        fuel = fuel > 1 ? 1 : fuel;
        ms.translate(.5f, 3f / 16f + fuel / 4f, .5f);
        ms.scale(1f, (float) (1 * fuel), 1f);

        ms.mulPose(Vector3f.YP.rotationDegrees(90));
        ms.mulPose(Vector3f.XP.rotationDegrees(90));
        itemRenderer.renderStatic(stack, ItemTransforms.TransformType.FIXED, light, overlay, ms, buffer, 0);
    }
}
