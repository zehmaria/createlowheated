package zeh.createlowheated.compat;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.loading.LoadingModList;
import java.util.Locale;

public enum Mods {

    PETROCHEM,
    CREATEBIGCANNONS,
    CREATEDIESELGENERATORS,
    CREATEMETALLURGY,
    CREATEADDITION;

    private final String id;
    private final boolean isLoaded;

    Mods() {
        id = asId(name());
        isLoaded = LoadingModList.get().getModFileById(id) != null;
    }

    public static String asId(String name) {
        return name.toLowerCase(Locale.ROOT);
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public String id() {
        return id;
    }

    public ResourceLocation rl(String path) {
        return new ResourceLocation(id, path);
    }

    public Block getBlock(String id) {
        return BuiltInRegistries.BLOCK.get(rl(id));
    }

    public Item getItem(String id) {
        return BuiltInRegistries.ITEM.get(rl(id));
    }

}