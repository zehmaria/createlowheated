package zeh.createlowheated;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class CreateLowHeatedClient {

    public static void onCtorClient(IEventBus modEventBus, IEventBus forgeEventBus) {
        modEventBus.addListener(CreateLowHeatedClient::clientInit);
    }

    public static void clientInit(final FMLClientSetupEvent event) {
    }

}