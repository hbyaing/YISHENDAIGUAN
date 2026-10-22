package dev.starcrown.mixin;
import dev.starcrown.YuleHumility;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonStrafePlayerPhase;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(DragonStrafePlayerPhase.class)
public abstract class HumilityDragon {
 @Shadow(aliases={"f_31353_"},remap=false) private LivingEntity attackTarget;
 @Inject(method={"setTarget(Lnet/minecraft/world/entity/LivingEntity;)V","m_31358_(Lnet/minecraft/world/entity/LivingEntity;)V"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void humilityDragonTarget(LivingEntity target,CallbackInfo ci){if(YuleHumility.active(target)){attackTarget=null;ci.cancel();}}
 @Inject(method={"doServerTick()V","m_6989_()V"},at=@At("HEAD"),remap=false,require=1)
 private void humilityDragonTick(CallbackInfo ci){if(YuleHumility.active(attackTarget))attackTarget=null;}
}
