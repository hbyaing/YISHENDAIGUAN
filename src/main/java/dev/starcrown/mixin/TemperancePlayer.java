package dev.starcrown.mixin;
import dev.starcrown.YuleTemperance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(Player.class)
public abstract class TemperancePlayer {
 @Inject(method={"getProjectile(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;","m_6298_(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;"},at=@At("RETURN"),cancellable=true,remap=false,require=1)
 private void ammo(ItemStack weapon,CallbackInfoReturnable<ItemStack> c){if(YuleTemperance.active((Player)(Object)this)&&weapon.getItem() instanceof BowItem&&c.getReturnValue().isEmpty())c.setReturnValue(new ItemStack(Items.ARROW));}
 @Inject(method={"eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;","m_5584_(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void eat(Level l,ItemStack s,CallbackInfoReturnable<ItemStack> c){if(YuleTemperance.foodDenied((Player)(Object)this,s))c.setReturnValue(s);}
}
