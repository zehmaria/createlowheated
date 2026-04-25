package zeh.createlowheated.common;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Configuration {

    public static ForgeConfigSpec COMMON_CONFIG;
	public static ForgeConfigSpec.IntValue FAN_MULTIPLIER;
	public static ForgeConfigSpec.IntValue FAN_SPEED_REQUIRED;
	public static ForgeConfigSpec.BooleanValue FAN_HORIZONTAL_ONLY;
	public static ForgeConfigSpec.IntValue BASE_MULTIPLIER;
	public static ForgeConfigSpec.BooleanValue HOT_BURNERS;
	public static ForgeConfigSpec.BooleanValue IGNORES_BURNER_STARTERS;
	public static ForgeConfigSpec.BooleanValue IGNORES_FUEL_TAG_WHITELIST;
	public static ForgeConfigSpec.BooleanValue BASIC_BURNER_BOILER;
	public static ForgeConfigSpec.BooleanValue PASSIVE_BOILER_HEATERS_TAG;
	public static ForgeConfigSpec.BooleanValue DISPENSER_BURNER;

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

		PASSIVE_BOILER_HEATERS_TAG = COMMON_BUILDER.comment("When set to true, it re-enables all passive heaters.")
				.define("passiveBoilerHeatersTag", false);

		IGNORES_FUEL_TAG_WHITELIST = COMMON_BUILDER.comment("When set to true, ignores Basic Burner Fuel Item " +
						"Tag Whitelist, instead accepts anything with a valid BurnTime.")
				.define("ignoresFuelTagWhitelist", true);

		IGNORES_BURNER_STARTERS = COMMON_BUILDER.comment("When set to true, basic burners lit up whenever fuel is inserted," +
						"Ignoring the burner starters item tag requirement.")
				.define("ignoresBurnerStarters", false);
		
		BASE_MULTIPLIER = COMMON_BUILDER.comment("How much more fuel a non-empowered Basic Burner consumes. " +
						"Use fanMultiplier for fan-empowered burners. Intended for use with the hotBurners option, " +
						"the default value of 1 is recommended otherwise.")
				.defineInRange("baseMultiplier", 1, 1, Integer.MAX_VALUE);
		
		FAN_MULTIPLIER = COMMON_BUILDER.comment("How much more fuel a Basic Burner consumes when empowered by an encased fan.")
				.defineInRange("fanMultiplier", 32, 1, Integer.MAX_VALUE);

		FAN_SPEED_REQUIRED = COMMON_BUILDER.comment("How much fan speed is needed for the Basic Burner to be empowered.")
				.defineInRange("fanSpeedRequired", 256, 1, Integer.MAX_VALUE);

		FAN_HORIZONTAL_ONLY = COMMON_BUILDER.comment("Fan direction preference.")
				.define("fanHorizontalOnly", true);

		DISPENSER_BURNER = COMMON_BUILDER.comment("Dispenser interacts with burner.")
				.define("dispenserBurner", true);

		COMMON_BUILDER.pop();

		COMMON_CONFIG = COMMON_BUILDER.build();

	}
}
