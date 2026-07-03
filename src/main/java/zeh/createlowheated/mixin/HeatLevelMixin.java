package zeh.createlowheated.mixin;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
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

    private static final HeatLevel LOW = heatExpansion$addVariant("LOW");

    @Invoker("<init>")
    public static HeatLevel heatExpansion$invokeInit(String internalName, int internalId) {
        throw new AssertionError();
    }

    private static HeatLevel heatExpansion$addVariant(String internalName) {
        ArrayList<HeatLevel> variants = new ArrayList<>(Arrays.asList(HeatLevelMixin.$VALUES));
        HeatLevel heat = heatExpansion$invokeInit(internalName, variants.get(variants.size() - 1).ordinal() + 1);
        variants.add(heat);
        HeatLevelMixin.$VALUES = variants.toArray(new HeatLevel[0]);
        return heat;
    }

    //Since only nextActiveLevel used this no mixin is used
    /*
    @Inject(method = "byIndex", at = @At("RETURN"))
    private static void byIndex(int index, CallbackInfoReturnable<HeatLevel> cir) {
    }
     */

    @Inject(method = "nextActiveLevel", at = @At("RETURN"), cancellable = true)
    public void nextActiveLevel(CallbackInfoReturnable<HeatLevel> cir) {
        if (this.getSerializedName().equals("NONE".toLowerCase(Locale.ROOT))) {
            cir.setReturnValue(HeatLevelMixin.LOW);
            return;
        }
        if (this.getSerializedName().equals("LOW".toLowerCase(Locale.ROOT))) {
            cir.setReturnValue(HeatLevel.SMOULDERING);
            return;
        }
        if (this.getSerializedName().equals("SEETHING".toLowerCase(Locale.ROOT))) {
            cir.setReturnValue(HeatLevelMixin.LOW); //Next ACTIVE HeatLevel
        }
    }

    @Inject(method = "isAtLeast", at = @At("RETURN"), cancellable = true)
    public void isAtLeast(HeatLevel heatLevel, CallbackInfoReturnable<Boolean> cir) {
        if (heatLevel.equals(HeatLevel.NONE)) {
            cir.setReturnValue(true);
            return;
        }
        if (heatLevel.equals(HeatLevelMixin.LOW)) {
            if (this.equals(HeatLevel.NONE)) {
                cir.setReturnValue(false);
            } else {
                cir.setReturnValue(true);
            }
            return;
        }
        if (this.equals(HeatLevelMixin.LOW)) {
            if (heatLevel.equals(HeatLevelMixin.LOW /*|| heatLevel.equals(HeatLevel.NONE)*/ )) {
                cir.setReturnValue(true);
            } else {
                cir.setReturnValue(false);
            }
        }
    }

    @Shadow
    public abstract String getSerializedName();

}
