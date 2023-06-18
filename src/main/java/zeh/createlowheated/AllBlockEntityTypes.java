package zeh.createlowheated;

import static zeh.createlowheated.CreateLowHeated.REGISTRATE;

import com.tterrag.registrate.util.entry.BlockEntityEntry;

import zeh.createlowheated.content.processing.charcoal.CharcoalBurnerBlockEntity;
import zeh.createlowheated.content.processing.charcoal.CharcoalBurnerRenderer;

public class AllBlockEntityTypes {

    public static final BlockEntityEntry<CharcoalBurnerBlockEntity> CHARCOAL_HEATER = REGISTRATE
            .blockEntity("charcoal_heater", CharcoalBurnerBlockEntity::new)
            .validBlocks(AllBlocks.CHARCOAL_BURNER)
            .renderer(() -> CharcoalBurnerRenderer::new)
            .register();

    public static void register() {}

}
