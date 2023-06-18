package zeh.createlowheated.content.processing.charcoal;

import java.util.Random;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.foundation.block.IBE;

import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
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
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayer;

import zeh.createlowheated.AllBlockEntityTypes;
import zeh.createlowheated.AllBlocks;
import zeh.createlowheated.AllShapes;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import zeh.createlowheated.CreateLowHeated;
import zeh.createlowheated.mixin.HeatLevelMixin;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CharcoalBurnerBlock extends HorizontalDirectionalBlock implements IBE<CharcoalBurnerBlockEntity>, IWrenchable {

    public static final EnumProperty<HeatLevel> HEAT_LEVEL = EnumProperty.create("charcoal", HeatLevel.class);
    public static final BooleanProperty LIT = BooleanProperty.create("lit");
    public static final BooleanProperty FUELED = BooleanProperty.create("fueled");
    public static final BooleanProperty EMPOWERED = BooleanProperty.create("empowered");

    public CharcoalBurnerBlock(Properties properties) {
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
    public Class<CharcoalBurnerBlockEntity> getBlockEntityClass() {
        return CharcoalBurnerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CharcoalBurnerBlockEntity> getBlockEntityType() {
        return AllBlockEntityTypes.CHARCOAL_HEATER.get();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return IBE.super.newBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockRayTraceResult) {
        ItemStack heldItem = player.getItemInHand(hand);
        HeatLevel heat = state.getValue(HEAT_LEVEL);
        if (heldItem.isEmpty() && heat != HeatLevel.NONE) return onBlockEntityUse(world, pos, bbte -> { return InteractionResult.PASS; });

        if (state.getValue(FUELED) && !state.getValue(LIT) && heldItem.getItem() instanceof FlintAndSteelItem) {
            world.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F,
                    world.random.nextFloat() * 0.4F + 0.8F);
            if (world.isClientSide) return InteractionResult.SUCCESS;
            heldItem.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
            world.setBlockAndUpdate(pos, state.setValue(CharcoalBurnerBlock.LIT, true));
            return InteractionResult.SUCCESS;
        }

        boolean doNotConsume = player.isCreative();
        boolean forceOverflow = !(player instanceof FakePlayer);

        InteractionResultHolder<ItemStack> res = tryInsert(state, world, pos, heldItem, doNotConsume, forceOverflow, false);
        ItemStack leftover = res.getObject();
        if (!world.isClientSide && !doNotConsume && !leftover.isEmpty()) {
            if (heldItem.isEmpty()) player.setItemInHand(hand, leftover);
            else if (!player.getInventory().add(leftover)) player.drop(leftover, false);
        }

        return res.getResult() == InteractionResult.SUCCESS ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    public static InteractionResultHolder<ItemStack> tryInsert(BlockState state, Level world, BlockPos pos,
                                                               ItemStack stack, boolean doNotConsume, boolean forceOverflow, boolean simulate) {
        if (!state.hasBlockEntity()) return InteractionResultHolder.fail(ItemStack.EMPTY);
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof CharcoalBurnerBlockEntity)) return InteractionResultHolder.fail(ItemStack.EMPTY);

        CharcoalBurnerBlockEntity burnerBE = (CharcoalBurnerBlockEntity) be;
        if (!burnerBE.tryUpdateFuel(stack, forceOverflow, simulate)) return InteractionResultHolder.fail(ItemStack.EMPTY);

        if (((CharcoalBurnerBlockEntity) be).remainingBurnTime > 0 && !state.getValue(FUELED)) {
            world.setBlockAndUpdate(pos, state.setValue(CharcoalBurnerBlock.FUELED, true));
        }

        if (!doNotConsume) {
            ItemStack container = stack.hasCraftingRemainingItem() ? stack.getCraftingRemainingItem() : ItemStack.EMPTY;
            if (!world.isClientSide) stack.shrink(1);
            return InteractionResultHolder.success(container);
        }
        return InteractionResultHolder.success(ItemStack.EMPTY);
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
        return AllShapes.CHARCOAL_HEATER_BLOCK_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState p_220071_1_, BlockGetter p_220071_2_, BlockPos p_220071_3_,
                                        CollisionContext p_220071_4_) {
        //if (p_220071_4_ == CollisionContext.empty()) return AllShapes.HEATER_BLOCK_SPECIAL_COLLISION_SHAPE;
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
        return blockState.hasProperty(CharcoalBurnerBlock.HEAT_LEVEL) ? blockState.getValue(CharcoalBurnerBlock.HEAT_LEVEL)
                : HeatLevel.NONE;
    }
    public static boolean getLitOf(BlockState blockState) { return blockState.getValue(CharcoalBurnerBlock.LIT); }

    public static boolean getEmpoweredOf(BlockState blockState) { return blockState.getValue(CharcoalBurnerBlock.EMPOWERED); }

    public static int getLight(BlockState state) {
        HeatLevel level = state.getValue(HEAT_LEVEL);
        return switch (level) {
            case NONE -> 0;
            default -> 15;
        };
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            if (level.isClientSide()) return;
            BlockEntity tileEntity = level.getBlockEntity(pos);
            if (tileEntity instanceof CharcoalBurnerBlockEntity) {
                int n  = Mth.floor(((CharcoalBurnerBlockEntity) tileEntity).getRemainingBurnTime() / 1600);
                for (int i = 0; i < n; i++) {
                    ItemEntity charcoal = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), Items.CHARCOAL.getDefaultInstance());
                    level.addFreshEntity(charcoal);
                }
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
}