package dev.starcrown.mixin;
import dev.starcrown.YuleCharity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
@Mixin(net.minecraft.world.entity.LivingEntity.class)
public abstract class CharityDeathXp {
 @Group(name="deathXp",min=1)
 @Redirect(method={"dropExperience","m_21226_"},at=@At(value="INVOKE",target="Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V"),remap=false,require=0)
 private void xp0(ServerLevel l,Vec3 pos,int amount){YuleCharity.deathExperience(l,pos,amount);}
 @Group(name="deathXp",min=1)
 @Redirect(method={"dropExperience","m_21226_"},at=@At(value="INVOKE",target="Lnet/minecraft/world/entity/ExperienceOrb;m_147082_(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V"),remap=false,require=0)
 private void xp1(ServerLevel l,Vec3 pos,int amount){YuleCharity.deathExperience(l,pos,amount);}
}
