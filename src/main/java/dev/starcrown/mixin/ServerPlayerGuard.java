package dev.starcrown.mixin;
import dev.starcrown.DivineProtection;
import net.minecraft.world.entity.*;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(net.minecraft.server.level.ServerPlayer.class)
public abstract class ServerPlayerGuard {
 @Inject(method={"die(Lnet/minecraft/world/damagesource/DamageSource;)V","m_6667_(Lnet/minecraft/world/damagesource/DamageSource;)V"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void divineDeath(DamageSource source,CallbackInfo ci){if(DivineProtection.blockDeath((Entity)(Object)this,"ServerPlayerGuard.die"))ci.cancel();}
}
