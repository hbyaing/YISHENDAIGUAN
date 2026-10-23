package dev.starcrownqa;
import net.minecraft.world.effect.*;
import net.minecraftforge.registries.*;
import net.minecraftforge.fml.common.Mod;
@Mod.EventBusSubscriber(modid="starcrownqa",bus=Mod.EventBusSubscriber.Bus.MOD)
public final class BenefitFixture {
 public static MobEffect BENEFIT;
 @net.minecraftforge.eventbus.api.SubscribeEvent public static void register(RegisterEvent e){
  e.register(ForgeRegistries.Keys.MOB_EFFECTS,h->{BENEFIT=new MobEffect(MobEffectCategory.BENEFICIAL,0x00ffff){};h.register(new net.minecraft.resources.ResourceLocation("starcrownqa","ordinary_benefit"),BENEFIT);});
 }
}
