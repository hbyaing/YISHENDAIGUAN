package dev.starcrownqa;

import dev.starcrown.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.nio.file.*;
import java.util.*;

@Mod.EventBusSubscriber(modid="starcrownqa")
public final class RiceQa {
 static volatile UUID playerId;static volatile boolean sawEffect;
 static final List<String> out=new java.util.concurrent.CopyOnWriteArrayList<>();
 static void log(String text){out.add(text);try{Files.write(Path.of("rice-results.txt"),out);}catch(Exception e){throw new RuntimeException(e);}}
 static void check(boolean value,String label){if(!value)throw new AssertionError(label);log("PASS "+label);}
 @SubscribeEvent public static void added(MobEffectEvent.Added event){
  if(event.getEntity().level().isClientSide||!event.getEntity().getUUID().equals(playerId))return;
  var effect=event.getEffectInstance();
  if(effect.getEffect()==MobEffects.HEAL){
   check(effect.getAmplifier()==28&&effect.getDuration()==1,"actual server effect Added: Instant Health XXIX, amplifier28, duration1");
   sawEffect=true;
  }
 }
 @Mod.EventBusSubscriber(modid="starcrownqa",value=net.minecraftforge.api.distmarker.Dist.CLIENT)
 public static final class Client {
  static int ticks;static boolean started;
  static void server(java.util.function.Consumer<net.minecraft.server.level.ServerPlayer> action){
   var mc=net.minecraft.client.Minecraft.getInstance();var s=mc.getSingleplayerServer();var id=mc.player.getUUID();s.execute(()->{try{action.accept(s.getPlayerList().getPlayer(id));}catch(Throwable t){log("FAIL "+t);t.printStackTrace();}});
  }
  static void shot(String name){var mc=net.minecraft.client.Minecraft.getInstance();net.minecraft.client.Screenshot.grab(mc.gameDirectory,name+".png",mc.getMainRenderTarget(),message->{});log("CAPTURE "+name);}
  @SubscribeEvent public static void tick(TickEvent.ClientTickEvent event){
   if(event.phase!=TickEvent.Phase.END||!Files.exists(Path.of("enable-rice-qa")))return;
   var mc=net.minecraft.client.Minecraft.getInstance();mc.options.pauseOnLostFocus=false;
   if(mc.player==null||mc.getSingleplayerServer()==null)return;
   try{
    if(!started){started=true;mc.setScreen(null);mc.mouseHandler.releaseMouse();mc.options.setCameraType(net.minecraft.client.CameraType.FIRST_PERSON);
     server(p->{playerId=p.getUUID();var level=p.getServer().overworld();DivineAnchor.unlock(p);p.setItemSlot(EquipmentSlot.HEAD,ItemStack.EMPTY);p.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);p.getAbilities().flying=false;p.onUpdateAbilities();
      for(int x=-6;x<=6;x++)for(int z=-4;z<=8;z++){level.setBlockAndUpdate(new BlockPos(x,80,z),net.minecraft.world.level.block.Blocks.STONE.defaultBlockState());for(int y=81;y<91;y++)level.setBlockAndUpdate(new BlockPos(x,y,z),net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());}
      for(var mob:level.getEntitiesOfClass(net.minecraft.world.entity.Mob.class,new net.minecraft.world.phys.AABB(new BlockPos(0,81,0)).inflate(32)))mob.discard();
      level.setDayTime(6000);p.teleportTo(level,.5,81,4.5,180,10);p.addEffect(new net.minecraft.world.effect.MobEffectInstance(MobEffects.NIGHT_VISION,1200,0,false,false));
      p.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH).setBaseValue(40);p.setHealth(1);p.getFoodData().setFoodLevel(20);p.getInventory().selected=0;p.setItemSlot(EquipmentSlot.MAINHAND,new ItemStack(StarcrownDoll.WHITE_RICE.get()));p.setItemSlot(EquipmentSlot.OFFHAND,ItemStack.EMPTY);
     });
    }
    ticks++;
    if(ticks==50){
     Item rice=StarcrownDoll.WHITE_RICE.get();var stack=new ItemStack(rice);var food=rice.getFoodProperties();
     check(food.canAlwaysEat(),"food usable with full hunger");
     check(rice.getUseAnimation(stack)==UseAnim.EAT&&rice.getUseDuration(stack)==32,"vanilla EAT animation, 32 tick use duration");
     var effects=food.getEffects();check(effects.size()==1&&effects.get(0).getFirst().getEffect()==MobEffects.HEAL&&effects.get(0).getFirst().getAmplifier()==28&&effects.get(0).getSecond()==1.0f,"food has 100 percent Instant Health level29 effect");
    }
    if(ticks>=80&&ticks<225)mc.options.keyUse.setDown(true);
    if(ticks==80){mc.options.keyUse.setDown(true);mc.gameMode.useItem(mc.player,net.minecraft.world.InteractionHand.MAIN_HAND);}
    if(ticks==85)server(p->{p.startUsingItem(net.minecraft.world.InteractionHand.MAIN_HAND);log("SERVER started rice use");});
    if(ticks==95){check(mc.player.isUsingItem()&&mc.player.getUseItem().is(StarcrownDoll.WHITE_RICE.get()),"actual client right-click eating in progress");shot("rice-eating");}
    if(ticks==225)mc.options.keyUse.setDown(false);
    if(ticks==260)server(p->{
     check(sawEffect,"consumption emitted real Instant Health effect event");
     check(p.getHealth()==40,"native level29 effect heals to 40 HP test maximum, not 29 points");
     check(p.getMainHandItem().is(Items.BOWL),"survival consumption returns empty bowl");
     check(!p.hasEffect(MobEffects.HEAL),"instant effect resolves and expires normally");
     p.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH).setBaseValue(20);p.setHealth(20);p.setGameMode(net.minecraft.world.level.GameType.CREATIVE);p.setItemSlot(EquipmentSlot.MAINHAND,new ItemStack(StarcrownDoll.WHITE_RICE.get()));
    });
    if(ticks==310)shot("rice-held");
    if(ticks==340){
     CreativeModeTabs.tryRebuildTabContents(mc.level.enabledFeatures(),true,mc.level.registryAccess());
     var contents=StarcrownDoll.TAB.get().getDisplayItems();check(contents.stream().anyMatch(i->i.is(StarcrownDoll.WHITE_RICE.get()))&&contents.stream().anyMatch(i->i.is(StarcrownDoll.YULE_ITEM.get()))&&contents.stream().anyMatch(i->i.is(StarcrownDoll.ITEM.get())),"creative tab includes rice and both dolls");
     var screen=new net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen(mc.player,mc.level.enabledFeatures(),true);mc.setScreen(screen);
     for(var method:screen.getClass().getDeclaredMethods())if(method.getParameterCount()==1&&method.getParameterTypes()[0]==CreativeModeTab.class&&method.getReturnType()==void.class){method.setAccessible(true);method.invoke(screen,StarcrownDoll.TAB.get());break;}
    }
    if(ticks==360)shot("rice-creative-tab");
    if(ticks==400){check(out.stream().noneMatch(s->s.startsWith("FAIL")),"no server or client assertion failures");log("ALL_PASS");mc.setScreen(null);mc.stop();}
   }catch(Throwable t){log("FAIL "+t);t.printStackTrace();mc.options.keyUse.setDown(false);mc.stop();}
  }
 }
}


