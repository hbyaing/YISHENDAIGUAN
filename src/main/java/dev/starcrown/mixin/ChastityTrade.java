package dev.starcrown.mixin;
import dev.starcrown.YuleChastity;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(net.minecraft.world.inventory.MerchantMenu.class)
public abstract class ChastityTrade {
 @Inject(method={"stillValid(Lnet/minecraft/world/entity/player/Player;)Z","m_6875_(Lnet/minecraft/world/entity/player/Player;)Z"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void pureValid(Player player,CallbackInfoReturnable<Boolean> ci){if(YuleChastity.active(player))ci.setReturnValue(false);}
 @Inject(method={"quickMoveStack(Lnet/minecraft/world/entity/player/Player;I)Lnet/minecraft/world/item/ItemStack;","m_7648_(Lnet/minecraft/world/entity/player/Player;I)Lnet/minecraft/world/item/ItemStack;"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void pureQuick(Player player,int slot,CallbackInfoReturnable<net.minecraft.world.item.ItemStack> ci){if(YuleChastity.active(player))ci.setReturnValue(net.minecraft.world.item.ItemStack.EMPTY);}
}
