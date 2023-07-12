package zeh.createlowheated.common;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Configuration {

    public static ForgeConfigSpec COMMON_CONFIG;

	public static ForgeConfigSpec.IntValue FAN_MULTIPLIER;

    static {

		ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

		COMMON_BUILDER.comment("#Charcoal Burner Requirements").push("charcoal_burner");
		FAN_MULTIPLIER = COMMON_BUILDER.comment("How much more a burner consumes when empowered by a maxed encased fan.")
				.defineInRange("fanMultiplier", 100, 1, Integer.MAX_VALUE);

		COMMON_BUILDER.pop();

		COMMON_CONFIG = COMMON_BUILDER.build();

	}
}
