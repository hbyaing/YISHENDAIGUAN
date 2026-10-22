package dev.starcrown.mixin;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Pseudo
@Mixin(targets="com.erchien.arcanevortex.Help.VanShForce.HealthLockManager",priority=2000,remap=false)
public abstract class ArcaneGuard4 {
 @Inject(method="lockEntityHealth(Lnet/minecraft/world/entity/LivingEntity;FFLjava/util/List;)V",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av0(net.minecraft.world.entity.LivingEntity a0,float a1,float a2,java.util.List a3,CallbackInfo ci){if(dev.starcrown.DivineCombat.reject(a0,"AV.Help.VanShForce.HealthLockManager.lockEntityHealth")){ci.cancel();}}
 @Inject(method="forceSetLockedFields(Lnet/minecraft/world/entity/LivingEntity;F)V",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av1(net.minecraft.world.entity.LivingEntity a0,float a1,CallbackInfo ci){if(dev.starcrown.DivineCombat.reject(a0,"AV.Help.VanShForce.HealthLockManager.forceSetLockedFields")){ci.cancel();}}
 @Inject(method="checkAndFixFields(Lnet/minecraft/world/entity/LivingEntity;)Z",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av2(net.minecraft.world.entity.LivingEntity a0,CallbackInfoReturnable<Boolean> ci){if(dev.starcrown.DivineCombat.reject(a0,"AV.Help.VanShForce.HealthLockManager.checkAndFixFields")){ci.setReturnValue(false);}}
}
