package dev.starcrown;
import net.minecraft.world.effect.*;
import net.minecraftforge.registries.*;
public final class DivineEffects {
 public static final DeferredRegister<MobEffect> EFFECTS=DeferredRegister.create(ForgeRegistries.MOB_EFFECTS,StarcrownDoll.ID);
 public static final RegistryObject<MobEffect> COST=EFFECTS.register("cost",Cost::new);
 private static final class Cost extends MobEffect {Cost(){super(MobEffectCategory.HARMFUL,0xd55af5);}}
}
