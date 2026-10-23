package dev.starcrown;
import java.util.*;
import java.util.function.Supplier;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.server.level.*;
import net.minecraft.world.item.*;
import net.minecraft.world.effect.*;
import net.minecraft.nbt.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.fml.common.Mod;
@Mod.EventBusSubscriber(modid=StarcrownDoll.ID)
public final class YuleGenerosity {
 public static final String BLESSED="starcrowndoll_generosity_blessed",DONOR="starcrowndoll_gift_donor",CLOCK="starcrowndoll_scatter_ticks";
 private static final ThreadLocal<UUID> MANUAL=new ThreadLocal<>();
 private static final ThreadLocal<LivingEntity> GRANT=new ThreadLocal<>();
 private static final Map<AttributeInstance,Boolean> BOOSTED=Collections.synchronizedMap(new WeakHashMap<>());
 private static final Map<LivingEntity,Integer> BOUND=Collections.synchronizedMap(new WeakHashMap<>());
 private static final Map<ServerPlayer,Integer> SENT=Collections.synchronizedMap(new WeakHashMap<>());
 public static boolean active(Entity e){return YuleHumility.active(e);}
 public static boolean locked(Player p,int slot){return active(p)&&slot>=9&&slot<36;}
 public static boolean boosted(AttributeInstance instance){return BOOSTED.containsKey(instance);}
 public static void bind(LivingEntity e){
  if(!e.getEntityData().get(GenerosityData.BLESSED))return;
  if(BOUND.containsKey(e)&&e.tickCount%100!=0)return;
  for(var attribute:net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES){var instance=e.getAttribute(attribute);if(instance!=null)BOOSTED.put(instance,true);}
  BOUND.put(e,e.tickCount);
 }
 public static <T> T manual(Player player,Supplier<T> action){var before=MANUAL.get();MANUAL.set(player.getUUID());try{return action.get();}finally{if(before==null)MANUAL.remove();else MANUAL.set(before);}}
 public static void mark(ItemEntity gift,UUID owner){gift.getPersistentData().putUUID(DONOR,owner);gift.setThrower(owner);}
 public static ItemStack upgrade(ItemStack original){
  Item result=GenerosityUpgrades.result(original);
  if(result==null||result==original.getItem())return original.copy();
  var upgraded=new ItemStack(result,original.getCount());if(original.hasTag())upgraded.setTag(original.getTag().copy());return upgraded;
 }
 @SubscribeEvent(priority=EventPriority.LOWEST) public static void tossed(ItemTossEvent event){
  if(!(event.getPlayer() instanceof ServerPlayer p)||!active(p)||event.getEntity().getItem().isEmpty())return;
  var item=event.getEntity();mark(item,p.getUUID());
  if(p.getUUID().equals(MANUAL.get())){var before=item.getItem();var after=upgrade(before);item.setItem(after);item.getPersistentData().putBoolean("starcrowndoll_manual_gift",true);if(after.getItem()==before.getItem())item.setCustomName(net.minecraft.network.chat.Component.translatable("entity.starcrowndoll.generous_gift",after.getHoverName()));}
 }
 public static boolean throwAutomatic(ServerPlayer p,ItemStack stack){
  if(stack.isEmpty())return true;
  var item=new ItemEntity(p.level(),p.getX(),p.getEyeY()-.2,p.getZ(),stack.copy());mark(item,p.getUUID());item.setPickUpDelay(40);
  double angle=p.getRandom().nextDouble()*Math.PI*2;item.setDeltaMovement(Math.cos(angle)*.35,.27,Math.sin(angle)*.35);
  return p.serverLevel().addFreshEntity(item);
 }
 private static boolean eligibleSlot(int index){return index>=0&&index<9||index>=36&&index<=38||index==40;}
 public static void scatter(ServerPlayer p){
  int count=1+p.getRandom().nextInt(3);
  for(int n=0;n<count;n++){
   var candidates=new ArrayList<Integer>();for(int i=0;i<p.getInventory().getContainerSize();i++)if(eligibleSlot(i)&&!p.getInventory().getItem(i).isEmpty())candidates.add(i);
   if(candidates.isEmpty())break;int slot=candidates.get(p.getRandom().nextInt(candidates.size()));var stack=p.getInventory().getItem(slot);var piece=stack.split(1);
   if(!throwAutomatic(p,piece)){stack.grow(1);break;}
  }
  p.getInventory().setChanged();p.inventoryMenu.broadcastChanges();
 }
 public static void hotbar(ServerPlayer p,ItemStack stack){
  for(int i=0;i<9&&!stack.isEmpty();i++){if(i==p.getInventory().selected&&YuleHumility.forbidden(stack))continue;var stored=p.getInventory().items.get(i);if(!stored.isEmpty()&&ItemStack.isSameItemSameTags(stored,stack)){int n=Math.min(stack.getCount(),stored.getMaxStackSize()-stored.getCount());if(n>0){stored.grow(n);stack.shrink(n);}}}
  for(int i=0;i<9&&!stack.isEmpty();i++){if(i==p.getInventory().selected&&YuleHumility.forbidden(stack))continue;if(p.getInventory().items.get(i).isEmpty())p.getInventory().items.set(i,stack.split(Math.min(stack.getCount(),YuleTemperance.active(p)?Math.min(16,stack.getMaxStackSize()):stack.getMaxStackSize())));}
 }
 public static void escrow(ServerPlayer p,ItemStack stack){if(stack.isEmpty())return;var list=p.getPersistentData().getList("starcrowndoll_pending_scatter",Tag.TAG_COMPOUND);list.add(stack.save(new CompoundTag()));p.getPersistentData().put("starcrowndoll_pending_scatter",list);}
 public static void redirect(ServerPlayer p,ItemStack incoming){var copy=incoming.copy();hotbar(p,copy);if(!copy.isEmpty()&&!throwAutomatic(p,copy))escrow(p,copy);}
 public static void enforce(ServerPlayer p){
  boolean changed=false;
  for(int i=9;i<36;i++){var item=p.getInventory().items.get(i);if(item.isEmpty())continue;p.getInventory().items.set(i,ItemStack.EMPTY);redirect(p,item);changed=true;}
  var list=p.getPersistentData().getList("starcrowndoll_pending_scatter",Tag.TAG_COMPOUND);if(!list.isEmpty()){var remaining=new ListTag();for(var entry:list){var item=ItemStack.of((CompoundTag)entry);hotbar(p,item);if(!item.isEmpty()&&!throwAutomatic(p,item))remaining.add(item.save(new CompoundTag()));}if(remaining.isEmpty())p.getPersistentData().remove("starcrowndoll_pending_scatter");else p.getPersistentData().put("starcrowndoll_pending_scatter",remaining);changed=true;}
  if(changed){p.getInventory().setChanged();p.inventoryMenu.broadcastChanges();}
 }
 public static void blessing(LivingEntity e){
  if(e.level().isClientSide)return;
  e.getPersistentData().putBoolean(BLESSED,true);e.getEntityData().set(GenerosityData.BLESSED,true);bind(e);
  var current=e.getEffect(MobEffects.HERO_OF_THE_VILLAGE);
  if(current==null||current.getAmplifier()<9||current.getAmplifier()==9&&!current.isInfiniteDuration()){
   GRANT.set(e);try{e.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE,-1,9,false,true,true));}finally{GRANT.remove();}
  }
  if(e instanceof ServerPlayer p)GenerosityRewards.get(p.getServer()).grant(p.getUUID());
 }
 public static void received(LivingEntity receiver,ItemEntity gift,int count){
  if(receiver.level().isClientSide||count<=0||!gift.getPersistentData().hasUUID(DONOR))return;
  UUID donor=gift.getPersistentData().getUUID(DONOR);if(donor.equals(receiver.getUUID()))return;
  blessing(receiver);var server=receiver.level().getServer();if(server!=null){GenerosityRewards.get(server).grant(donor);var online=server.getPlayerList().getPlayer(donor);if(online!=null)blessing(online);}
 }
 @SubscribeEvent(priority=EventPriority.LOWEST) public static void applicable(MobEffectEvent.Applicable event){if(GRANT.get()==event.getEntity()&&event.getEffectInstance().getEffect()==MobEffects.HERO_OF_THE_VILLAGE)event.setResult(Event.Result.ALLOW);}
 @SubscribeEvent public static void joined(EntityJoinLevelEvent event){if(event.getLevel() instanceof ServerLevel&&event.getEntity() instanceof LivingEntity living&&(living.getPersistentData().getBoolean(BLESSED)||living instanceof ServerPlayer p&&GenerosityRewards.get(p.getServer()).has(p.getUUID())))blessing(living);}
 @SubscribeEvent public static void clone(PlayerEvent.Clone event){if(event.getOriginal().getPersistentData().getBoolean(BLESSED))event.getEntity().getPersistentData().putBoolean(BLESSED,true);}
 @SubscribeEvent public static void dimension(PlayerEvent.PlayerChangedDimensionEvent event){if(event.getEntity() instanceof ServerPlayer p)SENT.remove(p);}
 @SubscribeEvent(priority=EventPriority.LOWEST,receiveCanceled=true) public static void living(LivingEvent.LivingTickEvent event){var e=event.getEntity();if(e.level().isClientSide){bind(e);return;}if(!(e instanceof Player)&&e.getPersistentData().getBoolean(BLESSED))blessing(e);}
 @SubscribeEvent public static void player(TickEvent.PlayerTickEvent event){
  if(event.phase!=TickEvent.Phase.END)return;var p=event.player;if(p.level().isClientSide){bind(p);return;}if(!(p instanceof ServerPlayer server))return;
  if(p.getPersistentData().getBoolean(BLESSED)||GenerosityRewards.get(server.getServer()).has(p.getUUID())){
   blessing(p);
   // Vanilla entity trackers broadcast metadata to observers, not the owning player.
   var last=SENT.get(server);if(server.connection!=null&&(last==null||p.tickCount-last>=100||p.tickCount<last)){
    server.connection.send(new net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket(p.getId(),List.of(net.minecraft.network.syncher.SynchedEntityData.DataValue.create(GenerosityData.BLESSED,true))));SENT.put(server,p.tickCount);
   }
  }
  if(!active(p))return;enforce(server);int ticks=p.getPersistentData().getInt(CLOCK)+1;if(ticks>=1200){scatter(server);ticks=0;}p.getPersistentData().putInt(CLOCK,ticks);
 }
}
