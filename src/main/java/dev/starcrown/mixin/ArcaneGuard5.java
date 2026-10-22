package dev.starcrown.mixin;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Pseudo
@Mixin(targets="com.erchien.arcanevortex.Help.TrueDamageHelper",priority=2000,remap=false)
public abstract class ArcaneGuard5 {
 @Inject(method="trueDamage(Lnet/minecraft/world/entity/LivingEntity;FLnet/minecraft/world/entity/Entity;)Z",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av0(net.minecraft.world.entity.LivingEntity a0,float a1,net.minecraft.world.entity.Entity a2,CallbackInfoReturnable<Boolean> ci){if(dev.starcrown.DivineCombat.rejectCompatibility(a0,"AV.Help.TrueDamageHelper.trueDamage")){ci.setReturnValue(false);}}
 @Inject(method="trueDamage(Lnet/minecraft/world/entity/LivingEntity;F)Z",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av1(net.minecraft.world.entity.LivingEntity a0,float a1,CallbackInfoReturnable<Boolean> ci){if(dev.starcrown.DivineCombat.rejectCompatibility(a0,"AV.Help.TrueDamageHelper.trueDamage")){ci.setReturnValue(false);}}
 @Inject(method="instantKill(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/Entity;)Z",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av2(net.minecraft.world.entity.LivingEntity a0,net.minecraft.world.entity.Entity a1,CallbackInfoReturnable<Boolean> ci){if(dev.starcrown.DivineCombat.rejectCompatibility(a0,"AV.Help.TrueDamageHelper.instantKill")){ci.setReturnValue(false);}}
 @Inject(method="playerTrueDamageAttack(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/player/Player;F)V",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av3(net.minecraft.world.entity.LivingEntity a0,net.minecraft.world.entity.player.Player a1,float a2,CallbackInfo ci){if(dev.starcrown.DivineCombat.rejectCompatibility(a0,"AV.Help.TrueDamageHelper.playerTrueDamageAttack")){ci.cancel();}}
 @Inject(method="playerTrueDamageAttack(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/player/Player;FZ)V",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av4(net.minecraft.world.entity.LivingEntity a0,net.minecraft.world.entity.player.Player a1,float a2,boolean a3,CallbackInfo ci){if(dev.starcrown.DivineCombat.rejectCompatibility(a0,"AV.Help.TrueDamageHelper.playerTrueDamageAttack")){ci.cancel();}}
 @Inject(method="playerTrueDamageAttack(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/player/Player;FZLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundEvent;)V",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av5(net.minecraft.world.entity.LivingEntity a0,net.minecraft.world.entity.player.Player a1,float a2,boolean a3,net.minecraft.sounds.SoundEvent a4,net.minecraft.sounds.SoundEvent a5,CallbackInfo ci){if(dev.starcrown.DivineCombat.rejectCompatibility(a0,"AV.Help.TrueDamageHelper.playerTrueDamageAttack")){ci.cancel();}}
 @Inject(method="playerTrueDamageAttack(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/player/Player;FZLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundEvent;I)V",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av6(net.minecraft.world.entity.LivingEntity a0,net.minecraft.world.entity.player.Player a1,float a2,boolean a3,net.minecraft.sounds.SoundEvent a4,net.minecraft.sounds.SoundEvent a5,net.minecraft.sounds.SoundEvent a6,int a7,CallbackInfo ci){if(dev.starcrown.DivineCombat.rejectCompatibility(a0,"AV.Help.TrueDamageHelper.playerTrueDamageAttack")){ci.cancel();}}
 @Inject(method="dealTrueDamage(Lnet/minecraft/world/entity/LivingEntity;FLnet/minecraft/world/damagesource/DamageSource;)Z",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av7(net.minecraft.world.entity.LivingEntity a0,float a1,net.minecraft.world.damagesource.DamageSource a2,CallbackInfoReturnable<Boolean> ci){if(dev.starcrown.DivineCombat.rejectCompatibility(a0,"AV.Help.TrueDamageHelper.dealTrueDamage")){ci.setReturnValue(false);}}
 @Inject(method="forceSetHealthSynced(Lnet/minecraft/world/entity/LivingEntity;F)V",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av8(net.minecraft.world.entity.LivingEntity a0,float a1,CallbackInfo ci){if(dev.starcrown.DivineCombat.rejectCompatibility(a0,"AV.Help.TrueDamageHelper.forceSetHealthSynced")){ci.cancel();}}
 @Inject(method="dealTrueDamage(Lnet/minecraft/world/entity/LivingEntity;FLnet/minecraft/world/entity/Entity;)Z",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av9(net.minecraft.world.entity.LivingEntity a0,float a1,net.minecraft.world.entity.Entity a2,CallbackInfoReturnable<Boolean> ci){if(dev.starcrown.DivineCombat.rejectCompatibility(a0,"AV.Help.TrueDamageHelper.dealTrueDamage")){ci.setReturnValue(false);}}
 @Inject(method="dealTrueDamage(Lnet/minecraft/world/entity/LivingEntity;F)Z",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av10(net.minecraft.world.entity.LivingEntity a0,float a1,CallbackInfoReturnable<Boolean> ci){if(dev.starcrown.DivineCombat.rejectCompatibility(a0,"AV.Help.TrueDamageHelper.dealTrueDamage")){ci.setReturnValue(false);}}
 @Inject(method="instantKill(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/damagesource/DamageSource;)Z",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av11(net.minecraft.world.entity.LivingEntity a0,net.minecraft.world.damagesource.DamageSource a1,CallbackInfoReturnable<Boolean> ci){if(dev.starcrown.DivineCombat.rejectCompatibility(a0,"AV.Help.TrueDamageHelper.instantKill")){ci.setReturnValue(false);}}
 @Inject(method="handleDeath(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/damagesource/DamageSource;F)V",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av12(net.minecraft.world.entity.LivingEntity a0,net.minecraft.world.damagesource.DamageSource a1,float a2,CallbackInfo ci){if(dev.starcrown.DivineCombat.rejectCompatibility(a0,"AV.Help.TrueDamageHelper.handleDeath")){ci.cancel();}}
 @Inject(method="updatePlayerStats(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/damagesource/DamageSource;F)V",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av13(net.minecraft.world.entity.LivingEntity a0,net.minecraft.world.damagesource.DamageSource a1,float a2,CallbackInfo ci){if(dev.starcrown.DivineCombat.rejectCompatibility(a0,"AV.Help.TrueDamageHelper.updatePlayerStats")){ci.cancel();}}
}
