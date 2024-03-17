package zeh.createlowheated;

import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import com.simibubi.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes.DepositOnlyArmInteractionPoint;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public class AllArmInteractionPointTypes {

    private static final BasicBurnerType BASIC_BURNER = register("basic_burner", BasicBurnerType::new);

    private static <T extends ArmInteractionPointType> T register(String id, Function<ResourceLocation, T> factory) {
        T type = factory.apply(CreateLowHeated.asResource(id));
        ArmInteractionPointType.register(type);
        return type;
    }

    public static void register() {}

    public static class BasicBurnerType extends ArmInteractionPointType {
        public BasicBurnerType(ResourceLocation id) { super(id); }

        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return AllBlocks.BASIC_BURNER.has(state);
        }

        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new BasicBurnerPoint(this, level, pos, state);
        }
    }

    public static class BasicBurnerPoint extends DepositOnlyArmInteractionPoint {
        public BasicBurnerPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
            super(type, level, pos, state);
        }
    }
}
