package zeh.createlowheated.mixin;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zeh.createlowheated.common.Configuration;
import zeh.createlowheated.content.processing.basicburner.BasicBurnerBlockEntity;

@Mixin(value = EncasedFanBlockEntity.class, remap = false)
public abstract class EncasedFanBlockEntityMixin extends KineticBlockEntity {

    @Shadow public abstract Direction getAirflowOriginSide();

    public EncasedFanBlockEntityMixin(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Unique
    public void createLowHeated$updateBasicBurner(boolean rm) {
        Direction fanFacingDir = getAirflowOriginSide();

        BlockEntity poweredBurner = level.getBlockEntity(worldPosition.relative(fanFacingDir));
        if (!(poweredBurner instanceof BasicBurnerBlockEntity burnerBE))  return;

        burnerBE.setEmpowered(!rm && (Mth.abs(getSpeed()) >= Configuration.FAN_SPEED_REQUIRED.get()), fanFacingDir.getOpposite());
    }

    @Inject(method = "onSpeedChanged", at = @At("HEAD"))
    protected void addBasicBurnerToSpeedChange(float prevSpeed, CallbackInfo ci) { createLowHeated$updateBasicBurner(false); }

    @Inject(method = "remove", at = @At("HEAD"))
    protected void addBasicBurnerToRemove(CallbackInfo ci) {
        createLowHeated$updateBasicBurner(true);
    }

}
