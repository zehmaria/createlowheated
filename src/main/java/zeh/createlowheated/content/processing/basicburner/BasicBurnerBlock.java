package zeh.createlowheated.content.processing.basicburner;

import java.util.Random;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.foundation.block.IBE;

import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import zeh.createlowheated.AllBlockEntityTypes;
import zeh.createlowheated.AllShapes;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import zeh.createlowheated.AllTags;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BasicBurnerBlock extends HorizontalDirectionalBlock implements IBE<BasicBurnerBlockEntity>, IWrenchable {

    public static final EnumProperty<HeatLevel> HEAT_LEVEL = EnumProperty.create("low", HeatLevel.class);
    public static final BooleanProperty LIT = BooleanProperty.create("lit");
    public static final BooleanProperty FUELED = BooleanProperty.create("fueled");
    public static final BooleanProperty EMPOWERED = BooleanProperty.create("empowered");

    public BasicBurnerBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(HEAT_LEVEL, HeatLevel.NONE)
                .setValue(LIT, false)
                .setValue(FUELED, false)
                .setValue(EMPOWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(HEAT_LEVEL, LIT, FUELED, EMPOWERED, FACING);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState p_220082_4_, boolean p_220082_5_) {
        if (world.isClientSide) return;
        BlockEntity blockEntity = world.getBlockEntity(pos.above());
        if (!(blockEntity instanceof BasinBlockEntity)) return;
        BasinBlockEntity basin = (BasinBlockEntity) blockEntity;
        basin.notifyChangeOfContents();
    }

    @Override
    public Class<BasicBurnerBlockEntity> getBlockEntityClass() {
        return BasicBurnerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends BasicBurnerBlockEntity> getBlockEntityType() {
        return AllBlockEntityTypes.BASIC_HEATER.get();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return IBE.super.newBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockRayTraceResult) {
        ItemStack heldItem = player.getItemInHand(hand);
        boolean wasEmptyHanded = heldItem.isEmpty() && hand == InteractionHand.MAIN_HAND;
        boolean shouldntPlaceItem = AllBlocks.MECHANICAL_ARM.isIn(heldItem);

        if (!state.hasBlockEntity()) return InteractionResult.PASS;
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof BasicBurnerBlockEntity)) return InteractionResult.PASS;
        BasicBurnerBlockEntity burnerBE = (BasicBurnerBlockEntity) be;

        if (!burnerBE.inputInv.getStackInSlot(0).isEmpty() && !state.getValue(LIT) && heldItem.is(AllTags.AllItemTags.BURNER_STARTERS.tag)) {
            world.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F,
                    world.random.nextFloat() * 0.4F + 0.8F);
            if (world.isClientSide) return InteractionResult.SUCCESS;
            heldItem.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
            world.setBlockAndUpdate(pos, state.setValue(BasicBurnerBlock.LIT, true));
            burnerBE.notifyUpdate();
            return InteractionResult.SUCCESS;
        }

        ItemStack mainItemStack = burnerBE.inputInv.getStackInSlot(0);

        if (!mainItemStack.isEmpty() && wasEmptyHanded) {
            player.getInventory().placeItemBackInInventory(mainItemStack);
            burnerBE.inputInv.setStackInSlot(0, ItemStack.EMPTY);
            world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f,
                    1f + Create.RANDOM.nextFloat());
        }

        if (!wasEmptyHanded && !shouldntPlaceItem) {
            ItemStack remainder = burnerBE.itemHandler.insertItem(0, heldItem.copy(), false);
            if (remainder.getCount() == heldItem.getCount()) return InteractionResult.PASS;
            player.setItemInHand(hand, remainder);
            AllSoundEvents.DEPOT_SLIDE.playOnServer(world, pos);
        }

        burnerBE.notifyUpdate();
        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        ItemStack stack = context.getItemInHand();
        Item item = stack.getItem();
        boolean isEmpowered = false;
        for (Direction side : Iterate.directions) {
            BlockPos offsetPos = context.getClickedPos().relative(side);
            BlockEntity fan = context.getLevel().getBlockEntity(offsetPos);
            if (!(fan instanceof EncasedFanBlockEntity)) continue;
            EncasedFanBlockEntity fanBE = (EncasedFanBlockEntity) fan;
            isEmpowered = (Mth.abs(fanBE.airCurrent.source.getSpeed()) == 256 ? true : false);
        }
        return super.getStateForPlacement(context).setValue(EMPOWERED, isEmpowered);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        return AllShapes.BASIC_HEATER_BLOCK_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState p_220071_1_, BlockGetter p_220071_2_, BlockPos p_220071_3_,
                                        CollisionContext p_220071_4_) {
        return getShape(p_220071_1_, p_220071_2_, p_220071_3_, p_220071_4_);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState p_149740_1_) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level p_180641_2_, BlockPos p_180641_3_) {
        return Math.max(0, state.getValue(HEAT_LEVEL).ordinal() - 1);
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, Level world, BlockPos pos, Random random) {
        if (random.nextInt(10) != 0) return;
        if (!state.getValue(HEAT_LEVEL).isAtLeast(HeatLevel.byIndex(5))) return;
        world.playLocalSound((double) ((float) pos.getX() + 0.5F), (double) ((float) pos.getY() + 0.5F),
                (double) ((float) pos.getZ() + 0.5F), SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS,
                0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.6F, false);
    }

    public static HeatLevel getHeatLevelOf(BlockState blockState) {
        return blockState.hasProperty(BasicBurnerBlock.HEAT_LEVEL) ? blockState.getValue(BasicBurnerBlock.HEAT_LEVEL)
                : HeatLevel.NONE;
    }
    public static boolean getLitOf(BlockState blockState) { return blockState.getValue(BasicBurnerBlock.LIT); }

    public static boolean getEmpoweredOf(BlockState blockState) { return blockState.getValue(BasicBurnerBlock.EMPOWERED); }

    public static int getLight(BlockState state) {
        HeatLevel level = state.getValue(HEAT_LEVEL);
        return switch (level) {
            case NONE -> 0;
            default -> 15;
        };
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        IBE.onRemove(state, level, pos, newState);
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter worldIn, Entity entityIn) {
        super.updateEntityAfterFallOn(worldIn, entityIn);
        if (!(entityIn instanceof ItemEntity)) return;
        if (!entityIn.isAlive()) return;
        if (entityIn.level.isClientSide) return;

        BasicBurnerBlockEntity burner = null;
        for (BlockPos pos : Iterate.hereAndBelow(entityIn.blockPosition()))
            if (burner == null) burner = getBlockEntity(worldIn, pos);

        if (burner == null) return;

        ItemEntity itemEntity = (ItemEntity) entityIn;
        LazyOptional<IItemHandler> capability = burner.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        if (!capability.isPresent())
            return;

        ItemStack remainder = capability.orElse(new ItemStackHandler())
                .insertItem(0, itemEntity.getItem(), false);
        if (remainder.isEmpty()) itemEntity.discard();
        if (remainder.getCount() < itemEntity.getItem().getCount()) itemEntity.setItem(remainder);
    }
}