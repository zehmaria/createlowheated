package zeh.createlowheated.common;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Configuration {

    public static ForgeConfigSpec COMMON_CONFIG;
	public static ForgeConfigSpec.IntValue FAN_MULTIPLIER;
	public static ForgeConfigSpec.IntValue FAN_SPEED_REQUIRED;
	public static ForgeConfigSpec.IntValue BASE_MULTIPLIER;
	public static ForgeConfigSpec.BooleanValue HOT_BURNERS;
	public static ForgeConfigSpec.BooleanValue IGNORES_FUEL_TAG_WHITELIST;
	public static ForgeConfigSpec.BooleanValue BASIC_BURNER_BOILER;

    static {

		ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

		COMMON_BUILDER.comment("#Basic Burner Requirements").push("basic_burner");
		
		HOT_BURNERS = COMMON_BUILDER.comment("When set to true, an active Basic Burner produces the same heat as " +
						"a Kindled Blaze Burner and an empowered Basic Burner produces the same heat as a Seething " +
						"Blaze Burner. Inactive state is unaffected.")
				.define("hotBurners", false);

		BASIC_BURNER_BOILER = COMMON_BUILDER.comment("When set to false, it disables Basic Burner role in the steam " +
						"engine heating system, only leaving it for recipes. This will also re-enable all " +
						"passive heaters as a consequence. The Basic Burner Block tooltip must be altered via " +
						"changing the lang file, if you use this and the inaccuracy bothers you.")
				.define("basicBurnerBoiler", true);

		IGNORES_FUEL_TAG_WHITELIST = COMMON_BUILDER.comment("When set to true, ignores Basic Burner Fuel Item " +
						"Tag Whitelist, instead accepts anything with a valid BurnTime.")
				.define("ignoresFuelTagWhitelist", true);

		
		BASE_MULTIPLIER = COMMON_BUILDER.comment("How much more fuel a non-empowered Basic Burner consumes. " +
						"Use fanMultiplier for fan-empowered burners. Intended for use with the hotBurners option, " +
						"the default value of 1 is recommended otherwise.")
				.defineInRange("baseMultiplier", 1, 1, Integer.MAX_VALUE);
		
		FAN_MULTIPLIER = COMMON_BUILDER.comment("How much more fuel a Basic Burner consumes when empowered by an encased fan.")
				.defineInRange("fanMultiplier", 32, 1, Integer.MAX_VALUE);

		FAN_SPEED_REQUIRED = COMMON_BUILDER.comment("How much fan speed is needed for the Basic Burner to be empowered.")
				.defineInRange("fanSpeedRequired", 256, 1, Integer.MAX_VALUE);

		COMMON_BUILDER.pop();

		COMMON_CONFIG = COMMON_BUILDER.build();

	}
}
