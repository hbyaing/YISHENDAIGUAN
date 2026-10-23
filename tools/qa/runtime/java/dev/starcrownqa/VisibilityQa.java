package dev.starcrownqa;
import dev.starcrown.*;
import java.nio.file.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.*;
@Mod.EventBusSubscriber(modid="starcrownqa",value=net.minecraftforge.api.distmarker.Dist.CLIENT)
public final class VisibilityQa {
 static int tick;static volatile boolean done,failed;
 static void check(boolean b,String label){if(!b)throw new AssertionError(label);try{Files.writeString(Path.of("visibility-results.txt"),"PASS "+label+"\n",StandardOpenOption.CREATE,StandardOpenOption.APPEND);}catch(Exception e){throw new RuntimeException(e);}}
 @SubscribeEvent public static void client(TickEvent.ClientTickEvent event)throws Exception{
  if(event.phase!=TickEvent.Phase.END||!Files.exists(Path.of("enable-visibility-qa")))return;
  var mc=net.minecraft.client.Minecraft.getInstance();mc.options.pauseOnLostFocus=false;
  if(mc.player==null||mc.getSingleplayerServer()==null)return;tick++;
  if(tick==1){Files.writeString(Path.of("visibility-results.txt"),"");mc.setScreen(null);mc.mouseHandler.releaseMouse();}
  if(tick==40){var id=mc.player.getUUID();mc.getSingleplayerServer().execute(()->{var p=mc.getSingleplayerServer().getPlayerList().getPlayer(id);try{
   var level=p.getServer().overworld();for(int x=-5;x<=5;x++)for(int z=-5;z<=7;z++){level.setBlockAndUpdate(new net.minecraft.core.BlockPos(x,80,z),net.minecraft.world.level.block.Blocks.STONE.defaultBlockState());for(int y=81;y<87;y++)level.setBlockAndUpdate(new net.minecraft.core.BlockPos(x,y,z),net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());}p.teleportTo(level,.5,81,4.5,180,0);level.setDayTime(6000);
   DivineAnchor.unlock(p);p.setItemSlot(EquipmentSlot.HEAD,ItemStack.EMPTY);p.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);
   p.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY,1200));p.setInvisible(true);check(p.isInvisible(),"unequipped invisibility works");
   p.setItemSlot(EquipmentSlot.HEAD,new ItemStack(StarcrownDoll.ITEM.get()));DivineWill.update(p,true);
   check(p.isInvisible()&&p.hasEffect(MobEffects.INVISIBILITY),"wearer retains invisibility status and flag");
   p.removeEffect(MobEffects.INVISIBILITY);check(p.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY,1200)),"new invisibility effect allowed");p.setInvisible(true);check(p.isInvisible(),"direct invisibility flag preserved");
   p.setRemainingFireTicks(200);check(!p.displayFireAnimation(),"third-person flame animation suppressed");DivineWill.update(p,true);check(p.getRemainingFireTicks()<=0,"burning cleared");
   p.tickCount=100;DivineWill.update(p,true);check(p.hasEffect(BenefitFixture.BENEFIT),"registered third-party beneficial effect automatically granted");
   check(DivineWill.automaticBenefit(MobEffects.REGENERATION)&&DivineWill.automaticBenefit(MobEffects.INVISIBILITY),"all vanilla beneficial effects included");
   DivineAnchor.unlock(p);p.setItemSlot(EquipmentSlot.HEAD,ItemStack.EMPTY);DivineWill.update(p,false);p.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY,1200));p.setInvisible(true);p.setRemainingFireTicks(200);check(p.isInvisible()&&p.displayFireAnimation(),"unequipping restores normal visibility and fire behavior");
   p.setItemSlot(EquipmentSlot.HEAD,new ItemStack(StarcrownDoll.ITEM.get()));DivineWill.update(p,true);
   var ghost=net.minecraftforge.registries.ForgeRegistries.MOB_EFFECTS.getValue(new net.minecraft.resources.ResourceLocation("cataclysm","ghost_form"));check(ghost!=null,"actual Cataclysm ghost_form loaded");check(p.hasEffect(ghost),"actual Cataclysm ghost form automatically granted");DivineWill.update(p,true);check(p.hasEffect(ghost)&&p.hasEffect(MobEffects.INVISIBILITY),"ghost form and invisibility both retained after update");
done=true;
  }catch(Throwable t){try{Files.writeString(Path.of("visibility-results.txt"),"FAIL "+t+"\n",StandardOpenOption.APPEND);}catch(Exception ignored){}failed=true;}});}
  if(failed){mc.stop();return;}
  if(tick==110&&done){
   check(mc.player.hasEffect(MobEffects.INVISIBILITY)&&!mc.player.displayFireAnimation(),"client retains invisibility effect and suppresses fire animation");
   var overlay=new net.minecraftforge.client.event.RenderBlockScreenEffectEvent(mc.player,event==null?null:new com.mojang.blaze3d.vertex.PoseStack(),net.minecraftforge.client.event.RenderBlockScreenEffectEvent.OverlayType.FIRE,net.minecraft.world.level.block.Blocks.FIRE.defaultBlockState(),mc.player.blockPosition());
   DivineVisibilityClient.overlay(overlay);check(overlay.isCanceled(),"first-person fire overlay canceled");
   var renderer=(net.minecraft.client.renderer.entity.player.PlayerRenderer)mc.getEntityRenderDispatcher().getRenderer(mc.player);
   renderer.getModel().setAllVisible(false);
   var pre=new net.minecraftforge.client.event.RenderPlayerEvent.Pre(mc.player,renderer,0,new com.mojang.blaze3d.vertex.PoseStack(),mc.renderBuffers().bufferSource(),15728880);pre.setCanceled(true);DivineVisibilityClient.player(pre);check(!pre.isCanceled()&&renderer.getModel().body.visible&&renderer.getModel().head.visible,"player render cancellation and hidden model restored");
   var living=new net.minecraftforge.client.event.RenderLivingEvent.Pre(mc.player,renderer,0,new com.mojang.blaze3d.vertex.PoseStack(),mc.renderBuffers().bufferSource(),15728880);living.setCanceled(true);renderer.getModel().setAllVisible(false);DivineVisibilityClient.living(living);check(!living.isCanceled()&&renderer.getModel().body.visible,"living render cancellation restored");
   mc.options.setCameraType(net.minecraft.client.CameraType.THIRD_PERSON_FRONT);
  }
  if(tick==125&&done&&!Files.exists(Path.of("enable-hands-music-qa"))){net.minecraft.client.Screenshot.grab(mc.gameDirectory,"skin-third-person.png",mc.getMainRenderTarget(),m->{});mc.setScreen(new net.minecraft.client.gui.screens.inventory.InventoryScreen(mc.player));}
  if(tick==140&&done&&!Files.exists(Path.of("enable-hands-music-qa")))net.minecraft.client.Screenshot.grab(mc.gameDirectory,"skin-inventory.png",mc.getMainRenderTarget(),m->{});
  if(tick==150&&done&&!Files.exists(Path.of("enable-hands-music-qa"))){Files.writeString(Path.of("visibility-results.txt"),"ALL_PASS\n",StandardOpenOption.APPEND);mc.stop();
  }
 }
}
