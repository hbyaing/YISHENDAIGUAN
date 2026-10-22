package dev.starcrown.mixin;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Pseudo
@Mixin(targets="com.erchien.arcanevortex.Content.Item.VanSh.VanShCrusher",priority=2000,remap=false)
public abstract class ArcaneGuard11 {
 @Inject(method="remove(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/Level;)Z",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av0(net.minecraft.world.entity.Entity a0,net.minecraft.world.level.Level a1,CallbackInfoReturnable<Boolean> ci){if(dev.starcrown.DivineCombat.rejectCompatibility(a0,"AV.Content.Item.VanSh.VanShCrusher.remove")){ci.setReturnValue(false);}}
}
