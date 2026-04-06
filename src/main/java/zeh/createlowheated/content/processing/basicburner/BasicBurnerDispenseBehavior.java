package zeh.createlowheated.content.processing.basicburner;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import zeh.createlowheated.AllTags;

public class BasicBurnerDispenseBehavior extends OptionalDispenseItemBehavior {
    public static final BasicBurnerDispenseBehavior INSTANCE = new BasicBurnerDispenseBehavior();

    public final ItemStack dispense(BlockSource source, ItemStack stack, DispenseItemBehavior defaults) {
        setSuccess(source, stack);
        return this.isSuccess() ? this.dispense(source, stack) :
                (defaults != DispenseItemBehavior.NOOP ? defaults.dispense(source, stack) : stack);
    }

    @NotNull
    @Override
    protected ItemStack execute(@NotNull BlockSource source, @NotNull ItemStack stack) {
        stack.hurt(1, source.getLevel().getRandom(), null);
        return stack;
    }

    public void setSuccess(BlockSource source, ItemStack stack) {
        setSuccess(false);
        Level level = source.getLevel();
        BlockPos pos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
        BlockState state = level.getBlockState(pos);

        if (!state.hasBlockEntity()) return;
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof BasicBurnerBlockEntity burnerBE)) return;
        if (!burnerBE.inputInv.getStackInSlot(0).isEmpty()
                && !state.getValue(BasicBurnerBlock.LIT)
                && stack.is(AllTags.AllItemTags.BURNER_STARTERS.tag)) {
            level.setBlockAndUpdate(pos, state.setValue(BasicBurnerBlock.LIT, true));
            burnerBE.notifyUpdate();
            setSuccess(true);
        }
    }

}
