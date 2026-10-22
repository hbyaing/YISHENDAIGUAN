package dev.starcrown.mixin;
import dev.starcrown.DivineEternity;
import net.minecraft.world.entity.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(Mob.class)
public abstract class MobStillness {
 // Suspend execution, not the persisted NoAI flag. No protected-field alias is needed.
 @Inject(method={"isEffectiveAi()Z","m_21515_()Z"},at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private void eternityEffectiveAi(CallbackInfoReturnable<Boolean> ci){if(DivineEternity.sealed((Mob)(Object)this))ci.setReturnValue(false);}
 @Inject(method={"serverAiStep()V","m_6140_()V"},at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private void eternityAi(CallbackInfo ci){if(DivineEternity.sealed((Mob)(Object)this)){DivineEternity.stop((Mob)(Object)this);ci.cancel();}}
 @Inject(method={"setTarget(Lnet/minecraft/world/entity/LivingEntity;)V","m_6710_(Lnet/minecraft/world/entity/LivingEntity;)V"},at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private void eternityTarget(LivingEntity target,CallbackInfo ci){if(target!=null&&DivineEternity.sealed((Mob)(Object)this))ci.cancel();}
}
