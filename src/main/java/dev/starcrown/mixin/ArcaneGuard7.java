package dev.starcrown.mixin;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Pseudo
@Mixin(targets="com.erchien.arcanevortex.Content.Item.EntityRemover.EntityRemover",priority=2000,remap=false)
public abstract class ArcaneGuard7 {
 @Inject(method="remove(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/player/Player;Lcom/erchien/arcanevortex/Content/Item/EntityRemover/EntityRemover$AttackMode;)Z",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av0(net.minecraft.world.entity.Entity a0,net.minecraft.world.entity.player.Player a1,@Coerce java.lang.Object a2,CallbackInfoReturnable<Boolean> ci){if(dev.starcrown.DivineCombat.rejectCompatibility(a0,"AV.Content.Item.EntityRemover.EntityRemover.remove")){ci.setReturnValue(false);}}
 @Inject(method="attack(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/player/Player;Lcom/erchien/arcanevortex/Content/Item/EntityRemover/EntityRemover$AttackMode;)Z",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av1(net.minecraft.world.entity.Entity a0,net.minecraft.world.entity.player.Player a1,@Coerce java.lang.Object a2,CallbackInfoReturnable<Boolean> ci){if(dev.starcrown.DivineCombat.rejectCompatibility(a0,"AV.Content.Item.EntityRemover.EntityRemover.attack")){ci.setReturnValue(false);}}
}
