package dev.starcrown.mixin;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(net.minecraft.network.syncher.SynchedEntityData.class)
public abstract class VitalDataGuard {
 @Unique private net.minecraft.world.entity.Entity divine$owner;
 @Inject(method="<init>",at=@At("RETURN"),remap=false,require=1)
 private void divineOwner(net.minecraft.world.entity.Entity owner,CallbackInfo ci){divine$owner=owner;}
 @Inject(method={"set(Lnet/minecraft/network/syncher/EntityDataAccessor;Ljava/lang/Object;Z)V","m_276349_(Lnet/minecraft/network/syncher/EntityDataAccessor;Ljava/lang/Object;Z)V"},at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private void divineData(net.minecraft.network.syncher.EntityDataAccessor<?> key,Object value,boolean force,CallbackInfo ci){if(dev.starcrown.DivineCombat.blockData(divine$owner,key,value))ci.cancel();}
}
