package dev.starcrown;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BowlFoodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import java.util.List;

/** Uses the vanilla food/effect pipeline, including eating animation and empty bowl. */
public final class WhiteRiceItem extends BowlFoodItem {
 public WhiteRiceItem(){
  super(new Item.Properties().stacksTo(1).food(new FoodProperties.Builder()
   .nutrition(6).saturationMod(.6f).alwaysEat()
   // Potion levels are one-based in the UI: amplifier 28 is Instant Health XXIX.
   .effect(()->new MobEffectInstance(MobEffects.HEAL,1,28),1.0f).build()));
 }
 @Override public void appendHoverText(ItemStack stack,Level level,List<Component> lines,TooltipFlag flag){
  super.appendHoverText(stack,level,lines,flag);
  lines.add(Component.translatable("tooltip.starcrowndoll.white_rice.effect").withStyle(ChatFormatting.GREEN));
 }
}
