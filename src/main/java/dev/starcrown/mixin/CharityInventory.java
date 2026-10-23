package dev.starcrown.mixin;
import dev.starcrown.YuleCharity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(Inventory.class)
public abstract class CharityInventory {
 @Inject(method={"add(ILnet/minecraft/world/item/ItemStack;)Z","m_36040_(ILnet/minecraft/world/item/ItemStack;)Z"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void add(int slot,ItemStack s,CallbackInfoReturnable<Boolean> c){if(YuleCharity.active(((Inventory)(Object)this).player)&&YuleCharity.deathLoot(s))c.setReturnValue(false);}
}
