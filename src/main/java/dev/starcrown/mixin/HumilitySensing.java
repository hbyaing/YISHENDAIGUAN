package dev.starcrown.mixin;
import dev.starcrown.YuleHumility;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.sensing.Sensing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(Sensing.class)
public abstract class HumilitySensing {
 @Inject(method={"hasLineOfSight(Lnet/minecraft/world/entity/Entity;)Z","m_148306_(Lnet/minecraft/world/entity/Entity;)Z"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void humilitySight(Entity target,CallbackInfoReturnable<Boolean> ci){if(YuleHumility.active(target))ci.setReturnValue(false);}
}
