package dev.starcrown.mixin;
import dev.starcrown.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.network.syncher.EntityDataAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(LivingEntity.class)
public abstract class GenerosityLiving {
 @Inject(method="<clinit>",at=@At("TAIL")) private static void init(CallbackInfo ci){GenerosityData.init();}
 @Inject(method={"defineSynchedData()V","m_8097_()V"},at=@At("TAIL"),remap=false,require=1)
 private void define(CallbackInfo ci){((LivingEntity)(Object)this).getEntityData().define(GenerosityData.BLESSED,false);}
 @Inject(method={"onSyncedDataUpdated(Lnet/minecraft/network/syncher/EntityDataAccessor;)V","m_7350_(Lnet/minecraft/network/syncher/EntityDataAccessor;)V"},at=@At("TAIL"),remap=false,require=1)
 private void sync(EntityDataAccessor<?> key,CallbackInfo ci){if(key.equals(GenerosityData.BLESSED))YuleGenerosity.bind((LivingEntity)(Object)this);}
 @Inject(method={"take(Lnet/minecraft/world/entity/Entity;I)V","m_7938_(Lnet/minecraft/world/entity/Entity;I)V"},at=@At("HEAD"),remap=false,require=1)
 private void pickup(Entity item,int count,CallbackInfo ci){if(item instanceof ItemEntity gift)YuleGenerosity.received((LivingEntity)(Object)this,gift,count);}
}
