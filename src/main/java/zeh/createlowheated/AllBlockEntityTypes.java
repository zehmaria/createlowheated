package zeh.createlowheated;

import static zeh.createlowheated.CreateLowHeated.REGISTRATE;

import com.tterrag.registrate.util.entry.BlockEntityEntry;

import zeh.createlowheated.content.processing.basicburner.BasicBurnerBlockEntity;
import zeh.createlowheated.content.processing.basicburner.BasicBurnerRenderer;

public class AllBlockEntityTypes {

    public static final BlockEntityEntry<BasicBurnerBlockEntity> BASIC_HEATER = REGISTRATE
            .blockEntity("basic_heater", BasicBurnerBlockEntity::new)
            .validBlocks(AllBlocks.BASIC_BURNER)
            .renderer(() -> BasicBurnerRenderer::new)
            .register();

    public static void register() {}

}
