package zeh.createlowheated.mixin;

import dev.latvian.mods.kubejs.recipe.RecipeJS;
import org.spongepowered.asm.mixin.Mixin;
import dev.latvian.mods.kubejs.create.ProcessingRecipeSchema;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = ProcessingRecipeSchema.ProcessingRecipeJS.class, remap = false)
public abstract class ProcessingRecipeSchemaMixin extends RecipeJS {
    @Unique
    public RecipeJS lowheated() {
        return setValue(ProcessingRecipeSchema.HEAT_REQUIREMENT, "lowheated");
    }
}