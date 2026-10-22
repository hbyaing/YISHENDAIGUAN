package dev.starcrownqa;

import dev.starcrown.*;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Run /defenseqa in a disposable test world with the separate QA mod installed. */
@Mod.EventBusSubscriber(modid="starcrownqa")
public final class DefenseQa {
 private static void check(boolean ok,String message){if(!ok)throw new AssertionError(message);}
 @SubscribeEvent public static void commands(RegisterCommandsEvent event){
  event.getDispatcher().register(Commands.literal("defenseqa").requires(s->s.hasPermission(2)).executes(c->{
   var p=c.getSource().getPlayerOrException();
   run(p);
   c.getSource().sendSuccess(()->Component.literal("PASS: spectator, lethal damage, kill, locked raw slot repair, unlock and removal exemptions"),false);
   return 1;
  }));
 }
 private static void run(ServerPlayer p){
  var pool=DivinePool.get(p.getServer());var entry=pool.entry(p.getUUID());
  boolean locked=entry.locked;var crown=entry.crown.copy();
  var head=p.getItemBySlot(EquipmentSlot.HEAD).copy();var mode=p.gameMode.getGameModeForPlayer();
  try{
   DivineAnchor.unlock(p);p.setGameMode(GameType.SURVIVAL);
   p.setItemSlot(EquipmentSlot.HEAD,new ItemStack(StarcrownDoll.ITEM.get()));
   DivineAnchor.lock(p);
   p.setGameMode(GameType.SPECTATOR);
   check(DivineProtection.active(p),"spectator must retain life protection");
   check(!DivineWill.wearing(p),"spectator must not receive movement/AI perks");
   p.setHealth(0);p.kill();p.hurt(p.damageSources().genericKill(),Float.MAX_VALUE);
   check(p.isAlive()&&!p.isRemoved()&&p.getHealth()>0,"spectator survived lethal APIs");
   p.getInventory().armor.set(3,ItemStack.EMPTY);
   check(DivineProtection.active(p),"lock retains identity after raw slot removal");
   p.deathTime=100;p.hurtTime=100;
   // Invoke only the server watchdog: this test does not rely on PlayerTickEvent.
   DivineProtection.watchdog(new TickEvent.ServerTickEvent(TickEvent.Phase.END,()->true,p.getServer()));
   check(p.getItemBySlot(EquipmentSlot.HEAD).is(StarcrownDoll.ITEM.get()),"server watchdog restored crown");
   check(p.deathTime==0&&p.hurtTime==0,"server watchdog reset death and hurt timers");
   check(DivineProtection.blockRemoval(p,net.minecraft.world.entity.Entity.RemovalReason.KILLED,"qa"),"lethal removal rejected");
   check(!DivineProtection.blockRemoval(p,net.minecraft.world.entity.Entity.RemovalReason.CHANGED_DIMENSION,"qa"),"dimension transition allowed");
   check(!DivineProtection.blockRemoval(p,net.minecraft.world.entity.Entity.RemovalReason.UNLOADED_WITH_PLAYER,"qa"),"logout allowed");
   DivineAnchor.unlock(p);p.setItemSlot(EquipmentSlot.HEAD,ItemStack.EMPTY);
   check(!DivineProtection.active(p),"explicit unlock and unequip release protection");
  }finally{
   DivineAnchor.unlock(p);p.setItemSlot(EquipmentSlot.HEAD,head);p.setGameMode(mode);
   entry.locked=locked;entry.crown=crown;pool.active(p.getUUID(),DivineProtection.active(p));pool.setDirty();
  }
 }
}
