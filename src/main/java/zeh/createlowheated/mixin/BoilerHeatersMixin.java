package zeh.createlowheated.mixin;

import com.simibubi.create.content.fluids.tank.BoilerHeaters;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zeh.createlowheated.AllBlocks;
import zeh.createlowheated.common.Configuration;
import zeh.createlowheated.content.processing.basicburner.BasicBurnerBlock;

@Mixin(value = BoilerHeaters.class, remap = false)
public class BoilerHeatersMixin {
    @Inject(method = "registerDefaults", at = @At("HEAD"), cancellable = true)
    private static void registerDefaultsMixin(CallbackInfo ci) {
        if (!Configuration.BASIC_BURNER_BOILER.get()) return;

        registerHeater(com.simibubi.create.AllBlocks.BLAZE_BURNER.get(), (level, pos, state) -> {
            HeatLevel value = state.getValue(BlazeBurnerBlock.HEAT_LEVEL);
            if (value == HeatLevel.NONE) return -1;
            if (value == HeatLevel.SEETHING) return 2;
            if (value.isAtLeast(HeatLevel.FADING)) return 1;
            return -1;
        });

        registerHeater(AllBlocks.BASIC_BURNER.get(), (level, pos, state) -> {
            HeatLevel value = state.getValue(BasicBurnerBlock.HEAT_LEVEL);
            if (value == HeatLevel.NONE) return -1;
            if (value == HeatLevel.valueOf("LOW")) return 0;
            if (value == HeatLevel.SEETHING) return 2;
            if (value.isAtLeast(HeatLevel.FADING)) return 1;
            return -1;
        });

        ci.cancel();
    }

    @Shadow public static void registerHeater(Block block, BoilerHeaters.Heater heater) {}

}
