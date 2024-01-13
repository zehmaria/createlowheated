package zeh.createlowheated.mixin;

import com.simibubi.create.content.fluids.tank.BoilerHeaters;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import zeh.createlowheated.AllBlocks;
import zeh.createlowheated.content.processing.charcoal.CharcoalBurnerBlock;

@Mixin(value = BoilerHeaters.class, remap = false)
public class BoilerHeatersMixin {
    /**
     * @author ZehMaria
     * @reason Adds Charcoal Burner, removes all passive heaters.
     */
    @Overwrite
    public static void registerDefaults() {
        registerHeater(com.simibubi.create.AllBlocks.BLAZE_BURNER.get(), (level, pos, state) -> {
            HeatLevel value = state.getValue(BlazeBurnerBlock.HEAT_LEVEL);
            if (value == HeatLevel.NONE) return -1;
            if (value == HeatLevel.SEETHING) return 2;
            if (value.isAtLeast(HeatLevel.FADING)) return 1;
            return -1;
        });

        registerHeater(AllBlocks.CHARCOAL_BURNER.get(), (level, pos, state) -> {
            HeatLevel value = state.getValue(CharcoalBurnerBlock.HEAT_LEVEL);
            if (value == HeatLevel.NONE) return -1;
            if (value == HeatLevel.byIndex(5)) return 0;
            if (value == HeatLevel.SEETHING) return 2;
            if (value.isAtLeast(HeatLevel.FADING)) return 1;
            return -1;
        });

        registerHeaterProvider((level, pos, state) -> {
            //if (AllTags.AllBlockTags.PASSIVE_BOILER_HEATERS.matches(state)) return (level1, pos1, state1) -> 0;
            return null;
        });
    }

    @Shadow public static void registerHeaterProvider(BoilerHeaters.HeaterProvider provider) {}

    @Shadow public static void registerHeater(Block block, BoilerHeaters.Heater heater) {}

    /* For future reference
    @Inject(method = "apply(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;Z)Z",
            at = @At(value = "NEW", target = "()Ljava/util/ArrayList;", ordinal = 0), cancellable = true)
    private static void onApplyNew(BasinBlockEntity basin, Recipe<?> recipe, boolean test, CallbackInfoReturnable<Boolean> info) {
        //HeatLevel heat = BasinBlockEntity.getHeatLevelOf(basin.getLevel().getBlockState(basin.getBlockPos().below(1)));
        //if (recipe instanceof BasinRecipe && !((BasinRecipe) recipe).getRequiredHeat().testBlazeBurner(heat)) info.setReturnValue(false);
    }
     */

}
