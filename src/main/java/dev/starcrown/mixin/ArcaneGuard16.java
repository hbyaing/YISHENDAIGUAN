package dev.starcrown.mixin;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Pseudo
@Mixin(targets="com.erchien.arcanevortex.Core.CoreEntity.RemovalReasonHelper",priority=2000,remap=false)
public abstract class ArcaneGuard16 {
 @Inject(method="containsEntity(Ljava/lang/Object;)Z",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av0(java.lang.Object a0,CallbackInfoReturnable<Boolean> ci){if(dev.starcrown.DivineCombat.protectedTarget(a0)){ci.setReturnValue(false);}}
 @Inject(method="setRemovalReason(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity$RemovalReason;)V",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av1(net.minecraft.world.entity.Entity a0,net.minecraft.world.entity.Entity.RemovalReason a1,CallbackInfo ci){if(dev.starcrown.DivineCombat.reject(a0,"AV.Core.CoreEntity.RemovalReasonHelper.setRemovalReason")){ci.cancel();}}
}
