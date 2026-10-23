package dev.starcrown;

import java.lang.reflect.Method;
import java.util.*;
import net.minecraft.world.item.*;
import net.minecraft.util.RandomSource;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

/** Optional public EMC API supplies initial references; saved world prices remain authoritative. */
public final class GenerosityEmc {
 private static Object proxy;
 private static Method value;
 private static boolean attempted;
 public static boolean installed(){return ModList.get().isLoaded("projecte");}
 private static boolean initialize(){
  if(attempted)return value!=null;attempted=true;
  try{var api=Class.forName("moze_intel.projecte.api.proxy.IEMCProxy");proxy=api.getField("INSTANCE").get(null);value=api.getMethod("getValue",ItemStack.class);return true;}
  catch(ReflectiveOperationException|LinkageError e){com.mojang.logging.LogUtils.getLogger().warn("ProjectE EMC API unavailable; generosity preserves items",e);return false;}
 }
 private static long emc(ItemStack stack)throws ReflectiveOperationException{return ((Number)value.invoke(proxy,stack)).longValue();}
 /** Hidden world prices, with quantity and metadata preserved by the caller. */
 public static synchronized Item result(ItemStack input,RandomSource random){
  var server=net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
  if(server==null||input.isEmpty())return null;
  return GenerosityPrices.get(server).result(input,random);
 }
 static long reference(Item item){
  if(!installed()||!initialize())return 0;
  try{return Math.max(0,emc(new ItemStack(item)));}catch(ReflectiveOperationException|RuntimeException e){return 0;}
 }
 private GenerosityEmc(){}
}
