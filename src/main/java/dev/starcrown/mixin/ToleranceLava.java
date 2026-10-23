package dev.starcrown.mixin;
import dev.starcrown.YuleHazards;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(Entity.class)
public abstract class ToleranceLava {
 @Inject(method={"lavaHurt()V","m_20093_()V"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void harmlessLava(CallbackInfo ci){var e=(Entity)(Object)this;if(e.level() instanceof ServerLevel level&&YuleHazards.get(level).contact(e)!=null){e.clearFire();ci.cancel();}}
}
