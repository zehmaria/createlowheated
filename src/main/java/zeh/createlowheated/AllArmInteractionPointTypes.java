package zeh.createlowheated;

import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import com.simibubi.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes.DepositOnlyArmInteractionPoint;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.Containers;

import zeh.createlowheated.content.processing.charcoal.CharcoalBurnerBlock;

import java.util.function.Function;

public class AllArmInteractionPointTypes {

    private static final CharcoalBurnerType CHARCOAL_BURNER = register("charcoal_burner", CharcoalBurnerType::new);

    private static <T extends ArmInteractionPointType> T register(String id, Function<ResourceLocation, T> factory) {
        T type = factory.apply(CreateLowHeated.asResource(id));
        ArmInteractionPointType.register(type);
        return type;
    }

    public static void register() {}

    public static class CharcoalBurnerType extends ArmInteractionPointType {
        public CharcoalBurnerType(ResourceLocation id) { super(id); }

        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return AllBlocks.CHARCOAL_BURNER.has(state);
        }

        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new CharcoalBurnerPoint(this, level, pos, state);
        }
    }

    public static class CharcoalBurnerPoint extends DepositOnlyArmInteractionPoint {
        public CharcoalBurnerPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
            super(type, level, pos, state);
        }

        @Override
        public ItemStack insert(ItemStack stack, boolean simulate) {
            ItemStack input = stack.copy();
            InteractionResultHolder<ItemStack> res = CharcoalBurnerBlock
                    .tryInsert(cachedState, level, pos, input, false, false, simulate);
            ItemStack remainder = res.getObject();
            if (input.isEmpty()) return remainder;
            else {
                if (!simulate) Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), remainder);
                return input;
            }
        }
    }
}
