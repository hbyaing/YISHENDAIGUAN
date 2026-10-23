package dev.starcrown.mixin;
import dev.starcrown.YuleChastity;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(net.minecraft.world.item.PotionItem.class)
public abstract class ChastityPotion {
 @Inject(method={"finishUsingItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;","m_5922_(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void pureDrink(net.minecraft.world.item.ItemStack stack,net.minecraft.world.level.Level level,LivingEntity entity,CallbackInfoReturnable<net.minecraft.world.item.ItemStack> ci){if(YuleChastity.blockedPotion(entity,stack))ci.setReturnValue(stack);}
}
