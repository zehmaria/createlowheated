package zeh.createlowheated.foundation.data;

import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import com.tterrag.registrate.util.nullness.NonNullFunction;

import net.minecraft.core.Holder;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
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

import java.util.function.Function;
import java.util.stream.Stream;

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

    private static void genBlockTags(RegistrateTagsProvider<Block> provIn) {
        CreateTagsProvider<Block> prov = new CreateTagsProvider<>(provIn, Block::builtInRegistryHolder);
        // VALIDATE

        for (AllBlockTags tag : AllBlockTags.values()) {
            if (tag.alwaysDatagen) {
                prov.getOrCreateRawBuilder(tag.tag);
            }
        }
    }

    private static void genItemTags(RegistrateTagsProvider<Item> provIn) {
        CreateTagsProvider<Item> prov = new CreateTagsProvider<>(provIn, Item::builtInRegistryHolder);

        prov.tag(AllItemTags.CHARCOAL_BURNER_FUEL.tag).add(Items.CHARCOAL);

        // VALIDATE
        for (AllItemTags tag : AllItemTags.values()) {
            if (tag.alwaysDatagen) {
                prov.getOrCreateRawBuilder(tag.tag);
            }
        }
    }

    private static void genFluidTags(RegistrateTagsProvider<Fluid> provIn) {
        CreateTagsProvider<Fluid> prov = new CreateTagsProvider<>(provIn, Fluid::builtInRegistryHolder);

        // VALIDATE

        for (AllFluidTags tag : AllFluidTags.values()) {
            if (tag.alwaysDatagen) {
                prov.getOrCreateRawBuilder(tag.tag);
            }
        }
    }

    public static class CreateTagsProvider<T> {

        private RegistrateTagsProvider<T> provider;
        private Function<T, ResourceKey<T>> keyExtractor;

        public CreateTagsProvider(RegistrateTagsProvider<T> provider, Function<T, Holder.Reference<T>> refExtractor) {
            this.provider = provider;
            this.keyExtractor = refExtractor.andThen(Holder.Reference::key);
        }

        public CreateTagAppender<T> tag(TagKey<T> tag) {
            TagBuilder tagbuilder = getOrCreateRawBuilder(tag);
            return new CreateTagAppender<>(tagbuilder, keyExtractor, CreateLowHeated.ID);
        }

        public TagBuilder getOrCreateRawBuilder(TagKey<T> tag) {
            return provider.addTag(tag).getInternalBuilder();
        }

    }

    public static class CreateTagAppender<T> extends TagsProvider.TagAppender<T> {

        private Function<T, ResourceKey<T>> keyExtractor;

        public CreateTagAppender(TagBuilder pBuilder, Function<T, ResourceKey<T>> pKeyExtractor, String modId) {
            super(pBuilder, modId);
            this.keyExtractor = pKeyExtractor;
        }

        public CreateTagAppender<T> add(T entry) {
            this.add(this.keyExtractor.apply(entry));
            return this;
        }

        @SafeVarargs
        public final CreateTagAppender<T> add(T... entries) {
            Stream.<T>of(entries)
                    .map(this.keyExtractor)
                    .forEach(this::add);
            return this;
        }

    }


}
