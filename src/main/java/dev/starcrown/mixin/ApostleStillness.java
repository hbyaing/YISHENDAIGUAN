package dev.starcrown.mixin;
import dev.starcrown.DivineEternity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/** Goety/Revelation execute attacks outside vanilla goal selectors. Suspend their action tick too. */
@Pseudo
@Mixin(targets="com.Polarice3.Goety.common.entities.boss.Apostle",priority=2000,remap=false)
public abstract class ApostleStillness {
 @Inject(method={"tick()V","m_8119_()V","aiStep()V","m_8107_()V"},at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private void eternityApostle(CallbackInfo ci){if(DivineEternity.sealed((Mob)(Object)this)){DivineEternity.stop((Mob)(Object)this);ci.cancel();}}
}
