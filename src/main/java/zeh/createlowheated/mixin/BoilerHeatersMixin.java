package zeh.createlowheated.mixin;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTags;
import com.simibubi.create.api.boiler.BoilerHeater;
import com.simibubi.create.api.registry.SimpleRegistry;
import com.simibubi.create.content.fluids.tank.BoilerHeaters;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zeh.createlowheated.common.Configuration;
import zeh.createlowheated.content.processing.basicburner.BasicBurnerBlock;

@Mixin(value = BoilerHeaters.class, remap = false)
public class BoilerHeatersMixin {
    private static BoilerHeater BasicHeater = BoilerHeatersMixin::basicHeater;
    private static BoilerHeater BlazeHeater = BoilerHeatersMixin::blazeHeater;

    private static int basicHeater(Level level, BlockPos pos, BlockState state) {
        HeatLevel value = state.getValue(BasicBurnerBlock.HEAT_LEVEL);
        if (value == HeatLevel.NONE) return BoilerHeater.NO_HEAT;
        if (value == HeatLevel.valueOf("LOW")) return BoilerHeater.PASSIVE_HEAT;
        if (value == HeatLevel.SEETHING) return 2;
        if (value.isAtLeast(HeatLevel.FADING)) return 1;
        return BoilerHeater.NO_HEAT;
    }
    private static int blazeHeater(Level level, BlockPos pos, BlockState state) {
        HeatLevel value = state.getValue(BlazeBurnerBlock.HEAT_LEVEL);
        if (value == HeatLevel.NONE) return BoilerHeater.NO_HEAT;
        if (value == HeatLevel.SEETHING) return 2;
        if (value.isAtLeast(HeatLevel.FADING)) return 1;
        return BoilerHeater.NO_HEAT;
    }

    @Inject(method = "registerDefaults", at = @At("HEAD"), cancellable = true)
    private static void registerDefaultsMixin(CallbackInfo ci) {
        if (!Configuration.BASIC_BURNER_BOILER.get()) return;
        BoilerHeater.REGISTRY.register(AllBlocks.BLAZE_BURNER.get(), BlazeHeater);
        BoilerHeater.REGISTRY.register(zeh.createlowheated.AllBlocks.BASIC_BURNER.get(), BasicHeater);
        if (Configuration.PASSIVE_BOILER_HEATERS_TAG.get()) {
            BoilerHeater.REGISTRY.registerProvider(SimpleRegistry.Provider.forBlockTag(AllTags.AllBlockTags.PASSIVE_BOILER_HEATERS.tag, BoilerHeater.PASSIVE));
        }
        ci.cancel();
    }

}
