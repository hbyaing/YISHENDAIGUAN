package dev.starcrown.mixin;
import dev.starcrown.YuleChastity;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(net.minecraft.world.entity.animal.horse.AbstractHorse.class)
public abstract class ChastityHorse {
 @Inject(method={"tameWithName(Lnet/minecraft/world/entity/player/Player;)Z","m_30637_(Lnet/minecraft/world/entity/player/Player;)Z"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void pureTame(Player player,CallbackInfoReturnable<Boolean> ci){if(YuleChastity.active(player))ci.setReturnValue(false);}
 @Inject(method={"setOwnerUUID(Ljava/util/UUID;)V","m_30586_(Ljava/util/UUID;)V"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void pureOwner(java.util.UUID owner,CallbackInfo ci){if(YuleChastity.blockedOwner((Entity)(Object)this,owner))ci.cancel();}
}
