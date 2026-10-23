package dev.starcrown.mixin;
import dev.starcrown.YuleChastity;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(LivingEntity.class)
public abstract class ChastityUse {
 @Inject(method={"startUsingItem(Lnet/minecraft/world/InteractionHand;)V","m_6672_(Lnet/minecraft/world/InteractionHand;)V"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void pureUse(net.minecraft.world.InteractionHand hand,CallbackInfo ci){var self=(LivingEntity)(Object)this;if(YuleChastity.blockedPotion(self,self.getItemInHand(hand))||dev.starcrown.YuleTemperance.foodDenied(self,self.getItemInHand(hand)))ci.cancel();}
}
