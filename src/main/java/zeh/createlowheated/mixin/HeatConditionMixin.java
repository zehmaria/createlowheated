package zeh.createlowheated.mixin;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;

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

    private static final HeatCondition LOWHEATED = heatExpansion$addVariant("LOWHEATED",  0xED9C33);

    @Invoker("<init>")
    public static HeatCondition heatExpansion$invokeInit(String internalName, int internalId, int color) {
        throw new AssertionError();
    }

    private static HeatCondition heatExpansion$addVariant(String internalName, int color) {
        ArrayList<HeatCondition> variants = new ArrayList<>(Arrays.asList(HeatConditionMixin.$VALUES));
        HeatCondition heat = heatExpansion$invokeInit(internalName, variants.get(variants.size() - 1).ordinal() + 1, color);
        variants.add(heat);
        HeatConditionMixin.$VALUES = variants.toArray(new HeatCondition[0]);
        return heat;
    }

    /**
     * @author zeh_maria
     * @reason Creating a new heatRequirement (lowheated).
     */
    @Overwrite
    public boolean testBlazeBurner(HeatLevel level) {
        if (this.equals(LOWHEATED)) return level.isAtLeast(HeatLevel.FADING);
        if (this.equals(HeatCondition.SUPERHEATED)) return level == HeatLevel.SEETHING;
        if (this.equals(HeatCondition.HEATED)) {
            return level == HeatLevel.KINDLED || level == HeatLevel.FADING || level == HeatLevel.SEETHING;
        }
        return true;
    }

    /**
     * @author zeh_maria
     * @reason Creating a new heatRequirement (lowheated).
     */
    @Overwrite
    public HeatLevel visualizeAsBlazeBurner() {
        if (this.equals(LOWHEATED)) return HeatLevel.valueOf("CHARCOAL");
        if (this.equals(HeatCondition.SUPERHEATED)) return HeatLevel.SEETHING;
        if (this.equals(HeatCondition.HEATED)) return HeatLevel.KINDLED;
        return HeatLevel.NONE;
    }

}
