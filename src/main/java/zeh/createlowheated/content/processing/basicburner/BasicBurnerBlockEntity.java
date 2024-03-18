package zeh.createlowheated.content.processing.basicburner;

import java.util.List;
import java.util.Random;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import zeh.createlowheated.AllTags;
import zeh.createlowheated.common.Configuration;

public class BasicBurnerBlockEntity extends SmartBlockEntity {

    public static final int MAX_HEAT_CAPACITY = 4000;
    public static final int INSERTION_THRESHOLD = 400;

    protected FuelType activeFuel;
    protected int remainingBurnTime;
    protected int fanMultiplier;
    protected int baseMultiplier;
    protected boolean hotBurners;
    protected HeatLevel activeHeatLevel;
    protected HeatLevel empoweredHeatLevel;

    public ItemStackHandler inputInv;
    public LazyOptional<IItemHandler> capability;
    BurnerItemHandler itemHandler;

    public BasicBurnerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        inputInv = new ItemStackHandler(1);
        itemHandler = new BurnerItemHandler();
        capability = LazyOptional.of(() -> itemHandler);
        activeFuel = FuelType.NONE;
        remainingBurnTime = 0;
        fanMultiplier = Configuration.FAN_MULTIPLIER.get();
        baseMultiplier= Configuration.BASE_MULTIPLIER.get();
        hotBurners = Configuration.HOT_BURNERS.get();
        activeHeatLevel = hotBurners ? HeatLevel.KINDLED : HeatLevel.byIndex(5);
        empoweredHeatLevel = hotBurners ? HeatLevel.SEETHING : HeatLevel.KINDLED;
    }

    public FuelType getActiveFuel() {
        return activeFuel;
    }

    public int getRemainingBurnTime() {
        return remainingBurnTime;
    }

    public void setEmpowered(boolean value) {
        if (getEmpoweredFromBlock() == value) return;
        level.setBlockAndUpdate(worldPosition, getBlockState().setValue(BasicBurnerBlock.EMPOWERED, value));
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

        tickFuel();

        if (remainingBurnTime > 0) {
            if (getEmpoweredFromBlock()) remainingBurnTime -= fanMultiplier;
            else remainingBurnTime -= baseMultiplier;
        }
        if (remainingBurnTime < 0) remainingBurnTime = 0;
        if (activeFuel == FuelType.NORMAL) updateBlockState();
        if (remainingBurnTime > 0) return;
        activeFuel = FuelType.NONE;

        if (remainingBurnTime == 0 && getLitFromBlock()) {
            level.setBlockAndUpdate(worldPosition, getBlockState()
                    .setValue(BasicBurnerBlock.LIT, false));
            notifyUpdate();
        }

        updateBlockState();
    }

    public void tickFuel() {
        if (inputInv.getStackInSlot(0).isEmpty()) return;
        ItemStack stackInSlot = inputInv.getStackInSlot(0);

        if (tryUpdateFuel(stackInSlot, false, false)) {
            stackInSlot.shrink(1);
            inputInv.setStackInSlot(0, stackInSlot);

            if (remainingBurnTime > 0 && !getBlockState().getValue(BasicBurnerBlock.FUELED)) {
                level.setBlockAndUpdate(worldPosition, getBlockState()
                        .setValue(BasicBurnerBlock.FUELED, true));
                notifyUpdate();
            }
        }
    }

    /**
     * @return true if the heater updated its burn time and an item should be
     *         consumed
     */
    protected boolean tryUpdateFuel(ItemStack itemStack, boolean forceOverflow, boolean simulate) {
        FuelType newFuel = FuelType.NONE;
        int newBurnTime;

        if (isFuelValid(itemStack)) newFuel = FuelType.NORMAL;

        if (newFuel == FuelType.NONE) return false;
        if (newFuel.ordinal() < activeFuel.ordinal()) return false;
        newBurnTime = ForgeHooks.getBurnTime(itemStack, null);

        if (newFuel == activeFuel) {
            if (remainingBurnTime <= INSERTION_THRESHOLD) {
                newBurnTime += remainingBurnTime;
            } else if (forceOverflow && newFuel == FuelType.NORMAL) {
                if (remainingBurnTime + newBurnTime < MAX_HEAT_CAPACITY) {
                    newBurnTime = Math.min(remainingBurnTime + newBurnTime, MAX_HEAT_CAPACITY);
                } else return false;
            } else return false;
        }

        if (simulate) return true;

        activeFuel = newFuel;
        remainingBurnTime = newBurnTime;

        HeatLevel prev = getHeatLevelFromBlock();
        playSound();
        updateBlockState();

        if (prev != getHeatLevelFromBlock())
            level.playSound(null, worldPosition, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS,
                    .125f + level.random.nextFloat() * .125f, 1.15f - level.random.nextFloat() * .25f);

        return true;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new DirectBeltInputBehaviour(this));
    }

    @Override
    public void invalidate() {
        super.invalidate();
        capability.invalidate();
    }

    @Override
    public void destroy() {
        super.destroy();
        ItemHelper.dropContents(level, worldPosition, inputInv);
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        compound.put("InputInventory", inputInv.serializeNBT());
        compound.putInt("FuelLevel", activeFuel.ordinal());
        compound.putInt("BurnTimeRemaining", remainingBurnTime);
        super.write(compound, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        inputInv.deserializeNBT(compound.getCompound("InputInventory"));
        activeFuel = FuelType.values()[compound.getInt("FuelLevel")];
        remainingBurnTime = compound.getInt("BurnTimeRemaining");
        super.read(compound, clientPacket);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (isItemHandlerCap(cap)) return capability.cast();
        return super.getCapability(cap, side);
    }

    public HeatLevel getHeatLevelFromBlock() {
        return BasicBurnerBlock.getHeatLevelOf(getBlockState());
    }

    public boolean getLitFromBlock() {
        return BasicBurnerBlock.getLitOf(getBlockState());
    }

    public boolean getEmpoweredFromBlock() {
        return BasicBurnerBlock.getEmpoweredOf(getBlockState());
    }

    public void updateBlockState() {
        setBlockHeat(getHeatLevel());
    }

    protected void setBlockHeat(HeatLevel heat) {
        HeatLevel inBlockState = getHeatLevelFromBlock();
        if (inBlockState == heat) return;
        if (remainingBurnTime == 0) {
            level.setBlockAndUpdate(worldPosition, getBlockState()
                    .setValue(BasicBurnerBlock.HEAT_LEVEL, heat)
                    .setValue(BasicBurnerBlock.LIT, false)
                    .setValue(BasicBurnerBlock.FUELED, false));
        } else {
            level.setBlockAndUpdate(worldPosition, getBlockState()
                    .setValue(BasicBurnerBlock.HEAT_LEVEL, heat));
        }
        notifyUpdate();
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

        if (empty || r.nextInt(8) == 0) level.addParticle(ParticleTypes.SMOKE, v.x, v.y, v.z, 0, 0, 0);

        double yMotion = empty ? .0325f : r.nextDouble() * .0125f;
        Vec3 v2 = c.add(VecHelper.offsetRandomly(Vec3.ZERO, r, .5f).multiply(1, .25f, 1).normalize()
                        .scale((empty ? .25f : .5) + r.nextDouble() * .125f)).add(0, .5, 0);

        double yExtra = getEmpoweredFromBlock() ? .02f : 0;
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

    public boolean isFuelValid(ItemStack stack) {
        int burnTime = ForgeHooks.getBurnTime(stack, null);
        boolean tagged = !stack.is(AllTags.AllItemTags.BASIC_BURNER_FUEL_BLACKLIST.tag)
                && (stack.is(AllTags.AllItemTags.BASIC_BURNER_FUEL_WHITELIST.tag) || Configuration.IGNORES_FUEL_TAG_WHITELIST.get());
        return burnTime > 0 && tagged && inputInv.isItemValid(0, stack);
    }

    public enum FuelType { NONE, NORMAL }

    public class BurnerItemHandler implements IItemHandler {

        private static final int MAIN_SLOT = 0;

        public BurnerItemHandler() {
        }

        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return inputInv.getStackInSlot(slot);
        }

        @Override
        public int getSlotLimit(int slot) {
            return inputInv.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return isFuelValid(stack) && inputInv.isItemValid(slot, stack);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (slot != MAIN_SLOT) return stack;
            if (!inputInv.getStackInSlot(0).isEmpty() && !stack.is(inputInv.getStackInSlot(0).getItem())) return stack;
            if (!isItemValid(slot, stack)) return stack;
            ItemStack remainder = inputInv.insertItem(slot, stack, simulate);
            if (!simulate && remainder != stack) notifyUpdate();
            return remainder;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack held = inputInv.getStackInSlot(0);
            if (held == null) return ItemStack.EMPTY;
            ItemStack stack = held.copy();
            ItemStack extracted = stack.split(amount);
            if (!simulate) {
                inputInv.setStackInSlot(0, stack);
                notifyUpdate();
            }
            return extracted;
        }

    }

}
