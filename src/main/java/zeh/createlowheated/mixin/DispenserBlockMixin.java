package zeh.createlowheated.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import zeh.createlowheated.AllBlocks;
import zeh.createlowheated.common.Configuration;
import zeh.createlowheated.content.processing.basicburner.BasicBurnerDispenseBehavior;

@Mixin(DispenserBlock.class)
public abstract class DispenserBlockMixin {
    @Shadow
    protected abstract DispenseItemBehavior getDispenseMethod(Level level, ItemStack stack);

    @Inject(
            method = "dispenseFrom",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/DispenserBlock;getDispenseMethod(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/core/dispenser/DispenseItemBehavior;"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    public void basicBurnerDispenseFromInject(ServerLevel level, BlockState state, BlockPos pos, CallbackInfo ci,
                                              DispenserBlockEntity dispenser, BlockSource source, int slot, ItemStack stack) {
        BlockState facingState = level.getBlockState(pos.relative(state.getValue(DispenserBlock.FACING)));
        if (Configuration.DISPENSER_BURNER.get() && facingState.is(AllBlocks.BASIC_BURNER.get())) {
            dispenser.setItem(slot, BasicBurnerDispenseBehavior.INSTANCE.dispense(source, stack, getDispenseMethod(level, stack)));
            ci.cancel();
        }
    }
}