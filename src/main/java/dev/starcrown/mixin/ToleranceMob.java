package dev.starcrown.mixin;
import dev.starcrown.YuleTolerance;
import net.minecraft.world.entity.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(Mob.class)
public abstract class ToleranceMob {
 @Inject(method={"setTarget(Lnet/minecraft/world/entity/LivingEntity;)V","m_6710_(Lnet/minecraft/world/entity/LivingEntity;)V"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void peacefulTarget(LivingEntity target,CallbackInfo ci){if(target!=null&&YuleTolerance.friendly((Entity)(Object)this))ci.cancel();}
 @Inject(method={"doHurtTarget(Lnet/minecraft/world/entity/Entity;)Z","m_7327_(Lnet/minecraft/world/entity/Entity;)Z"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void peacefulAttack(Entity target,CallbackInfoReturnable<Boolean> ci){if(YuleTolerance.friendly((Entity)(Object)this))ci.setReturnValue(false);}
}
