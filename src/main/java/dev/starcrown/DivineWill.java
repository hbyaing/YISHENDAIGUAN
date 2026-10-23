package dev.starcrown;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.effect.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.fml.common.Mod;
@Mod.EventBusSubscriber(modid=StarcrownDoll.ID)
public final class DivineWill {
 static final String LEASE="starcrowndoll_will_lease";
 static final UUID SPEED=UUID.fromString("48ae1082-6b55-4db0-a437-7311a00e3021"),BLOCK=UUID.fromString("48ae1082-6b55-4db0-a437-7311a00e3022"),ENTITY=UUID.fromString("48ae1082-6b55-4db0-a437-7311a00e3023"),KNOCK=UUID.fromString("48ae1082-6b55-4db0-a437-7311a00e3024");
 public static boolean wearing(Player p){return !p.isSpectator()&&(p.getItemBySlot(EquipmentSlot.HEAD).is(StarcrownDoll.ITEM.get())||(p instanceof ServerPlayer sp&&DivineAnchor.locked(sp)));}
 public static boolean automaticBenefit(MobEffect effect){return effect.getCategory()==MobEffectCategory.BENEFICIAL;}
 public static boolean unwanted(MobEffectInstance effect){return effect!=null&&effect.getEffect().getCategory()!=MobEffectCategory.BENEFICIAL;}
 static void modifier(ServerPlayer p,Attribute attribute,UUID id,double value,AttributeModifier.Operation op,boolean enable){var a=p.getAttribute(attribute);if(a==null)return;if(enable){if(a.getModifier(id)!=null&&a.getModifier(id).getAmount()!=value)a.removeModifier(id);if(a.getModifier(id)==null)a.addTransientModifier(new AttributeModifier(id,"starcrowndoll_will",value,op));}else a.removeModifier(id);}
 static boolean ownVision(MobEffectInstance e){return e!=null&&e.getAmplifier()==0&&e.isAmbient()&&!e.isVisible()&&!e.showIcon()&&!e.isInfiniteDuration()&&e.getDuration()<=400;}
 static CompoundTag elapsedEffect(CompoundTag original,long elapsed){var t=original.copy();int d=t.getInt("Duration");if(d>=0)t.putInt("Duration",(int)Math.max(0,d-Math.min(elapsed,Integer.MAX_VALUE)));if(t.contains("HiddenEffect"))t.put("HiddenEffect",elapsedEffect(t.getCompound("HiddenEffect"),elapsed));return t;}
 private static void benefits(ServerPlayer p,boolean enabled){
  var lease=p.getPersistentData().getCompound(LEASE);var saved=lease.getCompound("benefits");long now=p.serverLevel().getGameTime();
  for(var effect:net.minecraftforge.registries.ForgeRegistries.MOB_EFFECTS){if(effect.getCategory()!=MobEffectCategory.BENEFICIAL||effect==MobEffects.NIGHT_VISION)continue;String key=net.minecraftforge.registries.ForgeRegistries.MOB_EFFECTS.getKey(effect).toString();var current=p.getEffect(effect);
   if(enabled){if(current!=null&&(current.isInfiniteDuration()||current.getAmplifier()>0||current.getDuration()>240))continue;
    if(!saved.contains(key)){var backup=new CompoundTag();backup.putLong("tick",now);if(current!=null)backup.put("effect",current.save(new CompoundTag()));saved.put(key,backup);}
    p.addEffect(new MobEffectInstance(effect,240,0,true,false,false));
   }else if(saved.contains(key)&&current!=null&&current.getAmplifier()==0&&current.isAmbient()&&!current.isVisible()&&!current.showIcon()&&current.getDuration()<=240){p.removeEffect(effect);var back=saved.getCompound(key);if(back.contains("effect")){var old=MobEffectInstance.load(elapsedEffect(back.getCompound("effect"),now-back.getLong("tick")));if(old!=null&&(old.isInfiniteDuration()||old.getDuration()>0))p.addEffect(old);}}
  }lease.put("benefits",saved);
 }
 public static void update(ServerPlayer p,boolean enable){
  var data=p.getPersistentData();long now=p.getServer().overworld().getGameTime();
  if(enable){
   if(!data.contains(LEASE)){var tag=new CompoundTag();tag.putBoolean("mayfly_before",p.getAbilities().mayfly);tag.putFloat("flight_before",p.getAbilities().getFlyingSpeed());data.put(LEASE,tag);}
   var tag=data.getCompound(LEASE);
   if(!p.getAbilities().mayfly){p.getAbilities().mayfly=true;p.onUpdateAbilities();}
   float flight=(float)(.05*DivineSeven.speed(p));if(p.getAbilities().getFlyingSpeed()!=flight){p.getAbilities().setFlyingSpeed(flight);p.onUpdateAbilities();}
   var vision=p.getEffect(MobEffects.NIGHT_VISION);
   if(!DivineSeven.settings(p).getBoolean("night_off")&&(vision==null||(!vision.isInfiniteDuration()&&vision.getAmplifier()==0&&vision.getDuration()<300))){
    if(vision!=null&&(!tag.getBoolean("vision_owned")||!ownVision(vision))){tag.put("vision_before",vision.save(new CompoundTag()));tag.putLong("vision_snapshot_tick",now);}
    p.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION,400,0,true,false,false));tag.putBoolean("vision_owned",true);
   }
   p.clearFire();p.setTicksFrozen(0);p.setAirSupply(p.getMaxAirSupply());p.getFoodData().setFoodLevel(20);p.getFoodData().setSaturation(20);
   if(p.tickCount%20==0)benefits(p,true);
   for(var effect:new java.util.ArrayList<>(p.getActiveEffects()))if(unwanted(effect))p.removeEffect(effect.getEffect());
  }else if(data.contains(LEASE)){
   var tag=data.getCompound(LEASE);if(tag.contains("flight_before")){p.getAbilities().setFlyingSpeed(tag.getFloat("flight_before"));p.onUpdateAbilities();}if(!p.isCreative()&&!p.isSpectator()&&!tag.getBoolean("mayfly_before")){p.getAbilities().mayfly=false;p.getAbilities().flying=false;p.onUpdateAbilities();}
   if(tag.getBoolean("vision_owned")&&ownVision(p.getEffect(MobEffects.NIGHT_VISION))){p.removeEffect(MobEffects.NIGHT_VISION);if(tag.contains("vision_before")){var original=MobEffectInstance.load(elapsedEffect(tag.getCompound("vision_before"),Math.max(0,now-tag.getLong("vision_snapshot_tick"))));if(original!=null&&(original.isInfiniteDuration()||original.getDuration()>0))p.addEffect(original);}}
   benefits(p,false);data.remove(LEASE);DivineSeven.release(p);
  }
  modifier(p,Attributes.MOVEMENT_SPEED,SPEED,DivineSeven.speed(p)-1,AttributeModifier.Operation.MULTIPLY_TOTAL,enable);
  modifier(p,ForgeMod.BLOCK_REACH.get(),BLOCK,16,AttributeModifier.Operation.ADDITION,enable);
  modifier(p,ForgeMod.ENTITY_REACH.get(),ENTITY,16,AttributeModifier.Operation.ADDITION,enable);
  modifier(p,ForgeMod.SWIM_SPEED.get(),UUID.fromString("48ae1082-6b55-4db0-a437-7311a00e3025"),4,AttributeModifier.Operation.ADDITION,enable);
  modifier(p,Attributes.KNOCKBACK_RESISTANCE,KNOCK,1,AttributeModifier.Operation.ADDITION,enable);
 }
 @SubscribeEvent public static void tick(TickEvent.PlayerTickEvent e){if(e.phase==TickEvent.Phase.END&&e.player instanceof ServerPlayer p)update(p,wearing(p));}
 @SubscribeEvent public static void logout(PlayerEvent.PlayerLoggedOutEvent e){if(e.getEntity() instanceof ServerPlayer p)update(p,false);}
 @SubscribeEvent public static void clone(PlayerEvent.Clone e){if(e.getOriginal().getPersistentData().contains(LEASE))e.getEntity().getPersistentData().put(LEASE,e.getOriginal().getPersistentData().getCompound(LEASE).copy());}
 @SubscribeEvent(priority=EventPriority.LOWEST) public static void applicable(MobEffectEvent.Applicable e){if(DivineProtection.active(e.getEntity())&&unwanted(e.getEffectInstance()))e.setResult(Event.Result.DENY);}
 @SubscribeEvent(priority=EventPriority.LOWEST,receiveCanceled=true) public static void remove(MobEffectEvent.Remove e){if(DivineProtection.active(e.getEntity())&&unwanted(e.getEffectInstance()))e.setCanceled(false);}
 @SubscribeEvent(priority=EventPriority.LOWEST) public static void knockback(LivingKnockBackEvent e){if(DivineProtection.active(e.getEntity()))e.setCanceled(true);}
 @SubscribeEvent public static void mining(PlayerEvent.BreakSpeed e){if(wearing(e.getEntity()))e.setNewSpeed(e.getNewSpeed()*20*(e.getEntity().isEyeInFluid(net.minecraft.tags.FluidTags.WATER)&&!net.minecraft.world.item.enchantment.EnchantmentHelper.hasAquaAffinity(e.getEntity())?5:1)*(e.getEntity().getAbilities().flying&&!e.getEntity().onGround()?5:1));}
}

