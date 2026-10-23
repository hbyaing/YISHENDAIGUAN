package dev.starcrown.mixin;
import dev.starcrown.DivineWill;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
/** Only the hand renderer sees a visible player; potion state and server AI visibility stay intact. */
@Mixin(ItemInHandRenderer.class)
public abstract class DivineHands {
 @Redirect(method={"renderArmWithItem","m_109371_"},at=@At(value="INVOKE",target="Lnet/minecraft/client/player/AbstractClientPlayer;isInvisible()Z",remap=false),remap=false,require=0)
 private boolean arm(AbstractClientPlayer p){return !DivineWill.wearing(p)&&p.isInvisible();}
 @Redirect(method={"renderArmWithItem","m_109371_"},at=@At(value="INVOKE",target="Lnet/minecraft/client/player/AbstractClientPlayer;m_20145_()Z",remap=false),remap=false,require=0)
 private boolean armProduction(AbstractClientPlayer p){return !DivineWill.wearing(p)&&p.isInvisible();}
 @Redirect(method={"renderOneHandedMap","m_109353_","renderTwoHandedMap","m_109339_"},at=@At(value="INVOKE",target="Lnet/minecraft/client/player/LocalPlayer;isInvisible()Z",remap=false),remap=false,require=0)
 private boolean map(LocalPlayer p){return !DivineWill.wearing(p)&&p.isInvisible();}
 @Redirect(method={"renderOneHandedMap","m_109353_","renderTwoHandedMap","m_109339_"},at=@At(value="INVOKE",target="Lnet/minecraft/client/player/LocalPlayer;m_20145_()Z",remap=false),remap=false,require=0)
 private boolean mapProduction(LocalPlayer p){return !DivineWill.wearing(p)&&p.isInvisible();}
}
