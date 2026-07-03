package zeh.createlowheated.mixin;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlazeBurnerBlock.class, remap = false)
public class BlazeBurnerBlockMixin {
    @Inject(method = "getLight", at = @At("RETURN"), cancellable = true)
    private static void getLightLowHeated(BlockState state, CallbackInfoReturnable<Integer> cir) {
        BlazeBurnerBlock.HeatLevel level = state.getValue(BlazeBurnerBlock.HEAT_LEVEL);
        if (level.name().equals("LOW")) {
            cir.setReturnValue(8);
        }
    }
}
