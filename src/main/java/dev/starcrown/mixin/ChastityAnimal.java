package dev.starcrown.mixin;
import dev.starcrown.YuleChastity;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(net.minecraft.world.entity.animal.Animal.class)
public abstract class ChastityAnimal {
 @Inject(method={"setInLove(Lnet/minecraft/world/entity/player/Player;)V","m_27595_(Lnet/minecraft/world/entity/player/Player;)V"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void pureLove(Player player,CallbackInfo ci){if(YuleChastity.active(player))ci.cancel();}
}
