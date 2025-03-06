package zeh.createlowheated.mixin;

import com.mojang.serialization.Codec;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;

/*
 * Thanks to https://github.com/SpongePowered/Mixin/issues/387#issuecomment-888408556 for the tip on how to mixin enum values!
 */
@Mixin(value = HeatCondition.class, remap = false)
public abstract class HeatConditionMixin implements StringRepresentable {

    @Shadow
    @Final
    @Mutable
    private static HeatCondition[] $VALUES;

    @Shadow public abstract String getTranslationKey();

    @Mutable @Shadow @Final public static Codec<HeatCondition> CODEC;
    @Mutable @Shadow @Final public static StreamCodec<ByteBuf, HeatCondition> STREAM_CODEC;
    @Unique
    private static HeatCondition LOWHEATED = heatExpansion$addVariant("LOWHEATED",  0xED9C33);

    @Invoker("<init>")
    public static HeatCondition heatExpansion$invokeInit(String internalName, int internalId, int color) {
        throw new AssertionError();
    }

    @Unique
    private static HeatCondition heatExpansion$addVariant(String internalName, int color) {
        ArrayList<HeatCondition> variants = new ArrayList<>(Arrays.asList(HeatConditionMixin.$VALUES));
        HeatCondition heat = heatExpansion$invokeInit(internalName, variants.get(variants.size() - 1).ordinal() + 1, color);
        variants.add(heat);
        HeatConditionMixin.$VALUES = variants.toArray(new HeatCondition[0]);
        CODEC = StringRepresentable.fromEnum(HeatCondition::values);
        STREAM_CODEC = CatnipStreamCodecBuilders.ofEnum(HeatCondition.class);
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
