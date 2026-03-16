package zeh.createlowheated.compat;

import mcjty.theoneprobe.api.CompoundText;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.InterModComms;
import zeh.createlowheated.CreateLowHeated;
import zeh.createlowheated.content.processing.basicburner.BasicBurnerBlock;
import zeh.createlowheated.content.processing.basicburner.BasicBurnerBlockEntity;

import java.util.function.Function;

public class TOPCompat {

    public static void register() {
        InterModComms.sendTo("theoneprobe", "getTheOneProbe", PluginTOPRegistry::new);
    }

    public static class PluginTOPRegistry implements Function<ITheOneProbe, Void> {

        @Override
        public Void apply(ITheOneProbe probe) {
            probe.registerProvider(new IProbeInfoProvider() {

                @Override
                public ResourceLocation getID() {
                    return CreateLowHeated.asResource("basic_burner_info");
                }

                @Override
                public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player player, Level level, BlockState blockState, IProbeHitData iProbeHitData) {
                    BlockEntity blockEntity = level.getBlockEntity(iProbeHitData.getPos());
                    if (!(blockEntity instanceof BasicBurnerBlockEntity basicBurnerBlockEntity) ||
                            !(blockState.getBlock() instanceof BasicBurnerBlock basicBurnerBlock)) {
                        return;
                    }
                    if (basicBurnerBlockEntity.getRemainingBurnTime() > 0) {
                        iProbeInfo.horizontal().text(CompoundText.create().label(
                                Component.translatable(basicBurnerBlockEntity.getHeatLevelKey())
                                        .append(": " + litTime(basicBurnerBlockEntity) + "s")));
                    }
                }
            });
            return null;
        }
        public int litTime(BasicBurnerBlockEntity entity) {
            return entity.getRemainingBurnTime() / 20 /
                    (entity.getEmpoweredFromBlock() ? entity.getFanMultiplier() : entity.getBaseMultiplier());
        }
    }
}