package zeh.createlowheated.mixin;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.index.CABlocks;
import com.simibubi.create.api.boiler.BoilerHeater;
import com.simibubi.create.api.registry.SimpleRegistry;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import zeh.createlowheated.common.Configuration;


@Mixin(value = CreateAddition.class, remap = false)
public abstract class CreateAdditionMixin {

    @Redirect(method = "postInit", at = @At(value = "INVOKE",
            target = "Lcom/simibubi/create/api/registry/SimpleRegistry;register(Ljava/lang/Object;Ljava/lang/Object;)V"))
    void inject(SimpleRegistry<Block, BoilerHeater> instance, Object k, Object v){
        BoilerHeater.REGISTRY.register(CABlocks.LIQUID_BLAZE_BURNER.get(), (level, pos, state) -> {
            BlazeBurnerBlock.HeatLevel value = state.getValue(BlazeBurnerBlock.HEAT_LEVEL);
            if (value == BlazeBurnerBlock.HeatLevel.NONE) return -1;
            if (value == BlazeBurnerBlock.HeatLevel.SEETHING) return 2;
            if (value.isAtLeast(BlazeBurnerBlock.HeatLevel.FADING)) return 1;
            if (Configuration.BASIC_BURNER_BOILER.get()) return -1;
            return 0;
        });
    }

}