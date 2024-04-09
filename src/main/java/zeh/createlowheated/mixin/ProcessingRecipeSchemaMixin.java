package zeh.createlowheated.mixin;

import org.spongepowered.asm.mixin.Mixin;
import dev.latvian.mods.kubejs.create.ProcessingRecipeJS;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = ProcessingRecipeJS.class, remap = false)
public abstract class ProcessingRecipeSchemaMixin {
    @Shadow public abstract ProcessingRecipeJS heatRequirement(String req);

    public ProcessingRecipeJS lowheated() {
        return heatRequirement("lowheated");
    }
}
