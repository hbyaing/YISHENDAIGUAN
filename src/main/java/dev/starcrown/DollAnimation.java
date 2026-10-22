package dev.starcrown;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.util.*;

@Mod.EventBusSubscriber(modid=StarcrownDoll.ID,value=Dist.CLIENT)
public final class DollAnimation {
 private record Pose(float previous,float current){}
 private static final Map<Player,Pose> ARMS=new WeakHashMap<>();
 private static final ArrayDeque<net.minecraft.world.entity.LivingEntity> RENDERING=new ArrayDeque<>();
 public static void headMotion(PoseStack pose,double ticks,double x,double y,double z){
  pose.translate(x,y,z);pose.mulPose(Axis.YP.rotationDegrees((float)(Math.sin(ticks*.065)*9)));pose.translate(-x,-y,-z);
 }
 public static float jumpAmount(Player player,float partial){
  if(player==null)return 0;Pose pose=ARMS.get(player);if(pose==null)return 0;
  return net.minecraft.util.Mth.lerp(partial,pose.previous,pose.current);
 }
 public static Player renderedPlayer(){return RENDERING.peek() instanceof Player player?player:null;}
 @SubscribeEvent public static void tick(TickEvent.ClientTickEvent event){
  var level=Minecraft.getInstance().level;
  if(event.phase!=TickEvent.Phase.END||Minecraft.getInstance().isPaused())return;
  if(level==null){ARMS.clear();RENDERING.clear();return;}
  for(Player player:level.players()){
   float target=!player.onGround()&&!player.getAbilities().flying&&!player.isFallFlying()&&!player.isInWater()&&!player.isPassenger()?1:0;
   float old=ARMS.getOrDefault(player,new Pose(0,0)).current;
   float value=old+net.minecraft.util.Mth.clamp(target-old,-.25f,.25f);
   ARMS.put(player,new Pose(old,value));
  }
 }
 @SubscribeEvent(priority=net.minecraftforge.eventbus.api.EventPriority.LOWEST)
 public static void before(RenderLivingEvent.Pre<?,?> event){RENDERING.push(event.getEntity());}
 @SubscribeEvent(priority=net.minecraftforge.eventbus.api.EventPriority.HIGHEST)
 public static void after(RenderLivingEvent.Post<?,?> event){if(RENDERING.peek()==event.getEntity())RENDERING.pop();}
}
