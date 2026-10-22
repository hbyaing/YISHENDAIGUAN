package dev.starcrown.mixin;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Pseudo
@Mixin(targets="com.erchien.arcanevortex.Help.HealthModifyHelp.Traversal",priority=2000,remap=false)
public abstract class ArcaneGuard2 {
 @Inject(method="modifyHealthStable(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/player/Player;FFZ)Z",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av0(net.minecraft.world.entity.LivingEntity a0,net.minecraft.world.entity.player.Player a1,float a2,float a3,boolean a4,CallbackInfoReturnable<Boolean> ci){if(dev.starcrown.DivineCombat.reject(a0,"AV.Help.HealthModifyHelp.Traversal.modifyHealthStable")){ci.setReturnValue(false);}}
}
