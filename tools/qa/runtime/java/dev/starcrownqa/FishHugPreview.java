package dev.starcrownqa;
import dev.starcrown.*;
import java.nio.file.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screens.*;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
@Mod.EventBusSubscriber(modid="starcrownqa",value=net.minecraftforge.api.distmarker.Dist.CLIENT)
public final class FishHugPreview {
 static boolean opened;static int frames;
 @SubscribeEvent public static void tick(TickEvent.ClientTickEvent e){if(e.phase!=TickEvent.Phase.END||!Files.exists(Path.of("enable-fish-hug-preview")))return;var mc=Minecraft.getInstance();if(!opened&&mc.screen!=null&&mc.getOverlay()==null){mc.options.guiScale().set(2);mc.resizeDisplay();mc.setScreen(new Preview());opened=true;}if(opened&&++frames==60){net.minecraft.client.Screenshot.grab(mc.gameDirectory,"yule-fish-jump-35-release.png",mc.getMainRenderTarget(),msg->{});}if(opened&&frames==85)mc.stop();}
 public static class Preview extends Screen {
  Preview(){super(Component.literal("余乐玩偶 · 抱鱼预览"));}
  public void render(GuiGraphics g,int mouseX,int mouseY,float partial){g.fill(0,0,width,height,0xff101625);g.drawCenteredString(font,title,width/2,20,0xe8edff);g.drawCenteredString(font,Component.literal("余乐玩偶 · 站立与35°跳跃举手"),width/2,40,0x96b6e8);draw(g,width*.28f,height*.80f,0);draw(g,width*.73f,height*.80f,55);g.drawCenteredString(font,Component.literal("正面"),(int)(width*.28),height-35,0xe8edff);g.drawCenteredString(font,Component.literal("跳跃举手 · 35°（侧面）"),(int)(width*.73),height-35,0xe8edff);super.render(g,mouseX,mouseY,partial);}
  private void draw(GuiGraphics g,float x,float y,float yaw){g.flush();var mc=Minecraft.getInstance();var p=g.pose();p.pushPose();p.translate(x,y,180);float scale=height*.58f;p.scale(scale,-scale,scale);p.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180+yaw));p.translate(-.5,0,-.5);com.mojang.blaze3d.platform.Lighting.setupForFlatItems();com.mojang.blaze3d.systems.RenderSystem.enableDepthTest();DollRenderer.renderBody(StarcrownDoll.YULE_DOLL.get().defaultBlockState(),p,mc.renderBuffers().bufferSource(),15728880,net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,yaw==0?0:1);mc.renderBuffers().bufferSource().endBatch();p.popPose();g.flush();}
 }
}
