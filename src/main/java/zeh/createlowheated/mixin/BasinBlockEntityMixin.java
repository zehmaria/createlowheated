package zeh.createlowheated.mixin;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import zeh.createlowheated.content.processing.basicburner.BasicBurnerBlock;

@Mixin(value = BasinBlockEntity.class, remap = false)
public class BasinBlockEntityMixin {

    /**
     * @author ZehMaria
     * @reason Adds Basic Burner as SMOULDERING, removes all passive heaters.
     */
    @Overwrite
    public static BlazeBurnerBlock.HeatLevel getHeatLevelOf(BlockState state) {
        if (state.hasProperty(BlazeBurnerBlock.HEAT_LEVEL)) return state.getValue(BlazeBurnerBlock.HEAT_LEVEL);
        if (state.hasProperty(BasicBurnerBlock.HEAT_LEVEL)) return state.getValue(BasicBurnerBlock.HEAT_LEVEL);
        return BlazeBurnerBlock.HeatLevel.NONE;
    }
}
