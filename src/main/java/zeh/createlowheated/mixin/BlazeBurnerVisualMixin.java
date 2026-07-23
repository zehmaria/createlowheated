package zeh.createlowheated.mixin;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerVisual;
import com.simibubi.create.content.processing.burner.ScrollInstance;
import net.createmod.catnip.render.SpriteShiftEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;
import java.util.Locale;

@Mixin(value = BlazeBurnerVisual.class, remap = false)
public abstract class BlazeBurnerVisualMixin {

    @Shadow
    @Nullable
    private ScrollInstance flame;

    @Shadow
    private BlazeBurnerBlock.HeatLevel heatLevel;

    @Inject(
            method = {"setupFlameInstance"},
            at = @At(value = "INVOKE_ASSIGN", target = "Lcom/simibubi/create/content/processing/burner/BlazeBurnerBlock$HeatLevel;ordinal()I"),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    //Smouldering doesn't have a flame ring animation so this doesn't matter a lot
    public void setupFlameInstanceMixin(CallbackInfo ci, SpriteShiftEntry spriteShift, float spriteWidth, float spriteHeight) {
        if (heatLevel.getSerializedName().equals("LOW".toLowerCase(Locale.ROOT))) {
            float speed = 1 / 32f + 1 / 64f * BlazeBurnerBlock.HeatLevel.SMOULDERING.ordinal();
            flame.speedU = speed / 2;
            flame.speedV = speed;

            flame.scaleU = spriteWidth / 2;
            flame.scaleV = spriteHeight / 2;

            flame.diffU = spriteShift.getTarget().getU0() - spriteShift.getOriginal().getU0();
            flame.diffV = spriteShift.getTarget().getV0() - spriteShift.getOriginal().getV0();

            ci.cancel();
        }
    }
}
