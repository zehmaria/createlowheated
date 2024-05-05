package zeh.createlowheated.mixin;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zeh.createlowheated.CreateLowHeated;

import java.util.ArrayList;
import java.util.Arrays;

/*
 * Thanks to https://github.com/SpongePowered/Mixin/issues/387#issuecomment-888408556 for the tip on how to mixin enum values!
 */
@Mixin(value = HeatCondition.class, remap = false)
public abstract class HeatConditionMixin {
    @Shadow
    @Final
    @Mutable
    private static HeatCondition[] $VALUES;

    @Shadow public abstract int getColor();

    @Shadow public abstract String serialize();

    @Shadow @Final public static HeatCondition SUPERHEATED;
    @Unique
    private static final HeatCondition LOWHEATED = heatExpansion$addVariant("LOWHEATED",  0xED9C33);

    @Invoker("<init>")
    public static HeatCondition heatExpansion$invokeInit(String internalName, int internalId, int color) {
        throw new AssertionError();
    }

    @Unique
    private static HeatCondition heatExpansion$addVariant(String internalName, int color) {
        ArrayList<HeatCondition> variants = new ArrayList<>(Arrays.asList(HeatConditionMixin.$VALUES));
        HeatCondition heat = heatExpansion$invokeInit(internalName, variants.get(variants.size() - 1).ordinal() + 1, color);
        CreateLowHeated.LOGGER.info("CREATELOWHEATED" + variants.size());
        variants.add(heat);
        HeatConditionMixin.$VALUES = variants.toArray(new HeatCondition[0]);
        return heat;
    }

    @Inject(method = "testBlazeBurner", at = @At("HEAD"), cancellable = true)
    private void testBlazeBurnerMixin(HeatLevel level, CallbackInfoReturnable<Boolean> cir) {
        if (this.equals(HeatCondition.SUPERHEATED)) {
            cir.setReturnValue(level == HeatLevel.SEETHING);
            return;
        }

        if (this.equals(HeatCondition.HEATED)) {
            cir.setReturnValue(level == HeatLevel.FADING || level == HeatLevel.KINDLED || level == HeatLevel.SEETHING);
            return;
        }

        if (this.equals(LOWHEATED)) {
            cir.setReturnValue(level == HeatLevel.valueOf("LOW") || level == HeatLevel.FADING ||
                    level == HeatLevel.KINDLED || level == HeatLevel.SEETHING);
        }
    }

    @Inject(method = "visualizeAsBlazeBurner", at = @At("HEAD"), cancellable = true)
    private void visualizeAsBlazeBurnerMixin(CallbackInfoReturnable<HeatLevel> cir) {
        if (this.equals(LOWHEATED)) cir.setReturnValue(HeatLevel.valueOf("LOW"));
    }

}
