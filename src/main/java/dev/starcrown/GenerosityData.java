package dev.starcrown;
import net.minecraft.network.syncher.*;
import net.minecraft.world.entity.LivingEntity;
/** Initialized from LivingEntity's class initializer before subclass data IDs are allocated. */
public final class GenerosityData {
 public static final EntityDataAccessor<Boolean> BLESSED=SynchedEntityData.defineId(LivingEntity.class,EntityDataSerializers.BOOLEAN);
 public static void init(){}
}
