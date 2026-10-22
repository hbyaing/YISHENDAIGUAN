package dev.starcrownqa;

import dev.starcrown.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.nio.file.*;
import java.util.*;

@Mod.EventBusSubscriber(modid="starcrownqa")
public final class DanceQa {
 static int ticks;static boolean running;static ServerLevel level;
 static final BlockPos A=new BlockPos(0,81,0),B=new BlockPos(3,81,0);
 static final List<Mob> mobs=new ArrayList<>();static final List<Vec3> positions=new ArrayList<>();
 static final List<String> out=new ArrayList<>();
 static void check(boolean value,String label){if(!value)throw new AssertionError(label);out.add("PASS "+label);write();}
 static void write(){try{Files.write(Path.of("dance-results.txt"),out);}catch(Exception e){throw new RuntimeException(e);}}
 static StarcrownDoll.DollEntity doll(BlockPos p){return (StarcrownDoll.DollEntity)level.getBlockEntity(p);}
 static void begin(net.minecraft.server.level.ServerPlayer player){
  try{
   level=player.serverLevel();DivineAnchor.unlock(player);player.setItemSlot(EquipmentSlot.HEAD,net.minecraft.world.item.ItemStack.EMPTY);
   player.setGameMode(net.minecraft.world.level.GameType.CREATIVE);
   player.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.NIGHT_VISION,1200,0,false,false));
   for(int x=-6;x<=6;x++)for(int z=-4;z<=8;z++)level.setBlockAndUpdate(new BlockPos(x,80,z),net.minecraft.world.level.block.Blocks.STONE.defaultBlockState());
   level.destroyBlock(A,false);level.destroyBlock(B,false);
   for(Mob old:level.getEntitiesOfClass(Mob.class,new net.minecraft.world.phys.AABB(A).inflate(20)))old.discard();
   level.setDayTime(6000);level.setBlockAndUpdate(A,StarcrownDoll.DOLL.get().defaultBlockState());
   player.teleportTo(level,.5,81,6.5,180,15);
   int i=0;for(EntityType<?> type:List.of(EntityType.PIG,EntityType.COW,EntityType.SHEEP,EntityType.ZOMBIE)){
    Mob mob=(Mob)type.create(level);mob.setPos(-3+i*2,81,0);mob.setPersistenceRequired();level.addFreshEntity(mob);mobs.add(mob);positions.add(mob.position());i++;
   }
   check(!DollDance.controlled(mobs.get(0)),"inactive doll does not freeze mobs");
   doll(A).toggleMusic();check(doll(A).isPlaying(),"right-click handler enables persistent playback");
   check(((net.minecraft.world.level.block.entity.BlockEntity)doll(A)).getUpdateTag().getBoolean("Playing"),"playing state present in late-join chunk update tag");
   running=true;ticks=0;
  }catch(Throwable t){fail(t);}
 }
 static void fail(Throwable t){out.add("FAIL "+t);write();t.printStackTrace();running=false;}
 @SubscribeEvent public static void server(TickEvent.ServerTickEvent event){
  if(!running||event.phase!=TickEvent.Phase.END)return;
  try{
   ticks++;
   if(ticks==30){for(Mob m:mobs)check(DollDance.controlled(m)&&!m.isEffectiveAi()&&!m.isNoAi(),"freeze without persistent NoAI: "+m.getType());}
   if(ticks==40){positions.clear();for(Mob m:mobs){positions.add(m.position());m.setDeltaMovement(2,1,2);m.move(MoverType.PISTON,new Vec3(.5,0,0));check(m.position().distanceToSqr(positions.get(positions.size()-1))<1e-8,"direct piston movement blocked");}}
   if(ticks==80){for(int i=0;i<mobs.size();i++)check(mobs.get(i).position().distanceToSqr(positions.get(i))<1e-8,"position held against velocity: "+mobs.get(i).getType());}
   if(ticks==100){level.setBlockAndUpdate(B,StarcrownDoll.DOLL.get().defaultBlockState());doll(B).toggleMusic();doll(A).toggleMusic();check(!doll(A).isPlaying()&&DollDance.controlled(mobs.get(0)),"stopping one overlapping source keeps other source active");}
   if(ticks==120){level.destroyBlock(B,false);check(!DollDance.controlled(mobs.get(0)),"breaking final source releases mobs");}
   if(ticks==140){for(Mob m:mobs)check(m.isEffectiveAi()&&!m.isNoAi(),"AI restored after source broken: "+m.getType());doll(A).toggleMusic();}
   if(ticks==160){var saved=((net.minecraft.world.level.block.entity.BlockEntity)doll(A)).saveWithoutMetadata();((net.minecraft.world.level.block.entity.BlockEntity)doll(A)).load(saved);check(doll(A).isPlaying()&&DollDance.controlled(mobs.get(0)),"save/load retains active toggle");doll(A).toggleMusic();check(!DollDance.controlled(mobs.get(0)),"manual stop immediately releases last source");}
   if(ticks==180){check(!DollDance.nearSource(level,new Vec3(100,81,0)),"outside radius excluded");out.add("ALL_PASS");write();running=false;}
  }catch(Throwable t){fail(t);}
 }
 @Mod.EventBusSubscriber(modid="starcrownqa",value=net.minecraftforge.api.distmarker.Dist.CLIENT)
 public static class Client {
  static boolean started,captured;static int counter;
  @SubscribeEvent public static void tick(TickEvent.ClientTickEvent event){
   if(event.phase!=TickEvent.Phase.END||!Files.exists(Path.of("enable-dance-qa")))return;var mc=net.minecraft.client.Minecraft.getInstance();
   mc.options.pauseOnLostFocus=false;if(mc.player==null||mc.getSingleplayerServer()==null)return;
   if(!started){started=true;mc.setScreen(null);mc.mouseHandler.releaseMouse();mc.getSingleplayerServer().execute(()->begin(mc.getSingleplayerServer().getPlayerList().getPlayer(mc.player.getUUID())));}
   if(++counter==70){try{
    var be=mc.level.getBlockEntity(A);if(!(be instanceof StarcrownDoll.DollEntity d)||!d.isPlaying())throw new AssertionError("client playback state");
    Class<?> c=Class.forName("dev.starcrown.DollMusic");var f=c.getDeclaredField("ACTIVE");f.setAccessible(true);var map=(Map<?,?>)f.get(null);var ref=(java.lang.ref.WeakReference<?>)map.get(d);var sound=(net.minecraft.client.resources.sounds.SoundInstance)(ref==null?null:ref.get());
    if(sound==null||!sound.isLooping()||!mc.getSoundManager().isActive(sound))throw new AssertionError("looping sound not active");
    var pose=new com.mojang.blaze3d.vertex.PoseStack();DanceVisuals.animateMob(pose,10);
    var v=pose.last().normal().transform(new org.joml.Vector3f(1,0,0));if(Math.abs(v.z())<.5||Math.abs(v.x())>.01)throw new AssertionError("quarter-turn transform");
    Files.writeString(Path.of("dance-client-results.txt"),"PASS client playing state synced\nPASS positional looping streaming sound active\nPASS animated mob rotates a quarter-turn after 10 ticks\n");
    net.minecraft.client.Screenshot.grab(mc.gameDirectory,mc.getMainRenderTarget(),message->{});captured=true;
   }catch(Throwable t){try{Files.writeString(Path.of("dance-client-results.txt"),"FAIL "+t);}catch(Exception ignored){}t.printStackTrace();}}
   if(counter==500)mc.stop();
  }
 }
}
