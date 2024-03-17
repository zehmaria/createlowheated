package zeh.createlowheated.mixin;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.fan.AirCurrent;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlock;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zeh.createlowheated.content.processing.basicburner.BasicBurnerBlockEntity;

@Mixin(value = EncasedFanBlockEntity.class, remap = false)
public abstract class EncasedFanBlockEntityMixin extends KineticBlockEntity {

    @Shadow public AirCurrent airCurrent;

    public EncasedFanBlockEntityMixin(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    public void updateBasicBurner(boolean rm) {
        Direction direction = getBlockState().getValue(EncasedFanBlock.FACING);
        if (!direction.getAxis().isHorizontal()) return;

        BlockEntity poweredBurner = level.getBlockEntity(worldPosition.relative(direction));
        if (!(poweredBurner instanceof BasicBurnerBlockEntity))  return;
        BasicBurnerBlockEntity burnerBE = (BasicBurnerBlockEntity) poweredBurner;

        burnerBE.setEmpowered(rm ? false : (Mth.abs(airCurrent.source.getSpeed()) == 256 ? true : false));
    }

    @Inject(method = "onSpeedChanged", at = @At("HEAD"), cancellable = true)
    protected void addBasicBurnerToSpeedChange(float prevSpeed, CallbackInfo ci) { updateBasicBurner(false); }

    @Inject(method = "remove", at = @At("HEAD"), cancellable = true)
    protected void addBasicBurnerToRemove(CallbackInfo ci) {
        updateBasicBurner(true);
    }

}
