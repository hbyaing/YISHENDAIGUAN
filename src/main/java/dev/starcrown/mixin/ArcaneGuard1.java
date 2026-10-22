package dev.starcrown.mixin;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Pseudo
@Mixin(targets="com.erchien.arcanevortex.Help.HealthModifyHelp.HealthModify",priority=2000,remap=false)
public abstract class ArcaneGuard1 {
 @Inject(method="entityDataModify(Lnet/minecraft/world/entity/Entity;F)Z",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av0(net.minecraft.world.entity.Entity a0,float a1,CallbackInfoReturnable<Boolean> ci){if(dev.starcrown.DivineCombat.reject(a0,"AV.Help.HealthModifyHelp.HealthModify.entityDataModify")){ci.setReturnValue(false);}}
 @Inject(method="traversalModify(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;FZ)Z",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av1(net.minecraft.world.entity.Entity a0,net.minecraft.world.entity.Entity a1,float a2,boolean a3,CallbackInfoReturnable<Boolean> ci){if(dev.starcrown.DivineCombat.reject(a0,"AV.Help.HealthModifyHelp.HealthModify.traversalModify")){ci.setReturnValue(false);}}
 @Inject(method="traversalModify(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;F)Z",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av2(net.minecraft.world.entity.Entity a0,net.minecraft.world.entity.Entity a1,float a2,CallbackInfoReturnable<Boolean> ci){if(dev.starcrown.DivineCombat.reject(a0,"AV.Help.HealthModifyHelp.HealthModify.traversalModify")){ci.setReturnValue(false);}}
 @Inject(method="forceModify(Lnet/minecraft/world/entity/Entity;FF)Z",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av3(net.minecraft.world.entity.Entity a0,float a1,float a2,CallbackInfoReturnable<Boolean> ci){if(dev.starcrown.DivineCombat.reject(a0,"AV.Help.HealthModifyHelp.HealthModify.forceModify")){ci.setReturnValue(false);}}
 @Inject(method="tagModify(Lnet/minecraft/world/entity/Entity;F)Z",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av4(net.minecraft.world.entity.Entity a0,float a1,CallbackInfoReturnable<Boolean> ci){if(dev.starcrown.DivineCombat.reject(a0,"AV.Help.HealthModifyHelp.HealthModify.tagModify")){ci.setReturnValue(false);}}
 @Inject(method="attributeModifier(Lnet/minecraft/world/entity/Entity;F)Z",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av5(net.minecraft.world.entity.Entity a0,float a1,CallbackInfoReturnable<Boolean> ci){if(dev.starcrown.DivineCombat.reject(a0,"AV.Help.HealthModifyHelp.HealthModify.attributeModifier")){ci.setReturnValue(false);}}
}
