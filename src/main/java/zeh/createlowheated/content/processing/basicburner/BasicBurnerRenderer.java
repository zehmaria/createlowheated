package zeh.createlowheated.content.processing.basicburner;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;

import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;

import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class BasicBurnerRenderer extends SafeBlockEntityRenderer<BasicBurnerBlockEntity> {
    public BasicBurnerRenderer(BlockEntityRendererProvider.Context context) {}
    @Override
    protected void renderSafe(BasicBurnerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        IItemHandler inv = be.capability.orElse(new ItemStackHandler());
        ItemStack stack = inv.getStackInSlot(0);
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        RandomSource r = RandomSource.create(be.getBlockPos().hashCode());

        ms.pushPose();
        ms.translate(.5f, .29f, .5f);
        ms.scale(.5f, .5f, .5f);

        for (int i = 0; i <= stack.getCount() / 8; i++) {
            ms.pushPose();
            Vec3 vec = VecHelper.offsetRandomly(Vec3.ZERO, (RandomSource) r, 1 / 8f);
            ms.translate(vec.x, Math.abs(i * vec.y/3), vec.z);
            TransformStack.cast(ms)
                    .rotateY(35 + (vec.x + vec.z) / (2 / 8f) * 20)
                    .rotateX(65 + (vec.y + vec.z) / (2 / 8f) * 20);
            itemRenderer.renderStatic(stack, TransformType.FIXED, light, overlay, ms, buffer, 0);
            ms.popPose();
        }
        ms.popPose();

        if (be.remainingBurnTime == 0) return;

        ms.pushPose();
        stack = Items.COAL_BLOCK.getDefaultInstance();

        ms.translate(.5f, .19f, .5f);
        ms.scale(1f, .02f, 1f);

        Vec3 itemPosition = VecHelper.rotate(new Vec3(0, 0, 0), 90f, Direction.Axis.Y);
        ms.translate(itemPosition.x, itemPosition.y, itemPosition.z);
        TransformStack.cast(ms).rotateY(90f).rotateX(0f);

        itemRenderer.renderStatic(stack, ItemTransforms.TransformType.FIXED, light, overlay, ms, buffer, 0);
        ms.popPose();
    }
}
