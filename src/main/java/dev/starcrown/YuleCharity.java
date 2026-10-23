package dev.starcrown;
import java.util.*;
import net.minecraft.server.level.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.parameters.*;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.fml.common.Mod;
@Mod.EventBusSubscriber(modid=StarcrownDoll.ID)
public final class YuleCharity {
 public static final String DEATH="starcrowndoll_death_loot",PACT="starcrowndoll_charity_owner",CLOCK="starcrowndoll_charity_ticks";
 public static final int RADIUS=16,RESISTANCE_TICKS=1200;
 private static final ThreadLocal<LivingEntity> COST=new ThreadLocal<>();
 public static boolean active(Entity e){return YuleHumility.active(e);}
 public static boolean costWrite(LivingEntity e){return COST.get()==e;}
 public static boolean deathLoot(ItemStack s){return s.hasTag()&&s.getTag().getBoolean(DEATH);}
 public static boolean deathLoot(Entity e){return e.getPersistentData().getBoolean(DEATH)||(e instanceof ItemEntity i&&deathLoot(i.getItem()));}
 public static void mark(ItemEntity i){i.getPersistentData().putBoolean(DEATH,true);i.getItem().getOrCreateTag().putBoolean(DEATH,true);}
 public static boolean ordinary(LivingEntity e){return e instanceof AbstractVillager||e instanceof Animal&&!(e instanceof Enemy)&&e.getMobType()!=MobType.UNDEAD&&!YuleChastity.purifiable(e);}
 public static boolean canUse(Player p,Entity e,ItemStack hand){return active(p)&&hand.isEmpty()&&e instanceof LivingEntity l&&l.isAlive()&&(l.getHealth()<l.getMaxHealth()||ordinary(l));}
 public static InteractionResult use(Player p,Entity e,InteractionHand hand){if(!canUse(p,e,p.getItemInHand(hand)))return InteractionResult.PASS;if(p.level().isClientSide)return InteractionResult.SUCCESS;var l=(LivingEntity)e;
  if(l.getHealth()<l.getMaxHealth()){l.setHealth(l.getMaxHealth());l.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,RESISTANCE_TICKS,4));}
  if(ordinary(l)){var d=l.getPersistentData();if(!d.hasUUID(PACT)||!d.getUUID(PACT).equals(p.getUUID())){d.putUUID(PACT,p.getUUID());d.putInt(CLOCK,0);}}
  ((ServerLevel)p.level()).sendParticles(net.minecraft.core.particles.ParticleTypes.HEART,l.getX(),l.getY()+l.getBbHeight(),l.getZ(),4,.25,.2,.25,.01);return InteractionResult.CONSUME;
 }
 public static void sorrow(LivingEntity victim){if(!(victim.level() instanceof ServerLevel level)||victim.getMobType()==MobType.UNDEAD)return;for(var p:level.players())if(p!=victim&&active(p)&&p.isAlive()&&p.distanceToSqr(victim)<=RADIUS*RADIUS){var before=COST.get();COST.set(p);try{p.setHealth(Math.max(Float.MIN_VALUE,p.getHealth()*.5f));}finally{if(before==null)COST.remove();else COST.set(before);}}}
 @SubscribeEvent(priority=EventPriority.LOWEST) public static void died(LivingDeathEvent e){sorrow(e.getEntity());}
 @SubscribeEvent(priority=EventPriority.LOWEST,receiveCanceled=true) public static void drops(LivingDropsEvent e){for(var item:e.getDrops())mark(item);}
 public static void deathExperience(ServerLevel level,Vec3 pos,int amount){while(amount>0){int n=ExperienceOrb.getExperienceValue(amount);amount-=n;var orb=new ExperienceOrb(level,pos.x,pos.y,pos.z,n);orb.getPersistentData().putBoolean(DEATH,true);level.addFreshEntity(orb);}}
 @SubscribeEvent(priority=EventPriority.HIGHEST) public static void pickup(net.minecraftforge.event.entity.player.EntityItemPickupEvent e){if(active(e.getEntity())&&deathLoot(e.getItem()))e.setCanceled(true);}
 @SubscribeEvent(priority=EventPriority.HIGHEST) public static void interact(PlayerInteractEvent.EntityInteract e){if(canUse(e.getEntity(),e.getTarget(),e.getItemStack())){var result=use(e.getEntity(),e.getTarget(),e.getHand());e.setCanceled(true);e.setCancellationResult(result);}}
 @SubscribeEvent(priority=EventPriority.HIGHEST) public static void specific(PlayerInteractEvent.EntityInteractSpecific e){if(canUse(e.getEntity(),e.getTarget(),e.getItemStack())){var result=use(e.getEntity(),e.getTarget(),e.getHand());e.setCanceled(true);e.setCancellationResult(result);}}
 public static java.util.List<ItemStack> gifts(Mob mob,ServerPlayer p){var result=new ArrayList<ItemStack>();if(mob instanceof AbstractVillager v){for(var offer:v.getOffers())if(!offer.getResult().isEmpty())result.add(offer.getResult().copy());}else if(mob instanceof Animal){var level=(ServerLevel)mob.level();var params=new LootParams.Builder(level).withParameter(LootContextParams.THIS_ENTITY,mob).withParameter(LootContextParams.ORIGIN,mob.position()).withParameter(LootContextParams.DAMAGE_SOURCE,level.damageSources().generic()).withOptionalParameter(LootContextParams.LAST_DAMAGE_PLAYER,p).withLuck(p.getLuck()).create(LootContextParamSets.ENTITY);for(var stack:level.getServer().getLootData().getLootTable(mob.getLootTable()).getRandomItems(params)){var copy=stack.copy();if(copy.hasTag())copy.getTag().remove(DEATH);result.add(copy);}}return YuleDiligence.improveGifts(result,p,mob instanceof AbstractVillager);}
 public static void produce(Mob mob){if(!(mob.level() instanceof ServerLevel level)||!mob.isAlive()||!ordinary(mob))return;var data=mob.getPersistentData();if(!data.hasUUID(PACT))return;var owner=level.getPlayerByUUID(data.getUUID(PACT));if(!(owner instanceof ServerPlayer p)||!active(p)||!p.isAlive()||p.distanceToSqr(mob)>RADIUS*RADIUS)return;int ticks=data.getInt(CLOCK)+1;if(ticks<20){data.putInt(CLOCK,ticks);return;}data.putInt(CLOCK,0);for(var stack:gifts(mob,p))if(!stack.isEmpty())YuleGenerosity.redirect(p,stack);p.getInventory().setChanged();p.inventoryMenu.broadcastChanges();}
 @SubscribeEvent public static void living(LivingEvent.LivingTickEvent e){if(e.getEntity() instanceof Mob mob)produce(mob);}
 private YuleCharity(){}
}
