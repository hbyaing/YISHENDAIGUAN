package dev.starcrown;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.*;
/** Opt-in anchor, with explicit release. Not a JVM security boundary. */
@Mod.EventBusSubscriber(modid=StarcrownDoll.ID)
public final class DivineAnchor {
 public static boolean locked(ServerPlayer p){return p.getServer()!=null&&DivinePool.get(p.getServer()).entry(p.getUUID()).locked;}
 public static void lock(ServerPlayer p){var stack=p.getItemBySlot(EquipmentSlot.HEAD);if(!stack.is(StarcrownDoll.ITEM.get()))throw new IllegalStateException("请先佩戴成然玩偶");var pool=DivinePool.get(p.getServer());var entry=pool.entry(p.getUUID());entry.crown=stack.copyWithCount(1).save(new net.minecraft.nbt.CompoundTag());entry.locked=true;pool.setDirty();}
 public static void unlock(ServerPlayer p){var pool=DivinePool.get(p.getServer());var entry=pool.entry(p.getUUID());entry.locked=false;entry.crown=new net.minecraft.nbt.CompoundTag();pool.setDirty();}
 public static ItemStack crown(ServerPlayer p){var saved=ItemStack.of(DivinePool.get(p.getServer()).entry(p.getUUID()).crown);return saved.is(StarcrownDoll.ITEM.get())?saved.copyWithCount(1):new ItemStack(StarcrownDoll.ITEM.get());}
 public static boolean blockSlot(net.minecraft.world.entity.player.Player owner,int slot,ItemStack next){
  if(!(owner instanceof ServerPlayer p)||slot!=39||!locked(p))return false;
  var expected=crown(p);if(next.getCount()==1&&ItemStack.isSameItemSameTags(expected,next))return false;
  DivineProtection.prevent(p,"anchor.headSlot",0);return true;
 }
 private static boolean movingSection(){return StackWalker.getInstance().walk(frames->frames.anyMatch(f->f.getClassName().equals("net.minecraft.world.level.entity.PersistentEntitySectionManager$Callback")&&(f.getMethodName().equals("onMove")||f.getMethodName().equals("m_142044_"))));}
 public static boolean blockContainer(Object value,String path){
  if(!(value instanceof ServerPlayer p)||p.isRemoved()||p.getServer()==null||p.getServer().isStopped()||movingSection()||!DivineProtection.active(p))return false;
  DivineProtection.prevent(p,path,0);return true;
 }
 public static void repair(ServerPlayer p){if(!locked(p))return;var expected=crown(p);var actual=p.getItemBySlot(EquipmentSlot.HEAD);if(actual.getCount()==1&&ItemStack.isSameItemSameTags(actual,expected))return;
  // A raw list write bypassed slot setters. Keep the displaced stack if one exists.
  if(!actual.isEmpty()){var displaced=actual.copy();if(!p.getInventory().add(displaced))p.drop(displaced,false);}
  p.getInventory().armor.set(3,expected);p.getInventory().setChanged();p.inventoryMenu.broadcastChanges();DivineProtection.prevent(p,"anchor.rawHeadRepair",0);
 }
 @SubscribeEvent(priority=EventPriority.HIGHEST) public static void tick(TickEvent.PlayerTickEvent e){if(e.phase==TickEvent.Phase.START&&e.player instanceof ServerPlayer p)repair(p);}
 @SubscribeEvent public static void commands(RegisterCommandsEvent e){e.getDispatcher().register(Commands.literal("yishen")
  .then(Commands.literal("lock").executes(c->{var p=c.getSource().getPlayerOrException();if(!p.getItemBySlot(EquipmentSlot.HEAD).is(StarcrownDoll.ITEM.get())){c.getSource().sendFailure(Component.literal("请先佩戴成然玩偶"));return 0;}lock(p);c.getSource().sendSuccess(()->Component.literal("防护锁已开启；使用 /yishen unlock 解锁后取下玩偶。"),false);return 1;}))
  .then(Commands.literal("unlock").executes(c->{unlock(c.getSource().getPlayerOrException());c.getSource().sendSuccess(()->Component.literal("防护锁已解除，可以正常取下玩偶。"),false);return 1;})));
 }
}
