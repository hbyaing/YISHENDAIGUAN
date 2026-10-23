package dev.starcrown.mixin;
import dev.starcrown.YuleGenerosity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(Inventory.class)
public abstract class GenerosityInventory {
 @Inject(method={"getFreeSlot()I","m_36062_()I"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void free(CallbackInfoReturnable<Integer> ci){var inv=(Inventory)(Object)this;if(YuleGenerosity.active(inv.player)){for(int i=0;i<9;i++)if(inv.items.get(i).isEmpty()){ci.setReturnValue(i);return;}ci.setReturnValue(-1);}}
 @Inject(method={"getSlotWithRemainingSpace(Lnet/minecraft/world/item/ItemStack;)I","m_36050_(Lnet/minecraft/world/item/ItemStack;)I"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void space(ItemStack incoming,CallbackInfoReturnable<Integer> ci){var inv=(Inventory)(Object)this;if(YuleGenerosity.active(inv.player)){for(int i=0;i<9;i++){var old=inv.items.get(i);if(!old.isEmpty()&&old.getCount()<old.getMaxStackSize()&&ItemStack.isSameItemSameTags(old,incoming)){ci.setReturnValue(i);return;}}ci.setReturnValue(-1);}}
 @Inject(method={"add(ILnet/minecraft/world/item/ItemStack;)Z","m_36040_(ILnet/minecraft/world/item/ItemStack;)Z"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void add(int slot,ItemStack incoming,CallbackInfoReturnable<Boolean> ci){if(YuleGenerosity.locked(((Inventory)(Object)this).player,slot))ci.setReturnValue(false);}
 @Inject(method={"setItem(ILnet/minecraft/world/item/ItemStack;)V","m_6836_(ILnet/minecraft/world/item/ItemStack;)V"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void set(int slot,ItemStack incoming,CallbackInfo ci){var inv=(Inventory)(Object)this;if(!incoming.isEmpty()&&YuleGenerosity.locked(inv.player,slot)){ci.cancel();if(inv.player instanceof ServerPlayer p)YuleGenerosity.redirect(p,incoming);}}
}
