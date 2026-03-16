package zeh.createlowheated;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;

import net.createmod.catnip.lang.FontHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;
import zeh.createlowheated.common.Configuration;
import zeh.createlowheated.compat.TOPCompat;
import zeh.createlowheated.infrastructure.data.CreateLowHeatedDatagen;
import zeh.createlowheated.infrastructure.data.LHRegistrate;

@Mod(zeh.createlowheated.CreateLowHeated.ID)
public class CreateLowHeated {

    public static final String ID = "createlowheated";
    public static final String NAME = "Create Low-Heated";

    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static final LHRegistrate REGISTRATE = LHRegistrate.create(ID)
            .defaultCreativeTab((ResourceKey<CreativeModeTab>) null)
            .setTooltipModifierFactory(item ->
                    new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                            .andThen(TooltipModifier.mapNull(KineticStats.create(item)))
            );

    public CreateLowHeated(IEventBus eventBus, ModContainer modContainer) {
        onCtor(eventBus, modContainer);
    }

    public static void onCtor(IEventBus modEventBus, ModContainer modContainer) {

        modEventBus.addListener(EventPriority.HIGH, CreateLowHeatedDatagen::gatherData);
        REGISTRATE.registerEventListeners(modEventBus);

        AllTags.init();
        AllCreativeModeTabs.register(modEventBus);

        AllBlocks.register();
        AllBlockEntityTypes.register();

        modContainer.registerConfig(ModConfig.Type.COMMON, Configuration.COMMON_CONFIG);

        modEventBus.addListener(CreateLowHeated::onRegister);

        //The One Probe registration.
        if (ModList.get().isLoaded("theoneprobe")) {
            TOPCompat.register();
        }
    }

    public static void onRegister(final RegisterEvent event) {
        AllArmInteractionPointTypes.init();
    }
    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(ID, path);
    }

}
