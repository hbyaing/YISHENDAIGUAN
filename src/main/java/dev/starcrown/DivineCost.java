package dev.starcrown;
import java.util.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
/** Actual health-loss capture plus bounded, non-recursive server pulses. Potion acceptance is cosmetic. */
@Mod.EventBusSubscriber(modid=StarcrownDoll.ID)
public final class DivineCost {
 public static final String KEY="starcrown_cost";
 private record Attempt(ServerPlayer owner,float before){}
 private static final Map<LivingEntity,Attempt> PENDING=new WeakHashMap<>();
 private static final ThreadLocal<Boolean> PULSE=ThreadLocal.withInitial(()->false);
 public static boolean pulsing(){return PULSE.get();}
 public static void prepare(LivingEntity target,ServerPlayer owner){if(!pulsing())PENDING.put(target,new Attempt(owner,target.getHealth()));}
 public static void finish(LivingEntity target){if(pulsing())return;var hit=PENDING.remove(target);if(hit==null)return;float actual=Math.max(0,hit.before-target.getHealth());if(actual>0&&Float.isFinite(actual)&&target.isAlive())apply(target,hit.owner,actual);}
 public static void apply(LivingEntity target,ServerPlayer owner,float actual){if(!(target.level() instanceof ServerLevel level)||!Float.isFinite(actual)||actual<=0)return;var tag=new CompoundTag();long now=level.getGameTime();tag.putUUID("owner",owner.getUUID());tag.putFloat("damage",actual);tag.putLong("until",now+200);tag.putLong("next",now+2);tag.putInt("pulses",0);target.getPersistentData().put(KEY,tag);target.addEffect(new MobEffectInstance(DivineEffects.COST.get(),200,0,false,true,true));}
 public static void clear(LivingEntity target){target.getPersistentData().remove(KEY);target.removeEffect(DivineEffects.COST.get());}
 public static void tick(LivingEntity target){if(!(target.level() instanceof ServerLevel level)||!target.getPersistentData().contains(KEY))return;var tag=target.getPersistentData().getCompound(KEY);long now=level.getGameTime();
  if(!target.isAlive()||now>tag.getLong("until")||tag.getInt("pulses")>=100||!tag.hasUUID("owner")){clear(target);return;}
  var owner=level.players().stream().filter(p->p.getUUID().equals(tag.getUUID("owner"))).findFirst().orElse(null);
  if(owner==null||!DivineWill.wearing(owner)||DivineSeven.ally(owner,target)){clear(target);return;}
  if(now<tag.getLong("next"))return;tag.putLong("next",now+2);tag.putInt("pulses",tag.getInt("pulses")+1);
  float damage=(float)Math.min(Float.MAX_VALUE,(double)tag.getFloat("damage")*20);if(!Float.isFinite(damage)||damage<=0){clear(target);return;}
  PULSE.set(true);try{target.invulnerableTime=0;var type=net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DAMAGE_TYPE,new net.minecraft.resources.ResourceLocation(StarcrownDoll.ID,"cost"));var holder=level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.DAMAGE_TYPE).getHolderOrThrow(type);target.hurt(new net.minecraft.world.damagesource.DamageSource(holder,owner,owner),damage);}finally{PULSE.set(false);}
  if(target.isAlive()&&tag.getInt("pulses")%5==0)level.sendParticles(net.minecraft.core.particles.ParticleTypes.WITCH,target.getX(),target.getY()+target.getBbHeight()*.6,target.getZ(),3,.25,.25,.25,0);
  if(!target.isAlive()||tag.getInt("pulses")>=100)clear(target);
 }
 @SubscribeEvent public static void living(LivingEvent.LivingTickEvent e){tick(e.getEntity());}
}
