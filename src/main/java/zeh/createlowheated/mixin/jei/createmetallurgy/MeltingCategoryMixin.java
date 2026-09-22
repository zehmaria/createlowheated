package zeh.createlowheated.mixin.jei.createmetallurgy;

import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedBlazeBurner;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import fr.lucreeper74.createmetallurgy.compat.jei.category.FoundryBasinCategory;
import fr.lucreeper74.createmetallurgy.compat.jei.category.MeltingCategory;
import fr.lucreeper74.createmetallurgy.content.blocks.foundry_basin.FoundryBasinRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import zeh.createlowheated.compat.jei.AnimatedBasicBurner;

@Mixin(value = MeltingCategory.class, remap = false)
public abstract class MeltingCategoryMixin extends FoundryBasinCategory {

    @Unique private final AnimatedBasicBurner createLowHeated$basic = new AnimatedBasicBurner();
    public MeltingCategoryMixin(CreateRecipeCategory.Info<FoundryBasinRecipe> info) { super(info, true); }

    @Redirect(
            method = "draw(Lfr/lucreeper74/createmetallurgy/content/blocks/foundry_basin/FoundryBasinRecipe;Lmezz/jei/api/gui/ingredient/IRecipeSlotsView;Lnet/minecraft/client/gui/GuiGraphics;DD)V",
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
