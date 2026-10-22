package dev.starcrown.mixin;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Pseudo
@Mixin(targets="com.erchien.arcanevortex.Agent.AntiHiddenClass.IsAddedToWorldHelper",priority=2000,remap=false)
public abstract class ArcaneGuard18 {
 @Inject(method="containsEntity(Ljava/lang/Object;)Z",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av0(java.lang.Object a0,CallbackInfoReturnable<Boolean> ci){if(dev.starcrown.DivineCombat.compatibilityTarget(a0)){ci.setReturnValue(false);}}
 @Inject(method="setAddedToWorld(Ljava/lang/Object;Z)V",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av1(java.lang.Object a0,boolean a1,CallbackInfo ci){if(dev.starcrown.DivineCombat.rejectCompatibility(a0,"AV.Agent.AntiHiddenClass.IsAddedToWorldHelper.setAddedToWorld")){ci.cancel();}}
}
