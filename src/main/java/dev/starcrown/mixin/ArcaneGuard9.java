package dev.starcrown.mixin;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Pseudo
@Mixin(targets="com.erchien.arcanevortex.Content.Item.EntityRemover.FindSetterMethod",priority=2000,remap=false)
public abstract class ArcaneGuard9 {
 @Inject(method="SetHealth(Lnet/minecraft/world/entity/Entity;F)V",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av0(net.minecraft.world.entity.Entity a0,float a1,CallbackInfo ci){if(dev.starcrown.DivineCombat.reject(a0,"AV.Content.Item.EntityRemover.FindSetterMethod.SetHealth")){ci.cancel();}}
}
