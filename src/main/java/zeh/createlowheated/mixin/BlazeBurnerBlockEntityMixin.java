package zeh.createlowheated.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlazeBurnerBlockEntity.class, remap = false)
public abstract class BlazeBurnerBlockEntityMixin {
    @Inject(method = "applyCreativeFuel", at = @At(value = "INVOKE",
            target = "Lcom/simibubi/create/content/processing/burner/BlazeBurnerBlockEntity;setBlockHeat(Lcom/simibubi/create/content/processing/burner/BlazeBurnerBlock$HeatLevel;)V"),
            cancellable = true
    )
    private void applyCreativeFuelMixin(CallbackInfo ci, @Local(name = "next") HeatLevel next) {
        if (next == HeatLevel.valueOf("LOW")) {
            next = next.nextActiveLevel();
            setBlockHeat(next);
            ci.cancel();
        }
    }

    @Shadow
    protected abstract void setBlockHeat(HeatLevel heat);
}
