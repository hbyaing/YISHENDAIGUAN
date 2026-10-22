package dev.starcrownqa;
import dev.starcrown.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.item.*;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.nio.file.*;
@Mod.EventBusSubscriber(modid="starcrownqa")
public final class FeastQa {
 static int tick;static Pig near,far;static net.minecraft.server.level.ServerPlayer player;static int effects;static final BlockPos POS=new BlockPos(0,81,0);
 static void log(String s){try{Files.writeString(Path.of("feast-results.txt"),s+"\n",StandardOpenOption.CREATE,StandardOpenOption.APPEND);}catch(Exception e){throw new RuntimeException(e);}}
 static void check(boolean b,String s){if(!b)throw new AssertionError(s);log("PASS "+s);}
 @SubscribeEvent public static void effect(net.minecraftforge.event.entity.living.MobEffectEvent.Added e){if(Files.exists(Path.of("enable-feast-qa"))&&!e.getEntity().level().isClientSide&&e.getEffectInstance().getEffect()==MobEffects.HEAL){check(e.getEffectInstance().getAmplifier()==28,"real level29 food effect");effects++;}}
 @SubscribeEvent public static void tick(TickEvent.LevelTickEvent event){
  if(event.phase!=TickEvent.Phase.END||!(event.level instanceof net.minecraft.server.level.ServerLevel l)||!Files.exists(Path.of("enable-feast-qa")))return;
  if(l.players().isEmpty())return;
  try{tick++;
   if(tick==20){Files.writeString(Path.of("feast-results.txt"),"");player=l.players().get(0);for(int x=-4;x<=28;x++)for(int z=-4;z<=7;z++){l.setBlockAndUpdate(new BlockPos(x,80,z),net.minecraft.world.level.block.Blocks.STONE.defaultBlockState());for(int y=81;y<87;y++)l.setBlockAndUpdate(new BlockPos(x,y,z),net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());}DivineAnchor.unlock(player);player.setItemSlot(EquipmentSlot.HEAD,ItemStack.EMPTY);player.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);player.teleportTo(.5,81,4.5);player.setItemSlot(EquipmentSlot.MAINHAND,new ItemStack(Items.DIAMOND_SWORD));player.setItemSlot(EquipmentSlot.OFFHAND,new ItemStack(Items.APPLE,7));player.setHealth(1);player.getFoodData().setFoodLevel(20);
    l.setBlockAndUpdate(POS,StarcrownDoll.YULE_DOLL.get().defaultBlockState());near=EntityType.PIG.create(l);near.moveTo(2,81,0);near.setHealth(1);l.addFreshEntity(near);far=EntityType.PIG.create(l);far.moveTo(25,81,0);far.setNoAi(true);far.setHealth(1);l.addFreshEntity(far);((StarcrownDoll.DollEntity)l.getBlockEntity(POS)).toggleMusic();
   }
   if(tick==100){check(player.getHealth()==player.getMaxHealth(),"player healed by feast");check(near.getHealth()==near.getMaxHealth(),"frozen mob healed by feast");check(far.getHealth()==1,"outside radius excluded");check(player.getMainHandItem().is(Items.DIAMOND_SWORD)&&player.getOffhandItem().is(Items.APPLE)&&player.getOffhandItem().getCount()==7,"player equipment preserved");check(effects>=4,"repeated servings for player and mob");((StarcrownDoll.DollEntity)l.getBlockEntity(POS)).toggleMusic();check(!RiceFeast.active(player)&&!RiceFeast.active(near),"music toggle cancels feast");effects=0;}
   if(tick==145){check(effects==0,"no servings after stop");((StarcrownDoll.DollEntity)l.getBlockEntity(POS)).toggleMusic();}
   if(tick==185){check(effects>=2,"restart resumes feast");l.destroyBlock(POS,false);check(!RiceFeast.active(player),"breaking doll cancels feast");effects=0;}
   if(tick==230){check(effects==0,"no servings after break");l.setBlockAndUpdate(POS,StarcrownDoll.DOLL.get().defaultBlockState());((StarcrownDoll.DollEntity)l.getBlockEntity(POS)).toggleMusic();check(!RiceFeast.active(player),"Chengran music does not force rice");}
   if(tick==275){check(effects==0,"Chengran remains unchanged");log("ALL_PASS");}
  }catch(Throwable t){log("FAIL "+t);t.printStackTrace();l.getServer().halt(false);}
 }
 @Mod.EventBusSubscriber(modid="starcrownqa",value=net.minecraftforge.api.distmarker.Dist.CLIENT)
 public static class Client {
  static int ticks,activeTicks;
  @SubscribeEvent public static void client(TickEvent.ClientTickEvent e)throws Exception{
   if(e.phase!=TickEvent.Phase.END||!Files.exists(Path.of("enable-feast-qa")))return;
   var mc=net.minecraft.client.Minecraft.getInstance();mc.options.pauseOnLostFocus=false;
   if(mc.player==null)return;ticks++;
   if(ticks==65){mc.setScreen(null);mc.mouseHandler.releaseMouse();}
   if(RiceFeast.active(mc.player)){activeTicks++;if(activeTicks==10)net.minecraft.client.Screenshot.grab(mc.gameDirectory,"feast-first-person.png",mc.getMainRenderTarget(),m->{});if(activeTicks==15)mc.options.setCameraType(net.minecraft.client.CameraType.THIRD_PERSON_FRONT);if(activeTicks==22)net.minecraft.client.Screenshot.grab(mc.gameDirectory,"feast-third-person.png",mc.getMainRenderTarget(),m->{});}
   if(Files.exists(Path.of("feast-results.txt"))){String s=Files.readString(Path.of("feast-results.txt"));if(s.contains("ALL_PASS")||s.contains("FAIL"))mc.stop();}
  }
 }
}


