package dev.starcrown;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.syncher.EntityDataAccessor;
/** Common attack boundary plus optional Arcane Vortex integration. Not a sandbox against arbitrary code. */
public final class DivineCombat {
 public static boolean protectedTarget(Object target){return target instanceof Entity e&&(e.level().isClientSide?e instanceof Player p&&p.getInventory()!=null&&p.getItemBySlot(EquipmentSlot.HEAD).is(StarcrownDoll.ITEM.get()):DivineProtection.active(e));}
 public static boolean reject(Object target,String path){if(!protectedTarget(target))return false;if(target instanceof Entity e)DivineProtection.prevent(e,path,0);return true;}
 public static boolean compatibilityTarget(Object target){return protectedTarget(target)||(target instanceof Entity e&&YuleHumility.active(e));}
 public static boolean rejectCompatibility(Object target,String path){return target instanceof Entity e&&YuleHumility.active(e)||reject(target,path);}
 private static class HealthKey {
  static final Object KEY=find();static Object find(){for(String name:new String[]{"DATA_HEALTH_ID","f_20961_"})try{var f=LivingEntity.class.getDeclaredField(name);f.setAccessible(true);return f.get(null);}catch(ReflectiveOperationException ignored){}throw new IllegalStateException("Minecraft 1.20.1 health accessor not found");}
 }
 public static boolean blockData(Entity owner,EntityDataAccessor<?> key,Object value){return owner instanceof LivingEntity e&&value instanceof Float f&&DivineProtection.active(e)&&key==HealthKey.KEY&&DivineProtection.blockHealth(e,f);}
 public static Object humilityData(Entity owner,EntityDataAccessor<?> key,Object value){return owner instanceof LivingEntity e&&value instanceof Float f&&key==HealthKey.KEY?YuleHumility.healthWrite(e,f):value;}
 public static float maximum(LivingEntity e){double n=e.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH);return Double.isFinite(n)&&n>=20?(float)Math.min(n,1024):20;}
 private static class ArcaneMaps {
  static final java.util.List<java.lang.reflect.Field> FIELDS=fields();
  static java.util.List<java.lang.reflect.Field> fields(){var out=new java.util.ArrayList<java.lang.reflect.Field>();if(!net.minecraftforge.fml.ModList.get().isLoaded("arcanevortex"))return out;
   for(String name:new String[]{"com.erchien.arcanevortex.Core.CoreEntity.HealthMethodHelper","com.erchien.arcanevortex.Agent.HealthMethod.AgentHealthMethodHelper","com.erchien.arcanevortex.NativeAgent.NativeAgentHealthMethod","com.erchien.arcanevortex.Core.CoreEntity.IsRemovedHelper","com.erchien.arcanevortex.Core.CoreEntity.RemovalReasonHelper","com.erchien.arcanevortex.Core.CoreEntity.AddedToWorldHelper","com.erchien.arcanevortex.Agent.AntiHiddenClass.IsAddedToWorldHelper","com.erchien.arcanevortex.Agent.AntiHiddenClass.IsAliveHelper","com.erchien.arcanevortex.Agent.AntiHiddenClass.IsInvisibleHelper","com.erchien.arcanevortex.Agent.AntiHiddenClass.IsRemovedHelper","com.erchien.arcanevortex.Agent.AntiHiddenClass.OnAddedToWorldHelper","com.erchien.arcanevortex.Agent.AntiHiddenClass.OnRemovedFromWorldHelper","com.erchien.arcanevortex.Help.VanShForce.HealthLockManager"})try{var c=Class.forName(name,false,DivineCombat.class.getClassLoader());for(var f:c.getDeclaredFields())if(java.lang.reflect.Modifier.isStatic(f.getModifiers())&&java.util.Map.class.isAssignableFrom(f.getType())){f.setAccessible(true);out.add(f);}}catch(ReflectiveOperationException ignored){}return out;
  }
 }
 public static void cleanArcane(ServerPlayer p){if(!DivineProtection.active(p)&&!YuleHumility.active(p))return;for(var field:ArcaneMaps.FIELDS)try{var map=(java.util.Map<?,?>)field.get(null);if(map!=null){map.remove(p);map.remove(p.getUUID());}}catch(IllegalAccessException ignored){}}
}
