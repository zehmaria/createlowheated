package zeh.createlowheated.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSourceImpl;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zeh.createlowheated.AllBlocks;
import zeh.createlowheated.common.Configuration;
import zeh.createlowheated.content.processing.basicburner.BasicBurnerDispenseBehavior;

@Mixin(DispenserBlock.class)
public abstract class DispenserBlockMixin {
    @Shadow
    protected abstract DispenseItemBehavior getDispenseMethod(ItemStack stack);

    @Inject(
            method = "dispenseFrom",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/DispenserBlock;getDispenseMethod(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/core/dispenser/DispenseItemBehavior;"
            ),
            cancellable = true
    )
    public void basicBurnerDispenseFromInject(ServerLevel level, BlockPos pos, CallbackInfo ci,
                                              @Local BlockSourceImpl source, @Local DispenserBlockEntity dispenser, @Local int slot, @Local ItemStack stack) {
        BlockState facingState = level.getBlockState(pos.relative(source.getBlockState().getValue(DispenserBlock.FACING)));
        if (Configuration.DISPENSER_BURNER.get() && facingState.is(AllBlocks.BASIC_BURNER.get())) {
            dispenser.setItem(slot, BasicBurnerDispenseBehavior.INSTANCE.dispense(source, stack, getDispenseMethod(stack)));
            ci.cancel();
        }
    }
}
