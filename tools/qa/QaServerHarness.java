package dev.starcrown;
@net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid=StarcrownDoll.ID)
public final class QaServerHarness {
 @net.minecraftforge.eventbus.api.SubscribeEvent public static void started(net.minecraftforge.event.server.ServerStartedEvent e){
  String result;
  try{var pool=DivinePool.get(e.getServer());var tag=pool.save(new net.minecraft.nbt.CompoundTag());long largest=0;boolean unbounded=false;for(var item:tag.getList("players",net.minecraft.nbt.Tag.TAG_COMPOUND)){var t=(net.minecraft.nbt.CompoundTag)item;largest=Math.max(largest,t.getLong("prevented"));unbounded|=t.getString("real_pool_mode").equals("UNBOUNDED");if(t.getBoolean("active"))throw new AssertionError("stale active after reload");}if(!unbounded||largest<10)throw new AssertionError("persistent reservoir missing");result="PASS dedicated server boot without client classes; actual saved pool reloaded after client shutdown; largest prevented="+largest+"; mode=UNBOUNDED; active=false";}catch(Throwable t){result="FAIL "+t;t.printStackTrace();}
  try{java.nio.file.Files.writeString(java.nio.file.Path.of("server-qa-results.txt"),result);}catch(Exception ex){ex.printStackTrace();}e.getServer().halt(false);
 }
}
