package dev.starcrown;
@net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid=StarcrownDoll.ID,value=net.minecraftforge.api.distmarker.Dist.CLIENT)
public final class YuleToleranceClient {
 @net.minecraftforge.eventbus.api.SubscribeEvent public static void attack(net.minecraftforge.client.event.InputEvent.InteractionKeyMappingTriggered event){if(event.isAttack()&&!(net.minecraft.client.Minecraft.getInstance().hitResult instanceof net.minecraft.world.phys.BlockHitResult)&&YuleTolerance.active(net.minecraft.client.Minecraft.getInstance().player)){event.setCanceled(true);event.setSwingHand(false);}}
}
