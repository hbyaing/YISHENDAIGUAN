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
 public static boolean unwanted(MobEffectInstance effect){return effect!=null&&effect.getEffect().getCategory()!=MobEffectCategory.BENEFICIAL;}
 static void modifier(ServerPlayer p,Attribute attribute,UUID id,double value,AttributeModifier.Operation op,boolean enable){var a=p.getAttribute(attribute);if(a==null)return;if(enable){if(a.getModifier(id)==null)a.addTransientModifier(new AttributeModifier(id,"starcrowndoll_will",value,op));}else a.removeModifier(id);}
 static boolean ownVision(MobEffectInstance e){return e!=null&&e.getAmplifier()==0&&e.isAmbient()&&!e.isVisible()&&!e.showIcon()&&!e.isInfiniteDuration()&&e.getDuration()<=400;}
 static CompoundTag elapsedEffect(CompoundTag original,long elapsed){var t=original.copy();int d=t.getInt("Duration");if(d>=0)t.putInt("Duration",(int)Math.max(0,d-Math.min(elapsed,Integer.MAX_VALUE)));if(t.contains("HiddenEffect"))t.put("HiddenEffect",elapsedEffect(t.getCompound("HiddenEffect"),elapsed));return t;}
 public static void update(ServerPlayer p,boolean enable){
  var data=p.getPersistentData();long now=p.getServer().overworld().getGameTime();
  if(enable){
   if(!data.contains(LEASE)){var tag=new CompoundTag();tag.putBoolean("mayfly_before",p.getAbilities().mayfly);data.put(LEASE,tag);}
   var tag=data.getCompound(LEASE);
   if(!p.getAbilities().mayfly){p.getAbilities().mayfly=true;p.onUpdateAbilities();}
   var vision=p.getEffect(MobEffects.NIGHT_VISION);
   if(vision==null||(!vision.isInfiniteDuration()&&vision.getAmplifier()==0&&vision.getDuration()<300)){
    if(vision!=null&&(!tag.getBoolean("vision_owned")||!ownVision(vision))){tag.put("vision_before",vision.save(new CompoundTag()));tag.putLong("vision_snapshot_tick",now);}
    p.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION,400,0,true,false,false));tag.putBoolean("vision_owned",true);
   }
   p.clearFire();p.setTicksFrozen(0);
   for(var effect:new java.util.ArrayList<>(p.getActiveEffects()))if(unwanted(effect))p.removeEffect(effect.getEffect());
  }else if(data.contains(LEASE)){
   var tag=data.getCompound(LEASE);if(!p.isCreative()&&!p.isSpectator()&&!tag.getBoolean("mayfly_before")){p.getAbilities().mayfly=false;p.getAbilities().flying=false;p.onUpdateAbilities();}
   if(tag.getBoolean("vision_owned")&&ownVision(p.getEffect(MobEffects.NIGHT_VISION))){p.removeEffect(MobEffects.NIGHT_VISION);if(tag.contains("vision_before")){var original=MobEffectInstance.load(elapsedEffect(tag.getCompound("vision_before"),Math.max(0,now-tag.getLong("vision_snapshot_tick"))));if(original!=null&&(original.isInfiniteDuration()||original.getDuration()>0))p.addEffect(original);}}
   data.remove(LEASE);
  }
  modifier(p,Attributes.MOVEMENT_SPEED,SPEED,.5,AttributeModifier.Operation.MULTIPLY_TOTAL,enable&&p.isSprinting());
  modifier(p,ForgeMod.BLOCK_REACH.get(),BLOCK,3,AttributeModifier.Operation.ADDITION,enable);
  modifier(p,ForgeMod.ENTITY_REACH.get(),ENTITY,3,AttributeModifier.Operation.ADDITION,enable);
  modifier(p,Attributes.KNOCKBACK_RESISTANCE,KNOCK,1,AttributeModifier.Operation.ADDITION,enable);
 }
 @SubscribeEvent public static void tick(TickEvent.PlayerTickEvent e){if(e.phase==TickEvent.Phase.END&&e.player instanceof ServerPlayer p)update(p,wearing(p));}
 @SubscribeEvent public static void logout(PlayerEvent.PlayerLoggedOutEvent e){if(e.getEntity() instanceof ServerPlayer p)update(p,false);}
 @SubscribeEvent public static void clone(PlayerEvent.Clone e){if(e.getOriginal().getPersistentData().contains(LEASE))e.getEntity().getPersistentData().put(LEASE,e.getOriginal().getPersistentData().getCompound(LEASE).copy());}
 @SubscribeEvent(priority=EventPriority.LOWEST) public static void applicable(MobEffectEvent.Applicable e){if(DivineProtection.active(e.getEntity())&&unwanted(e.getEffectInstance()))e.setResult(Event.Result.DENY);}
 @SubscribeEvent(priority=EventPriority.LOWEST,receiveCanceled=true) public static void remove(MobEffectEvent.Remove e){if(DivineProtection.active(e.getEntity())&&unwanted(e.getEffectInstance()))e.setCanceled(false);}
 @SubscribeEvent(priority=EventPriority.LOWEST) public static void knockback(LivingKnockBackEvent e){if(DivineProtection.active(e.getEntity()))e.setCanceled(true);}
 @SubscribeEvent public static void mining(PlayerEvent.BreakSpeed e){if(wearing(e.getEntity()))e.setNewSpeed(e.getNewSpeed()*3*(e.getEntity().getAbilities().flying&&!e.getEntity().onGround()?5:1));}
}

