package zeh.createlowheated.mixin;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.utility.BlockHelper;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zeh.createlowheated.CreateLowHeated;
import zeh.createlowheated.common.Configuration;
import zeh.createlowheated.content.processing.basicburner.BasicBurnerBlock;

@Mixin(value = BasinBlockEntity.class, remap = false)
public class BasinBlockEntityMixin {

    @Inject(method = "getHeatLevelOf", at = @At("HEAD"), cancellable = true)
    private static void getHeatLevelOfMixin(BlockState state, CallbackInfoReturnable<BlazeBurnerBlock.HeatLevel> cir) {
        if (state.hasProperty(BasicBurnerBlock.HEAT_LEVEL)) {
            cir.setReturnValue(state.getValue(BasicBurnerBlock.HEAT_LEVEL));
            return;
        }

        if(zeh.createlowheated.AllTags.AllBlockTags.LOWHEAT_RECIPE_HEATERS.matches(state) && BlockHelper.isNotUnheated(state)) {
            cir.setReturnValue(BlazeBurnerBlock.HeatLevel.valueOf("LOW"));
            return;
        }

        if (!Configuration.BASIC_BURNER_BOILER.get()) return;

        if (state.hasProperty(BlazeBurnerBlock.HEAT_LEVEL)) {
            cir.setReturnValue(state.getValue(BlazeBurnerBlock.HEAT_LEVEL));
            return;
        }

        if (Configuration.PASSIVE_BOILER_HEATERS_TAG.get()) {
            if (AllTags.AllBlockTags.PASSIVE_BOILER_HEATERS.matches(state) && BlockHelper.isNotUnheated(state)) {
                cir.setReturnValue(BlazeBurnerBlock.HeatLevel.SMOULDERING);
            } else {
                cir.setReturnValue(BlazeBurnerBlock.HeatLevel.NONE);
            }
        } else cir.setReturnValue(BlazeBurnerBlock.HeatLevel.NONE);

        cir.cancel();
    }
}
