package dev.starcrownqa;
import dev.starcrown.*;
import java.nio.file.*;
import net.minecraft.core.*;
import net.minecraft.server.level.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.item.*;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
@Mod.EventBusSubscriber(modid="starcrownqa")
public final class ToleranceQa {
 static int tick;static volatile boolean done,failed;static Skeleton existing;static ServerPlayer wearer;static BlockPos lava=new BlockPos(6,81,-2);
 static void log(String s){try{Files.writeString(Path.of("tolerance-results.txt"),s+"\n",StandardOpenOption.CREATE,StandardOpenOption.APPEND);}catch(Exception e){throw new RuntimeException(e);}}
 static void check(boolean b,String s){if(!b)throw new AssertionError(s);log("PASS "+s);}
 static Cow cow(ServerLevel l,double x,double z){var c=EntityType.COW.create(l);c.moveTo(x,81,z);c.setNoAi(true);l.addFreshEntity(c);return c;}
 static void wear(boolean active){wearer.setItemSlot(EquipmentSlot.HEAD,active?new ItemStack(StarcrownDoll.YULE_ITEM.get()):ItemStack.EMPTY);}
 @SubscribeEvent public static void level(TickEvent.LevelTickEvent event){
  if(event.phase!=TickEvent.Phase.END||!(event.level instanceof ServerLevel l)||l!=l.getServer().overworld()||!Files.exists(Path.of("enable-tolerance-qa"))||l.getServer().getPlayerList().getPlayers().isEmpty()||done||failed)return;
  try{tick++;
   if(tick==20){Files.writeString(Path.of("tolerance-results.txt"),"");wearer=l.getServer().getPlayerList().getPlayers().get(0);DivineAnchor.unlock(wearer);wear(false);wearer.removeAllEffects();wearer.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);wearer.teleportTo(l,.5,81,4.5,180,10);
    for(int x=-6;x<=120;x++)for(int z=-5;z<=8;z++){l.setBlockAndUpdate(new BlockPos(x,80,z),Blocks.STONE.defaultBlockState());for(int y=81;y<86;y++)l.setBlockAndUpdate(new BlockPos(x,y,z),Blocks.AIR.defaultBlockState());}l.setDayTime(6000);
    existing=EntityType.SKELETON.create(l);existing.moveTo(3,81,3);existing.setItemSlot(EquipmentSlot.MAINHAND,new ItemStack(Items.BOW));l.addFreshEntity(existing);wear(true);
    var zombie=EntityType.ZOMBIE.create(l);zombie.moveTo(4,81,4);zombie.setCustomName(net.minecraft.network.chat.Component.literal("tolerance-zombie"));check(!l.addFreshEntity(zombie),"new zombie replaced in join event");check(l.getEntitiesOfClass(Villager.class,zombie.getBoundingBox().inflate(1)).stream().anyMatch(v->v.getCustomName()!=null&&v.getCustomName().getString().equals("tolerance-zombie")),"zombie instantly restored as named villager");
    var skeleton=EntityType.SKELETON.create(l);skeleton.moveTo(5,81,3);skeleton.setItemSlot(EquipmentSlot.MAINHAND,new ItemStack(Items.BOW));l.addFreshEntity(skeleton);check(YuleTolerance.friendly(skeleton)&&skeleton.getMainHandItem().isEmpty()&&!skeleton.isNoAi(),"new skeleton disarmed and friendly without freezing AI");
    var outside=EntityType.ZOMBIE.create(l);outside.moveTo(49,81,4.5);l.addFreshEntity(outside);check(!YuleTolerance.friendly(outside)&&!outside.isRemoved(),"outside 48-block radius unchanged");outside.discard();
    var c=cow(l,2,2);float hp=c.getHealth();wearer.attack(c);check(c.getHealth()==hp,"Player.attack entry revoked");check(!c.hurt(l.damageSources().playerAttack(wearer),100)&&c.getHealth()==hp,"forced direct hurt attributed to wearer blocked");
    var stand=EntityType.ARMOR_STAND.create(l);stand.moveTo(2,81,1);l.addFreshEntity(stand);check(!stand.hurt(l.damageSources().playerAttack(wearer),100)&&!stand.isRemoved(),"nonliving-style armor stand harm blocked");
    var crystal=EntityType.END_CRYSTAL.create(l);crystal.moveTo(2,81,-1);l.addFreshEntity(crystal);check(!crystal.hurt(l.damageSources().playerAttack(wearer),100)&&!crystal.isRemoved(),"end crystal attack/explosion prevented");crystal.discard();
    var pursuer=EntityType.ZOMBIE.create(l);pursuer.moveTo(100,81,3);l.addFreshEntity(pursuer);pursuer.setTarget(skeleton);check(pursuer.getTarget()==null&&!skeleton.canBeSeenAsEnemy(),"pacified mobs no longer treated as enemies by other AI");pursuer.discard();
    var wolf=EntityType.WOLF.create(l);wolf.moveTo(2,81,-3);wolf.setOwnerUUID(wearer.getUUID());l.addFreshEntity(wolf);check(!c.hurt(l.damageSources().mobAttack(wolf),30)&&c.getHealth()==hp,"pet damage attributed to wearer blocked");wolf.discard();
    skeleton.setTarget(c);check(skeleton.getTarget()==null&&!skeleton.doHurtTarget(c),"pacified mob cannot target or melee anyone");check(!c.hurt(l.damageSources().mobAttack(skeleton),20)&&c.getHealth()==hp,"pacified mob damage sources harmless");
    wearer.setHealth(10);float before=wearer.getHealth();check(!wearer.hurt(l.damageSources().mobAttack(skeleton),100)&&wearer.getHealth()==before,"incoming attack canceled before health loss");for(var who:new LivingEntity[]{wearer,skeleton})check(who.getEffect(MobEffects.REGENERATION).getAmplifier()==9&&who.getEffect(MobEffects.ABSORPTION).getAmplifier()==9&&who.getEffect(MobEffects.REGENERATION).getDuration()==200,"both sides receive Regeneration X and Absorption X for 10 seconds");
    var arrow=new Arrow(l,wearer);arrow.moveTo(1,81,1);l.addFreshEntity(arrow);check(YuleTolerance.harmless(arrow),"wearer projectile permanently marked harmless");wear(false);check(!c.hurt(l.damageSources().arrow(arrow,wearer),100)&&c.getHealth()==hp,"projectile remains harmless after unequip");check(!c.addEffect(new MobEffectInstance(MobEffects.POISON,200),arrow),"attributed poison/debuff cannot become delayed harm");arrow.discard();wear(true);
    l.setBlockAndUpdate(new BlockPos(9,81,0),Blocks.REDSTONE_BLOCK.defaultBlockState());wearer.setItemSlot(EquipmentSlot.MAINHAND,new ItemStack(Items.TNT));var hit=new BlockHitResult(new Vec3(8.5,81,.5),Direction.UP,new BlockPos(8,80,0),false);var ctx=new BlockPlaceContext(new UseOnContext(wearer,net.minecraft.world.InteractionHand.MAIN_HAND,hit));check(((BlockItem)Items.TNT).place(ctx).consumesAction(),"actual TNT block-item placement succeeds");
    var explosives=l.getEntitiesOfClass(PrimedTnt.class,new AABB(new BlockPos(8,81,0)).inflate(2));check(!explosives.isEmpty()&&YuleTolerance.harmless(explosives.get(0)),"redstone-instant TNT ignition inherits owner");var tnt=explosives.get(0);var bombCow=cow(l,8.5,2);float bombHp=bombCow.getHealth();wear(false);l.explode(tnt,tnt.getX(),tnt.getY(),tnt.getZ(),4,net.minecraft.world.level.Level.ExplosionInteraction.TNT);check(bombCow.getHealth()==bombHp&&l.getBlockState(new BlockPos(8,80,0)).is(Blocks.STONE),"owned TNT explosion harms neither entities nor blocks after unequip");tnt.discard();wear(true);
    wearer.setItemSlot(EquipmentSlot.MAINHAND,new ItemStack(Items.LAVA_BUCKET));check(((BucketItem)Items.LAVA_BUCKET).emptyContents(wearer,l,lava,null,new ItemStack(Items.LAVA_BUCKET)),"actual lava bucket empty succeeds");check(wearer.getUUID().equals(YuleHazards.get(l).owner(lava)),"placed lava provenance recorded");
    var lavaCow=cow(l,6.5,-1.5);float lavaHp=lavaCow.getHealth();wear(false);lavaCow.lavaHurt();check(lavaCow.getHealth()==lavaHp&&lavaCow.getRemainingFireTicks()<=0,"placed lava cannot injure or ignite after unequip");wear(true);lavaCow.moveTo(1,81,-3);
    var ordinary=cow(l,111,0);var ordinaryTnt=new PrimedTnt(l,110.5,81,.5,null);l.addFreshEntity(ordinaryTnt);float normalHp=ordinary.getHealth();l.explode(ordinaryTnt,110.5,81,.5,4,net.minecraft.world.level.Level.ExplosionInteraction.TNT);check(ordinary.getHealth()<normalHp,"unrelated TNT retains ordinary damage");ordinaryTnt.discard();
    wearer.setItemSlot(EquipmentSlot.MAINHAND,ItemStack.EMPTY);
   }
   if(tick==80){check(YuleTolerance.friendly(existing)&&existing.getMainHandItem().isEmpty(),"existing hostile mob pacified by recurring field scan");boolean flowed=false;for(var dir:Direction.Plane.HORIZONTAL){var pos=lava.relative(dir);if(l.getFluidState(pos).is(net.minecraft.tags.FluidTags.LAVA)&&YuleHazards.get(l).owner(pos)!=null){flowed=true;var c=cow(l,pos.getX()+.5,pos.getZ()+.5);float hp=c.getHealth();c.lavaHurt();check(c.getHealth()==hp,"flowing lava descendant harmless");break;}}check(flowed,"actual lava flow inherits provenance");var restored=YuleHazards.load(((net.minecraft.world.level.saveddata.SavedData)YuleHazards.get(l)).save(new net.minecraft.nbt.CompoundTag()));check(wearer.getUUID().equals(restored.owner(lava)),"hazard owner survives SavedData serialization");l.setBlockAndUpdate(lava,Blocks.AIR.defaultBlockState());check(YuleHazards.get(l).owner(lava)==null,"removed hazard cell forgets owner");done=true;log("SERVER_PASS");}
  }catch(Throwable t){log("FAIL "+t);t.printStackTrace();failed=true;}
 }
 @Mod.EventBusSubscriber(modid="starcrownqa",value=net.minecraftforge.api.distmarker.Dist.CLIENT)
 public static class Client {
  static int ticks;static boolean finished;
  @SubscribeEvent public static void tick(TickEvent.ClientTickEvent e)throws Exception{
   if(e.phase!=TickEvent.Phase.END||!Files.exists(Path.of("enable-tolerance-qa")))return;var mc=net.minecraft.client.Minecraft.getInstance();mc.options.pauseOnLostFocus=false;if(mc.player==null)return;ticks++;
   if(ticks==1){mc.setScreen(null);mc.mouseHandler.releaseMouse();}
   if(failed){mc.stop();return;}
   if(done&&!finished){finished=true;var key=new net.minecraftforge.client.event.InputEvent.InteractionKeyMappingTriggered(0,mc.options.keyAttack,net.minecraft.world.InteractionHand.MAIN_HAND);YuleToleranceClient.attack(key);check(key.isCanceled()&&!key.shouldSwingHand(),"client attack key and swing stripped");check(net.minecraft.network.chat.Component.translatable("tooltip.starcrowndoll.yule.tolerance.name").getString().equals("Tolerance")||net.minecraft.network.chat.Component.translatable("tooltip.starcrowndoll.yule.tolerance.name").getString().equals("宽容"),"localized second virtue registered");log("ALL_PASS");mc.stop();}
  }
 }
}
