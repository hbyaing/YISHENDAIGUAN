package dev.starcrown.mixin;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Pseudo
@Mixin(targets="com.erchien.arcanevortex.Help.EntityKillerHelp",priority=2000,remap=false)
public abstract class ArcaneGuard0 {
 @Inject(method="killEntity(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/player/Player;)V",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av0(net.minecraft.world.entity.Entity a0,net.minecraft.world.entity.player.Player a1,CallbackInfo ci){if(dev.starcrown.DivineCombat.reject(a0,"AV.Help.EntityKillerHelp.killEntity")){ci.cancel();}}
 @Inject(method="kill(Lnet/minecraft/world/entity/Entity;)V",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av1(net.minecraft.world.entity.Entity a0,CallbackInfo ci){if(dev.starcrown.DivineCombat.reject(a0,"AV.Help.EntityKillerHelp.kill")){ci.cancel();}}
 @Inject(method="setEntityPos(Lnet/minecraft/world/entity/Entity;)Z",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av2(net.minecraft.world.entity.Entity a0,CallbackInfoReturnable<Boolean> ci){if(dev.starcrown.DivineCombat.reject(a0,"AV.Help.EntityKillerHelp.setEntityPos")){ci.setReturnValue(false);}}
 @Inject(method="setEntityPosReflection(Lnet/minecraft/world/entity/Entity;)Z",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av3(net.minecraft.world.entity.Entity a0,CallbackInfoReturnable<Boolean> ci){if(dev.starcrown.DivineCombat.reject(a0,"AV.Help.EntityKillerHelp.setEntityPosReflection")){ci.setReturnValue(false);}}
 @Inject(method="removeServerLevelEntity(Lnet/minecraft/world/entity/Entity;)Z",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av4(net.minecraft.world.entity.Entity a0,CallbackInfoReturnable<Boolean> ci){if(dev.starcrown.DivineCombat.reject(a0,"AV.Help.EntityKillerHelp.removeServerLevelEntity")){ci.setReturnValue(false);}}
 @Inject(method="deleteEntity(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/Level;)Z",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av5(net.minecraft.world.entity.Entity a0,net.minecraft.world.level.Level a1,CallbackInfoReturnable<Boolean> ci){if(dev.starcrown.DivineCombat.reject(a0,"AV.Help.EntityKillerHelp.deleteEntity")){ci.setReturnValue(false);}}
 @Inject(method="setRemoved(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity$RemovalReason;)Z",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av6(net.minecraft.world.entity.Entity a0,net.minecraft.world.entity.Entity.RemovalReason a1,CallbackInfoReturnable<Boolean> ci){if(dev.starcrown.DivineCombat.reject(a0,"AV.Help.EntityKillerHelp.setRemoved")){ci.setReturnValue(false);}}
 @Inject(method="remove(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/player/Player;)Z",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av7(net.minecraft.world.entity.Entity a0,net.minecraft.world.entity.player.Player a1,CallbackInfoReturnable<Boolean> ci){if(dev.starcrown.DivineCombat.reject(a0,"AV.Help.EntityKillerHelp.remove")){ci.setReturnValue(false);}}
 @Inject(method="AsynchronousRemove(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/player/Player;)V",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av8(net.minecraft.world.entity.Entity a0,net.minecraft.world.entity.player.Player a1,CallbackInfo ci){if(dev.starcrown.DivineCombat.reject(a0,"AV.Help.EntityKillerHelp.AsynchronousRemove")){ci.cancel();}}
 @Inject(method="tickDeath(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/player/Player;)V",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av9(net.minecraft.world.entity.Entity a0,net.minecraft.world.entity.player.Player a1,CallbackInfo ci){if(dev.starcrown.DivineCombat.reject(a0,"AV.Help.EntityKillerHelp.tickDeath")){ci.cancel();}}
 @Inject(method="setFieldValue(Ljava/lang/Object;[Ljava/lang/String;Ljava/lang/Object;)Z",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av10(java.lang.Object a0,java.lang.String[] a1,java.lang.Object a2,CallbackInfoReturnable<Boolean> ci){if(dev.starcrown.DivineCombat.reject(a0,"AV.Help.EntityKillerHelp.setFieldValue")){ci.setReturnValue(false);}}
}
