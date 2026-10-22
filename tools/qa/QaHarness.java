package dev.starcrown;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.CameraType;
import net.minecraft.client.Screenshot;
import java.nio.file.*;
@Mod.EventBusSubscriber(modid=StarcrownDoll.ID,value=Dist.CLIENT)
public class QaHarness {
 static void inspect(Minecraft mc)throws Exception{
  var f=net.minecraft.client.particle.ParticleEngine.class.getDeclaredField("particles");f.setAccessible(true);
  var ground=net.minecraft.client.particle.Particle.class.getDeclaredField("onGround");ground.setAccessible(true);
  var yy=net.minecraft.client.particle.Particle.class.getDeclaredField("y");yy.setAccessible(true);
  int count=0,landed=0;double min=999,max=-999;
  for(Object queue:((java.util.Map<?,?>)f.get(mc.particleEngine)).values())for(Object particle:(Iterable<?>)queue)if(particle.getClass().getName().contains("DollClient$Star")){count++;if(ground.getBoolean(particle))landed++;double y=yy.getDouble(particle);min=Math.min(min,y);max=Math.max(max,y);}
  String result="head="+mc.player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD)+" hand="+mc.player.getMainHandItem()+" stars="+count+" grounded="+landed+" minY="+min+" maxY="+max;
  Files.writeString(Path.of("qa-status.txt"),result);System.out.println("QA_STATUS "+result);
 }

 static void divineTests(net.minecraft.server.level.ServerPlayer p){
  java.util.List<String> results=new java.util.ArrayList<>();
  try {
   p.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);p.getAbilities().invulnerable=false;p.setInvulnerable(false);var spawn=net.minecraft.server.level.ServerPlayer.class.getDeclaredField("spawnInvulnerableTime");spawn.setAccessible(true);spawn.setInt(p,0);
   p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD,new net.minecraft.world.item.ItemStack(StarcrownDoll.ITEM.get()));
   p.setHealth(20);checkPlayer(p,"equip",results);
   p.hurt(p.damageSources().generic(),Float.MAX_VALUE);checkPlayer(p,"Float.MAX_VALUE damage",results);
   p.hurt(p.damageSources().generic(),Float.POSITIVE_INFINITY);checkPlayer(p,"infinite damage",results);
   p.setHealth(0);checkPlayer(p,"setHealth zero",results);
   p.setHealth(-Float.MAX_VALUE);checkPlayer(p,"setHealth negative",results);
   p.setHealth(Float.NaN);checkPlayer(p,"setHealth NaN",results);
   p.setHealth(Float.POSITIVE_INFINITY);checkPlayer(p,"setHealth infinity",results);
   p.die(p.damageSources().genericKill());checkPlayer(p,"direct die",results);
   p.kill();checkPlayer(p,"kill command path",results);
   p.discard();checkPlayer(p,"discard",results);
   p.remove(net.minecraft.world.entity.Entity.RemovalReason.KILLED);checkPlayer(p,"remove killed",results);
   p.remove(net.minecraft.world.entity.Entity.RemovalReason.UNLOADED_TO_CHUNK);checkPlayer(p,"remove unload-to-chunk",results);
   p.setRemoved(net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);checkPlayer(p,"direct setRemoved discarded",results);
   p.serverLevel().removePlayerImmediately(p,net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);checkPlayer(p,"ServerLevel.removePlayerImmediately",results);
   var field=net.minecraft.world.entity.LivingEntity.class.getDeclaredField("DATA_HEALTH_ID");field.setAccessible(true);
   @SuppressWarnings("unchecked") var key=(net.minecraft.network.syncher.EntityDataAccessor<Float>)field.get(null);
   p.getEntityData().set(key,0f);checkPlayer(p,"raw SynchedEntityData zero read shield",results);DivineProtection.repair(p);
   if(p.getEntityData().get(key)<=0)throw new AssertionError("raw health not repaired");results.add("PASS raw health storage repaired");
   var sword=net.minecraftforge.registries.ForgeRegistries.ITEMS.getValue(new net.minecraft.resources.ResourceLocation("avaritia:infinity_sword"));
   if(sword==null||sword==net.minecraft.world.item.Items.AIR)throw new AssertionError("real Avaritia infinity sword not loaded");
   var stack=new net.minecraft.world.item.ItemStack(sword);
   sword.onLeftClickEntity(stack,p,p);checkPlayer(p,"AvaritiaNeo 1.1.6 real onLeftClickEntity",results);
   sword.hurtEnemy(stack,p,p);checkPlayer(p,"AvaritiaNeo 1.1.6 real hurtEnemy",results);
   var pool=DivinePool.get(p.getServer());var before=pool.entry(p.getUUID());long prevented=before.prevented;
   var tag=pool.save(new net.minecraft.nbt.CompoundTag());var restored=DivinePool.load(tag).entry(p.getUUID());
   if(prevented<10||restored.prevented!=prevented)throw new AssertionError("persistent pool counters invalid");
   if(!tag.toString().contains("UNBOUNDED"))throw new AssertionError("infinite pool marker absent");results.add("PASS persistent unbounded pool roundtrip; prevented="+prevented+" last="+before.lastPath);
   if(DivineProtection.blockRemoval(p,net.minecraft.world.entity.Entity.RemovalReason.CHANGED_DIMENSION,"qa"))throw new AssertionError("dimension change blocked");
   if(DivineProtection.blockRemoval(p,net.minecraft.world.entity.Entity.RemovalReason.UNLOADED_WITH_PLAYER,"qa"))throw new AssertionError("logout blocked");results.add("PASS dimension and logout reasons explicitly allowed");
   p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD,net.minecraft.world.item.ItemStack.EMPTY);p.invulnerableTime=0;p.setHealth(20);p.hurt(p.damageSources().generic(),4);
   if(p.getHealth()>=20||!p.isAlive())throw new AssertionError("unequipped ordinary damage broken");results.add("PASS unequipped normal damage; health="+p.getHealth());
   p.setHealth(20);p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD,new net.minecraft.world.item.ItemStack(StarcrownDoll.ITEM.get()));
   results.add("ALL_PASS");
  }catch(Throwable ex){results.add("FAIL "+ex);ex.printStackTrace();}
  try{java.nio.file.Files.write(java.nio.file.Path.of("divine-results.txt"),results);}catch(Exception ex){ex.printStackTrace();}
 }
 static void checkPlayer(net.minecraft.server.level.ServerPlayer p,String label,java.util.List<String> results){
  if(p.isRemoved()||!p.isAlive()||!Float.isFinite(p.getHealth())||p.getHealth()<=0||p.deathTime>0||p.serverLevel().getEntity(p.getUUID())!=p||p.getServer().getPlayerList().getPlayer(p.getUUID())!=p)throw new AssertionError(label+" failed: removed="+p.isRemoved()+" health="+p.getHealth());
  results.add("PASS "+label+" health="+p.getHealth()+" registered=true");
 }



 static void apollyonTest(net.minecraft.server.level.ServerPlayer p){
  java.util.List<String> results=new java.util.ArrayList<>();
  try{
   p.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);p.getAbilities().invulnerable=false;p.setInvulnerable(false);p.invulnerableTime=0;
   var spawn=net.minecraft.server.level.ServerPlayer.class.getDeclaredField("spawnInvulnerableTime");spawn.setAccessible(true);spawn.setInt(p,0);
   p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD,new net.minecraft.world.item.ItemStack(StarcrownDoll.ITEM.get()));
   if(p.serverLevel().dimension()!=net.minecraft.world.level.Level.NETHER)throw new AssertionError("test player must be in the Nether");
   var type=net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES.getValue(new net.minecraft.resources.ResourceLocation("goety:apostle"));
   var boss=(net.minecraft.world.entity.Mob)type.create(p.serverLevel());if(boss==null)throw new AssertionError("apostle create failed");
   boss.moveTo(p.getX()+1.5,p.getY(),p.getZ(),0,0);boss.setPersistenceRequired();boss.setNoAi(true);boss.setTarget(p);
   Class<?> api=Class.forName("z1gned.goetyrevelation.util.ApollyonAbilityHelper");api.getMethod("allTitlesApostle_1_20_1$setApollyon",boolean.class).invoke(boss,true);
   p.serverLevel().addFreshEntity(boss);
   if(!(boolean)boss.getClass().getMethod("isInNether").invoke(boss))throw new AssertionError("not Nether Apostle");
   results.add("PASS real Goety 2.5.58.4 + Revelation 2.3.4fix2 + bundled RevelationFix 4.7; Nether Apollyon=true");
   Class<?> death=Class.forName("com.mega.revelationfix.common.apollyon.common.DeathPerformance");Class<?> apostle=Class.forName("com.Polarice3.Goety.common.entities.boss.Apostle");
   var perform=death.getMethod("perform",apostle,api,int.class);
   death.getMethod("setLeftTime",apostle,int.class).invoke(null,boss,300);
   death.getMethod("setDeathGrowingTime",apostle,int.class).invoke(null,boss,80);
   long before=DivinePool.get(p.getServer()).entry(p.getUUID()).prevented;
   for(int i=0;i<40;i++)perform.invoke(null,boss,boss,300);
   checkPlayer(p,"40 real DeathPerformance barrier executions",results);
   long delta=DivinePool.get(p.getServer()).entry(p.getUUID()).prevented-before;
   if(delta==0)throw new AssertionError("real barrier did not reach player");results.add("PASS real barrier damage reached and was blocked="+delta+" last="+DivinePool.get(p.getServer()).entry(p.getUUID()).lastPath);
   // Negative control: low-health unprotected pig, same unmodified tail damage routine.
   var control=net.minecraft.world.entity.EntityType.PIG.create(p.serverLevel());control.moveTo(p.getX()+.8,p.getY(),p.getZ(),0,0);control.setNoAi(true);control.setHealth(.001f);p.serverLevel().addFreshEntity(control);
   for(int i=0;i<8&&control.isAlive();i++)perform.invoke(null,boss,boss,300);
   if(control.isAlive()&&!control.isRemoved())throw new AssertionError("unprotected low-health control survived tail");results.add("PASS actual barrier killed unprotected low-health control pig");
   // Traverse the actual timing range of the whole 560-tick tail routine.
   for(int remaining=560;remaining>=0;remaining--)perform.invoke(null,boss,boss,remaining);
   checkPlayer(p,"complete 560-step tail callback timing sweep",results);
   results.add("PASS complete timing sweep; protected player remains alive and world/player-list registered");
   boss.discard();results.add("ALL_PASS");
  }catch(Throwable ex){results.add("FAIL "+ex);ex.printStackTrace();}
  try{java.nio.file.Files.write(java.nio.file.Path.of("apollyon-results.txt"),results);}catch(Exception ex){ex.printStackTrace();}
 }


 static net.minecraft.world.entity.Mob liveBoss;
 static net.minecraft.world.entity.animal.Pig liveControl;
 static net.minecraft.server.level.ServerPlayer livePlayer;
 static java.lang.reflect.Method livePerform;
 static int liveStep;
 static long liveBefore;
 static boolean previousGravity;
 static void startLive(net.minecraft.server.level.ServerPlayer p){
  try{
   p.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);p.getAbilities().invulnerable=false;p.setInvulnerable(false);
   var spawn=net.minecraft.server.level.ServerPlayer.class.getDeclaredField("spawnInvulnerableTime");spawn.setAccessible(true);spawn.setInt(p,0);
   p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD,new net.minecraft.world.item.ItemStack(StarcrownDoll.ITEM.get()));
   if(p.serverLevel().dimension()!=net.minecraft.world.level.Level.NETHER)throw new AssertionError("must enter Nether");
   var level=p.serverLevel();for(int x=-5;x<=5;x++)for(int z=-5;z<=5;z++){level.setBlockAndUpdate(new net.minecraft.core.BlockPos(x,79,z),net.minecraft.world.level.block.Blocks.OBSIDIAN.defaultBlockState());for(int y=80;y<=86;y++)level.setBlockAndUpdate(new net.minecraft.core.BlockPos(x,y,z),net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());}
   p.teleportTo(0.5,80,0.5);p.setOldPosAndRot();p.clearFire();previousGravity=p.isNoGravity();p.setNoGravity(true);
   var type=net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES.getValue(new net.minecraft.resources.ResourceLocation("goety:apostle"));liveBoss=(net.minecraft.world.entity.Mob)type.create(level);liveBoss.moveTo(2,80,.5,0,0);liveBoss.setNoAi(true);liveBoss.setNoGravity(true);liveBoss.setTarget(p);level.addFreshEntity(liveBoss);
   Class<?> api=Class.forName("z1gned.goetyrevelation.util.ApollyonAbilityHelper");api.getMethod("allTitlesApostle_1_20_1$setApollyon",boolean.class).invoke(liveBoss,true);
   Class<?> death=Class.forName("com.mega.revelationfix.common.apollyon.common.DeathPerformance");Class<?> apostle=Class.forName("com.Polarice3.Goety.common.entities.boss.Apostle");livePerform=death.getMethod("perform",apostle,api,int.class);death.getMethod("setLeftTime",apostle,int.class).invoke(null,liveBoss,560);death.getMethod("setDeathGrowingTime",apostle,int.class).invoke(null,liveBoss,0);
   liveControl=net.minecraft.world.entity.EntityType.PIG.create(level);liveControl.moveTo(1,80,.5,0,0);liveControl.setNoAi(true);liveControl.setHealth(.001f);level.addFreshEntity(liveControl);
   livePlayer=p;liveStep=0;liveBefore=DivinePool.get(p.getServer()).entry(p.getUUID()).prevented;
   java.nio.file.Files.writeString(java.nio.file.Path.of("apollyon-live-results.txt"),"RUNNING real 560 server ticks, one actual DeathPerformance call per tick");
  }catch(Throwable ex){try{java.nio.file.Files.writeString(java.nio.file.Path.of("apollyon-live-results.txt"),"FAIL "+ex);}catch(Exception ignored){}ex.printStackTrace();livePlayer=null;}
 }
 @SubscribeEvent public static void liveTick(TickEvent.ServerTickEvent e){
  if(e.phase!=TickEvent.Phase.END||livePlayer==null)return;
  var p=livePlayer;
  try{
   livePerform.invoke(null,liveBoss,liveBoss,560-liveStep);
   checkPlayer(p,"live tick "+liveStep,new java.util.ArrayList<>());liveStep++;
   if(liveStep%100==0)java.nio.file.Files.writeString(java.nio.file.Path.of("apollyon-live-results.txt"),"RUNNING tick="+liveStep+" health="+p.getHealth()+" removed="+p.isRemoved());
   if(liveStep>560){long blocked=DivinePool.get(p.getServer()).entry(p.getUUID()).prevented-liveBefore;boolean controlDied=!liveControl.isAlive()||liveControl.isRemoved();if(!controlDied||blocked<1)throw new AssertionError("control/damage not exercised");java.nio.file.Files.writeString(java.nio.file.Path.of("apollyon-live-results.txt"),"ALL_PASS real 561 consecutive server ticks (~28 seconds), real DeathPerformance one call each tick; health="+p.getHealth()+"; world/player-list registered=true; removed=false; blocked="+blocked+"; unprotected low-health control died=true");p.setNoGravity(previousGravity);liveBoss.discard();livePlayer=null;}
  }catch(Throwable ex){try{java.nio.file.Files.writeString(java.nio.file.Path.of("apollyon-live-results.txt"),"FAIL tick="+liveStep+" "+ex);}catch(Exception ignored){}p.setNoGravity(previousGravity);if(liveBoss!=null)liveBoss.discard();livePlayer=null;ex.printStackTrace();}
 }

 static int ticks;
 @SubscribeEvent public static void client(TickEvent.ClientTickEvent e){
  if(e.phase!=TickEvent.Phase.END||++ticks%10!=0)return;
  var mc=Minecraft.getInstance();mc.options.pauseOnLostFocus=false;if(mc.player==null||mc.getSingleplayerServer()==null)return;mc.mouseHandler.releaseMouse();
  Path cmd=Path.of("qa-command.txt");if(!Files.exists(cmd))return;
  try{var lines=Files.readAllLines(cmd);Files.delete(cmd);for(String line:lines){
   if(line.equals("resume")){mc.setScreen(null);}
   else if(line.startsWith("camera:")){mc.options.setCameraType(switch(line.substring(7)){case "front"->CameraType.THIRD_PERSON_FRONT;case "back"->CameraType.THIRD_PERSON_BACK;default->CameraType.FIRST_PERSON;});}
   else if(line.startsWith("capture:")){Screenshot.grab(mc.gameDirectory,line.substring(8),mc.getMainRenderTarget(),c->System.out.println("QA_SCREENSHOT "+c.getString()));}
   else if(line.equals("hide")){mc.options.hideGui=true;}
   else if(line.equals("live-apollyon-test")){var server=mc.getSingleplayerServer();var id=mc.player.getUUID();server.execute(()->startLive(server.getPlayerList().getPlayer(id)));}
   else if(line.equals("apollyon-test")){var server=mc.getSingleplayerServer();var id=mc.player.getUUID();server.execute(()->apollyonTest(server.getPlayerList().getPlayer(id)));}
   else if(line.equals("divine-test")){var server=mc.getSingleplayerServer();var id=mc.player.getUUID();server.execute(()->divineTests(server.getPlayerList().getPlayer(id)));}
   else if(line.equals("use")){mc.gameMode.useItem(mc.player,net.minecraft.world.InteractionHand.MAIN_HAND);}
   else if(line.equals("inspect")){inspect(mc);}
   else if(line.equals("reload")){mc.reloadResourcePacks();}
   else if(line.equals("show")){mc.options.hideGui=false;}
   else if(line.equals("quit")){mc.stop();}
   else if(line.startsWith("fov:")){mc.options.fov().set(Integer.parseInt(line.substring(4)));}
   else if(line.startsWith("pause:")){mc.pauseGame(false);}
   else {var server=mc.getSingleplayerServer();server.execute(()->server.getCommands().performPrefixedCommand(server.createCommandSourceStack().withSuppressedOutput(),line));}
  }}catch(Exception ex){ex.printStackTrace();}
 }
}
