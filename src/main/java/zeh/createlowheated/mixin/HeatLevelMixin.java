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

@Mixin(value = HeatLevel.class, remap = false)
public abstract class HeatLevelMixin {
    @Shadow
    @Final
    @Mutable
    private static HeatLevel[] $VALUES;

    @Shadow public abstract boolean isAtLeast(HeatLevel heatLevel);

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

    @Inject(method = "isAtLeast", at = @At("HEAD"), cancellable = true)
    protected void hardCodedLow(HeatLevel heatLevel, CallbackInfoReturnable<Boolean> cir) {
        if (heatLevel.getSerializedName() == "low") { cir.setReturnValue(this.isAtLeast(HeatLevel.FADING)); }
    }

}
