package dev.starcrownqa;
@net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid="starcrownqa",value=net.minecraftforge.api.distmarker.Dist.CLIENT)
public final class SkinRenderFault {
 @net.minecraftforge.eventbus.api.SubscribeEvent(priority=net.minecraftforge.eventbus.api.EventPriority.HIGHEST)
 public static void player(net.minecraftforge.client.event.RenderPlayerEvent.Pre e){if(java.nio.file.Files.exists(java.nio.file.Path.of("enable-skin-fault"))){e.getRenderer().getModel().setAllVisible(false);e.setCanceled(true);}}
 @net.minecraftforge.eventbus.api.SubscribeEvent(priority=net.minecraftforge.eventbus.api.EventPriority.HIGHEST)
 public static void living(net.minecraftforge.client.event.RenderLivingEvent.Pre<?,?> e){if(e.getEntity() instanceof net.minecraft.world.entity.player.Player&&java.nio.file.Files.exists(java.nio.file.Path.of("enable-skin-fault"))){if(e.getRenderer().getModel() instanceof net.minecraft.client.model.PlayerModel<?> model)model.setAllVisible(false);e.setCanceled(true);}}
}
