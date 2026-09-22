package zeh.createlowheated.mixin.jei.createmetallurgy;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.tterrag.registrate.util.entry.BlockEntry;
import fr.lucreeper74.createmetallurgy.compat.jei.category.FoundryBasinCategory;
import fr.lucreeper74.createmetallurgy.content.blocks.foundry_basin.FoundryBasinRecipe;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import zeh.createlowheated.AllBlocks;

@Mixin(value = FoundryBasinCategory.class, remap = false)

public abstract class FoundryBasinCategoryMixin extends CreateRecipeCategory<FoundryBasinRecipe> {

    public FoundryBasinCategoryMixin(Info<FoundryBasinRecipe> info) {
        super(info);
    }

    @Redirect(
            method = "setRecipe(Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;Lfr/lucreeper74/createmetallurgy/content/blocks/foundry_basin/FoundryBasinRecipe;Lmezz/jei/api/recipe/IFocusGroup;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/tterrag/registrate/util/entry/BlockEntry;asStack()Lnet/minecraft/world/item/ItemStack;"
            ),
            remap = false
    )
    private ItemStack drawMixin(BlockEntry instance, @Local(name = "requiredHeat") HeatCondition requiredHeat) {
        if (requiredHeat.name().equals("LOWHEATED")) return AllBlocks.BASIC_BURNER.asStack();
        else return instance.asStack();
    }

}
