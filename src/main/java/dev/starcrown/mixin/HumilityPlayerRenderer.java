package dev.starcrown.mixin;
import dev.starcrown.YuleHumility;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(PlayerRenderer.class)
public abstract class HumilityPlayerRenderer {
 @Inject(method={"setModelProperties(Lnet/minecraft/client/player/AbstractClientPlayer;)V","m_117818_(Lnet/minecraft/client/player/AbstractClientPlayer;)V"},at=@At("TAIL"),remap=false,require=1)
 private void divineSkin(AbstractClientPlayer player,CallbackInfo ci){if(dev.starcrown.DivineWill.wearing(player))((PlayerRenderer)(Object)this).getModel().setAllVisible(true);}
 @Inject(method={"scale(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;F)V","m_7546_(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;F)V"},at=@At("TAIL"),remap=false,require=1)
 private void humilityScale(AbstractClientPlayer player,PoseStack pose,float partial,CallbackInfo ci){if(YuleHumility.active(player))pose.scale(YuleHumility.SCALE,YuleHumility.SCALE,YuleHumility.SCALE);}
}
