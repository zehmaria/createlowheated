package zeh.createlowheated.infrastructure.data;

import com.simibubi.create.AllFluids;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.MixingRecipeGen;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.DifferenceIngredient;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.tag.CommonTags;
import vectorwing.farmersdelight.common.tag.ModTags;
import zeh.createlowheated.CreateLowHeated;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class LHMixingRecipeGen extends MixingRecipeGen {

    public HeatCondition low() {
        return HeatCondition.valueOf("LOWHEATED");
    }

    public ResourceLocation id(String id) {
        return CreateLowHeated.asResource(id);
    }

    GeneratedRecipe CHOCOLATE = create(id("chocolate"), b -> b
            .require(Items.SUGAR)
            .require(Items.COCOA_BEANS)
            .require(Tags.Fluids.MILK, 250)
            .output(AllFluids.CHOCOLATE.get(), 250)
            .requiresHeat(low())),

    TEA = create(id("builders_tea"), b -> b
            .require(Fluids.WATER, 250)
            .require(Tags.Fluids.MILK, 250)
            .require(ItemTags.LEAVES)
            .output(AllFluids.TEA.get(), 500)
            .requiresHeat(low())),

    CHOCOLATE_MELTING = create(id("chocolate_melting"), b -> b.require(AllItems.BAR_OF_CHOCOLATE.get())
            .output(AllFluids.CHOCOLATE.get(), 250)
            .requiresHeat(low())),

    HONEY = create(id("honey"), b -> b.require(Items.HONEY_BLOCK)
            .output(AllFluids.HONEY.get(), 1000)
            .requiresHeat(low())),

    BEETROOT_SOUP = create(id("minecraft/beetroot_soup"), b -> b
            .require(Items.BOWL)
            .require(Items.BEETROOT)
            .require(Items.BEETROOT)
            .require(Items.BEETROOT)
            .require(Fluids.WATER, 250)
            .output(Items.BEETROOT_SOUP)
            .requiresHeat(low())),

    MUSHROOR_STEW = create(id("minecraft/mushroom_stew"), b -> b
            .require(Items.BOWL)
            .require(Tags.Items.MUSHROOMS)
            .require(Tags.Items.MUSHROOMS)
            .require(Fluids.WATER, 250)
            .output(Items.MUSHROOM_STEW)
            .requiresHeat(low())),

    RABBIT_STEW = create(id("minecraft/rabbit_stew"), b -> b
            .require(Items.BOWL)
            .require(Items.POTATO)
            .require(Items.RABBIT)
            .require(Items.CARROT)
            .require(Tags.Items.MUSHROOMS)
            .require(Fluids.WATER, 250)
            .output(Items.RABBIT_STEW)
            .requiresHeat(low())),

    // FARMERS' DELIGHT
    HOT_COCOA = create(id("farmersdelight/hot_cocoa"), b -> b
            .require(Items.GLASS_BOTTLE)
            .require(Items.SUGAR)
            .require(Items.COCOA_BEANS)
            .require(Items.COCOA_BEANS)
            .require(Tags.Fluids.MILK, 250)
            .output(ModItems.HOT_COCOA.get())
            .whenModLoaded(FarmersDelight.MODID)
            .requiresHeat(low())),


    APPLE_CIDER = create(id("farmersdelight/apple_cider"), b -> b
            .require(Items.GLASS_BOTTLE)
            .require(Items.APPLE)
            .require(Items.APPLE)
            .require(Items.SUGAR)
            .output(ModItems.APPLE_CIDER.get())
            .whenModLoaded(FarmersDelight.MODID)
            .requiresHeat(low())),

    TOMATO_SAUCE = create(id("farmersdelight/tomato_sauce"), b -> b
            .require(Items.BOWL)
            .require(CommonTags.CROPS_TOMATO)
            .require(CommonTags.CROPS_TOMATO)
            .output(ModItems.TOMATO_SAUCE.get())
            .whenModLoaded(FarmersDelight.MODID)
            .requiresHeat(low())),

    DOG_FOOD = create(id("farmersdelight/dog_food"), b -> b
            .require(Items.BOWL)
            .require(Items.ROTTEN_FLESH)
            .require(Items.BONE_MEAL)
            .require(Tags.Items.FOODS_RAW_MEAT)
            .require(CommonTags.CROPS_RICE)
            .output(ModItems.DOG_FOOD.get())
            .whenModLoaded(FarmersDelight.MODID)
            .requiresHeat(low())),

    GLOW_BERRY_CUSTARD = create(id("farmersdelight/glow_berry_custard"), b -> b
            .require(Items.GLASS_BOTTLE)
            .require(Items.GLOW_BERRIES)
            .require(Tags.Fluids.MILK, 250)
            .require(Tags.Items.EGGS)
            .require(Items.SUGAR)
            .output(ModItems.GLOW_BERRY_CUSTARD.get())
            .whenModLoaded(FarmersDelight.MODID)
            .requiresHeat(low())),


    BAKED_COD_STEW = create(id("farmersdelight/baked_cod_stew"), b -> b
            .require(Items.BOWL)
            .require(CommonTags.FOODS_RAW_COD)
            .require(Items.POTATO)
            .require(Tags.Items.EGGS)
            .require(CommonTags.CROPS_TOMATO)
            .require(Fluids.WATER, 250)
            .output(ModItems.BAKED_COD_STEW.get())
            .whenModLoaded(FarmersDelight.MODID)
            .requiresHeat(low())),

    BEEF_STEW = create(id("farmersdelight/beef_stew"), b -> b
            .require(Items.BOWL)
            .require(CommonTags.FOODS_RAW_BEEF)
            .require(Items.CARROT)
            .require(Items.POTATO)
            .require(Fluids.WATER, 250)
            .output(ModItems.BEEF_STEW.get())
            .whenModLoaded(FarmersDelight.MODID)
            .requiresHeat(low())),

    BONE_BROTH = create(id("farmersdelight/bone_broth"), b -> b
            .require(Items.BOWL)
            .require(Tags.Items.BONES)
            .require(Tags.Items.BONES)
            .require(Ingredient.fromValues(Stream.of(
                    new Ingredient.ItemValue(new ItemStack(Items.GLOW_BERRIES)),
                    new Ingredient.TagValue(Tags.Items.MUSHROOMS),
                    new Ingredient.ItemValue(new ItemStack(Items.HANGING_ROOTS)),
                    new Ingredient.ItemValue(new ItemStack(Items.GLOW_LICHEN))
            )))
            .require(Fluids.WATER, 250)
            .output(ModItems.BONE_BROTH.get())
            .whenModLoaded(FarmersDelight.MODID)
            .requiresHeat(low())),

    CABBAGE_ROLLS = create(id("farmersdelight/cabbage_rolls"), b -> b
            .require(CommonTags.CROPS_CABBAGE)
            .require(ModTags.CABBAGE_ROLL_INGREDIENTS)
            .output(ModItems.CABBAGE_ROLLS.get())
            .whenModLoaded(FarmersDelight.MODID)
            .requiresHeat(low())),

    CHICKEN_SOUP = create(id("farmersdelight/chicken_soup"), b -> b
            .require(Items.BOWL)
            .require(CommonTags.FOODS_RAW_CHICKEN)
            .require(Items.CARROT)
            .require(CommonTags.FOODS_LEAFY_GREEN)
            .require(vegetablesPatch())
            .require(Fluids.WATER, 250)
            .output(ModItems.CHICKEN_SOUP.get())
            .whenModLoaded(FarmersDelight.MODID)
            .requiresHeat(low())),

    COOKED_RICE = create(id("farmersdelight/cooked_rice"), b -> b
            .require(Items.BOWL)
            .require(CommonTags.CROPS_RICE)
            .require(Fluids.WATER, 100)
            .output(ModItems.COOKED_RICE.get())
            .whenModLoaded(FarmersDelight.MODID)
            .requiresHeat(low())),

    DUMPLINGS = create(id("farmersdelight/dumplings"), b -> b
            .require(CommonTags.FOODS_DOUGH)
            .require(CommonTags.CROPS_CABBAGE)
            .require(CommonTags.CROPS_ONION)
            .require(Ingredient.fromValues(Stream.of(
                    new Ingredient.TagValue(CommonTags.FOODS_RAW_CHICKEN),
                    new Ingredient.TagValue(CommonTags.FOODS_RAW_PORK),
                    new Ingredient.TagValue(CommonTags.FOODS_RAW_BEEF),
                    new Ingredient.ItemValue(new ItemStack(Items.BROWN_MUSHROOM))
            )))
            .output(ModItems.DUMPLINGS.get(), 2)
            .whenModLoaded(FarmersDelight.MODID)
            .requiresHeat(low())),

    FISH_STEW = create(id("farmersdelight/fish_stew"), b -> b
            .require(CommonTags.FOODS_SAFE_RAW_FISH)
            .require(ModItems.TOMATO_SAUCE.get())
            .require(CommonTags.CROPS_ONION)
            .require(Fluids.WATER, 250)
            .output(ModItems.FISH_STEW.get())
            .whenModLoaded(FarmersDelight.MODID)
            .requiresHeat(low())),

    FRIED_RICE = create(id("farmersdelight/fried_rice"), b -> b
            .require(Items.BOWL)
            .require(CommonTags.CROPS_RICE)
            .require(Tags.Items.EGGS)
            .require(Items.CARROT)
            .require(CommonTags.CROPS_ONION)
            .require(Fluids.WATER, 100)
            .output(ModItems.FRIED_RICE.get())
            .whenModLoaded(FarmersDelight.MODID)
            .requiresHeat(low())),

    MUSHROOM_RICE = create(id("farmersdelight/mushrrom_rice"), b -> b
            .require(Items.BOWL)
            .require(Items.BROWN_MUSHROOM)
            .require(Items.RED_MUSHROOM)
            .require(CommonTags.CROPS_RICE)
            .require(Ingredient.of(Items.CARROT, Items.POTATO))
            .require(Fluids.WATER, 100)
            .output(ModItems.MUSHROOM_RICE.get())
            .whenModLoaded(FarmersDelight.MODID)
            .requiresHeat(low())),

    NOODLE_SOUP = create(id("farmersdelight/noodle_soup"), b -> b
            .require(Items.BOWL)
            .require(CommonTags.FOODS_PASTA)
            .require(CommonTags.FOODS_COOKED_EGG)
            .require(Items.DRIED_KELP)
            .require(CommonTags.FOODS_RAW_PORK)
            .require(Fluids.WATER, 250)
            .output(ModItems.NOODLE_SOUP.get())
            .whenModLoaded(FarmersDelight.MODID)
            .requiresHeat(low())),

    PASTA_WITH_MEATBALLS = create(id("farmersdelight/pasta_with_meatballs"), b -> b
            .require(ModItems.MINCED_BEEF.get())
            .require(CommonTags.FOODS_PASTA)
            .require(ModItems.TOMATO_SAUCE.get())
            .output(ModItems.PASTA_WITH_MEATBALLS.get())
            .whenModLoaded(FarmersDelight.MODID)
            .requiresHeat(low())),

    PASTA_WITH_MUTTON_CHOP = create(id("farmersdelight/pasta_with_mutton_chop"), b -> b
            .require(CommonTags.FOODS_RAW_MUTTON)
            .require(CommonTags.FOODS_PASTA)
            .require(ModItems.TOMATO_SAUCE.get())
            .output(ModItems.PASTA_WITH_MUTTON_CHOP.get())
            .whenModLoaded(FarmersDelight.MODID)
            .requiresHeat(low())),

    PUMPKIN_SOUP = create(id("farmersdelight/pumpkin_soup"), b -> b
            .require(Items.BOWL)
            .require(ModItems.PUMPKIN_SLICE.get())
            .require(CommonTags.FOODS_LEAFY_GREEN)
            .require(CommonTags.FOODS_RAW_PORK)
            .require(Tags.Fluids.MILK, 250)
            .output(ModItems.PUMPKIN_SOUP.get())
            .whenModLoaded(FarmersDelight.MODID)
            .requiresHeat(low())),

    RATATOUILLE = create(id("farmersdelight/ratatouille"), b -> b
            .require(Items.BOWL)
            .require(CommonTags.CROPS_TOMATO)
            .require(CommonTags.CROPS_ONION)
            .require(Items.BEETROOT)
            .require(vegetablesPatch())
            .output(ModItems.RATATOUILLE.get())
            .whenModLoaded(FarmersDelight.MODID)
            .requiresHeat(low())),

    SQUID_INK_PASTA = create(id("farmersdelight/squid_ink_pasta"), b -> b
            .require(Items.BOWL)
            .require(CommonTags.FOODS_SAFE_RAW_FISH)
            .require(CommonTags.FOODS_PASTA)
            .require(CommonTags.CROPS_TOMATO)
            .require(Items.INK_SAC)
            .output(ModItems.SQUID_INK_PASTA.get())
            .whenModLoaded(FarmersDelight.MODID)
            .requiresHeat(low())),

    STUFFED_PUMPKIN_BLOCK = create(id("farmersdelight/stuffed_pumpkin_block"), b -> b
            .require(Items.PUMPKIN)
            .require(CommonTags.CROPS_RICE)
            .require(CommonTags.CROPS_ONION)
            .require(Items.BROWN_MUSHROOM)
            .require(Items.POTATO)
            .require(Tags.Items.FOODS_BERRY)
            .require(vegetablesPatch())
            .require(Fluids.WATER, 100)
            .output(ModItems.STUFFED_PUMPKIN_BLOCK.get())
            .whenModLoaded(FarmersDelight.MODID)
            .requiresHeat(low())),

    VEGETABLE_NOODLES = create(id("farmersdelight/vegetable_noodles"), b -> b
            .require(Items.BOWL)
            .require(Items.CARROT)
            .require(Items.BROWN_MUSHROOM)
            .require(CommonTags.FOODS_PASTA)
            .require(CommonTags.FOODS_LEAFY_GREEN)
            .require(vegetablesPatch())
            .require(Fluids.WATER, 250)
            .output(ModItems.VEGETABLE_NOODLES.get())
            .whenModLoaded(FarmersDelight.MODID)
            .requiresHeat(low())),

    VEGETABLE_SOUP = create(id("farmersdelight/vegetable_soup"), b -> b
            .require(Items.BOWL)
            .require(Items.CARROT)
            .require(Items.POTATO)
            .require(Items.BEETROOT)
            .require(CommonTags.FOODS_LEAFY_GREEN)
            .require(Fluids.WATER, 250)
            .output(ModItems.VEGETABLE_SOUP.get())
            .whenModLoaded(FarmersDelight.MODID)
            .requiresHeat(low())),

    APPLE_PIE = create(id("farmersdelight/apple_pie"), b -> b
            .require(Items.SUGAR)
            .require(Items.SUGAR)
            .require(Items.APPLE)
            .require(Items.APPLE)
            .require(Items.APPLE)
            .require(CommonTags.FOODS_DOUGH)
            .require(ModItems.PIE_CRUST.get())
            .output(ModItems.APPLE_PIE.get())
            .whenModLoaded(FarmersDelight.MODID)
            .requiresHeat(low())),

    CHOCOLATE_PIE = create(id("farmersdelight/chocolate_pie"), b -> b
            .require(Items.SUGAR)
            .require(Items.SUGAR)
            .require(Items.COCOA_BEANS)
            .require(Items.COCOA_BEANS)
            .require(Items.COCOA_BEANS)
            .require(CommonTags.FOODS_DOUGH)
            .require(Tags.Fluids.MILK, 250)
            .require(ModItems.PIE_CRUST.get())
            .output(ModItems.CHOCOLATE_PIE.get())
            .whenModLoaded(FarmersDelight.MODID)
            .requiresHeat(low())),

    PIE_CRUST = create(id("farmersdelight/pie_crust"), b -> b
            .require(AllItems.WHEAT_FLOUR)
            .require(AllItems.WHEAT_FLOUR)
            .require(AllItems.WHEAT_FLOUR)
            .require(Tags.Fluids.MILK, 250)
            .output(ModItems.PIE_CRUST.get())
            .whenModLoaded(FarmersDelight.MODID)
            .requiresHeat(low())),

    HONEY_COOKIE = create(id("farmersdelight/honey_cookie"), b -> b
            .require(CommonTags.FOODS_DOUGH)
            .require(Tags.Fluids.HONEY, 250)
            .output(ModItems.HONEY_COOKIE.get(), 8)
            .whenModLoaded(FarmersDelight.MODID)
            .requiresHeat(low())),

    SWEET_BERRY_COOKIE = create(id("farmersdelight/sweet_berry_cookie"), b -> b
            .require(CommonTags.FOODS_DOUGH)
            .require(Items.SWEET_BERRIES)
            .require(Items.SWEET_BERRIES)
            .output(ModItems.SWEET_BERRY_COOKIE.get(), 8)
            .whenModLoaded(FarmersDelight.MODID)
            .requiresHeat(low())),

    STUFFED_POTATO = create(id("farmersdelight/stuffed_potato"), b -> b
            .require(Items.BAKED_POTATO)
            .require(ModItems.BEEF_PATTY.get())
            .require(Tags.Fluids.MILK, 250)
            .output(ModItems.STUFFED_POTATO.get(), 8)
            .whenModLoaded(FarmersDelight.MODID)
            .requiresHeat(low())),

    SWEET_BERRY_CHEESECAKE = create(id("farmersdelight/sweet_berry_cheesecake"), b -> b
            .require(Items.SWEET_BERRIES)
            .require(Items.SWEET_BERRIES)
            .require(Items.SWEET_BERRIES)
            .require(Items.SWEET_BERRIES)
            .require(Items.SWEET_BERRIES)
            .require(Items.SWEET_BERRIES)
            .require(ModItems.PIE_CRUST.get())
            .require(Tags.Fluids.MILK, 500)
            .output(ModItems.SWEET_BERRY_CHEESECAKE.get(), 8)
            .whenModLoaded(FarmersDelight.MODID)
            .requiresHeat(low()));

    public LHMixingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateLowHeated.ID);
    }

    // TODO: Deprecate this if NeoForge removes melon_slice from the vegetables tag.
    private static Ingredient vegetablesPatch() {
        return DifferenceIngredient.of(Ingredient.of(Tags.Items.FOODS_VEGETABLE), Ingredient.of(Items.MELON_SLICE));
    }

}
