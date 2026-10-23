package dev.starcrown;
import java.util.*;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.server.packs.resources.*;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.registries.ForgeRegistries;
/** Data packs extend the material ladder without changing Java code. Unknown valuables are preserved. */
public final class GenerosityUpgrades extends SimpleJsonResourceReloadListener {
 private static volatile Map<Item,Item> rules=Map.of();
 public GenerosityUpgrades(){super(new Gson(),"generosity_upgrades");}
 @Override protected void apply(Map<ResourceLocation,JsonElement> files,ResourceManager manager,ProfilerFiller profiler){
  GenerosityEmc.invalidate();var result=new HashMap<Item,Item>();
  for(var entry:new TreeMap<>(files).entrySet()){
   var values=entry.getValue().getAsJsonObject().getAsJsonObject("upgrades");if(values==null)continue;
   for(var rule:values.entrySet()){var from=ResourceLocation.tryParse(rule.getKey());var to=ResourceLocation.tryParse(rule.getValue().getAsString());if(from==null||to==null)continue;var source=ForgeRegistries.ITEMS.getValue(from);var target=ForgeRegistries.ITEMS.getValue(to);if(source!=null&&target!=null&&source!=Items.AIR&&target!=Items.AIR&&source.getMaxStackSize()<=target.getMaxStackSize())result.put(source,target);}
  }
  rules=Map.copyOf(result);
 }
 public static Item result(ItemStack stack){
  if(GenerosityEmc.installed())return GenerosityEmc.result(stack,net.minecraft.util.RandomSource.create());
  var direct=rules.get(stack.getItem());if(direct!=null)return direct;
  if(stack.hasTag())return null;
  if(stack.is(net.minecraftforge.common.Tags.Items.COBBLESTONE))return Items.IRON_INGOT;
  if(stack.is(net.minecraftforge.common.Tags.Items.INGOTS_IRON))return Items.GOLD_INGOT;
  if(stack.is(net.minecraftforge.common.Tags.Items.INGOTS_GOLD))return Items.DIAMOND;
  if(stack.is(net.minecraftforge.common.Tags.Items.GEMS_DIAMOND))return Items.NETHERITE_INGOT;
  if(stack.is(net.minecraftforge.common.Tags.Items.INGOTS_NETHERITE))return Items.NETHERITE_BLOCK;
  return null;
 }
}
