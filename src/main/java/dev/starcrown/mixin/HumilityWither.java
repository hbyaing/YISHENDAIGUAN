package dev.starcrown.mixin;
import dev.starcrown.YuleHumility;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(WitherBoss.class)
public abstract class HumilityWither {
 @ModifyVariable(method={"setAlternativeTarget(II)V","m_31454_(II)V"},at=@At("HEAD"),argsOnly=true,ordinal=1,remap=false,require=1)
 private int humilityHead(int entityId){return YuleHumility.active(((WitherBoss)(Object)this).level().getEntity(entityId))?0:entityId;}
 @Inject(method={"getAlternativeTarget(I)I","m_31512_(I)I"},at=@At("RETURN"),cancellable=true,remap=false,require=1)
 private void humilityExistingHead(int head,CallbackInfoReturnable<Integer> ci){if(YuleHumility.active(((WitherBoss)(Object)this).level().getEntity(ci.getReturnValue())))ci.setReturnValue(0);}
 @Inject(method={"performRangedAttack(ILnet/minecraft/world/entity/LivingEntity;)V","m_31457_(ILnet/minecraft/world/entity/LivingEntity;)V"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void humilitySkull(int head,LivingEntity target,CallbackInfo ci){if(YuleHumility.active(target))ci.cancel();}
 @Inject(method={"performRangedAttack(Lnet/minecraft/world/entity/LivingEntity;F)V","m_6504_(Lnet/minecraft/world/entity/LivingEntity;F)V"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void humilityRanged(LivingEntity target,float distance,CallbackInfo ci){if(YuleHumility.active(target))ci.cancel();}
}
