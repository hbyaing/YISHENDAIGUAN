package dev.starcrown.mixin;
import dev.starcrown.YuleChastity;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(Player.class)
public abstract class ChastityPlayer {
 @Inject(method={"interactOn(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;","m_36157_(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void pureInteract(Entity target,InteractionHand hand,CallbackInfoReturnable<InteractionResult> ci){var p=(Player)(Object)this;var charity=dev.starcrown.YuleCharity.use(p,target,hand);if(charity.consumesAction()){ci.setReturnValue(charity);return;}if(YuleChastity.blockedInteraction(p,target,p.getItemInHand(hand)))ci.setReturnValue(InteractionResult.FAIL);}
}
