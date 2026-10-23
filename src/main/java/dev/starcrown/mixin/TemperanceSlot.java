package dev.starcrown.mixin;
import dev.starcrown.YuleTemperance;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(Slot.class)
public abstract class TemperanceSlot {
 @Inject(method={"getMaxStackSize()I","m_6641_()I","getMaxStackSize(Lnet/minecraft/world/item/ItemStack;)I","m_5866_(Lnet/minecraft/world/item/ItemStack;)I"},at=@At("RETURN"),cancellable=true,remap=false,require=2)
 private void cap(CallbackInfoReturnable<Integer> c){if(((Slot)(Object)this).container instanceof Inventory i&&YuleTemperance.active(i.player))c.setReturnValue(Math.min(16,c.getReturnValue()));}
}
