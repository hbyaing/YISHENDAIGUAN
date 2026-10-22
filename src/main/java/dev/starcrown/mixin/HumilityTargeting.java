package dev.starcrown.mixin;
import dev.starcrown.YuleHumility;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(TargetingConditions.class)
public abstract class HumilityTargeting {
 @Inject(method={"test(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/LivingEntity;)Z","m_26885_(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/LivingEntity;)Z"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void humilityTarget(LivingEntity observer,LivingEntity target,CallbackInfoReturnable<Boolean> ci){if(YuleHumility.active(target))ci.setReturnValue(false);}
}
