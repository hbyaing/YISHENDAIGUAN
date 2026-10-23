package dev.starcrownqa;
import dev.starcrown.*;
import net.minecraft.gametest.framework.*;
import net.minecraftforge.gametest.*;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.world.item.*;
import net.minecraft.nbt.CompoundTag;
@GameTestHolder("starcrownqa")
@PrefixGameTestTemplate(false)
public final class PricingTests {
 @GameTest(template="empty",timeoutTicks=100)
 public static void coveragePersistenceAndUpgrade(GameTestHelper h){
  var prices=GenerosityPrices.get(h.getLevel().getServer());
  for(var item:ForgeRegistries.ITEMS)if(item!=Items.AIR)h.assertTrue(prices.price(item)>0,"all registered items valued: "+ForgeRegistries.ITEMS.getKey(item));
  var saved=prices.save(new CompoundTag());var loaded=GenerosityPrices.load(saved);
  for(var item:ForgeRegistries.ITEMS)h.assertTrue(prices.price(item)==loaded.price(item),"prices survive NBT roundtrip");
  for(var item:ForgeRegistries.ITEMS){
   if(item==Items.AIR)continue;
   var input=new ItemStack(item,Math.min(17,item.getMaxStackSize()));input.getOrCreateTag().putString("test","retained");
   var result=YuleGenerosity.upgrade(input);
   h.assertTrue(result.getCount()==input.getCount()&&result.hasTag()&&result.getTag().getString("test").equals("retained"),"quantity and custom NBT preserved: "+ForgeRegistries.ITEMS.getKey(item));
   h.assertTrue(result.is(item)||prices.price(result.getItem())>prices.price(item),"upgrades strictly increase value");
   h.assertTrue(result.getCount()<=result.getMaxStackSize(),"valid output stack");
  }
  h.succeed();
 }
}
