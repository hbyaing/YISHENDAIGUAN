package dev.starcrown;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.item.*;
import com.mojang.blaze3d.vertex.PoseStack;
public class DollItemRenderer extends BlockEntityWithoutLevelRenderer {
 static DollItemRenderer INSTANCE;
 public static DollItemRenderer instance(){if(INSTANCE==null)INSTANCE=new DollItemRenderer();return INSTANCE;}
 DollItemRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
 @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack p,MultiBufferSource b,int light,int overlay){
  var mc=Minecraft.getInstance();float raised=(context==ItemDisplayContext.THIRD_PERSON_LEFT_HAND||context==ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)?DollAnimation.jumpAmount(DollAnimation.renderedPlayer(),mc.getFrameTime()):0;
  p.pushPose();DollRenderer.renderBody(StarcrownDoll.dollState(stack),p,b,light,overlay,raised);
  p.translate(.5,0,.5);var level=Minecraft.getInstance().level;double t=level==null?0:(level.getGameTime()+Minecraft.getInstance().getFrameTime())/20.;
  DollRenderer.renderEffects(t,p,b,stack.is(StarcrownDoll.ITEM.get()));p.popPose();
 }
}
