package zeh.createlowheated.infrastructure.item;

import net.minecraft.world.item.ItemStack;
import zeh.createlowheated.AllBlocks;

public class BaseCreativeModeTab extends CreateCreativeModeTab {
    public BaseCreativeModeTab() {
        super("base");
    }
    @Override
    public ItemStack makeIcon() {
        return AllBlocks.CHARCOAL_BURNER.asStack();
    }
}

