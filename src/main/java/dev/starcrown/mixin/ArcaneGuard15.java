package dev.starcrown.mixin;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Pseudo
@Mixin(targets="com.erchien.arcanevortex.Core.CoreEntity.IsRemovedHelper",priority=2000,remap=false)
public abstract class ArcaneGuard15 {
 @Inject(method="containsEntity(Ljava/lang/Object;)Z",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av0(java.lang.Object a0,CallbackInfoReturnable<Boolean> ci){if(dev.starcrown.DivineCombat.protectedTarget(a0)){ci.setReturnValue(false);}}
 @Inject(method="setIsRemoved(Lnet/minecraft/world/entity/Entity;Z)V",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av1(net.minecraft.world.entity.Entity a0,boolean a1,CallbackInfo ci){if(dev.starcrown.DivineCombat.reject(a0,"AV.Core.CoreEntity.IsRemovedHelper.setIsRemoved")){ci.cancel();}}
}
