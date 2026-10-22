package dev.starcrown;

import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.fml.common.Mod;

/** The equipped Yule doll is the authority; no permanent player attributes or AI flags are changed. */
@Mod.EventBusSubscriber(modid=StarcrownDoll.ID)
public final class YuleHumility {
 public static final float SCALE=0.5f/1.8f;
 public static final TagKey<Item> ALLOWED=TagKey.create(Registries.ITEM,new ResourceLocation(StarcrownDoll.ID,"humility_allowed_equipment"));
 public static final TagKey<Item> FORBIDDEN=TagKey.create(Registries.ITEM,new ResourceLocation(StarcrownDoll.ID,"humility_forbidden_equipment"));
 private static final String SIZE_STATE="starcrowndoll_humility_size";
 public static boolean active(Entity entity){return entity instanceof Player p&&p.getInventory()!=null&&p.getItemBySlot(EquipmentSlot.HEAD).is(StarcrownDoll.YULE_ITEM.get());}
 public static boolean quietSource(Entity entity){return active(entity)||(entity instanceof Projectile projectile&&active(projectile.getOwner()))||(entity!=null&&active(entity.getControllingPassenger()));}
 public static float capped(float amount){return Float.isFinite(amount)?Math.min(1,Math.max(0,amount)):1;}
 public static float damage(LivingEntity victim,DamageSource source,float amount){return active(victim)||active(source.getEntity())?capped(amount):amount;}
 public static float healthWrite(LivingEntity entity,float next){
  if(!active(entity))return next;
  float current=entity.getHealth();if(!Float.isFinite(current)||current<=0)return Float.isFinite(next)?next:0;
  return Float.isFinite(next)?Math.max(next,Math.max(0,current-1)):Math.max(0,current-1);
 }
 public static boolean blockDeath(Entity entity){return active(entity)&&((LivingEntity)entity).getHealth()>0;}
 public static boolean kill(Entity entity){
  if(!(entity instanceof ServerPlayer p)||!active(p))return false;
  p.setHealth(Math.max(0,p.getHealth()-1));
  if(p.getHealth()<=0)p.die(p.damageSources().genericKill());
  return true;
 }
 public static boolean forbidden(ItemStack stack){
  if(stack.isEmpty()||StarcrownDoll.isDoll(stack))return false;
  if(stack.is(FORBIDDEN))return true;if(stack.is(ALLOWED))return false;
  var item=stack.getItem();
  if(item instanceof ArmorItem armor){var material=armor.getMaterial();return material!=ArmorMaterials.LEATHER&&material!=ArmorMaterials.CHAIN&&material!=ArmorMaterials.IRON&&material!=ArmorMaterials.GOLD&&material!=ArmorMaterials.TURTLE;}
  if(item instanceof TieredItem tool){
   var tier=tool.getTier();var order=TierSortingRegistry.getSortedTiers();int iron=order.indexOf(Tiers.IRON),actual=order.indexOf(tier);
   if(iron>=0&&actual>=0)return actual>iron;
   return tier!=Tiers.WOOD&&tier!=Tiers.STONE&&tier!=Tiers.IRON&&tier!=Tiers.GOLD;
  }
  if(item instanceof TridentItem)return true;
  // Untiered vanilla bows/crossbows have no material grade. Unknown attribute-based weapons require an explicit tag.
  var id=net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(item);
  return id!=null&&!id.getNamespace().equals("minecraft")&&!stack.getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_DAMAGE).isEmpty();
 }
 public static void enforceEquipment(ServerPlayer player){
  if(!active(player))return;
  for(var slot:EquipmentSlot.values()){
   var stack=player.getItemBySlot(slot);if(!forbidden(stack))continue;
   var returned=stack.copy();player.setItemSlot(slot,ItemStack.EMPTY);
   // Inventory.add would put the item straight back into an empty selected hotbar slot.
   for(int offset=0;offset<36&&!returned.isEmpty();offset++){
    int index=(offset+9)%36;if(index==player.getInventory().selected)continue;
    var stored=player.getInventory().getItem(index);
    if(stored.isEmpty()){int count=Math.min(returned.getCount(),returned.getMaxStackSize());player.getInventory().setItem(index,returned.split(count));}
    else if(ItemStack.isSameItemSameTags(stored,returned)){int count=Math.min(returned.getCount(),stored.getMaxStackSize()-stored.getCount());if(count>0){stored.grow(count);returned.shrink(count);}}
   }
   if(!returned.isEmpty())player.drop(returned,false);
   player.inventoryMenu.broadcastChanges();
  }
 }
 private static boolean concealedMemory(Object value){
  if(value instanceof Entity entity)return active(entity);
  if(value instanceof DamageSource source)return active(source.getEntity());
  if(value instanceof UUID id){var server=net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();return server!=null&&active(server.getPlayerList().getPlayer(id));}
  return false;
 }
 public static Optional<?> memory(Optional<?> input){
  if(input.isEmpty()||!(input.get() instanceof ExpirableValue<?> entry))return input;
  var value=entry.getValue();if(concealedMemory(value))return Optional.empty();
  if(value instanceof List<?> list){var filtered=list.stream().filter(e->!concealedMemory(e)).toList();if(filtered.size()!=list.size())return Optional.of(ExpirableValue.of(filtered,entry.getTimeToLive()));}
  return input;
 }
 public static void clearTarget(Mob mob){
  if(active(mob.getTarget()))mob.setTarget(null);
  var brain=mob.getBrain();
  for(var type:List.of(MemoryModuleType.ATTACK_TARGET,MemoryModuleType.HURT_BY_ENTITY,MemoryModuleType.NEAREST_VISIBLE_PLAYER,MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER,MemoryModuleType.AVOID_TARGET,MemoryModuleType.ANGRY_AT)){
   if(brain.hasMemoryValue(type)&&brain.getMemory(type).filter(YuleHumility::concealedMemory).isPresent())brain.eraseMemory(type);
  }
 }
 @SubscribeEvent(priority=EventPriority.LOWEST) public static void hurt(LivingHurtEvent event){event.setAmount(damage(event.getEntity(),event.getSource(),event.getAmount()));}
 @SubscribeEvent(priority=EventPriority.LOWEST) public static void damage(LivingDamageEvent event){event.setAmount(damage(event.getEntity(),event.getSource(),event.getAmount()));}
 @SubscribeEvent(priority=EventPriority.LOWEST) public static void target(LivingChangeTargetEvent event){if(active(event.getNewTarget()))event.setNewTarget(null);}
 @SubscribeEvent(priority=EventPriority.HIGHEST) public static void attack(AttackEntityEvent event){if(active(event.getEntity())&&forbidden(event.getEntity().getMainHandItem()))event.setCanceled(true);}
 @SubscribeEvent(priority=EventPriority.HIGHEST) public static void use(PlayerInteractEvent.RightClickItem event){if(active(event.getEntity())&&forbidden(event.getItemStack()))event.setCanceled(true);}
 @SubscribeEvent public static void equipment(LivingEquipmentChangeEvent event){if(event.getEntity() instanceof ServerPlayer p)enforceEquipment(p);}
 @SubscribeEvent(priority=EventPriority.LOWEST) public static void size(EntityEvent.Size event){if(active(event.getEntity())){event.setNewSize(EntityDimensions.scalable(.6f*SCALE,.5f),false);event.setNewEyeHeight(.425f);}}
 public static void updateSize(Player player){
  boolean enabled=active(player);var data=player.getPersistentData();boolean previous=data.getBoolean(SIZE_STATE);
  // Entity equality is ID-based; never share a player-keyed cache between integrated server and client.
  if(previous!=enabled||(enabled&&Math.abs(player.getBbHeight()-.5f)>.001f))player.refreshDimensions();
  if(enabled)data.putBoolean(SIZE_STATE,true);else if(previous)data.remove(SIZE_STATE);
 }
 @SubscribeEvent public static void tick(TickEvent.PlayerTickEvent event){
  if(event.phase!=TickEvent.Phase.START)return;var player=event.player;updateSize(player);
  if(player instanceof ServerPlayer p){enforceEquipment(p);if(active(p))DivineCombat.cleanArcane(p);}
 }
}
