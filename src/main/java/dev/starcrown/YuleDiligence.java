package dev.starcrown;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.fml.common.Mod;
@Mod.EventBusSubscriber(modid=StarcrownDoll.ID)
public final class YuleDiligence {
 public static final String COUNT="starcrowndoll_diligence_units";
 public static final UUID HEALTH=UUID.fromString("86a0918b-b3c1-4bf4-87a2-fce02f850e51"),LUCK=UUID.fromString("76ae3eb9-640d-463c-b276-b7531963bab9");
 public static final int XP=1000;
 private static final Map<Player,State> STATES=Collections.synchronizedMap(new WeakHashMap<>());
 private static final ThreadLocal<MobEffectInstance> PENALTY=new ThreadLocal<>();
 public static final class State {public final List<BlockEvent.EntityPlaceEvent> pending=new ArrayList<>();public Vec3 last;public Object dimension;public double distance;public int idle;public long workUntil=Long.MIN_VALUE;public MobEffectInstance penalty,haste,speed;public final Map<MobEffect,MobEffectInstance> before=new HashMap<>();public final Map<MobEffect,Long> beforeAt=new HashMap<>();}
 public static State state(Player p){return STATES.computeIfAbsent(p,k->new State());}
 public static boolean active(Entity e){return YuleHumility.active(e);}
 public static boolean isPenalty(MobEffectInstance effect){if(PENALTY.get()==effect)return true;synchronized(STATES){for(var entry:STATES.entrySet())if(active(entry.getKey())&&entry.getValue().idle>60&&entry.getValue().penalty==effect)return true;}return false;}
 public static long units(Player p){return Math.max(0,p.getPersistentData().getLong(COUNT));}
 public static void refresh(Player p){long n=units(p);if(n==0)return;modifier(p,Attributes.MAX_HEALTH,HEALTH,n*.1);modifier(p,Attributes.LUCK,LUCK,n*.01);}
 private static void modifier(Player p,Attribute a,UUID id,double amount){var value=p.getAttribute(a);if(value==null)return;var old=value.getModifier(id);if(old!=null&&old.getAmount()==amount)return;value.removeModifier(id);value.addPermanentModifier(new AttributeModifier(id,"Diligence permanent growth",amount,AttributeModifier.Operation.ADDITION));}
 /** Calculate only marked growth attributes beyond vanilla's range, preserving every other modifier. */
 public static double attributeValue(AttributeInstance a,double fallback){if(a.getModifier(HEALTH)==null&&a.getModifier(LUCK)==null)return fallback;double base=a.getBaseValue();for(var v:a.getModifiers(AttributeModifier.Operation.ADDITION))base+=v.getAmount();double result=base;for(var v:a.getModifiers(AttributeModifier.Operation.MULTIPLY_BASE))result+=base*v.getAmount();for(var v:a.getModifiers(AttributeModifier.Operation.MULTIPLY_TOTAL))result*=1+v.getAmount();return Double.isFinite(result)?Math.max(fallback,result):Double.MAX_VALUE;}
 public static void reward(ServerPlayer p,int count){if(!active(p)||count<=0)return;long old=units(p),next=old>Long.MAX_VALUE-count?Long.MAX_VALUE:old+count;p.getPersistentData().putLong(COUNT,next);refresh(p);long points=(long)XP*count;int room=Integer.MAX_VALUE-Math.max(0,p.totalExperience);if(room>0&&p.experienceLevel<Integer.MAX_VALUE)p.giveExperiencePoints((int)Math.min(points,room));var s=state(p);s.idle=0;s.workUntil=p.level().getGameTime()+2;}
 private static MobEffectInstance boost(Player p,MobEffect type,int amplifier,MobEffectInstance owned){var s=state(p);var current=p.getEffect(type);if(current!=null&&(current.getAmplifier()>amplifier||current.getAmplifier()==amplifier&&current.getDuration()>12)){s.before.remove(type);s.beforeAt.remove(type);return null;}if(current!=null&&current!=owned&&!s.before.containsKey(type)){s.before.put(type,new MobEffectInstance(current));s.beforeAt.put(type,p.level().getGameTime());}var effect=new MobEffectInstance(type,12,amplifier,true,false,true);p.addEffect(effect);return p.getEffect(type);}
 private static void release(Player p,MobEffect type,MobEffectInstance owned){var s=state(p);var before=s.before.remove(type);var time=s.beforeAt.remove(type);if(owned!=null&&p.getEffect(type)==owned){p.removeEffect(type);if(before!=null){var tag=before.save(new net.minecraft.nbt.CompoundTag());var back=MobEffectInstance.load(DivineWill.elapsedEffect(tag,Math.max(0,p.level().getGameTime()-(time==null?p.level().getGameTime():time))));if(back!=null&&(back.isInfiniteDuration()||back.getDuration()>0))p.addEffect(back);}}}
 public static void tickPlayer(ServerPlayer p){flush(p);refresh(p);var s=state(p);if(!active(p)||!p.isAlive()){release(p,MobEffects.WITHER,s.penalty);release(p,MobEffects.DIG_SPEED,s.haste);release(p,MobEffects.MOVEMENT_SPEED,s.speed);s.penalty=s.haste=s.speed=null;s.last=null;s.distance=0;s.idle=0;return;}if(p.isSleeping())p.stopSleepInBed(true,true);
  Vec3 now=p.position();boolean valid=s.last!=null&&s.dimension==p.level().dimension();Vec3 delta=valid?now.subtract(s.last):Vec3.ZERO;s.last=now;s.dimension=p.level().dimension();double length=delta.lengthSqr();boolean moving=valid&&!p.isPassenger()&&length>1.0e-6&&length<=64;boolean working=moving||p.swinging||p.level().getGameTime()<=s.workUntil;
  if(moving){s.distance+=Math.sqrt(delta.x*delta.x+delta.z*delta.z);int steps=(int)s.distance;if(steps>0){s.distance-=steps;reward(p,steps);}}else if(!valid||length>64||p.isPassenger())s.distance=0;
  if(working){s.idle=0;release(p,MobEffects.WITHER,s.penalty);s.penalty=null;s.haste=boost(p,MobEffects.DIG_SPEED,9,s.haste);s.speed=boost(p,MobEffects.MOVEMENT_SPEED,2,s.speed);}else{s.idle=Math.min(61,s.idle+1);release(p,MobEffects.DIG_SPEED,s.haste);release(p,MobEffects.MOVEMENT_SPEED,s.speed);s.haste=s.speed=null;if(s.idle>60){var effect=new MobEffectInstance(MobEffects.WITHER,40,2,false,true,true);PENALTY.set(effect);try{p.addEffect(effect);}finally{PENALTY.remove();}s.penalty=p.getEffect(MobEffects.WITHER);}}
 }
 @SubscribeEvent public static void tick(TickEvent.PlayerTickEvent e){if(e.phase==TickEvent.Phase.END&&e.player instanceof ServerPlayer p)tickPlayer(p);}
 @SubscribeEvent public static void clone(PlayerEvent.Clone e){e.getEntity().getPersistentData().putLong(COUNT,units(e.getOriginal()));refresh(e.getEntity());}
 @SubscribeEvent public static void login(PlayerEvent.PlayerLoggedInEvent e){refresh(e.getEntity());}
 @SubscribeEvent(priority=EventPriority.HIGHEST) public static void bed(PlayerInteractEvent.RightClickBlock e){if(active(e.getEntity())&&e.getLevel().getBlockState(e.getPos()).getBlock() instanceof BedBlock){e.setCanceled(true);e.setCancellationResult(InteractionResult.FAIL);}}
 @SubscribeEvent(priority=EventPriority.LOWEST) public static void mine(PlayerInteractEvent.LeftClickBlock e){if(!(e.getEntity() instanceof ServerPlayer p)||!active(p)||e.getAction()!=PlayerInteractEvent.LeftClickBlock.Action.START||YuleTemperance.denied(p,e.getPos())||p.level().getBlockState(e.getPos()).isAir()||e.getUseBlock()==Event.Result.DENY||e.getUseItem()==Event.Result.DENY)return;p.gameMode.destroyBlock(e.getPos());e.setCanceled(true);p.connection.send(new net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket(p.level(),e.getPos()));}
 @SubscribeEvent(priority=EventPriority.LOWEST) public static void placed(BlockEvent.EntityPlaceEvent e){if(e.getEntity() instanceof ServerPlayer p&&active(p))state(p).pending.add(e);}
 public static void flush(ServerPlayer p){var s=state(p);var pending=new ArrayList<>(s.pending);s.pending.clear();for(var event:pending)if(!event.isCanceled()){int count=event instanceof BlockEvent.EntityMultiPlaceEvent multi?multi.getReplacedBlockSnapshots().size():1;reward(p,count);}}
 @SubscribeEvent public static void logout(PlayerEvent.PlayerLoggedOutEvent e){STATES.remove(e.getEntity());}
 public static boolean bedSpawn(ServerPlayer p,net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dimension,BlockPos pos,boolean forced){if(!active(p)||forced||pos==null)return false;var level=p.getServer().getLevel(dimension);return level!=null&&level.getBlockState(pos).getBlock() instanceof BedBlock;}
 public static List<ItemStack> improveGifts(List<ItemStack> input,ServerPlayer p,boolean villager){double luck=Math.max(0,p.getLuck());if(luck==0)return input;var result=new ArrayList<ItemStack>();double bonus=luck/(luck+100);for(var original:input){var stack=original.copy();if(villager){var enchants=EnchantmentHelper.getEnchantments(stack);if(!enchants.isEmpty()){var improved=new LinkedHashMap<Enchantment,Integer>();for(var entry:enchants.entrySet())improved.put(entry.getKey(),Math.max(entry.getValue(),(int)Math.min(entry.getKey().getMaxLevel(),entry.getValue()+Math.floor(luck/10))));EnchantmentHelper.setEnchantments(improved,stack);}else if(stack.isEnchantable()||stack.is(Items.BOOK))stack=EnchantmentHelper.enchantItem(p.getRandom(),stack,(int)Math.min(30,Math.max(1,luck)),true);}result.add(stack);if(p.getRandom().nextDouble()<bonus)result.add(stack.copy());}return result;}
 private YuleDiligence(){}
}
