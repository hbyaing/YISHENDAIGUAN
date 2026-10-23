package dev.starcrown;

import java.util.*;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.*;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.registries.ForgeRegistries;

/** Internal, per-world unit values. Never synchronized to clients or added to item NBT. */
@net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid=StarcrownDoll.ID)
public final class GenerosityPrices extends SavedData {
 @net.minecraftforge.eventbus.api.SubscribeEvent public static void started(net.minecraftforge.event.server.ServerStartedEvent event){get(event.getServer());}
 private final Map<String,Long> prices=new TreeMap<>();
 private final NavigableMap<Long,List<Item>> tiers=new TreeMap<>();
 private boolean ready;
 public static GenerosityPrices get(MinecraftServer server){
  var data=server.overworld().getDataStorage().computeIfAbsent(GenerosityPrices::load,GenerosityPrices::new,"starcrowndoll_item_prices");
  if(!data.ready)data.complete(server);
  return data;
 }
 public static GenerosityPrices load(CompoundTag tag){
  var data=new GenerosityPrices();var values=tag.getCompound("prices");
  for(var key:values.getAllKeys())if(values.getLong(key)>0)data.prices.put(key,values.getLong(key));
  return data;
 }
 public long price(Item item){var key=ForgeRegistries.ITEMS.getKey(item);return key==null?0:prices.getOrDefault(key.toString(),0L);}
 private void put(Item item,long value){var key=ForgeRegistries.ITEMS.getKey(item);if(key!=null&&item!=Items.AIR&&value>0&&prices.putIfAbsent(key.toString(),value)==null)setDirty();}
 private void complete(MinecraftServer server){
  // Existing values always win, even after ProjectE or data packs are changed.
  for(var item:ForgeRegistries.ITEMS)put(item,GenerosityEmc.reference(item));
  Item[] bases={Items.COBBLESTONE,Items.STONE,Items.DIRT,Items.SAND,Items.GRAVEL,Items.OAK_LOG,Items.COAL,Items.CHARCOAL,Items.IRON_INGOT,Items.GOLD_INGOT,Items.COPPER_INGOT,Items.REDSTONE,Items.LAPIS_LAZULI,Items.QUARTZ,Items.DIAMOND,Items.EMERALD,Items.NETHERITE_SCRAP,Items.NETHERITE_INGOT,Items.NETHER_STAR,Items.DRAGON_EGG};
  long[] values={1,1,1,1,4,32,128,32,256,2048,128,64,864,256,8192,16384,16384,73728,139264,262144};
  for(int n=0;n<bases.length;n++)put(bases[n],values[n]);
  // Resolve known-input recipes before assigning defaults to uncraftable/opaque items.
  for(int pass=0;pass<32;pass++){
   int before=prices.size();
   var inferred=new HashMap<Item,Long>();
   for(var recipe:server.getRecipeManager().getRecipes()){
    var output=recipe.getResultItem(server.registryAccess());if(output.isEmpty()||price(output.getItem())>0||recipe.getIngredients().isEmpty())continue;
    long total=0;boolean known=true;
    for(var ingredient:recipe.getIngredients()){
     if(ingredient.isEmpty())continue;
     long minimum=Long.MAX_VALUE;
     for(var option:ingredient.getItems()){long v=price(option.getItem());if(v>0)minimum=Math.min(minimum,v);}
     if(minimum==Long.MAX_VALUE){known=false;break;}
     total=total>Long.MAX_VALUE-minimum?Long.MAX_VALUE:total+minimum;
    }
    if(known&&total>0)inferred.merge(output.getItem(),Math.max(1,total/output.getCount()),Math::min);
   }
   inferred.forEach(this::put);if(prices.size()==before)break;
  }
  for(var item:ForgeRegistries.ITEMS)if(item!=Items.AIR&&price(item)==0)put(item,fallback(item));
  for(var item:ForgeRegistries.ITEMS)if(item!=Items.AIR)tiers.computeIfAbsent(price(item),k->new ArrayList<>()).add(item);
  ready=true;
 }
 private static long fallback(Item item){
  if(item instanceof TieredItem tool)return Math.max(64,(long)(tool.getTier().getLevel()+1)*1024);
  if(item instanceof ArmorItem armor)return Math.max(64,(long)(armor.getDefense()+1)*512);
  if(item instanceof BlockItem)return 16;
  if(item.isEdible())return 64;
  if(item.getMaxDamage()>0)return Math.max(256,item.getMaxDamage()*8L);
  return item.getMaxStackSize()==1?4096:128;
 }
 public Item result(ItemStack input,RandomSource random){
  long source=price(input.getItem());if(source<=0)return null;
  long limit=source>Long.MAX_VALUE/4?Long.MAX_VALUE:source*4;
  var candidates=new ArrayList<Item>();
  for(var tier:tiers.tailMap(source,false).entrySet()){
   if(tier.getKey()>limit&&!candidates.isEmpty())break;
   for(var item:tier.getValue()){
    var output=new ItemStack(item,input.getCount());if(input.hasTag())output.setTag(input.getTag().copy());
    if(output.getMaxStackSize()>=input.getCount())candidates.add(item);
   }
   if(tier.getKey()>limit&&!candidates.isEmpty())break;
  }
  return candidates.isEmpty()?null:candidates.get(random.nextInt(candidates.size()));
 }
 @Override public CompoundTag save(CompoundTag tag){var values=new CompoundTag();prices.forEach(values::putLong);tag.put("prices",values);tag.putInt("schema",1);return tag;}
}
