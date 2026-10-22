package dev.starcrown.mixin;
import dev.starcrown.YuleHumility;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
@Mixin({Player.class,ServerPlayer.class})
public abstract class HumilityPlayerDamage {
 @ModifyVariable(method={"hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z","m_6469_(Lnet/minecraft/world/damagesource/DamageSource;F)Z"},at=@At("HEAD"),argsOnly=true,ordinal=0,remap=false,require=1)
 private float humilityDamage(float amount,DamageSource source,float original){return YuleHumility.damage((LivingEntity)(Object)this,source,amount);}
}
