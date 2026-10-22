package dev.starcrown.mixin;
import dev.starcrown.DivineProtection;
import net.minecraft.world.entity.*;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(Entity.class)
public abstract class EntityGuard {
 @Inject(method={"move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V","m_6478_(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V"},at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private void danceMove(MoverType type,net.minecraft.world.phys.Vec3 delta,CallbackInfo ci){if((Object)this instanceof Mob mob&&dev.starcrown.DollDance.controlled(mob))ci.cancel();}
 @Inject(method={"remove(Lnet/minecraft/world/entity/Entity$RemovalReason;)V","m_142687_(Lnet/minecraft/world/entity/Entity$RemovalReason;)V","setRemoved(Lnet/minecraft/world/entity/Entity$RemovalReason;)V","m_142467_(Lnet/minecraft/world/entity/Entity$RemovalReason;)V"},at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private void divineRemove(Entity.RemovalReason reason,CallbackInfo ci){if(DivineProtection.blockRemoval((Entity)(Object)this,reason,"Entity.remove/setRemoved"))ci.cancel();}
}
