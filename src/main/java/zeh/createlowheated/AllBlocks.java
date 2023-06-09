package zeh.createlowheated;

import static zeh.createlowheated.CreateLowHeated.REGISTRATE;

import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.data.AssetLookup;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.client.renderer.RenderType;

import net.minecraftforge.client.model.generators.ConfiguredModel;
import zeh.createlowheated.content.processing.charcoal.*;

import com.tterrag.registrate.util.entry.BlockEntry;

public class AllBlocks {

    static {
        REGISTRATE.creativeModeTab(() -> AllCreativeModeTabs.BASE_CREATIVE_TAB);
    }

    public static final BlockEntry<CharcoalBurnerBlock> CHARCOAL_BURNER =
            REGISTRATE.block("charcoal_burner", CharcoalBurnerBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .properties(p -> p.color(MaterialColor.COLOR_GRAY))
                    .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
                    .properties(p -> p.lightLevel(CharcoalBurnerBlock::getLight))
                    .transform(pickaxeOnly())
                    .tag(AllTags.AllBlockTags.FAN_TRANSPARENT.tag)
                    .blockstate((c, p) -> {
                        p.getVariantBuilder(c.getEntry())
                                .forAllStatesExcept(state -> {
                                    return ConfiguredModel.builder().modelFile(p.models()
                                            .getExistingFile(p.modLoc("block/" + (state.getValue(CharcoalBurnerBlock.LIT) == false
                                                    ? "charcoal_burner_off"
                                                    : "charcoal_burner"))))
                                            .build();
                                }, CharcoalBurnerBlock.EMPOWERED, CharcoalBurnerBlock.HEAT_LEVEL, CharcoalBurnerBlock.FUELED, CharcoalBurnerBlock.FACING);
                    })
                    .addLayer(() -> RenderType::cutoutMipped)
                    .item()
                    .transform(customItemModel("charcoal_burner_off"))
                    .register();

    public static void register() {}

}
