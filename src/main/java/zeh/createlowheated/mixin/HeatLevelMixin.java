package zeh.createlowheated.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.Arrays;

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

    @ModifyReturnValue(method = "nextActiveLevel", at = @At("RETURN"))
    private HeatLevel nextActiveLevelMixin(HeatLevel original) {
        if (this.equals(HeatLevel.NONE)) return LOW;
        if (this.equals(LOW)) return HeatLevel.SMOULDERING;
        if (this.equals(HeatLevel.SEETHING)) return LOW;
        return original;
    }

    @ModifyReturnValue(method = "isAtLeast", at = @At("RETURN"))
    private boolean isAtLeastMixin(boolean original, @Local(name = "heatLevel") HeatLevel heatLevel) {
        if (heatLevel.equals(HeatLevel.NONE)) return true;
        if (heatLevel.equals(LOW)) return !this.equals(HeatLevel.NONE);
        if (this.equals(LOW)) return heatLevel.equals(LOW);
        return original;
    }

}
