package dev.starcrown.mixin;
import dev.starcrown.YuleTolerance;
import net.minecraft.world.entity.*;
import net.minecraft.world.effect.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(LivingEntity.class)
public abstract class ToleranceEffects {
 @Inject(method={"canBeSeenAsEnemy()Z","m_142066_()Z"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void friendlyNotEnemy(CallbackInfoReturnable<Boolean> ci){if(YuleTolerance.friendly((Entity)(Object)this))ci.setReturnValue(false);}
 @Inject(method={"addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z","m_147207_(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void noHarmfulEffects(MobEffectInstance effect,Entity source,CallbackInfoReturnable<Boolean> ci){if(effect.getEffect().getCategory()!=MobEffectCategory.BENEFICIAL&&YuleTolerance.harmless(source))ci.setReturnValue(false);}
}
