package dev.starcrown;

import java.lang.reflect.Method;
import java.util.*;
import net.minecraft.world.item.*;
import net.minecraft.util.RandomSource;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

/** Optional ProjectE bridge: only its public EMC API is used, never its implementation classes. */
public final class GenerosityEmc {
 private static Object proxy;
 private static Method value;
 private static boolean attempted;
 private static long refreshed;
 private static final NavigableMap<Long,List<Item>> tiers=new TreeMap<>();
 public static boolean installed(){return ModList.get().isLoaded("projecte");}
 public static synchronized void invalidate(){tiers.clear();refreshed=0;}
 private static boolean initialize(){
  if(attempted)return value!=null;attempted=true;
  try{var api=Class.forName("moze_intel.projecte.api.proxy.IEMCProxy");proxy=api.getField("INSTANCE").get(null);value=api.getMethod("getValue",ItemStack.class);return true;}
  catch(ReflectiveOperationException|LinkageError e){com.mojang.logging.LogUtils.getLogger().warn("ProjectE EMC API unavailable; generosity preserves items",e);return false;}
 }
 private static long emc(ItemStack stack)throws ReflectiveOperationException{return ((Number)value.invoke(proxy,stack)).longValue();}
 /** Random item in the next fourfold EMC band; if empty, use the nearest higher tier. */
 public static synchronized Item result(ItemStack input,RandomSource random){
  if(!initialize())return null;
  try{
   long source=emc(input);if(source<=0)return null;
   long now=System.nanoTime();
   if(tiers.isEmpty()||now-refreshed>10_000_000_000L){
    tiers.clear();for(var item:ForgeRegistries.ITEMS){if(item==Items.AIR)continue;long amount=emc(new ItemStack(item));if(amount>0)tiers.computeIfAbsent(amount,k->new ArrayList<>()).add(item);}refreshed=now;
   }
   long limit=source>Long.MAX_VALUE/4?Long.MAX_VALUE:source*4;
   var candidates=new ArrayList<Item>();
   for(var tier:tiers.tailMap(source,false).entrySet()){
    if(tier.getKey()>limit&&!candidates.isEmpty())break;
    for(var item:tier.getValue()){
     if(item==input.getItem())continue;
     var output=new ItemStack(item,input.getCount());if(input.hasTag())output.setTag(input.getTag().copy());
     // Recheck actual tagged value and stack limits, including EMC changed since the cache was built.
     if(output.getMaxStackSize()>=input.getCount()&&emc(output)>source)candidates.add(item);
    }
    if(tier.getKey()>limit&&!candidates.isEmpty())break;
   }
   return candidates.isEmpty()?null:candidates.get(random.nextInt(candidates.size()));
  }catch(ReflectiveOperationException|RuntimeException e){com.mojang.logging.LogUtils.getLogger().warn("ProjectE EMC conversion failed; original gift retained",e);invalidate();return null;}
 }
 private GenerosityEmc(){}
}
