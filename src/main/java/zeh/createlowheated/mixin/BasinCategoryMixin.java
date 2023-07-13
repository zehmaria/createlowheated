package zeh.createlowheated.mixin;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import mezz.jei.api.recipe.RecipeIngredientRole;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import zeh.createlowheated.CreateLowHeated;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;

@Mixin(value = BasinCategory.class, remap = false)
public abstract class BasinCategoryMixin {

    @Inject(method = "setRecipe(Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;Lcom/simibubi/create/content/processing/basin/BasinRecipe;Lmezz/jei/api/recipe/IFocusGroup;)V",
            at = @At(value = "INVOKE_ASSIGN",
                    target = "Lcom/simibubi/create/content/processing/basin/BasinRecipe;getRequiredHeat()Lcom/simibubi/create/content/processing/recipe/HeatCondition;",
                    ordinal = 0),
            cancellable = true
    )
    private void onSetBurnerType(IRecipeLayoutBuilder builder, BasinRecipe recipe, IFocusGroup focuses, CallbackInfo ci) {
        HeatCondition requiredHeat = recipe.getRequiredHeat();
        if (!requiredHeat.testBlazeBurner(HeatLevel.NONE)) {
            if (requiredHeat.testBlazeBurner(HeatLevel.valueOf("CHARCOAL"))) {
                builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 134, 81).addItemStack(zeh.createlowheated.AllBlocks.CHARCOAL_BURNER.asStack());
            } else {
                builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 134, 81).addItemStack(AllBlocks.BLAZE_BURNER.asStack());
                if (!requiredHeat.testBlazeBurner(HeatLevel.KINDLED)) {
                    builder.addSlot(RecipeIngredientRole.CATALYST, 153, 81).addItemStack(AllItems.BLAZE_CAKE.asStack());
                }
            }
        }
        ci.cancel();
    }
}