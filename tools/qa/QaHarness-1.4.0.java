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


 static void willTests(net.minecraft.server.level.ServerPlayer p){
  java.util.List<String> out=new java.util.ArrayList<>();
  try{
   p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD,net.minecraft.world.item.ItemStack.EMPTY);DivineWill.update(p,false);p.removeAllEffects();p.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);p.getAbilities().mayfly=false;p.getAbilities().flying=false;p.setSprinting(false);
   var spawn=net.minecraft.server.level.ServerPlayer.class.getDeclaredField("spawnInvulnerableTime");spawn.setAccessible(true);spawn.setInt(p,0);
   double walk=p.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED),br=p.getAttributeValue(net.minecraftforge.common.ForgeMod.BLOCK_REACH.get()),er=p.getAttributeValue(net.minecraftforge.common.ForgeMod.ENTITY_REACH.get());
   p.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.POISON,200));p.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.NIGHT_VISION,120));
   p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD,new net.minecraft.world.item.ItemStack(StarcrownDoll.ITEM.get()));DivineWill.update(p,true);
   require(p.getAbilities().mayfly,"survival creative-style flight",out);require(p.hasEffect(net.minecraft.world.effect.MobEffects.NIGHT_VISION),"night vision enabled",out);require(!p.hasEffect(net.minecraft.world.effect.MobEffects.POISON),"existing poison removed",out);
   near(p.getAttributeValue(net.minecraftforge.common.ForgeMod.BLOCK_REACH.get()),br+3,"block reach +3",out);near(p.getAttributeValue(net.minecraftforge.common.ForgeMod.ENTITY_REACH.get()),er+3,"entity reach +3",out);near(p.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED),walk,"walking not sped up",out);
   p.setSprinting(true);double sprintBase=p.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED);DivineWill.update(p,true);near(p.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED),sprintBase*1.5,"sprint speed +50%",out);p.setSprinting(false);DivineWill.update(p,true);near(p.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED),walk,"sprint modifier removed when walking",out);
   var state=net.minecraft.world.level.block.Blocks.STONE.defaultBlockState();var mining=new net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed(p,state,2,net.minecraft.core.BlockPos.ZERO);net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(mining);near(mining.getNewSpeed(),6,"3x mining event",out);
   p.setOnGround(false);p.getAbilities().flying=true;mining=new net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed(p,state,2,net.minecraft.core.BlockPos.ZERO);net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(mining);near(mining.getNewSpeed(),30,"flying mining air penalty compensated",out);
   int rejected=0;for(var effect:net.minecraftforge.registries.ForgeRegistries.MOB_EFFECTS){if(effect.getCategory()!=net.minecraft.world.effect.MobEffectCategory.BENEFICIAL){boolean added=p.addEffect(new net.minecraft.world.effect.MobEffectInstance(effect,100));if(added||p.hasEffect(effect))throw new AssertionError("debuff accepted "+net.minecraftforge.registries.ForgeRegistries.MOB_EFFECTS.getKey(effect));rejected++;}}
   require(rejected>10,"all registered non-beneficial effects rejected; count="+rejected,out);
   p.forceAddEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.WITHER,100),null);require(!p.hasEffect(net.minecraft.world.effect.MobEffects.WITHER),"forceAddEffect wither rejected",out);
   p.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.REGENERATION,100));DivineWill.update(p,true);require(p.hasEffect(net.minecraft.world.effect.MobEffects.REGENERATION),"beneficial regeneration preserved",out);
   p.setRemainingFireTicks(100);p.setTicksFrozen(100);DivineWill.update(p,true);require(!p.isOnFire()&&p.getTicksFrozen()==0,"burn and freeze cleared",out);
   p.setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO);p.knockback(2,1,0);require(p.getDeltaMovement().lengthSqr()<.00001,"knockback blocked",out);
   for(int i=0;i<40;i++)DivineWill.update(p,true);near(p.getAttributeValue(net.minecraftforge.common.ForgeMod.BLOCK_REACH.get()),br+3,"repeated updates do not stack reach",out);
   p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD,net.minecraft.world.item.ItemStack.EMPTY);DivineWill.update(p,false);
   require(!p.getAbilities().mayfly&&!p.getAbilities().flying,"unequip revokes borrowed flight",out);near(p.getAttributeValue(net.minecraftforge.common.ForgeMod.BLOCK_REACH.get()),br,"unequip restores block reach",out);near(p.getAttributeValue(net.minecraftforge.common.ForgeMod.ENTITY_REACH.get()),er,"unequip restores entity reach",out);near(p.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED),walk,"unequip restores movement",out);near(p.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.KNOCKBACK_RESISTANCE),0,"unequip removes knockback modifier",out);
   var nv=p.getEffect(net.minecraft.world.effect.MobEffects.NIGHT_VISION);require(nv!=null&&nv.getDuration()==120&&nv.isVisible(),"prior night vision restored",out);
   p.getAbilities().mayfly=true;p.getAbilities().flying=true;p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD,new net.minecraft.world.item.ItemStack(StarcrownDoll.ITEM.get()));DivineWill.update(p,true);p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD,net.minecraft.world.item.ItemStack.EMPTY);DivineWill.update(p,false);require(p.getAbilities().mayfly&&p.getAbilities().flying,"prior external flight retained",out);
   p.setGameMode(net.minecraft.world.level.GameType.CREATIVE);p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD,new net.minecraft.world.item.ItemStack(StarcrownDoll.ITEM.get()));DivineWill.update(p,true);p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD,net.minecraft.world.item.ItemStack.EMPTY);DivineWill.update(p,false);require(p.getAbilities().mayfly,"creative flight retained",out);
   p.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD,new net.minecraft.world.item.ItemStack(StarcrownDoll.ITEM.get()));DivineWill.update(p,true);p.setHealth(0);p.discard();checkPlayer(p,"skill-one health/removal regression",out);
   p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD,net.minecraft.world.item.ItemStack.EMPTY);DivineWill.update(p,false);p.setHealth(20);p.invulnerableTime=0;p.hurt(p.damageSources().generic(),4);require(p.getHealth()<20,"normal damage returns after unequip",out);p.setHealth(20);p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD,new net.minecraft.world.item.ItemStack(StarcrownDoll.ITEM.get()));DivineWill.update(p,true);out.add("ALL_PASS");
  }catch(Throwable t){out.add("FAIL "+t);t.printStackTrace();}
  try{java.nio.file.Files.write(java.nio.file.Path.of("will-results.txt"),out);}catch(Exception ex){ex.printStackTrace();}
 }
 static void require(boolean condition,String label,java.util.List<String> out){if(!condition)throw new AssertionError(label);out.add("PASS "+label);}
 static void near(double actual,double expected,String label,java.util.List<String> out){require(Math.abs(actual-expected)<.0001,label+" actual="+actual,out);}

 static void eternityTests(net.minecraft.server.level.ServerPlayer p){
  var out=new java.util.ArrayList<String>();var mobs=new java.util.ArrayList<net.minecraft.world.entity.Mob>();
  try{
   var level=p.serverLevel();p.getAbilities().mayfly=true;p.getAbilities().flying=true;p.onUpdateAbilities();p.setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO);p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD,new net.minecraft.world.item.ItemStack(StarcrownDoll.ITEM.get()));
   var pig=net.minecraft.world.entity.EntityType.PIG.create(level);pig.moveTo(p.getX()+2,p.getY(),p.getZ(),0,0);level.addFreshEntity(pig);mobs.add(pig);
   require(DivineEternity.sealed(pig)&&pig.isNoAi(),"inside cube reports NoAI through mixin",out);
   var count=new int[1];pig.goalSelector.addGoal(0,new net.minecraft.world.entity.ai.goal.Goal(){public boolean canUse(){return true;}public void tick(){count[0]++;}});
   pig.goalSelector.tick();require(count[0]>0,"goal control runs before stop",out);DivineEternity.stop(pig);require(pig.goalSelector.getAvailableGoals().stream().noneMatch(g->g.isRunning()),"active goals stopped without removing definitions",out);
   pig.setTarget(p);require(pig.getTarget()==null,"inside target acquisition blocked",out);
   var tag=new net.minecraft.nbt.CompoundTag();pig.addAdditionalSaveData(tag);require(!tag.getBoolean("NoAI"),"persistent NoAI flag unchanged",out);
   pig.setPos(p.getX()+3.501,p.getY(),p.getZ());require(!pig.isNoAi()&&!DivineEternity.sealed(pig),"outside 3.5 boundary restores AI",out);pig.setTarget(p);require(pig.getTarget()==p,"outside target acquisition restored",out);
   pig.setNoAi(true);tag=new net.minecraft.nbt.CompoundTag();pig.addAdditionalSaveData(tag);require(tag.getBoolean("NoAI"),"original persistent NoAI survives saving inside aura",out);pig.setPos(p.getX()+1,p.getY(),p.getZ());p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD,net.minecraft.world.item.ItemStack.EMPTY);require(pig.isNoAi(),"original NoAI retained on unequip",out);pig.setNoAi(false);require(!pig.isNoAi(),"unequip removes only aura suppression",out);
   p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD,new net.minecraft.world.item.ItemStack(StarcrownDoll.ITEM.get()));pig.setPos(p.getX(),p.getY()+3.501,p.getZ());require(!DivineEternity.sealed(pig),"vertical boundary respected",out);
   var type=net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES.getValue(new net.minecraft.resources.ResourceLocation("goety:apostle"));require(type!=null,"real Goety Apostle registered",out);
   var boss=(net.minecraft.world.entity.Mob)type.create(level);boss.moveTo(p.getX()+2,p.getY(),p.getZ(),0,0);mobs.add(boss);
   var api=Class.forName("z1gned.goetyrevelation.util.ApollyonAbilityHelper");api.getMethod("allTitlesApostle_1_20_1$setApollyon",boolean.class).invoke(boss,true);level.addFreshEntity(boss);
   require((Boolean)api.getMethod("allTitlesApostle_1_20_1$isApollyon").invoke(boss),"actual Apollyon variant enabled",out);
   require((Boolean)boss.getClass().getMethod("isInNether").invoke(boss),"Apollyon test in actual Nether",out);
   boss.getClass().getMethod("setCoolDown",int.class).invoke(boss,0);int before=(Integer)boss.getClass().getMethod("getCoolDown").invoke(boss);
   int age=boss.tickCount;for(int i=0;i<40;i++){boss.tick();boss.aiStep();boss.setTarget(p);}
   require(boss.isNoAi()&&boss.getTarget()==null,"Apollyon AI and targets sealed",out);require((Integer)boss.getClass().getMethod("getCoolDown").invoke(boss)==before,"40 actual tick/aiStep calls: custom action cooldown frozen",out);
   p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD,net.minecraft.world.item.ItemStack.EMPTY);boss.setTarget(p);boss.tick();boss.aiStep();require(!boss.isNoAi()&&boss.getTarget()==p,"Apollyon action tick resumes after unequip",out);
   require((Integer)boss.getClass().getMethod("getCoolDown").invoke(boss)!=before,"Apollyon custom cooldown resumes",out);
   p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD,new net.minecraft.world.item.ItemStack(StarcrownDoll.ITEM.get()));out.add("ALL_PASS");
   boss.setTarget(null);boss.getClass().getMethod("setCoolDown",int.class).invoke(boss,0);mobs.remove(boss);liveBoss=boss;livePlayer=p;liveOut=new java.util.ArrayList<>();liveTicks=0;liveBefore=0;
  }catch(Throwable t){out.add("FAIL "+t);t.printStackTrace();}finally{for(var mob:mobs)mob.discard();}
  try{java.nio.file.Files.write(java.nio.file.Path.of("eternity-results.txt"),out);}catch(Exception e){e.printStackTrace();}
 }
 static net.minecraft.world.entity.Mob liveBoss;static net.minecraft.server.level.ServerPlayer livePlayer;static int liveTicks,liveBefore;static java.util.List<String> liveOut;
 @SubscribeEvent public static void live(net.minecraftforge.event.TickEvent.ServerTickEvent e){
  if(e.phase!=net.minecraftforge.event.TickEvent.Phase.END||liveBoss==null)return;
  try{
   liveTicks++;
   if(liveTicks==60){require(liveBoss.isNoAi()&&liveBoss.getTarget()==null,"60 real server ticks: no AI or target",liveOut);require((Integer)liveBoss.getClass().getMethod("getCoolDown").invoke(liveBoss)==liveBefore,"60 real ticks: Apollyon custom cooldown frozen",liveOut);liveBoss.setPos(livePlayer.getX()+6,livePlayer.getY(),livePlayer.getZ());}
   if(liveTicks==90){require(!liveBoss.isNoAi(),"leaving cube restores Apollyon AI",liveOut);require((Integer)liveBoss.getClass().getMethod("getCoolDown").invoke(liveBoss)!=liveBefore,"30 real ticks outside: custom cooldown resumes",liveOut);liveBoss.setPos(livePlayer.getX()+2,livePlayer.getY(),livePlayer.getZ());liveBefore=(Integer)liveBoss.getClass().getMethod("getCoolDown").invoke(liveBoss);}
   if(liveTicks==120){require(liveBoss.isNoAi()&&(Integer)liveBoss.getClass().getMethod("getCoolDown").invoke(liveBoss)==liveBefore,"reentry suspends actions again",liveOut);liveOut.add("ALL_PASS");liveBoss.discard();liveBoss=null;java.nio.file.Files.write(java.nio.file.Path.of("eternity-live-results.txt"),liveOut);}
  }catch(Throwable t){liveOut.add("FAIL "+t);t.printStackTrace();liveBoss.discard();liveBoss=null;try{java.nio.file.Files.write(java.nio.file.Path.of("eternity-live-results.txt"),liveOut);}catch(Exception ignored){}}
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
   else if(line.equals("apollyon-test")){var server=mc.getSingleplayerServer();var id=mc.player.getUUID();server.execute(()->apollyonTest(server.getPlayerList().getPlayer(id)));}
   else if(line.equals("eternity-test")){var server=mc.getSingleplayerServer();var id=mc.player.getUUID();server.execute(()->eternityTests(server.getPlayerList().getPlayer(id)));}
   else if(line.equals("will-test")){var server=mc.getSingleplayerServer();var id=mc.player.getUUID();server.execute(()->willTests(server.getPlayerList().getPlayer(id)));}
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
