package zeh.createlowheated.compat;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;
import zeh.createlowheated.CreateLowHeated;
import zeh.createlowheated.content.processing.basicburner.BasicBurnerBlock;
import zeh.createlowheated.content.processing.basicburner.BasicBurnerBlockEntity;

@SuppressWarnings("unused")
@WailaPlugin()
public class JadeCompat implements IWailaPlugin, IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(this, BasicBurnerBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(this, BasicBurnerBlock.class);
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig pluginConfig) {
        CompoundTag serverData = accessor.getServerData();
        if (serverData.contains("remainingLitTime")) {
            tooltip.add(Component.translatable(serverData.getString("heatLevelKey")));
            tooltip.append(Component.literal(": "));
            tooltip.append(IThemeHelper.get().seconds(serverData.getInt("remainingLitTime"), 20));
        }
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor blockAccessor) {
        BlockState blockState = blockAccessor.getBlockState();

        if (blockAccessor.getBlockEntity() instanceof BasicBurnerBlockEntity basicBurnerBlockEntity
                && blockState.getBlock() instanceof BasicBurnerBlock basicBurnerBlock) {
            if (basicBurnerBlockEntity.getRemainingBurnTime() > 0) {
                data.putString("heatLevelKey", basicBurnerBlockEntity.getHeatLevelKey());
                data.putInt("remainingLitTime", litTicks(basicBurnerBlockEntity));
            }
        }
    }

    public int litTicks(BasicBurnerBlockEntity entity) {
        return entity.getRemainingBurnTime() / (entity.getEmpoweredFromBlock() ? entity.getFanMultiplier() : entity.getBaseMultiplier());
    }

    @Override
    public ResourceLocation getUid() {
        return CreateLowHeated.asResource("basic_burner_info");
    }
}