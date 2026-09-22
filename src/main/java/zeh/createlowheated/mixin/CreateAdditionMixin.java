package zeh.createlowheated.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.index.CABlocks;
import com.simibubi.create.api.boiler.BoilerHeater;
import com.simibubi.create.api.registry.SimpleRegistry;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import zeh.createlowheated.common.Configuration;


@Mixin(value = CreateAddition.class, remap = false)
public abstract class CreateAdditionMixin {

    @WrapOperation(method = "postInit", at = @At(value = "INVOKE",
            target = "Lcom/simibubi/create/api/registry/SimpleRegistry;register(Ljava/lang/Object;Ljava/lang/Object;)V"))
    void inject(SimpleRegistry instance, Object k, Object v, Operation<Void> original){
        if (Configuration.BASIC_BURNER_BOILER.get()) {
            BoilerHeater.REGISTRY.register(CABlocks.LIQUID_BLAZE_BURNER.get(), (level, pos, state) -> {
                BlazeBurnerBlock.HeatLevel value = state.getValue(BlazeBurnerBlock.HEAT_LEVEL);
                if (value == BlazeBurnerBlock.HeatLevel.NONE) return -1;
                if (value == BlazeBurnerBlock.HeatLevel.SEETHING) return 2;
                if (value.isAtLeast(BlazeBurnerBlock.HeatLevel.FADING)) return 1;
                return -1;
            });
        } else original.call(instance, k, v);
    }

}