package zeh.createlowheated.infrastructure.data;

import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateTagsProvider;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import zeh.createlowheated.AllTags.AllBlockTags;
import zeh.createlowheated.AllTags.AllItemTags;
import zeh.createlowheated.AllTags.AllFluidTags;
import zeh.createlowheated.AllTags.AllEntityTags;
import zeh.createlowheated.CreateLowHeated;

public class CreateLowHeatedRegistrateTags {
    public static void addGenerators() {
        CreateLowHeated.REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, CreateLowHeatedRegistrateTags::genBlockTags);
        CreateLowHeated.REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, CreateLowHeatedRegistrateTags::genItemTags);
        CreateLowHeated.REGISTRATE.addDataGenerator(ProviderType.FLUID_TAGS, CreateLowHeatedRegistrateTags::genFluidTags);
        CreateLowHeated.REGISTRATE.addDataGenerator(ProviderType.ENTITY_TAGS, CreateLowHeatedRegistrateTags::genEntityTags);
    }

    private static void genBlockTags(RegistrateTagsProvider<Block> prov) {

        // COMPAT

        // VALIDATE

        for (AllBlockTags tag : AllBlockTags.values()) {
            if (tag.alwaysDatagen) {
                prov.getOrCreateRawBuilder(tag.tag);
            }
        }
    }

    private static void genItemTags(RegistrateTagsProvider<Item> prov) {
        prov.tag(AllItemTags.BASIC_BURNER_FUEL_WHITELIST.tag).add(Items.CHARCOAL);
        prov.tag(AllItemTags.DELIGHT_INCLUDED.tag).add(Items.BEDROCK);
        prov.tag(AllItemTags.BURNER_STARTERS.tag).add(Items.FLINT_AND_STEEL);

        // COMPAT

        // VALIDATE

        for (AllItemTags tag : AllItemTags.values()) {
            if (tag.alwaysDatagen) {
                prov.getOrCreateRawBuilder(tag.tag);
            }
        }
    }

    private static void genFluidTags(RegistrateTagsProvider<Fluid> prov) {

        // VALIDATE

        for (AllFluidTags tag : AllFluidTags.values()) {
            if (tag.alwaysDatagen) {
                prov.getOrCreateRawBuilder(tag.tag);
            }
        }
    }

    private static void genEntityTags(RegistrateTagsProvider<EntityType<?>> prov) {

        // VALIDATE

        for (AllEntityTags tag : AllEntityTags.values()) {
            if (tag.alwaysDatagen) {
                prov.getOrCreateRawBuilder(tag.tag);
            }
        }
    }
}