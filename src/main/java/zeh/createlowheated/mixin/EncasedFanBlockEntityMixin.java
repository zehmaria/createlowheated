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

    @Unique
    private int createLowHeated$basicBurnerCheckCooldown;

    @Shadow public abstract Direction getAirflowOriginSide();

    public EncasedFanBlockEntityMixin(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Unique
    public boolean createLowHeated$powerUpBasicBurner(int distance) {
        Direction fanFacingDir = getAirflowOriginSide();
        if (Configuration.FAN_HORIZONTAL_ONLY.get()) if (!fanFacingDir.getAxis().isHorizontal()) return false;
        if (Mth.abs(getSpeed()) < Configuration.FAN_SPEED_REQUIRED.get()) return false;
        BlockEntity poweredBurner = level != null ? level.getBlockEntity(worldPosition.relative(fanFacingDir, distance)) : null;
        if (!(poweredBurner instanceof BasicBurnerBlockEntity burnerBE)) return false;
        burnerBE.powerUp();
        return true;
    }

    @Unique
    public void createLowHeated$powerUpBasicBurners() {
        if (createLowHeated$powerUpBasicBurner(1))
            if (createLowHeated$powerUpBasicBurner(2))
                createLowHeated$powerUpBasicBurner(3);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    protected void tickMixin(CallbackInfo ci) {
        if (createLowHeated$basicBurnerCheckCooldown-- <= 0) {
            createLowHeated$basicBurnerCheckCooldown = 10;
            createLowHeated$powerUpBasicBurners();
        }
    }

}
