package dev.starcrown.mixin;
import dev.starcrown.YuleHumility;
import net.minecraft.world.entity.*;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(LivingEntity.class)
public abstract class HumilityLiving {
 @ModifyVariable(method={"hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z","m_6469_(Lnet/minecraft/world/damagesource/DamageSource;F)Z"},at=@At("HEAD"),argsOnly=true,ordinal=0,remap=false,require=1)
 private float humilityDamage(float amount,DamageSource source,float original){return YuleHumility.damage((LivingEntity)(Object)this,source,amount);}
 @ModifyVariable(method={"setHealth(F)V","m_21153_(F)V"},at=@At("HEAD"),argsOnly=true,ordinal=0,remap=false,require=1)
 private float humilityHealth(float next){return YuleHumility.healthWrite((LivingEntity)(Object)this,next);}
 @Inject(method={"canBeSeenAsEnemy()Z","m_142066_()Z","canBeSeenByAnyone()Z","m_142065_()Z"},at=@At("HEAD"),cancellable=true,remap=false,require=2)
 private void humilitySeen(CallbackInfoReturnable<Boolean> ci){if(YuleHumility.active((Entity)(Object)this))ci.setReturnValue(false);}
}
