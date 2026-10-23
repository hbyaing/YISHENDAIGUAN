package dev.starcrown.mixin;
import dev.starcrown.YuleChastity;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(net.minecraft.world.inventory.Slot.class)
public abstract class ChastityTradeSlot {
 @Inject(method={"mayPickup(Lnet/minecraft/world/entity/player/Player;)Z","m_8010_(Lnet/minecraft/world/entity/player/Player;)Z"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void pureTrade(Player player,CallbackInfoReturnable<Boolean> ci){if((Object)this instanceof net.minecraft.world.inventory.MerchantResultSlot&&YuleChastity.active(player))ci.setReturnValue(false);}
}
