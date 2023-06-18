package zeh.createlowheated;

import static zeh.createlowheated.CreateLowHeated.REGISTRATE;

import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import com.tterrag.registrate.util.entry.ItemEntry;

public class AllItems {
    static {
        REGISTRATE.creativeModeTab(() -> AllCreativeModeTabs.BASE_CREATIVE_TAB);
    }
/*
    public static final ItemEntry<CharcoalBurnerBlockItem> EMPTY_CHARCOAL_BURNER =
            REGISTRATE.item("empty_charcoal_burner", CharcoalBurnerBlockItem::empty)
                    .model(AssetLookup.customBlockItemModel("charcoal_burner", "block"))
                    .register();*/

    // Shortcuts
    private static ItemEntry<Item> ingredient(String name) {
        return REGISTRATE.item(name, Item::new)
                .register();
    }

    private static ItemEntry<SequencedAssemblyItem> sequencedIngredient(String name) {
        return REGISTRATE.item(name, SequencedAssemblyItem::new)
                .register();
    }

    @SafeVarargs
    private static ItemEntry<Item> taggedIngredient(String name, TagKey<Item>... tags) {
        return REGISTRATE.item(name, Item::new)
                .tag(tags)
                .register();
    }

    public static void register() {}

}
