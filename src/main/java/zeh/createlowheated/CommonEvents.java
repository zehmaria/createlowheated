package zeh.createlowheated;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import zeh.createlowheated.content.processing.basicburner.BasicBurnerBlockEntity;

public class CommonEvents {

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void registerCapabilities(RegisterCapabilitiesEvent event) {
            BasicBurnerBlockEntity.registerCapabilities(event);
        }
    }

}
