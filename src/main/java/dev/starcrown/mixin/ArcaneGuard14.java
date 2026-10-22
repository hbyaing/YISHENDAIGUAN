package dev.starcrown.mixin;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Pseudo
@Mixin(targets="com.erchien.arcanevortex.NativeAgent.NativeAgentHealthMethod",priority=2000,remap=false)
public abstract class ArcaneGuard14 {
 @Inject(method="forceSetHealth(Ljava/lang/Object;FF)V",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av0(java.lang.Object a0,float a1,float a2,CallbackInfo ci){if(dev.starcrown.DivineCombat.rejectCompatibility(a0,"AV.NativeAgent.NativeAgentHealthMethod.forceSetHealth")){ci.cancel();}}
 @Inject(method="forceSetHealth(Ljava/lang/Object;F)V",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av1(java.lang.Object a0,float a1,CallbackInfo ci){if(dev.starcrown.DivineCombat.rejectCompatibility(a0,"AV.NativeAgent.NativeAgentHealthMethod.forceSetHealth")){ci.cancel();}}
 @Inject(method="forceSetMaxHealth(Ljava/lang/Object;F)V",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av2(java.lang.Object a0,float a1,CallbackInfo ci){if(dev.starcrown.DivineCombat.rejectCompatibility(a0,"AV.NativeAgent.NativeAgentHealthMethod.forceSetMaxHealth")){ci.cancel();}}
 @Inject(method="isModified(Ljava/lang/Object;)Z",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av3(java.lang.Object a0,CallbackInfoReturnable<Boolean> ci){if(dev.starcrown.DivineCombat.compatibilityTarget(a0)){ci.setReturnValue(false);}}
}
