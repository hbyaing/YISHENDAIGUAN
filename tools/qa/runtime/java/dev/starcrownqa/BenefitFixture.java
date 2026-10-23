package dev.starcrownqa;
import net.minecraft.world.effect.*;
import net.minecraftforge.registries.*;
import net.minecraftforge.fml.common.Mod;
@Mod.EventBusSubscriber(modid="starcrownqa",bus=Mod.EventBusSubscriber.Bus.MOD)
public final class BenefitFixture {
 public static MobEffect BENEFIT,CURSE,NEUTRAL,BAD;
 @net.minecraftforge.eventbus.api.SubscribeEvent public static void register(RegisterEvent e){
  e.register(ForgeRegistries.Keys.MOB_EFFECTS,h->{BENEFIT=new MobEffect(MobEffectCategory.BENEFICIAL,0x00ffff){};h.register(new net.minecraft.resources.ResourceLocation("starcrownqa","ordinary_benefit"),BENEFIT);CURSE=new MobEffect(MobEffectCategory.BENEFICIAL,0xff0000){};NEUTRAL=new MobEffect(MobEffectCategory.NEUTRAL,0x440044){};BAD=new MobEffect(MobEffectCategory.HARMFUL,0x220000){};h.register(new net.minecraft.resources.ResourceLocation("starcrownqa","beneficial_curse"),CURSE);h.register(new net.minecraft.resources.ResourceLocation("starcrownqa","neutral_curse"),NEUTRAL);h.register(new net.minecraft.resources.ResourceLocation("starcrownqa","custom_curse"),BAD);});
 }
}
