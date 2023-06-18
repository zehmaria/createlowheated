package zeh.createlowheated.common;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Configuration {

    public static ForgeConfigSpec COMMON_CONFIG;

	public static ForgeConfigSpec.IntValue WATERWHEELS_THRESHOLD;
	public static ForgeConfigSpec.IntValue WATERWHEELS_RANGE;

	public static ForgeConfigSpec.IntValue WINDMILLS_THRESHOLD;
	public static ForgeConfigSpec.IntValue WINDMILLS_REQUIRED_RANGE;

	public static ForgeConfigSpec.IntValue WINDMILLS_REQUIRED_RANGE_POINTS;
	public static ForgeConfigSpec.IntValue WINDMILLS_MAX_RANGE;
	public static ForgeConfigSpec.IntValue WINDMILLS_ABOVEX;

	public static ForgeConfigSpec.IntValue WINDMILLS_ABOVE;

    static {

		ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

	    COMMON_BUILDER.comment("#Waterwheel requirements").push("waterwheels");
	    WATERWHEELS_THRESHOLD = COMMON_BUILDER.comment("The minimum amount of fluid blocks the waterwheel needs to find before rotation begins.")
				.defineInRange("waterwheelThreshold", 3200, 1, Integer.MAX_VALUE);

		WATERWHEELS_RANGE = COMMON_BUILDER.comment("The maximum distance a waterwheel can consider fluid blocks from.")
				.defineInRange("waterwheelRange", 128, 1, Integer.MAX_VALUE);

		COMMON_BUILDER.pop();

		COMMON_BUILDER.comment("#Windmill requirements").push("windmills");
		WINDMILLS_THRESHOLD = COMMON_BUILDER.comment("The minimum floor area required. Default: 1/4 of the max area [PI * 32 ^ 2]")
				.defineInRange("windmillThreshold", 804, 1, Integer.MAX_VALUE);

		WINDMILLS_REQUIRED_RANGE = COMMON_BUILDER.comment("The minimum length of air current required.")
				.defineInRange("windmillRequiredRange", 24, 1, Integer.MAX_VALUE);

		WINDMILLS_REQUIRED_RANGE_POINTS = COMMON_BUILDER.comment("The minimum amount of points that must hit windmillRequiredRange.")
				.defineInRange("windmillRequiredRangePoints", 128, 1, Integer.MAX_VALUE);

		WINDMILLS_MAX_RANGE = COMMON_BUILDER.comment("The maximum distance a waterwheel can consider air blocks from.")
				.defineInRange("windmillMaxRange", 32, 1, Integer.MAX_VALUE);

		WINDMILLS_ABOVEX = COMMON_BUILDER.comment("The multiplier for the benefit given for raised windmills.")
				.defineInRange("windmillAboveX", 4, 1, Integer.MAX_VALUE);

		WINDMILLS_ABOVE = COMMON_BUILDER.comment("The height required for the full benefit from windmillAboveX.")
				.defineInRange("windmillAbove", 16, 1, Integer.MAX_VALUE);


		COMMON_BUILDER.pop();

		COMMON_CONFIG = COMMON_BUILDER.build();

	}
}
