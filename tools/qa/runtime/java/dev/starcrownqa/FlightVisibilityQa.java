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
public final class FlightVisibilityQa {
 static int tick;static volatile boolean failed;static double startY;
 static void check(boolean ok,String text){try{Files.writeString(Path.of("flight-visibility-results.txt"),(ok?"PASS ":"FAIL ")+text+"\n",StandardOpenOption.CREATE,StandardOpenOption.APPEND);}catch(Exception e){throw new RuntimeException(e);}if(!ok)failed=true;}
 @SubscribeEvent public static void client(TickEvent.ClientTickEvent event){
  if(event.phase!=TickEvent.Phase.END||!Files.exists(Path.of("enable-flight-visibility-qa")))return;
  var mc=net.minecraft.client.Minecraft.getInstance();mc.options.pauseOnLostFocus=false;
  if(mc.player==null||mc.getSingleplayerServer()==null)return;tick++;
  if(tick==1){mc.setScreen(null);mc.mouseHandler.releaseMouse();var id=mc.player.getUUID();mc.getSingleplayerServer().execute(()->{
   var p=mc.getSingleplayerServer().getPlayerList().getPlayer(id);DivineAnchor.unlock(p);p.setItemSlot(EquipmentSlot.HEAD,ItemStack.EMPTY);DivineWill.update(p,false);p.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);p.removeAllEffects();p.teleportTo(p.serverLevel(),.5,100,.5,0,0);p.setItemSlot(EquipmentSlot.HEAD,new ItemStack(StarcrownDoll.ITEM.get()));p.tickCount=20;DivineWill.update(p,true);
  });}
  if(tick==40){check(mc.player.getAbilities().mayfly,"client receives survival flight permission");check(!mc.player.hasEffect(MobEffects.INVISIBILITY)&&!mc.player.isInvisible(),"wearer visible without automatic invisibility");check(mc.player.getBbHeight()>1.7&&mc.player.getBoundingBox().getXsize()>.5,"normal bounding box retained");mc.player.getAbilities().mayfly=false;}
  if(tick==65){check(mc.player.getAbilities().mayfly,"client flight permission repaired by periodic synchronization");mc.options.keyJump.setDown(true);}
  if(tick==66)mc.options.keyJump.setDown(false);
  if(tick==68)mc.options.keyJump.setDown(true);
  if(tick==70){check(mc.player.getAbilities().flying,"double jump activates flight");startY=mc.player.getY();}
  if(tick==85){check(mc.player.getY()>startY+1,"holding jump ascends in flight");mc.options.keyJump.setDown(false);mc.options.setCameraType(net.minecraft.client.CameraType.THIRD_PERSON_FRONT);}
  if(tick==100){net.minecraft.client.Screenshot.grab(mc.gameDirectory,"chengran-flight-visible.png",mc.getMainRenderTarget(),m->{});}
  if(tick==115){check(mc.player.getAbilities().flying,"flight remains enabled after synchronization");mc.stop();}
 }
}
