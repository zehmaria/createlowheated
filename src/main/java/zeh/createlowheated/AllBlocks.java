package zeh.createlowheated;

import static zeh.createlowheated.CreateLowHeated.REGISTRATE;

import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.SharedProperties;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.client.renderer.RenderType;

import net.minecraftforge.client.model.generators.ConfiguredModel;
import zeh.createlowheated.content.processing.basicburner.*;

import com.tterrag.registrate.util.entry.BlockEntry;

public class AllBlocks {

    static { REGISTRATE.setCreativeTab(AllCreativeModeTabs.MAIN_TAB); }


    public static final BlockEntry<BasicBurnerBlock> BASIC_BURNER =
            REGISTRATE.block("basic_burner", BasicBurnerBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .properties(p -> p.mapColor(MapColor.COLOR_GRAY))
                    .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
                    .properties(p -> p.lightLevel(BasicBurnerBlock::getLight))
                    .transform(pickaxeOnly())
                    .addLayer(() -> RenderType::cutoutMipped)
                    .tag(AllTags.AllBlockTags.FAN_TRANSPARENT.tag)
                    .blockstate((c, p) -> {
                        p.getVariantBuilder(c.getEntry())
                                .forAllStatesExcept(state -> {
                                    return ConfiguredModel.builder().modelFile(p.models()
                                            .getExistingFile(p.modLoc("block/" + (state.getValue(BasicBurnerBlock.LIT) == false
                                                    ? "basic_burner_off"
                                                    : "basic_burner"))))
                                            .build();
                                }, BasicBurnerBlock.EMPOWERED, BasicBurnerBlock.HEAT_LEVEL, BasicBurnerBlock.FUELED, BasicBurnerBlock.FACING);
                    })
                    .item()
                    .transform(customItemModel("basic_burner_off"))
                    .register();

    public static void register() {}

}
