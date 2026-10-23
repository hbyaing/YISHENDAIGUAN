package dev.starcrown;
@net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid=StarcrownDoll.ID)
public final class GenerosityReload {
 @net.minecraftforge.eventbus.api.SubscribeEvent public static void reload(net.minecraftforge.event.AddReloadListenerEvent event){event.addListener(new GenerosityUpgrades());}
}
