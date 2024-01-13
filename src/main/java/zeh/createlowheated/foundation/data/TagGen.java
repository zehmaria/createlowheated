package zeh.createlowheated.foundation.data;

import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import com.tterrag.registrate.util.nullness.NonNullFunction;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import zeh.createlowheated.AllTags.AllBlockTags;
import zeh.createlowheated.AllTags.AllItemTags;
import zeh.createlowheated.AllTags.AllFluidTags;
import zeh.createlowheated.AllTags;
import zeh.createlowheated.CreateLowHeated;

public class TagGen {
    public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> axeOrPickaxe() {
        return b -> b.tag(BlockTags.MINEABLE_WITH_AXE)
                .tag(BlockTags.MINEABLE_WITH_PICKAXE);
    }

    public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> axeOnly() {
        return b -> b.tag(BlockTags.MINEABLE_WITH_AXE);
    }

    public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> pickaxeOnly() {
        return b -> b.tag(BlockTags.MINEABLE_WITH_PICKAXE);
    }

    public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, ItemBuilder<BlockItem, BlockBuilder<T, P>>> tagBlockAndItem(
            String... path) {
        return b -> {
            for (String p : path)
                b.tag(AllTags.forgeBlockTag(p));
            ItemBuilder<BlockItem, BlockBuilder<T, P>> item = b.item();
            for (String p : path)
                item.tag(AllTags.forgeItemTag(p));
            return item;
        };
    }

    public static void datagen() {
        //CreateLowHeated.REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, TagGen::genBlockTags);
        CreateLowHeated.REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, TagGen::genItemTags);
        //CreateLowHeated.REGISTRATE.addDataGenerator(ProviderType.FLUID_TAGS, TagGen::genFluidTags);
    }

    private static void genBlockTags(RegistrateTagsProvider<Block> prov) {
        // VALIDATE

        for (AllBlockTags tag : AllBlockTags.values()) {
            if (tag.alwaysDatagen) {
                prov.getOrCreateRawBuilder(tag.tag);
            }
        }
    }

    private static void genItemTags(RegistrateTagsProvider<Item> prov) {
        prov.tag(AllItemTags.CHARCOAL_BURNER_FUEL.tag).add(Items.CHARCOAL);
        prov.tag(AllItemTags.DELIGHT_INCLUDED.tag).add(Items.BEDROCK);

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

}
