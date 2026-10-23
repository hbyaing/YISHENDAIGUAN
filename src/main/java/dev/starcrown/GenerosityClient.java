package dev.starcrown;
@net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid=StarcrownDoll.ID,value=net.minecraftforge.api.distmarker.Dist.CLIENT)
public final class GenerosityClient {
 @net.minecraftforge.eventbus.api.SubscribeEvent public static void slots(net.minecraftforge.client.event.ScreenEvent.Render.Post event){
  if(!(event.getScreen() instanceof net.minecraft.client.gui.screens.inventory.AbstractContainerScreen<?> screen))return;
  for(var slot:screen.getMenu().slots)if(slot.container instanceof net.minecraft.world.entity.player.Inventory inv&&YuleGenerosity.locked(inv.player,slot.getSlotIndex())){
   int x=screen.getGuiLeft()+slot.x,y=screen.getGuiTop()+slot.y;event.getGuiGraphics().fill(x,y,x+16,y+16,0xA04A1828);event.getGuiGraphics().drawString(net.minecraft.client.Minecraft.getInstance().font,"×",x+5,y+3,0xFFCC7799,false);
  }
 }
}
