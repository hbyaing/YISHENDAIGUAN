package dev.starcrown;
import java.util.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.*;
import net.minecraftforge.registries.ForgeRegistries;
/** Per-player settings live in DivineSeven's persistent, clone-safe settings compound. */
public final class PotionFilter {
 public static boolean enabled(ServerPlayer p,MobEffect effect){
  var id=ForgeRegistries.MOB_EFFECTS.getKey(effect);var settings=DivineSeven.settings(p);
  return id!=null&&!settings.getCompound("potion_disabled").getBoolean(id.toString())&&(effect!=MobEffects.NIGHT_VISION||!settings.getBoolean("night_off"));
 }
 public static Set<String> disabled(ServerPlayer p){var result=new HashSet<String>();for(var effect:ForgeRegistries.MOB_EFFECTS)if(!enabled(p,effect))result.add(ForgeRegistries.MOB_EFFECTS.getKey(effect).toString());return result;}
 public static void set(ServerPlayer p,ResourceLocation id,boolean enable){
  var effect=ForgeRegistries.MOB_EFFECTS.getValue(id);if(effect==null)return;
  var settings=DivineSeven.settings(p);var disabled=settings.getCompound("potion_disabled");
  if(enable)disabled.remove(id.toString());else disabled.putBoolean(id.toString(),true);settings.put("potion_disabled",disabled);
  if(effect==MobEffects.NIGHT_VISION)settings.putBoolean("night_off",!enable);
  if(!enable){p.removeEffect(effect);p.getPersistentData().getCompound(DivineWill.LEASE).getCompound("benefits").remove(id.toString());if(effect==MobEffects.NIGHT_VISION)p.getPersistentData().getCompound(DivineWill.LEASE).remove("vision_owned");}
  DivineWill.update(p,true);
 }
 private PotionFilter(){}
}
