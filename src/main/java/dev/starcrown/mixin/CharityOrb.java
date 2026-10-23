package dev.starcrown.mixin;
import dev.starcrown.YuleCharity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(ExperienceOrb.class)
public abstract class CharityOrb {
 @Inject(method={"playerTouch(Lnet/minecraft/world/entity/player/Player;)V","m_6123_(Lnet/minecraft/world/entity/player/Player;)V"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void pickup(Player p,CallbackInfo c){if(YuleCharity.active(p)&&YuleCharity.deathLoot((ExperienceOrb)(Object)this))c.cancel();}
 @Inject(method={"canMerge(Lnet/minecraft/world/entity/ExperienceOrb;II)Z","m_147088_(Lnet/minecraft/world/entity/ExperienceOrb;II)Z"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private static void merge(ExperienceOrb other,int id,int value,CallbackInfoReturnable<Boolean> c){if(YuleCharity.deathLoot(other))c.setReturnValue(false);}
 @Inject(method={"canMerge(Lnet/minecraft/world/entity/ExperienceOrb;)Z","m_147086_(Lnet/minecraft/world/entity/ExperienceOrb;)Z"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void mergeSelf(ExperienceOrb other,CallbackInfoReturnable<Boolean> c){if(YuleCharity.deathLoot((ExperienceOrb)(Object)this)||YuleCharity.deathLoot(other))c.setReturnValue(false);}
}
