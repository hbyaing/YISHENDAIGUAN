package dev.starcrown.mixin;
import dev.starcrown.DivineProtection;
import net.minecraft.world.entity.*;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(LivingEntity.class)
public abstract class LivingGuard {
 @Inject(method={"hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z","m_6469_(Lnet/minecraft/world/damagesource/DamageSource;F)Z"},at=@At("RETURN"),remap=false,require=1)
 private void costActual(DamageSource source,float amount,CallbackInfoReturnable<Boolean> ci){dev.starcrown.DivineCost.finish((LivingEntity)(Object)this);}

 @ModifyVariable(method={"setHealth(F)V","m_21153_(F)V"},at=@At("HEAD"),argsOnly=true,ordinal=0,remap=false,require=1)
 private float sevenHealth(float next){return dev.starcrown.DivineSeven.healthWrite((LivingEntity)(Object)this,next);}
 @Inject(method={"getMaxHealth()F","m_21233_()F"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void divineMaximum(CallbackInfoReturnable<Float> ci){if(DivineProtection.active((Entity)(Object)this))ci.setReturnValue(dev.starcrown.DivineCombat.maximum((LivingEntity)(Object)this));}

 @Inject(method={"setHealth(F)V","m_21153_(F)V"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void divineHealth(float value,CallbackInfo ci){if(DivineProtection.blockHealth((LivingEntity)(Object)this,value))ci.cancel();}
 @Inject(method={"getHealth()F","m_21223_()F"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void divineDisplay(CallbackInfoReturnable<Float> cir){if(DivineProtection.active((Entity)(Object)this))cir.setReturnValue(DivineProtection.displayHealth((LivingEntity)(Object)this));}
 @Inject(method={"die(Lnet/minecraft/world/damagesource/DamageSource;)V","m_6667_(Lnet/minecraft/world/damagesource/DamageSource;)V"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void divineDeath(DamageSource source,CallbackInfo ci){if(DivineProtection.blockDeath((Entity)(Object)this,"LivingEntity.die"))ci.cancel();}
 @Inject(method={"kill()V","m_6074_()V"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void divineKill(CallbackInfo ci){if(dev.starcrown.YuleHumility.kill((Entity)(Object)this)||DivineProtection.blockDeath((Entity)(Object)this,"LivingEntity.kill"))ci.cancel();}
 @Inject(method={"remove(Lnet/minecraft/world/entity/Entity$RemovalReason;)V","m_142687_(Lnet/minecraft/world/entity/Entity$RemovalReason;)V"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void divineRemove(Entity.RemovalReason reason,CallbackInfo ci){if(DivineProtection.blockRemoval((Entity)(Object)this,reason,"LivingEntity.remove"))ci.cancel();}

 @Inject(method={"addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z","m_147207_(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void divineDebuff(net.minecraft.world.effect.MobEffectInstance effect,Entity source,CallbackInfoReturnable<Boolean> cir){if((DivineProtection.active((Entity)(Object)this)&&dev.starcrown.DivineWill.unwanted(effect))||(dev.starcrown.YuleChastity.active((Entity)(Object)this)&&dev.starcrown.YuleChastity.unwanted(effect)))cir.setReturnValue(false);}
 @Inject(method={"forceAddEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)V","m_147215_(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)V"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void divineForcedDebuff(net.minecraft.world.effect.MobEffectInstance effect,Entity source,CallbackInfo ci){if((DivineProtection.active((Entity)(Object)this)&&dev.starcrown.DivineWill.unwanted(effect))||(dev.starcrown.YuleChastity.active((Entity)(Object)this)&&dev.starcrown.YuleChastity.unwanted(effect)))ci.cancel();}
}
