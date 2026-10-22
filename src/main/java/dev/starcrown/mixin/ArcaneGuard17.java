package dev.starcrown.mixin;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Pseudo
@Mixin(targets="com.erchien.arcanevortex.Core.CoreEntity.AddedToWorldHelper",priority=2000,remap=false)
public abstract class ArcaneGuard17 {
 @Inject(method="setAddedToWorldValue(Lnet/minecraft/world/entity/Entity;Z)V",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av0(net.minecraft.world.entity.Entity a0,boolean a1,CallbackInfo ci){if(dev.starcrown.DivineCombat.rejectCompatibility(a0,"AV.Core.CoreEntity.AddedToWorldHelper.setAddedToWorldValue")){ci.cancel();}}
 @Inject(method="containsEntity(Lnet/minecraft/world/entity/Entity;)Z",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av1(net.minecraft.world.entity.Entity a0,CallbackInfoReturnable<Boolean> ci){if(dev.starcrown.DivineCombat.compatibilityTarget(a0)){ci.setReturnValue(false);}}
}
