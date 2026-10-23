package dev.starcrown.mixin;
import dev.starcrown.YuleCharity;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(Slot.class)
public abstract class CharitySlot {
 @Inject(method={"mayPickup(Lnet/minecraft/world/entity/player/Player;)Z","m_8010_(Lnet/minecraft/world/entity/player/Player;)Z"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void pickup(Player p,CallbackInfoReturnable<Boolean> c){if(YuleCharity.active(p)&&YuleCharity.deathLoot(((Slot)(Object)this).getItem()))c.setReturnValue(false);}
}
