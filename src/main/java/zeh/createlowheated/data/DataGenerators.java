package zeh.createlowheated.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.data.event.GatherDataEvent;
import zeh.createlowheated.CreateLowHeated;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = CreateLowHeated.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();

        BlockTags blockTags = new BlockTags(generator, CreateLowHeated.ID, helper);
        generator.addProvider(event.includeServer(), blockTags);
        //generator.addProvider(event.includeServer(), new ItemTags(generator, blockTags, CreateLowHeated.ID, helper));
        //generator.addProvider(event.includeServer(), new EntityTags(generator, CreateLowHeated.ID, helper));
        //generator.addProvider(event.includeServer(), new Recipes(generator));
        //generator.addProvider(event.includeServer(), new Advancements(generator));

        //lockStates blockStates = new BlockStates(generator, helper);
        //generator.addProvider(event.includeClient(), blockStates);
        //generator.addProvider(event.includeClient(), new ItemModels(generator, blockStates.models().existingFileHelper));
    }
}