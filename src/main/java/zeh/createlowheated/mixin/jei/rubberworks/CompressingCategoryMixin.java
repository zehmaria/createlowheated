package zeh.createlowheated.mixin.jei.rubberworks;

import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedBlazeBurner;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import io.github.hadron13.rubberworks.compat.jei.category.CompressingCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import zeh.createlowheated.compat.jei.AnimatedBasicBurner;

@Mixin(value = CompressingCategory.class, remap = false)
public abstract class CompressingCategoryMixin extends CreateRecipeCategory<BasinRecipe> {

    @Unique private final AnimatedBasicBurner createLowHeated$basic = new AnimatedBasicBurner();
    public CompressingCategoryMixin(Info<BasinRecipe> info) {super(info);}

    @Redirect(
            method = "draw(Lio/github/hadron13/rubberworks/blocks/compressor/CompressingRecipe;Lmezz/jei/api/gui/ingredient/IRecipeSlotsView;Lnet/minecraft/client/gui/GuiGraphics;DD)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/compat/jei/category/animations/AnimatedBlazeBurner;withHeat(Lcom/simibubi/create/content/processing/burner/BlazeBurnerBlock$HeatLevel;)Lcom/simibubi/create/compat/jei/category/animations/AnimatedBlazeBurner;"
            ),
            remap = false
    )
    private AnimatedBlazeBurner drawMixin(AnimatedBlazeBurner instance, BlazeBurnerBlock.HeatLevel heatLevel) {
        if (heatLevel == BlazeBurnerBlock.HeatLevel.valueOf("LOW")) return createLowHeated$basic.withHeat(heatLevel);
        else return instance.withHeat(heatLevel);
    }
}
