package dev.starcrown.mixin;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Pseudo
@Mixin(targets="com.erchien.arcanevortex.Help.HealthModifyHelp.TraversalUnstable",priority=2000,remap=false)
public abstract class ArcaneGuard3 {
 @Inject(method="smartHealthModify(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/player/Player;FFZ)V",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av0(net.minecraft.world.entity.LivingEntity a0,net.minecraft.world.entity.player.Player a1,float a2,float a3,boolean a4,CallbackInfo ci){if(dev.starcrown.DivineCombat.rejectCompatibility(a0,"AV.Help.HealthModifyHelp.TraversalUnstable.smartHealthModify")){ci.cancel();}}
 @Inject(method="lockHealthMode(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/player/Player;F)V",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av1(net.minecraft.world.entity.LivingEntity a0,net.minecraft.world.entity.player.Player a1,float a2,CallbackInfo ci){if(dev.starcrown.DivineCombat.rejectCompatibility(a0,"AV.Help.HealthModifyHelp.TraversalUnstable.lockHealthMode")){ci.cancel();}}
 @Inject(method="forceHealthSync(Lnet/minecraft/world/entity/LivingEntity;)V",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av2(net.minecraft.world.entity.LivingEntity a0,CallbackInfo ci){if(dev.starcrown.DivineCombat.rejectCompatibility(a0,"AV.Help.HealthModifyHelp.TraversalUnstable.forceHealthSync")){ci.cancel();}}
}
