package zeh.createlowheated.content.processing.charcoal;

import java.util.List;
import java.util.Random;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import zeh.createlowheated.AllTags;
import zeh.createlowheated.common.Configuration;

public class CharcoalBurnerBlockEntity extends SmartBlockEntity {

    public static final int MAX_HEAT_CAPACITY = 16000;
    public static final int INSERTION_THRESHOLD = 800;

    protected FuelType activeFuel;
    protected int remainingBurnTime;
    protected int fanMultiplier;

    public CharcoalBurnerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        activeFuel = FuelType.NONE;
        remainingBurnTime = 0;
        fanMultiplier = Configuration.FAN_MULTIPLIER.get();
    }

    public FuelType getActiveFuel() {
        return activeFuel;
    }

    public int getRemainingBurnTime() {
        return remainingBurnTime;
    }

    public void setEmpowered(boolean value) {
        if (getEmpoweredFromBlock() == value) return;
        level.setBlockAndUpdate(worldPosition, getBlockState().setValue(CharcoalBurnerBlock.EMPOWERED, value));
        notifyUpdate();
    }

    @Override
    public void tick() {
        super.tick();

        if (!getLitFromBlock()) return;

        if (level.isClientSide) {
            if (!isVirtual()) spawnParticles(getHeatLevelFromBlock(), 1);
            return;
        }

        if (remainingBurnTime > 0) {
            if (getEmpoweredFromBlock()) remainingBurnTime -= fanMultiplier;
            else remainingBurnTime--;
        }
        if (remainingBurnTime < 0) remainingBurnTime = 0;
        if (activeFuel == FuelType.NORMAL) updateBlockState();
        if (remainingBurnTime > 0) return;
        activeFuel = FuelType.NONE;

        updateBlockState();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        compound.putInt("FuelLevel", activeFuel.ordinal());
        compound.putInt("BurnTimeRemaining", remainingBurnTime);
        super.write(compound, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        activeFuel = FuelType.values()[compound.getInt("FuelLevel")];
        remainingBurnTime = compound.getInt("BurnTimeRemaining");
        super.read(compound, clientPacket);
    }

    public HeatLevel getHeatLevelFromBlock() {
        return CharcoalBurnerBlock.getHeatLevelOf(getBlockState());
    }

    public boolean getLitFromBlock() {
        return CharcoalBurnerBlock.getLitOf(getBlockState());
    }

    public boolean getEmpoweredFromBlock() {
        return CharcoalBurnerBlock.getEmpoweredOf(getBlockState());
    }

    public void updateBlockState() {
        setBlockHeat(getHeatLevel());
    }

    protected void setBlockHeat(HeatLevel heat) {
        HeatLevel inBlockState = getHeatLevelFromBlock();
        if (inBlockState == heat) return;
        if (remainingBurnTime == 0) {
            level.setBlockAndUpdate(worldPosition, getBlockState()
                    .setValue(CharcoalBurnerBlock.HEAT_LEVEL, heat)
                    .setValue(CharcoalBurnerBlock.LIT, false)
                    .setValue(CharcoalBurnerBlock.FUELED, false));
        } else {
            level.setBlockAndUpdate(worldPosition, getBlockState()
                    .setValue(CharcoalBurnerBlock.HEAT_LEVEL, heat));
        }
        notifyUpdate();
    }

    /**
     * @return true if the heater updated its burn time and an item should be
     *         consumed
     */
    protected boolean tryUpdateFuel(ItemStack itemStack, boolean forceOverflow, boolean simulate) {
        FuelType newFuel = FuelType.NONE;
        int newBurnTime;

        newBurnTime = ForgeHooks.getBurnTime(itemStack, null);

        if (newBurnTime > 0 && itemStack.is(AllTags.AllItemTags.CHARCOAL_BURNER_FUEL.tag)) {
            newFuel = FuelType.NORMAL;
        }

        if (newFuel == FuelType.NONE) return false;
        if (newFuel.ordinal() < activeFuel.ordinal()) return false;

        if (newFuel == activeFuel) {
            if (remainingBurnTime <= INSERTION_THRESHOLD) {
                newBurnTime += remainingBurnTime;
            } else if (forceOverflow && newFuel == FuelType.NORMAL) {
                if (remainingBurnTime < MAX_HEAT_CAPACITY) {
                    newBurnTime = Math.min(remainingBurnTime + newBurnTime, MAX_HEAT_CAPACITY);
                } else return false;
            } else return false;
        }

        if (simulate) return true;

        activeFuel = newFuel;
        remainingBurnTime = newBurnTime;

        if (level.isClientSide) {
            spawnParticleBurst();
            return true;
        }

        HeatLevel prev = getHeatLevelFromBlock();
        playSound();
        updateBlockState();

        if (prev != getHeatLevelFromBlock())
            level.playSound(null, worldPosition, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS,
                    .125f + level.random.nextFloat() * .125f, 1.15f - level.random.nextFloat() * .25f);

        return true;
    }

    public boolean isValidBlockAbove() {
        if (isVirtual()) return false;
        BlockState blockState = level.getBlockState(worldPosition.above());
        return AllBlocks.BASIN.has(blockState) || blockState.getBlock() instanceof FluidTankBlock;
    }

    protected void playSound() {
        level.playSound(null, worldPosition, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS,
                .125f + level.random.nextFloat() * .125f, .75f - level.random.nextFloat() * .25f);
    }

    protected HeatLevel getHeatLevel() {
        HeatLevel level = HeatLevel.NONE;
        if (!getLitFromBlock()) return level;
        switch (activeFuel) {
            case NORMAL:
                level = getEmpoweredFromBlock() ? HeatLevel.KINDLED : HeatLevel.byIndex(5);
                break;
            default:
            case NONE:
                break;
        }
        return level;
    }

    protected void spawnParticles(HeatLevel heatLevel, double burstMult) {
        if (level == null) return;
        if (heatLevel == HeatLevel.NONE) return;

        Random r = level.getRandom();

        Vec3 c = VecHelper.getCenterOf(worldPosition);
        Vec3 v = c.add(VecHelper.offsetRandomly(Vec3.ZERO, r, .125f).multiply(1, 0, 1));

        if (r.nextInt(4) != 0) return;

        boolean empty = level.getBlockState(worldPosition.above()).getCollisionShape(level, worldPosition.above()).isEmpty();

        if (empty || r.nextInt(8) == 0) level.addParticle(ParticleTypes.LARGE_SMOKE, v.x, v.y, v.z, 0, 0, 0);

        double yMotion = empty ? .0625f : r.nextDouble() * .0125f;
        Vec3 v2 = c.add(VecHelper.offsetRandomly(Vec3.ZERO, r, .5f).multiply(1, .25f, 1).normalize()
                        .scale((empty ? .25f : .5) + r.nextDouble() * .125f)).add(0, .5, 0);

        double yExtra = getEmpoweredFromBlock() ? .05f : 0;
        if (heatLevel.isAtLeast(HeatLevel.FADING)) {
            level.addParticle(ParticleTypes.FLAME, v2.x, v2.y, v2.z, 0, yMotion + yExtra, 0);
            if (getEmpoweredFromBlock()) level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, v2.x, v2.y, v2.z, 0, yMotion + yExtra, 0);
        }
    }

    public void spawnParticleBurst() {
        Vec3 c = VecHelper.getCenterOf(worldPosition);
        Random r = level.random;
        for (int i = 0; i < 20; i++) {
            Vec3 offset = VecHelper.offsetRandomly(Vec3.ZERO, r, .5f).multiply(1, .25f, 1).normalize();
            Vec3 v = c.add(offset.scale(.5 + r.nextDouble() * .125f)).add(0, .125, 0);
            Vec3 m = offset.scale(1 / 129f);
            level.addParticle(ParticleTypes.ASH , v.x, v.y, v.z, m.x, m.y, m.z);
        }
    }

    public enum FuelType { NONE, NORMAL }

}