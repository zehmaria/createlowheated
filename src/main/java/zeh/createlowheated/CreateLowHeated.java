package zeh.createlowheated;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.LangMerger;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipHelper.Palette;
import com.simibubi.create.foundation.item.TooltipModifier;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.ModLoadingContext;

import org.slf4j.Logger;
import zeh.createlowheated.foundation.data.AllLangPartials;
import zeh.createlowheated.foundation.data.TagGen;

@Mod(zeh.createlowheated.CreateLowHeated.ID)
public class CreateLowHeated {

    public static final String ID = "createlowheated";
    public static final String NAME = "Create Low-Heated";

    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(ID);

    static {
        REGISTRATE.setTooltipModifierFactory(item -> {
            return new ItemDescription.Modifier(item, Palette.STANDARD_CREATE)
                    .andThen(TooltipModifier.mapNull(KineticStats.create(item)));
        });
    }

    public CreateLowHeated() {
        onCtor();
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static void onCtor() {
        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        REGISTRATE.registerEventListeners(modEventBus);

        AllTags.init();
        AllCreativeModeTabs.init();

        AllBlocks.register();
        AllItems.register();
        //AllFluids.register();
        AllBlockEntityTypes.register();
        // AllRecipeTypes.register(modEventBus);

        //ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Configuration.COMMON_CONFIG);

        AllArmInteractionPointTypes.register();
        // BlockSpoutingBehaviour.registerDefaults();

        modEventBus.addListener(CreateLowHeated::init);
        modEventBus.addListener(EventPriority.LOWEST, CreateLowHeated::gatherData);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> CreateLowHeatedClient.onCtorClient(modEventBus, forgeEventBus));
    }

    public static void init(final FMLCommonSetupEvent event) {
    }

    public static void gatherData(GatherDataEvent event) {
        TagGen.datagen();
        DataGenerator gen = event.getGenerator();
        if (event.includeClient()) {
            gen.addProvider(new LangMerger(gen, ID, NAME, AllLangPartials.values()));
        }
        if (event.includeServer()) {
            //gen.addProvider(new AllAdvancements(gen));
            //gen.addProvider(new StandardRecipeGen(gen));
            //ProcessingRecipeGen.registerAll(gen);
        }
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(ID, path);
    }

}
