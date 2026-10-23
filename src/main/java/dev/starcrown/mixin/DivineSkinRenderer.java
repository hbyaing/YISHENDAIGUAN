package dev.starcrown.mixin;
import dev.starcrown.DivineWill;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(LivingEntityRenderer.class)
public abstract class DivineSkinRenderer {
 @Inject(method={"isBodyVisible(Lnet/minecraft/world/entity/LivingEntity;)Z","m_5933_(Lnet/minecraft/world/entity/LivingEntity;)Z"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void visible(LivingEntity entity,CallbackInfoReturnable<Boolean> ci){if(entity instanceof AbstractClientPlayer p&&DivineWill.wearing(p))ci.setReturnValue(true);}
 @Inject(method={"getRenderType(Lnet/minecraft/world/entity/LivingEntity;ZZZ)Lnet/minecraft/client/renderer/RenderType;","m_7225_(Lnet/minecraft/world/entity/LivingEntity;ZZZ)Lnet/minecraft/client/renderer/RenderType;"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void skin(LivingEntity entity,boolean visible,boolean translucent,boolean glowing,CallbackInfoReturnable<RenderType> ci){
  if(entity instanceof AbstractClientPlayer p&&DivineWill.wearing(p))ci.setReturnValue(RenderType.entityCutoutNoCull(p.getSkinTextureLocation()));
 }
}
