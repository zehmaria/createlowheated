package zeh.createlowheated.mixin;

import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import com.simibubi.create.foundation.ponder.PonderStoryBoardEntry;
import com.simibubi.create.foundation.ponder.PonderTag;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import com.simibubi.create.foundation.ponder.PonderStoryBoardEntry.PonderStoryBoard;

@Mixin(value = PonderRegistrationHelper.class, remap = false)
public abstract class PonderRegistrationHelperMixin {

    @Shadow
    public abstract ResourceLocation asLocation(String path);

    @Shadow
    public abstract PonderStoryBoardEntry addStoryBoard(ItemProviderEntry<?> component,
                                                        ResourceLocation schematicLocation,
                                                        PonderStoryBoard storyBoard,
                                                        PonderTag... tags);

    /**
     * @author ZehMaria
     * @reason Changing Water Wheel to require a big body of water [like the Hose Pulley] and a River Biomes.
     */
    @Overwrite
    public PonderStoryBoardEntry addStoryBoard(ItemProviderEntry<?> component,
                                               String schematicPath,
                                               PonderStoryBoard storyBoard,
                                               PonderTag... tags) {
        PonderStoryBoardEntry result;
        if (schematicPath.equals("large_water_wheel") || schematicPath.equals("water_wheel")) {
            result = addStoryBoard(component, asLocation(schematicPath + '2'), storyBoard, tags);
        } else {
            result = addStoryBoard(component, asLocation(schematicPath), storyBoard, tags);
        }
        return result;
    }

}
