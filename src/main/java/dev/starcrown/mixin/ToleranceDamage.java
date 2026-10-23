package dev.starcrown.mixin;
import dev.starcrown.YuleTolerance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.vehicle.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin({Entity.class,LivingEntity.class,Player.class,ServerPlayer.class,ArmorStand.class,Boat.class,AbstractMinecart.class,ItemEntity.class,EndCrystal.class})
public abstract class ToleranceDamage {
 @Inject(method={"hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z","m_6469_(Lnet/minecraft/world/damagesource/DamageSource;F)Z"},at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private void peace(DamageSource source,float amount,CallbackInfoReturnable<Boolean> ci){if(dev.starcrown.YuleTolerance.blockDamage((Entity)(Object)this,source))ci.setReturnValue(false);}
}
