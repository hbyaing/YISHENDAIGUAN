package dev.starcrown.mixin;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Pseudo
@Mixin(targets="com.erchien.arcanevortex.Content.Item.EntityRemover.Pig2Remover",priority=2000,remap=false)
public abstract class ArcaneGuard8 {
 @Inject(method="removePig2(Lnet/minecraft/world/entity/Entity;)Z",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av0(net.minecraft.world.entity.Entity a0,CallbackInfoReturnable<Boolean> ci){if(dev.starcrown.DivineCombat.reject(a0,"AV.Content.Item.EntityRemover.Pig2Remover.removePig2")){ci.setReturnValue(false);}}
}
