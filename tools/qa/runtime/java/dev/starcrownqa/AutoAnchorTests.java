package dev.starcrownqa;
import dev.starcrown.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraftforge.gametest.*;
@GameTestHolder("starcrownqa")
@PrefixGameTestTemplate(false)
public final class AutoAnchorTests {
 private static ServerPlayer wearer(GameTestHelper h){
  var level=h.getLevel();var p=new ServerPlayer(level.getServer(),level,new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(),"anchor-test"));
  var connection=new net.minecraft.network.Connection(net.minecraft.network.protocol.PacketFlow.SERVERBOUND);new io.netty.channel.embedded.EmbeddedChannel(connection);connection.setProtocol(net.minecraft.network.ConnectionProtocol.PLAY);p.connection=new net.minecraft.server.network.ServerGamePacketListenerImpl(level.getServer(),connection,p);p.moveTo(h.absolutePos(new net.minecraft.core.BlockPos(2,2,2)),0,0);level.addNewPlayer(p);
  var crown=new ItemStack(StarcrownDoll.ITEM.get());crown.getOrCreateTag().putString("qa","retain-this");p.setItemSlot(EquipmentSlot.HEAD,crown);DivineAnchor.observe(p);return p;
 }
 private static void cleanup(ServerPlayer p){DivineAnchor.unlock(p);p.setItemSlot(EquipmentSlot.HEAD,ItemStack.EMPTY);p.serverLevel().removePlayerImmediately(p,Entity.RemovalReason.DISCARDED);p.connection.connection.disconnect(net.minecraft.network.chat.Component.literal("done"));}
 @GameTest(template="empty",timeoutTicks=100)
 public static void automaticDeleteAndMenuRelease(GameTestHelper h){var p=wearer(h);try{
  h.assertTrue(!DivineAnchor.locked(p),"wearing needs no manual lock");
  p.getInventory().setItem(39,ItemStack.EMPTY);h.assertTrue(DivineAnchor.locked(p)&&p.getItemBySlot(EquipmentSlot.HEAD).is(StarcrownDoll.ITEM.get()),"slot delete auto-blocked");
  p.getInventory().clearContent();h.assertTrue(p.getItemBySlot(EquipmentSlot.HEAD).getOrCreateTag().getString("qa").equals("retain-this"),"clear preserves NBT");
  p.inventoryMenu.clicked(5,0,net.minecraft.world.inventory.ClickType.PICKUP,p);
  h.assertTrue(!DivineAnchor.locked(p)&&p.getItemBySlot(EquipmentSlot.HEAD).isEmpty(),"normal inventory click automatically releases lock");
  h.assertTrue(p.inventoryMenu.getCarried().is(StarcrownDoll.ITEM.get()),"only original crown moved to cursor");
  h.assertTrue(!DivineProtection.active(p),"manual unequip releases life protection");h.succeed();
 }finally{cleanup(p);}}
 @GameTest(template="empty",timeoutTicks=100)
 public static void rawDeletionAndKill(GameTestHelper h){var p=wearer(h);try{
  p.getInventory().armor.set(3,ItemStack.EMPTY);p.kill();
  h.assertTrue(p.isAlive()&&!p.isRemoved()&&DivineAnchor.locked(p),"raw-delete-then-kill rejected without tick delay");
  DivineAnchor.repair(p);h.assertTrue(p.getItemBySlot(EquipmentSlot.HEAD).getOrCreateTag().getString("qa").equals("retain-this"),"raw deletion restored exact crown");
  p.getItemBySlot(EquipmentSlot.HEAD).setCount(0);DivineAnchor.repair(p);
  h.assertTrue(p.getItemBySlot(EquipmentSlot.HEAD).getCount()==1,"in-place count-zero repaired");h.succeed();
 }finally{cleanup(p);}}
}
