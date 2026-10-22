package dev.starcrown;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.event.*;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.commands.Commands;
@Mod.EventBusSubscriber(modid=StarcrownDoll.ID)
public final class DivineProtection {
 private static final ThreadLocal<Boolean> REPAIRING=ThreadLocal.withInitial(()->false);
 public static boolean active(Entity e){return e instanceof ServerPlayer p && p.getInventory()!=null && !p.isSpectator() && (p.getItemBySlot(EquipmentSlot.HEAD).is(StarcrownDoll.ITEM.get())||DivineAnchor.locked(p));}
 public static float displayHealth(LivingEntity p){return DivineCombat.maximum(p);}
 public static void prevent(Entity e,String path,float amount){if(e instanceof ServerPlayer p&&p.getServer()!=null){var server=p.getServer();if(server.isSameThread())DivinePool.get(server).prevent(p.getUUID(),path,amount);else server.execute(()->DivinePool.get(server).prevent(p.getUUID(),path,amount));}}
 public static boolean blockHealth(LivingEntity e,float next){if(REPAIRING.get()||!active(e))return false;if(!Float.isFinite(next)||next<displayHealth(e)){prevent(e,"setHealth",Float.isFinite(next)?Math.max(0,displayHealth(e)-next):next);return true;}return false;}
 public static boolean blockDeath(Entity e,String path){if(!active(e))return false;prevent(e,path,0);repair((ServerPlayer)e);return true;}
 public static boolean blockRemoval(Entity e,Entity.RemovalReason reason,String path){if(!active(e)||reason==Entity.RemovalReason.CHANGED_DIMENSION||reason==Entity.RemovalReason.UNLOADED_WITH_PLAYER)return false;prevent(e,path+":"+reason,0);return true;}
 public static void repair(ServerPlayer p){if(REPAIRING.get())return;REPAIRING.set(true);try{p.setHealth(displayHealth(p));p.deathTime=0;p.hurtTime=0;}finally{REPAIRING.set(false);}}
 @SubscribeEvent(priority=EventPriority.HIGHEST) public static void attack(LivingAttackEvent e){if(active(e.getEntity())){prevent(e.getEntity(),"LivingAttack",e.getAmount());e.setCanceled(true);}}
 @SubscribeEvent(priority=EventPriority.LOWEST) public static void hurt(LivingHurtEvent e){if(active(e.getEntity())){prevent(e.getEntity(),"LivingHurt",e.getAmount());e.setAmount(0);e.setCanceled(true);}}
 @SubscribeEvent(priority=EventPriority.LOWEST) public static void damage(LivingDamageEvent e){if(active(e.getEntity())){prevent(e.getEntity(),"LivingDamage",e.getAmount());e.setAmount(0);e.setCanceled(true);}}
 @SubscribeEvent(priority=EventPriority.LOWEST) public static void death(LivingDeathEvent e){if(active(e.getEntity())){prevent(e.getEntity(),"LivingDeath",0);e.setCanceled(true);repair((ServerPlayer)e.getEntity());}}
 @SubscribeEvent(priority=EventPriority.HIGHEST) public static void first(TickEvent.PlayerTickEvent e){if(e.phase==TickEvent.Phase.START&&e.player instanceof ServerPlayer p&&active(p)){DivineCombat.cleanArcane(p);repair(p);}}
 @SubscribeEvent public static void tick(TickEvent.PlayerTickEvent e){if(e.phase!=TickEvent.Phase.END||!(e.player instanceof ServerPlayer p))return;boolean enabled=active(p);DivinePool.get(p.getServer()).active(p.getUUID(),enabled);if(enabled)repair(p);}
 @SubscribeEvent public static void logout(PlayerEvent.PlayerLoggedOutEvent e){if(e.getEntity() instanceof ServerPlayer p)DivinePool.get(p.getServer()).active(p.getUUID(),false);}
 @SubscribeEvent public static void command(RegisterCommandsEvent e){e.getDispatcher().register(Commands.literal("yishen").then(Commands.literal("status").executes(c->{ServerPlayer p=c.getSource().getPlayerOrException();var state=DivinePool.get(p.getServer()).entry(p.getUUID());c.getSource().sendSuccess(()->Component.literal("真实血池：∞ | 佩戴生效："+active(p)+" | 显示血量："+p.getHealth()+" | 已拦截："+state.prevented+" | 最近入口："+state.lastPath),false);return 1;})));}
}
