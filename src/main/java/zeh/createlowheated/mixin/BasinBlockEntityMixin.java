package zeh.createlowheated.mixin;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import zeh.createlowheated.content.processing.charcoal.CharcoalBurnerBlock;

@Mixin(value = BasinBlockEntity.class, remap = false)
public class BasinBlockEntityMixin {

    /**
     * @author ZehMaria
     * @reason Adds Charcoal Burner as SMOULDERING, removes all passive heaters.
     */
    @Overwrite
    public static BlazeBurnerBlock.HeatLevel getHeatLevelOf(BlockState state) {
        if (state.hasProperty(BlazeBurnerBlock.HEAT_LEVEL)) return state.getValue(BlazeBurnerBlock.HEAT_LEVEL);
        if (state.hasProperty(CharcoalBurnerBlock.HEAT_LEVEL)) return state.getValue(CharcoalBurnerBlock.HEAT_LEVEL);
        return BlazeBurnerBlock.HeatLevel.NONE;
    }
}
