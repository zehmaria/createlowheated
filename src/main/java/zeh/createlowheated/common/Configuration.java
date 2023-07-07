package zeh.createlowheated.common;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Configuration {

    public static ForgeConfigSpec COMMON_CONFIG;

	public static ForgeConfigSpec.IntValue WATERWHEELS_THRESHOLD;
	public static ForgeConfigSpec.IntValue WATERWHEELS_RANGE;

    static {

		ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

	    COMMON_BUILDER.comment("#Waterwheel requirements").push("waterwheels");
	    WATERWHEELS_THRESHOLD = COMMON_BUILDER.comment("The minimum amount of fluid blocks the waterwheel needs to find before rotation begins.")
				.defineInRange("waterwheelThreshold", 3200, 1, Integer.MAX_VALUE);

		WATERWHEELS_RANGE = COMMON_BUILDER.comment("The maximum distance a waterwheel can consider fluid blocks from.")
				.defineInRange("waterwheelRange", 128, 1, Integer.MAX_VALUE);

		COMMON_BUILDER.pop();

		COMMON_CONFIG = COMMON_BUILDER.build();

	}
}
