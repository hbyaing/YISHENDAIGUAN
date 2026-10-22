package dev.starcrown.mixin;
import dev.starcrown.DivineAnchor;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(Inventory.class)
public abstract class InventoryAnchor {
 @Inject(method={"clearContent()V","m_6211_()V"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void anchorClear(CallbackInfo ci){var inventory=(Inventory)(Object)this;if(DivineAnchor.blockSlot(inventory.player,39,ItemStack.EMPTY)){for(int slot=0;slot<inventory.getContainerSize();slot++)if(slot!=39)inventory.setItem(slot,ItemStack.EMPTY);inventory.setChanged();ci.cancel();}}
 @Inject(method={"setItem(ILnet/minecraft/world/item/ItemStack;)V","m_6836_(ILnet/minecraft/world/item/ItemStack;)V"},at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private void anchorSet(int slot,ItemStack stack,CallbackInfo ci){if(DivineAnchor.blockSlot(((Inventory)(Object)this).player,slot,stack))ci.cancel();}
 @Inject(method={"removeItem(II)Lnet/minecraft/world/item/ItemStack;","m_7407_(II)Lnet/minecraft/world/item/ItemStack;"},at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private void anchorRemove(int slot,int count,CallbackInfoReturnable<ItemStack> ci){if(DivineAnchor.blockSlot(((Inventory)(Object)this).player,slot,ItemStack.EMPTY))ci.setReturnValue(ItemStack.EMPTY);}
 @Inject(method={"removeItemNoUpdate(I)Lnet/minecraft/world/item/ItemStack;","m_8016_(I)Lnet/minecraft/world/item/ItemStack;"},at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private void anchorRemoveDirect(int slot,CallbackInfoReturnable<ItemStack> ci){if(DivineAnchor.blockSlot(((Inventory)(Object)this).player,slot,ItemStack.EMPTY))ci.setReturnValue(ItemStack.EMPTY);}
}
