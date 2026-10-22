package dev.starcrown.mixin;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(net.minecraft.network.syncher.SynchedEntityData.class)
public abstract class VitalDataGuard {
 @ModifyVariable(method={"set(Lnet/minecraft/network/syncher/EntityDataAccessor;Ljava/lang/Object;Z)V","m_276349_(Lnet/minecraft/network/syncher/EntityDataAccessor;Ljava/lang/Object;Z)V"},at=@At("HEAD"),argsOnly=true,ordinal=0,remap=false,require=1)
 private Object humilityData(Object value,net.minecraft.network.syncher.EntityDataAccessor<?> key,Object original,boolean force){return dev.starcrown.DivineCombat.humilityData(divine$owner,key,value);}
 @Unique private net.minecraft.world.entity.Entity divine$owner;
 @Inject(method="<init>",at=@At("RETURN"),remap=false,require=1)
 private void divineOwner(net.minecraft.world.entity.Entity owner,CallbackInfo ci){divine$owner=owner;}
 @Inject(method={"set(Lnet/minecraft/network/syncher/EntityDataAccessor;Ljava/lang/Object;Z)V","m_276349_(Lnet/minecraft/network/syncher/EntityDataAccessor;Ljava/lang/Object;Z)V"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void divineData(net.minecraft.network.syncher.EntityDataAccessor<?> key,Object value,boolean force,CallbackInfo ci){if(dev.starcrown.DivineCombat.blockData(divine$owner,key,value))ci.cancel();}
}
