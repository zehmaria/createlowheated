package zeh.createlowheated.mixin;

import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.neoforged.neoforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;

import static com.simibubi.create.compat.jei.category.CreateRecipeCategory.*;

@Mixin(value = BasinCategory.class, remap = false)
public abstract class BasinCategoryMixin {
    @Inject(
            method = "Lcom/simibubi/create/compat/jei/category/BasinCategory;setRecipe(Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;Lcom/simibubi/create/content/processing/basin/BasinRecipe;Lmezz/jei/api/recipe/IFocusGroup;)V",
            at = @At(
                    value = "INVOKE",
                    ordinal = 0,
                    target = "Lcom/simibubi/create/content/processing/basin/BasinRecipe;getRollableResults()Ljava/util/List;"),
            cancellable = true
    )
    private void onSetBurnerType(IRecipeLayoutBuilder builder, BasinRecipe recipe, IFocusGroup focuses, CallbackInfo ci) {
        if (recipe.getRequiredHeat().name().equals("LOWHEATED")) {

            int size = recipe.getRollableResults().size() + recipe.getFluidResults().size();
            int i = 0;

            for (ProcessingOutput result : recipe.getRollableResults()) {
                int xPosition = 142 - (size % 2 != 0 && i == size - 1 ? 0 : i % 2 == 0 ? 10 : -9);
                int yPosition = -19 * (i / 2) + 51;

                builder
                        .addSlot(RecipeIngredientRole.OUTPUT, xPosition, yPosition)
                        .setBackground(getRenderedSlot(result), -1, -1)
                        .addItemStack(result.getStack())
                        .addRichTooltipCallback(addStochasticTooltip(result));
                i++;
            }

            for (FluidStack fluidResult : recipe.getFluidResults()) {
                int xPosition = 142 - (size % 2 != 0 && i == size - 1 ? 0 : i % 2 == 0 ? 10 : -9);
                int yPosition = -19 * (i / 2) + 51;

                builder
                        .addSlot(RecipeIngredientRole.OUTPUT, xPosition, yPosition)
                        .setBackground(getRenderedSlot(), -1, -1)
                        .addIngredient(NeoForgeTypes.FLUID_STACK, withImprovedVisibility(fluidResult))
                        .addRichTooltipCallback(addFluidTooltip(fluidResult.getAmount()));
                i++;
            }

            builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 134, 81)
                    .addItemStack(zeh.createlowheated.AllBlocks.BASIC_BURNER.asStack());
            ci.cancel();
        }
    }
}