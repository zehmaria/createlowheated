package zeh.createlowheated.mixin;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

@Mixin(value = HeatLevel.class, remap = false)
public abstract class HeatLevelMixin {

    @Shadow
    @Final
    @Mutable
    private static HeatLevel[] $VALUES;

    @Unique
    private static final HeatLevel LOW = heatExpansion$addVariant("LOW");

    @Invoker("<init>")
    public static HeatLevel heatExpansion$invokeInit(String internalName, int internalId) {
        throw new AssertionError();
    }

    @Unique
    private static HeatLevel heatExpansion$addVariant(String internalName) {
        ArrayList<HeatLevel> variants = new ArrayList<>(Arrays.asList(HeatLevelMixin.$VALUES));
        HeatLevel heat = heatExpansion$invokeInit(internalName, variants.getLast().ordinal() + 1);
        variants.add(heat);
        HeatLevelMixin.$VALUES = variants.toArray(new HeatLevel[0]);
        return heat;
    }

    @Inject(method = "nextActiveLevel", at = @At("RETURN"), cancellable = true)
    public void nextActiveLevel(CallbackInfoReturnable<HeatLevel> cir) {
        if (this.equals(HeatLevel.NONE)) {
            cir.setReturnValue(LOW);
            return;
        }
        if (this.equals(LOW)) {
            cir.setReturnValue(HeatLevel.SMOULDERING);
            return;
        }
        if (this.equals(HeatLevel.SEETHING)) {
            cir.setReturnValue(LOW);
        }
    }

    @Inject(method = "isAtLeast", at = @At("RETURN"), cancellable = true)
    public void isAtLeast(HeatLevel heatLevel, CallbackInfoReturnable<Boolean> cir) {
        if (heatLevel.equals(HeatLevel.NONE)) {
            cir.setReturnValue(true);
            return;
        }
        if (heatLevel.equals(LOW)) {
            if (this.equals(HeatLevel.NONE)) {
                cir.setReturnValue(false);
            } else {
                cir.setReturnValue(true);
            }
            return;
        }
        if (this.equals(LOW)) {
            cir.setReturnValue(heatLevel.equals(LOW));
        }
    }

}
