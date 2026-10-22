package dev.starcrown.mixin;
import dev.starcrown.DivineProtection;
import net.minecraft.world.entity.*;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(Entity.class)
public abstract class EntityGuard {
 @Inject(method={"teleportTo(DDD)V","m_6021_(DDD)V"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void sevenTeleport(double x,double y,double z,CallbackInfo ci){if(dev.starcrown.DivineSeven.ruled((Entity)(Object)this))ci.cancel();}

 @Inject(method={"makeStuckInBlock(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/phys/Vec3;)V","m_7601_(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/phys/Vec3;)V"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void sevenUnstuck(net.minecraft.world.level.block.state.BlockState state,net.minecraft.world.phys.Vec3 speed,CallbackInfo ci){if((Object)this instanceof net.minecraft.world.entity.player.Player p&&dev.starcrown.DivineWill.wearing(p))ci.cancel();}

 @Inject(method={"isIgnoringBlockTriggers()Z","m_6090_()Z"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void humilityTriggers(CallbackInfoReturnable<Boolean> ci){if(dev.starcrown.YuleHumility.active((Entity)(Object)this))ci.setReturnValue(true);}
 @Inject(method={"move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V","m_6478_(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V"},at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private void danceMove(MoverType type,net.minecraft.world.phys.Vec3 delta,CallbackInfo ci){if((Object)this instanceof Mob mob&&dev.starcrown.DollDance.controlled(mob))ci.cancel();}
 @Inject(method={"remove(Lnet/minecraft/world/entity/Entity$RemovalReason;)V","m_142687_(Lnet/minecraft/world/entity/Entity$RemovalReason;)V","setRemoved(Lnet/minecraft/world/entity/Entity$RemovalReason;)V","m_142467_(Lnet/minecraft/world/entity/Entity$RemovalReason;)V"},at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private void divineRemove(Entity.RemovalReason reason,CallbackInfo ci){if(DivineProtection.blockRemoval((Entity)(Object)this,reason,"Entity.remove/setRemoved"))ci.cancel();}
}
