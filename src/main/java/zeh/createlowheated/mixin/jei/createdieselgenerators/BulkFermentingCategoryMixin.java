package zeh.createlowheated.mixin.jei.createdieselgenerators;

import com.jesz.createdieselgenerators.compat.jei.BulkFermentingCategory;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedBlazeBurner;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import zeh.createlowheated.AllBlocks;
import zeh.createlowheated.compat.jei.AnimatedBasicBurner;

@Mixin(value = BulkFermentingCategory.class, remap = false)
public abstract class BulkFermentingCategoryMixin extends CreateRecipeCategory<BasinRecipe> {

    @Unique private final AnimatedBasicBurner createLowHeated$basic = new AnimatedBasicBurner();
    public BulkFermentingCategoryMixin(Info<BasinRecipe> info) {super(info);}

    @WrapOperation(
            method = "setRecipe(Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;Lcom/jesz/createdieselgenerators/content/bulk_fermenter/BulkFermentingRecipe;Lmezz/jei/api/recipe/IFocusGroup;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/tterrag/registrate/util/entry/BlockEntry;asStack()Lnet/minecraft/world/item/ItemStack;"
            ),
            remap = false
    )
    private ItemStack drawMixin(BlockEntry instance, Operation<ItemStack> original, @Local(name = "requiredHeat") HeatCondition requiredHeat) {
        if (requiredHeat.name().equals("LOWHEATED")) return AllBlocks.BASIC_BURNER.asStack();
        else return original.call(instance);
    }

    @WrapOperation(
            method = "draw(Lcom/jesz/createdieselgenerators/content/bulk_fermenter/BulkFermentingRecipe;Lmezz/jei/api/gui/ingredient/IRecipeSlotsView;Lnet/minecraft/client/gui/GuiGraphics;DD)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/compat/jei/category/animations/AnimatedBlazeBurner;withHeat(Lcom/simibubi/create/content/processing/burner/BlazeBurnerBlock$HeatLevel;)Lcom/simibubi/create/compat/jei/category/animations/AnimatedBlazeBurner;"
            ),
            remap = false
    )
    private AnimatedBlazeBurner drawMixin(AnimatedBlazeBurner instance, BlazeBurnerBlock.HeatLevel heatLevel, Operation<AnimatedBlazeBurner> original) {
        if (heatLevel == BlazeBurnerBlock.HeatLevel.valueOf("LOW")) return createLowHeated$basic.withHeat(heatLevel);
        else return original.call(instance, heatLevel);
    }

}
