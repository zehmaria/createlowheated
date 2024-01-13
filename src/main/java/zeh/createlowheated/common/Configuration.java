package zeh.createlowheated.common;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Configuration {

    public static ForgeConfigSpec COMMON_CONFIG;

	public static ForgeConfigSpec.IntValue FAN_MULTIPLIER;
	public static ForgeConfigSpec.IntValue BASE_MULTIPLIER;
	public static ForgeConfigSpec.BooleanValue HOT_BURNERS;

    static {

		ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

		COMMON_BUILDER.comment("#Charcoal Burner Requirements").push("charcoal_burner");

		HOT_BURNERS = COMMON_BUILDER.comment("When set to True, an active Charcoal Burner produces the same heat as a Kindled Blaze Burner and an empowered Charcoal Burner produces the same heat as a Seething Blaze Burner. Inactive state is unaffected.")
				.define("hotBurners", false);

		BASE_MULTIPLIER = COMMON_BUILDER.comment("How much more fuel a non-empowered Charcoal Burner consumes. Use fanMultiplier for fan-empowered burners. Intended for use with the hotBurners option, the default value of 1 is recommended otherwise.")
				.defineInRange("baseMultiplier", 1, 1, Integer.MAX_VALUE);

		FAN_MULTIPLIER = COMMON_BUILDER.comment("How much more fuel a Charcoal Burner consumes when empowered by a maxed encased fan.")
				.defineInRange("fanMultiplier", 100, 1, Integer.MAX_VALUE);

		COMMON_BUILDER.pop();

		COMMON_CONFIG = COMMON_BUILDER.build();

	}
}
