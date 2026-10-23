package dev.starcrown;
import java.util.*;
import java.lang.ref.WeakReference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.*;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
@Mod.EventBusSubscriber(modid=StarcrownDoll.ID)
public final class YuleTemperance {
 public static final String MINING="starcrowndoll_temperance_mining";
 private static final Map<Player,Boolean> PLAYERS=Collections.synchronizedMap(new WeakHashMap<>());
 private static final Map<FoodData,WeakReference<Player>> FOOD=Collections.synchronizedMap(new WeakHashMap<>());
 public static boolean active(Entity e){return YuleHumility.active(e);}
 public static void bind(Player p){PLAYERS.put(p,true);FOOD.put(p.getFoodData(),new WeakReference<>(p));}
 public static boolean locked(FoodData f){var ref=FOOD.get(f);return ref!=null&&active(ref.get());}
 public static Player owner(ItemStack s){if(s==ItemStack.EMPTY)return null;synchronized(PLAYERS){for(var p:PLAYERS.keySet())if(active(p)){for(int i=0;i<p.getInventory().getContainerSize();i++)if(p.getInventory().getItem(i)==s)return p;if(p.containerMenu.getCarried()==s)return p;}}return null;}
 public static boolean equipped(ItemStack s,LivingEntity e){if(!active(e))return false;for(var slot:EquipmentSlot.values())if(e.getItemBySlot(slot)==s)return true;return false;}
 public static boolean durable(ItemStack s){var p=owner(s);return p!=null&&equipped(s,p);}
 public static boolean foodDenied(LivingEntity e,ItemStack s){if(!(e instanceof Player p)||!active(p))return false;var f=s.getFoodProperties(p);return f!=null&&f.getNutrition()>Items.BREAD.getFoodProperties().getNutrition();}
 public static void fill(Player p){bind(p);if(active(p)){var f=p.getFoodData();f.setFoodLevel(20);f.setSaturation(20);f.setExhaustion(0);}}
 private static String chunk(BlockPos pos){return Long.toString(net.minecraft.world.level.ChunkPos.asLong(pos.getX()>>4,pos.getZ()>>4));}
 public static int used(Player p,BlockPos pos,Block b){return p.getPersistentData().getCompound(MINING).getCompound(p.level().dimension().location().toString()).getCompound(chunk(pos)).getInt(BuiltInRegistries.BLOCK.getKey(b).toString());}
 public static boolean denied(Player p,BlockPos pos){return active(p)&&used(p,pos,p.level().getBlockState(pos).getBlock())>=10;}
 public static void mined(Player p,BlockPos pos,Block b){var root=p.getPersistentData().getCompound(MINING);String dim=p.level().dimension().location().toString();var d=root.getCompound(dim);String key=chunk(pos);var c=d.getCompound(key);String id=BuiltInRegistries.BLOCK.getKey(b).toString();c.putInt(id,Math.min(10,c.getInt(id)+1));d.put(key,c);root.put(dim,d);p.getPersistentData().put(MINING,root);}
 @SubscribeEvent public static void equipment(net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent e){if(e.getEntity() instanceof Player p)bind(p);}
 @SubscribeEvent public static void clone(PlayerEvent.Clone e){if(e.getOriginal().getPersistentData().contains(MINING))e.getEntity().getPersistentData().put(MINING,e.getOriginal().getPersistentData().getCompound(MINING).copy());}
 @SubscribeEvent public static void eating(LivingEntityUseItemEvent.Start e){if(foodDenied(e.getEntity(),e.getItem()))e.setCanceled(true);}
 public static void enforce(Player p){fill(p);if(!active(p))return;for(var slot:EquipmentSlot.values()){var s=p.getItemBySlot(slot);if(s.isDamageableItem()&&s.getDamageValue()>0)s.setDamageValue(0);}if(!(p instanceof ServerPlayer server))return;
  var extra=new ArrayList<ItemStack>();for(int i=0;i<p.getInventory().getContainerSize();i++){var s=p.getInventory().getItem(i);int cap=s.getMaxStackSize();if(s.getCount()>cap)extra.add(s.split(s.getCount()-cap));}var cursor=p.containerMenu.getCarried();if(cursor.getCount()>cursor.getMaxStackSize())extra.add(cursor.split(cursor.getCount()-cursor.getMaxStackSize()));for(var s:extra)YuleGenerosity.redirect(server,s);
 }
 @SubscribeEvent public static void tick(TickEvent.PlayerTickEvent e){enforce(e.player);}
}
