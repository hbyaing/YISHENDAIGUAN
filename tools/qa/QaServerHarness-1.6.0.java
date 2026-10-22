package dev.starcrown;
@net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid=StarcrownDoll.ID)
public final class QaServerHarness {
 static void ok(boolean b,String s,java.util.List<String> out){if(!b)throw new AssertionError(s);out.add("PASS "+s);}
 @net.minecraftforge.eventbus.api.SubscribeEvent public static void start(net.minecraftforge.event.server.ServerStartedEvent e){var out=new java.util.ArrayList<String>();net.minecraft.server.level.ServerPlayer p=null;net.minecraft.world.entity.animal.Pig pig=null;var level=e.getServer().overworld();try{
  ok(!net.minecraftforge.fml.ModList.get().isLoaded("arcanevortex"),"dedicated server boots without Arcane Vortex",out);
  p=net.minecraftforge.common.util.FakePlayerFactory.get(level,new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(),"BaseGuardQA"));p.setPos(0,80,0);p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD,new net.minecraft.world.item.ItemStack(StarcrownDoll.ITEM.get()));level.players().add(p);
  var f=net.minecraft.world.entity.LivingEntity.class.getDeclaredField("DATA_HEALTH_ID");f.setAccessible(true);@SuppressWarnings("unchecked")var key=(net.minecraft.network.syncher.EntityDataAccessor<Float>)f.get(null);p.getEntityData().set(key,0f,true);ok(p.getEntityData().get(key)>0&&p.getHealth()>0,"generic forced synced health protected without Arcane",out);
  var max=p.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH);double base=max.getBaseValue();max.setBaseValue(0);ok(p.getMaxHealth()>=20&&p.getHealth()>0,"protected maximum health stays finite positive",out);max.setBaseValue(base);
  pig=net.minecraft.world.entity.EntityType.PIG.create(level);pig.setPos(1,80,0);ok(DivineEternity.sealed(pig)&&!pig.isEffectiveAi()&&!pig.isNoAi(),"aura suspends AI without changing original NoAI",out);var tag=new net.minecraft.nbt.CompoundTag();pig.addAdditionalSaveData(tag);ok(!tag.getBoolean("NoAI"),"original NoAI flag remains false in saved NBT",out);
  p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD,net.minecraft.world.item.ItemStack.EMPTY);ok(pig.isEffectiveAi(),"AI restores after unequip",out);p.setHealth(10);ok(p.getHealth()==10,"unprotected health setter works normally",out);out.add("ALL_PASS");
 }catch(Throwable t){out.add("FAIL "+t);t.printStackTrace();}finally{if(p!=null)level.players().remove(p);if(pig!=null)pig.discard();}try{java.nio.file.Files.write(java.nio.file.Path.of("av-free-server-results.txt"),out);}catch(Exception ex){ex.printStackTrace();}e.getServer().halt(false);}
}
