package dev.starcrown.mixin;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Pseudo
@Mixin(targets="com.erchien.arcanevortex.Core.CoreEntity.HealthMethodHelper",priority=2000,remap=false)
public abstract class ArcaneGuard12 {
 @Inject(method="forceSetHealth(Lnet/minecraft/world/entity/Entity;FF)V",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av0(net.minecraft.world.entity.Entity a0,float a1,float a2,CallbackInfo ci){if(dev.starcrown.DivineCombat.rejectCompatibility(a0,"AV.Core.CoreEntity.HealthMethodHelper.forceSetHealth")){ci.cancel();}}
 @Inject(method="forceSetHealth(Ljava/lang/Object;FF)V",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av1(java.lang.Object a0,float a1,float a2,CallbackInfo ci){if(dev.starcrown.DivineCombat.rejectCompatibility(a0,"AV.Core.CoreEntity.HealthMethodHelper.forceSetHealth")){ci.cancel();}}
 @Inject(method="isModified(Ljava/lang/Object;)Z",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av2(java.lang.Object a0,CallbackInfoReturnable<Boolean> ci){if(dev.starcrown.DivineCombat.compatibilityTarget(a0)){ci.setReturnValue(false);}}
}
