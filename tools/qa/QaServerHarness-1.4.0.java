package dev.starcrown;
@net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid=StarcrownDoll.ID)
public final class QaServerHarness {
 static void check(boolean ok,String label,java.util.List<String> out){if(!ok)throw new AssertionError(label);out.add("PASS "+label);}
 @net.minecraftforge.eventbus.api.SubscribeEvent public static void started(net.minecraftforge.event.server.ServerStartedEvent e){
  var out=new java.util.ArrayList<String>();var level=e.getServer().overworld();net.minecraft.world.entity.animal.Pig mob=null;net.minecraft.server.level.ServerPlayer a=null,b=null;
  try{
   check(!net.minecraftforge.fml.ModList.get().isLoaded("goety"),"dedicated server boots without optional Goety",out);
   a=net.minecraftforge.common.util.FakePlayerFactory.get(level,new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(),"EternityQaA"));b=net.minecraftforge.common.util.FakePlayerFactory.get(level,new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(),"EternityQaB"));
   a.setPos(0,80,0);b.setPos(6,80,0);a.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD,new net.minecraft.world.item.ItemStack(StarcrownDoll.ITEM.get()));b.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD,new net.minecraft.world.item.ItemStack(StarcrownDoll.ITEM.get()));level.players().add(a);level.players().add(b);
   mob=net.minecraft.world.entity.EntityType.PIG.create(level);mob.setPos(3,80,0);
   check(mob.isNoAi(),"two synthetic server wearers: overlap sealed",out);a.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD,net.minecraft.world.item.ItemStack.EMPTY);check(mob.isNoAi(),"first wearer unequips: second aura keeps mob sealed",out);
   mob.setPos(6,80,0);check(mob.isNoAi(),"second wearer's exclusive region sealed",out);b.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD,net.minecraft.world.item.ItemStack.EMPTY);check(!mob.isNoAi(),"all wearers unequipped: AI restored",out);out.add("ALL_PASS");
  }catch(Throwable t){out.add("FAIL "+t);t.printStackTrace();}finally{if(a!=null)level.players().remove(a);if(b!=null)level.players().remove(b);if(mob!=null)mob.discard();}
  try{java.nio.file.Files.write(java.nio.file.Path.of("eternity-server-results.txt"),out);}catch(Exception ex){ex.printStackTrace();}e.getServer().halt(false);
 }
}
