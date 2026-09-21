package zeh.createlowheated.mixin;

import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import zeh.createlowheated.compat.Mods;

public class Plugin implements IMixinConfigPlugin {

    @Override public void onLoad(String mixinPackage) {}
    @Override public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        //if (mixinClassName.startsWith("zehmaria.mingle.mixin.compat.xaeros") && !Mods.XAEROWORLDMAP.isLoaded())
        return switch (mixinClassName) {
            case "zeh.createlowheated.mixin.CreateAdditionMixin" -> Mods.CREATEADDITION.isLoaded();
            default -> true;
        };
    }

    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
    @Override public List<String> getMixins() { return null; }
    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

}