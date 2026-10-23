package dev.starcrown.mixin;
import dev.starcrown.YuleChastity;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(TamableAnimal.class)
public abstract class ChastityPet {
 @Inject(method={"tame(Lnet/minecraft/world/entity/player/Player;)V","m_21828_(Lnet/minecraft/world/entity/player/Player;)V"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void pureTame(Player player,CallbackInfo ci){if(YuleChastity.active(player))ci.cancel();}
 @Inject(method={"setOwnerUUID(Ljava/util/UUID;)V","m_21816_(Ljava/util/UUID;)V"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void pureOwner(java.util.UUID owner,CallbackInfo ci){if(YuleChastity.blockedOwner((Entity)(Object)this,owner))ci.cancel();}
}
