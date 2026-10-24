package dev.starcrown;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.npc.*;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.block.*;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
@Mod.EventBusSubscriber(modid=StarcrownDoll.ID)
public final class YuleChastity {
 public static final TagKey<EntityType<?>> NETHER_BORN=TagKey.create(Registries.ENTITY_TYPE,new ResourceLocation(StarcrownDoll.ID,"nether_born"));
 public static final TagKey<Item> POTIONS=TagKey.create(Registries.ITEM,new ResourceLocation(StarcrownDoll.ID,"chastity_potions"));
 public static final TagKey<MobEffect> CURSES=TagKey.create(Registries.MOB_EFFECT,new ResourceLocation(StarcrownDoll.ID,"chastity_curses"));
 private static final Map<MinecraftServer,Set<EntityType<?>>> NETHER_TYPES=new WeakHashMap<>();
 private static final List<BlockPos> FREEZE_OFFSETS=freezeOffsets();
 private static List<BlockPos> freezeOffsets(){var offsets=new ArrayList<BlockPos>();for(int x=-12;x<=12;x++)for(int z=-12;z<=12;z++)if(x*x+z*z<=144)offsets.add(new BlockPos(x,0,z));return List.copyOf(offsets);}
 public static boolean active(Entity e){return YuleHumility.active(e);}
 public static boolean unwanted(MobEffectInstance effect){return effect!=null&&!YuleDiligence.isPenalty(effect)&&(effect.getEffect().getCategory()!=MobEffectCategory.BENEFICIAL||ForgeRegistries.MOB_EFFECTS.tags().getTag(CURSES).contains(effect.getEffect()));}
 public static boolean blockedPotion(LivingEntity e,ItemStack stack){return active(e)&&!(stack.getItem() instanceof ThrowablePotionItem)&&stack.getUseAnimation()==UseAnim.DRINK&&(stack.getItem() instanceof PotionItem||stack.is(POTIONS)||!PotionUtils.getMobEffects(stack).isEmpty());}
 public static boolean blockedInteraction(Player p,Entity target,ItemStack stack){
  if(!active(p))return false;
  if(YuleCharity.canUse(p,target,stack))return false;
  if(target instanceof Merchant)return true;
  if(target instanceof TamableAnimal pet&&!pet.isTame())return true;
  if(target instanceof AbstractHorse horse&&!horse.isTamed())return true;
  if(target instanceof Animal animal&&animal.getAge()==0&&animal.canFallInLove()&&animal.isFood(stack)){
   // Healing an already-owned pet does not establish a new bond.
   if(animal instanceof TamableAnimal pet&&pet.isTame()&&pet.getHealth()<pet.getMaxHealth())return false;
   return true;
  }
  return false;
 }
 public static boolean blockedOwner(Entity animal,UUID owner){if(owner==null||!animal.isAddedToWorld()||(animal instanceof OwnableEntity owned&&owner.equals(owned.getOwnerUUID())))return false;var player=animal.level().getPlayerByUUID(owner);if(player==null&&animal.level().getServer()!=null)player=animal.level().getServer().getPlayerList().getPlayer(owner);return player!=null&&active(player);}
 private static synchronized Set<EntityType<?>> netherTypes(ServerLevel level){return NETHER_TYPES.computeIfAbsent(level.getServer(),server->{
  Set<EntityType<?>> types=new HashSet<>(List.of(EntityType.BLAZE,EntityType.GHAST,EntityType.MAGMA_CUBE,EntityType.PIGLIN,EntityType.PIGLIN_BRUTE,EntityType.HOGLIN,EntityType.ZOGLIN,EntityType.ZOMBIFIED_PIGLIN,EntityType.WITHER_SKELETON,EntityType.STRIDER));
  var biomes=server.registryAccess().registryOrThrow(Registries.BIOME);biomes.getTag(BiomeTags.IS_NETHER).ifPresent(holders->{for(var holder:holders)for(var category:MobCategory.values())for(var spawn:holder.value().getMobSettings().getMobs(category).unwrap())types.add(spawn.type);});return types;
 });}
 @SubscribeEvent public static synchronized void tags(net.minecraftforge.event.TagsUpdatedEvent e){NETHER_TYPES.clear();}
 public static boolean purifiable(LivingEntity entity){return entity instanceof Mob&&entity.level() instanceof ServerLevel level&&(entity.getMobType()==MobType.UNDEAD||entity.getType().is(NETHER_BORN)||netherTypes(level).contains(entity.getType()));}
 public static boolean inside(LivingEntity entity){if(!purifiable(entity)||!(entity.level() instanceof ServerLevel level))return false;for(var p:level.players())if(active(p)&&p.distanceToSqr(entity)<=25)return true;return false;}
 public static void purify(ServerPlayer p){
  if(!active(p))return;
  var level=p.serverLevel();for(var mob:level.getEntitiesOfClass(Mob.class,p.getBoundingBox().inflate(5),e->e.isAlive()&&p.distanceToSqr(e)<=25&&purifiable(e))){
   mob.setAbsorptionAmount(0);mob.setHealth(0);mob.die(level.damageSources().genericKill());level.sendParticles(net.minecraft.core.particles.ParticleTypes.END_ROD,mob.getX(),mob.getY()+mob.getBbHeight()*.5,mob.getZ(),8,.2,.3,.2,.015);
  }
 }
 public static void freeze(ServerPlayer p){
  if(!active(p))return;
  var level=p.serverLevel();int y=net.minecraft.util.Mth.floor(p.getY()-.01),cx=p.getBlockX(),cz=p.getBlockZ();
  var pos=new BlockPos.MutableBlockPos();var above=new BlockPos.MutableBlockPos();
  for(var offset:FREEZE_OFFSETS){
   pos.set(cx+offset.getX(),y,cz+offset.getZ());if(!level.hasChunkAt(pos)||!level.getWorldBorder().isWithinBounds(pos)||level.isOutsideBuildHeight(pos))continue;
   var state=level.getBlockState(pos);if(!(state.getBlock() instanceof LiquidBlock)||!level.getBlockState(above.set(pos.getX(),y+1,pos.getZ())).isAir()||p.getBoundingBox().intersects(new AABB(pos)))continue;
   var fluid=state.getFluidState();var block=fluid.is(FluidTags.WATER)?Blocks.FROSTED_ICE:fluid.is(FluidTags.LAVA)?Blocks.OBSIDIAN:null;if(block==null)continue;
   var frozen=pos.immutable();
   if(net.minecraftforge.event.ForgeEventFactory.onBlockPlace(p,BlockSnapshot.create(level.dimension(),level,frozen),Direction.UP))continue;
   if(level.setBlockAndUpdate(frozen,block.defaultBlockState())&&block==Blocks.FROSTED_ICE)level.scheduleTick(frozen,block,60+level.random.nextInt(61));
  }
 }
 @SubscribeEvent(priority=EventPriority.HIGHEST) public static void interact(PlayerInteractEvent.EntityInteract e){if(blockedInteraction(e.getEntity(),e.getTarget(),e.getItemStack())){e.setCanceled(true);e.setCancellationResult(InteractionResult.FAIL);}}
 @SubscribeEvent(priority=EventPriority.HIGHEST) public static void interactSpecific(PlayerInteractEvent.EntityInteractSpecific e){if(blockedInteraction(e.getEntity(),e.getTarget(),e.getItemStack())){e.setCanceled(true);e.setCancellationResult(InteractionResult.FAIL);}}
 @SubscribeEvent(priority=EventPriority.HIGHEST) public static void right(PlayerInteractEvent.RightClickItem e){if(blockedPotion(e.getEntity(),e.getItemStack())){e.setCanceled(true);e.setCancellationResult(InteractionResult.FAIL);}}
 @SubscribeEvent(priority=EventPriority.HIGHEST) public static void use(LivingEntityUseItemEvent.Start e){if(blockedPotion(e.getEntity(),e.getItem()))e.setCanceled(true);}
 @SubscribeEvent(priority=EventPriority.HIGHEST) public static void tame(AnimalTameEvent e){if(active(e.getTamer()))e.setCanceled(true);}
 @SubscribeEvent(priority=EventPriority.LOWEST) public static void applicable(MobEffectEvent.Applicable e){if(active(e.getEntity())&&unwanted(e.getEffectInstance()))e.setResult(Event.Result.DENY);}
 @SubscribeEvent(priority=EventPriority.LOWEST,receiveCanceled=true) public static void remove(MobEffectEvent.Remove e){if(active(e.getEntity())&&unwanted(e.getEffectInstance()))e.setCanceled(false);}
 @SubscribeEvent public static void opened(PlayerContainerEvent.Open e){if(e.getEntity() instanceof ServerPlayer p&&active(p)&&e.getContainer() instanceof net.minecraft.world.inventory.MerchantMenu)p.closeContainer();}
 @SubscribeEvent(priority=EventPriority.HIGHEST) public static void tick(TickEvent.PlayerTickEvent e){
  if(e.phase!=TickEvent.Phase.START||!(e.player instanceof ServerPlayer p)||!active(p))return;
  for(var effect:new ArrayList<>(p.getActiveEffects()))if(unwanted(effect))p.removeEffect(effect.getEffect());
  if(blockedPotion(p,p.getUseItem()))p.stopUsingItem();if(p.containerMenu instanceof net.minecraft.world.inventory.MerchantMenu)p.closeContainer();
  freeze(p);purify(p);
 }
 private YuleChastity(){}
}
