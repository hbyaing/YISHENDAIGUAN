package dev.starcrown.mixin;
import dev.starcrown.YuleGenerosity;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(Slot.class)
public abstract class GenerositySlot {
 @Inject(method={"mayPlace(Lnet/minecraft/world/item/ItemStack;)Z","m_5857_(Lnet/minecraft/world/item/ItemStack;)Z"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void locked(ItemStack item,CallbackInfoReturnable<Boolean> ci){var slot=(Slot)(Object)this;if(slot.container instanceof Inventory inv&&YuleGenerosity.locked(inv.player,slot.getSlotIndex()))ci.setReturnValue(false);}
}
