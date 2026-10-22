package dev.starcrown;
import net.minecraft.world.entity.Mob;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
/** Runtime-only suppression: never writes a persistent NoAI flag or removes goal definitions. */
@Mod.EventBusSubscriber(modid=StarcrownDoll.ID)
public final class DivineEternity {
 public static boolean sealed(Mob mob){
  if(!(mob.level() instanceof ServerLevel level)||!mob.isAlive())return false;
  if(DollDance.controlled(mob))return true;
  for(var p:level.players())if(DivineWill.wearing(p)&&Math.abs(p.getX()-mob.getX())<=3.5&&Math.abs(p.getY()-mob.getY())<=3.5&&Math.abs(p.getZ()-mob.getZ())<=3.5)return true;
  return false;
 }
 public static void stop(Mob mob){
  mob.goalSelector.getAvailableGoals().forEach(g->{if(g.isRunning())g.stop();});
  mob.targetSelector.getAvailableGoals().forEach(g->{if(g.isRunning())g.stop();});
  mob.getNavigation().stop();mob.setXxa(0);mob.setYya(0);mob.setZza(0);mob.setJumping(false);mob.setTarget(null);mob.stopUsingItem();mob.setAggressive(false);
 }
 @SubscribeEvent public static void tick(TickEvent.LevelTickEvent e){
  if(e.phase!=TickEvent.Phase.START||!(e.level instanceof ServerLevel level))return;
  var seen=java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<Mob,Boolean>());
  for(var p:level.players())if(DivineWill.wearing(p))for(var mob:level.getEntitiesOfClass(Mob.class,new net.minecraft.world.phys.AABB(p.position(),p.position()).inflate(3.5)))if(seen.add(mob)&&sealed(mob))stop(mob);
 }
}
