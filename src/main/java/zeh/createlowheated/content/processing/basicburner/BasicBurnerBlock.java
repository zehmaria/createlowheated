package zeh.createlowheated.content.processing.basicburner;

import java.util.Random;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlock;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.foundation.block.IBE;

import net.createmod.catnip.data.Iterate;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import zeh.createlowheated.AllBlockEntityTypes;
import zeh.createlowheated.AllShapes;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import zeh.createlowheated.AllTags;
import zeh.createlowheated.common.Configuration;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BasicBurnerBlock extends HorizontalDirectionalBlock implements IBE<BasicBurnerBlockEntity>, IWrenchable {

    public static final EnumProperty<HeatLevel> HEAT_LEVEL = EnumProperty.create("low", HeatLevel.class);
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty FUELED = BooleanProperty.create("fueled");
    public static final BooleanProperty EMPOWERED = BooleanProperty.create("empowered");
    public static final IntegerProperty DUNSWE = IntegerProperty.create("dunswe", 0B000000, 0B111111);
    public static final MapCodec<BasicBurnerBlock> CODEC = simpleCodec(BasicBurnerBlock::new);

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    public BasicBurnerBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(HEAT_LEVEL, HeatLevel.NONE)
                .setValue(LIT, false)
                .setValue(FUELED, false)
                .setValue(EMPOWERED, false)
                .setValue(DUNSWE, 0B000000));
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(HEAT_LEVEL, LIT, FUELED, EMPOWERED, DUNSWE, FACING);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState p_220082_4_, boolean p_220082_5_) {
        if (world.isClientSide) return;
        BlockEntity blockEntity = world.getBlockEntity(pos.above());
        if (!(blockEntity instanceof BasinBlockEntity basin)) return;
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
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack heldItem = player.getItemInHand(hand);
        boolean wasEmptyHanded = heldItem.isEmpty() && hand == InteractionHand.MAIN_HAND;
        boolean shouldntPlaceItem = AllBlocks.MECHANICAL_ARM.isIn(heldItem);

        if (!state.hasBlockEntity()) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        BlockEntity be = getBlockEntity(level, pos);
        if (!(be instanceof BasicBurnerBlockEntity burnerBE)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (!burnerBE.inputInv.getStackInSlot(0).isEmpty() && !state.getValue(LIT) && AllTags.AllItemTags.BURNER_STARTERS.matches(heldItem)) {
            level.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F,
                    level.random.nextFloat() * 0.4F + 0.8F);
            if (level.isClientSide) return ItemInteractionResult.SUCCESS;
            heldItem.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);

            level.setBlockAndUpdate(pos, state.setValue(BasicBurnerBlock.LIT, true));
            burnerBE.notifyUpdate();
            return ItemInteractionResult.SUCCESS;
        }
        ItemStack mainItemStack = burnerBE.inputInv.getStackInSlot(0);

        if (!mainItemStack.isEmpty() && wasEmptyHanded) {
            player.getInventory().placeItemBackInInventory(mainItemStack);
            burnerBE.inputInv.setStackInSlot(0, ItemStack.EMPTY);
            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f,
                    1f + Create.RANDOM.nextFloat());
        }

        if (!wasEmptyHanded && !shouldntPlaceItem) {
            ItemStack remainder = burnerBE.itemHandler.insertItem(0, heldItem.copy(), false);
            if (remainder.getCount() == heldItem.getCount()) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            player.setItemInHand(hand, remainder);
            AllSoundEvents.DEPOT_SLIDE.playOnServer(level, pos);
        }

        if (!burnerBE.inputInv.getStackInSlot(0).isEmpty() && !state.getValue(LIT) && Configuration.IGNORES_BURNER_STARTERS.get()) {
            level.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.4F + 0.8F);
            if (level.isClientSide) return ItemInteractionResult.SUCCESS;
            level.setBlockAndUpdate(pos, state.setValue(BasicBurnerBlock.LIT, true));
        }

        burnerBE.notifyUpdate();
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean isEmpowered = false;
        int dunswe = 0B000000;
        BlockPos burnerPos = context.getClickedPos();
        for (Direction side : Iterate.directions) {
            BlockPos fanPos = burnerPos.relative(side);
            BlockEntity fan = context.getLevel().getBlockEntity(fanPos);
            if (!(fan instanceof EncasedFanBlockEntity fanBE)) continue;

            Direction fanFacingDir = fan.getBlockState().getValue(EncasedFanBlock.FACING);
            BlockPos fanFacingPos = fanPos.relative(fanFacingDir);
            if (!burnerPos.equals(fanFacingPos)) continue;

            boolean empowering = (Mth.abs(fanBE.getSpeed()) >= Configuration.FAN_SPEED_REQUIRED.get());
            if (empowering) {
                int mask = 0B100000 >> side.ordinal();
                dunswe = dunswe | mask;
            }
            if (Configuration.FAN_HORIZONTAL_ONLY.get() && !side.getAxis().isHorizontal()) continue;
            if (!isEmpowered) {isEmpowered = true;}
        }
        return super.getStateForPlacement(context).setValue(EMPOWERED, isEmpowered).setValue(DUNSWE, dunswe);
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
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, Level world, BlockPos pos, Random random) {
        if (random.nextInt(10) != 0) return;
        if (!state.getValue(HEAT_LEVEL).equals(HeatLevel.NONE)) return;
        //if (!state.getValue(HEAT_LEVEL).isAtLeast(HeatLevel.valueOf("LOW"))) return;
        world.playLocalSound((float) pos.getX() + 0.5F, (float) pos.getY() + 0.5F,
                (float) pos.getZ() + 0.5F, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS,
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
        if (!(entityIn instanceof ItemEntity itemEntity)) return;
        if (!entityIn.isAlive()) return;
        if (entityIn.level().isClientSide) return;

        BasicBurnerBlockEntity burner = null;
        for (BlockPos pos : Iterate.hereAndBelow(entityIn.blockPosition()))
            if (burner == null) burner = getBlockEntity(worldIn, pos);

        if (burner == null) return;

        IItemHandler capability = burner.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, burner.getBlockPos(), null);
        if (capability == null) return;

        ItemStack remainder = capability.insertItem(0, itemEntity.getItem(), false);
        if (remainder.isEmpty()) itemEntity.discard();
        if (remainder.getCount() < itemEntity.getItem().getCount()) itemEntity.setItem(remainder);
    }
}