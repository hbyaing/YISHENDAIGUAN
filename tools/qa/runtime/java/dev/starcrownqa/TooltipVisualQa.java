package dev.starcrownqa;
import dev.starcrown.*;
import java.nio.file.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
@Mod.EventBusSubscriber(modid="starcrownqa",value=Dist.CLIENT)
public final class TooltipVisualQa {
 private static int ticks;
 @SubscribeEvent public static void tick(TickEvent.ClientTickEvent e){
  if(e.phase!=TickEvent.Phase.END||!Files.exists(Path.of("enable-tooltip-visual-qa")))return;
  var mc=net.minecraft.client.Minecraft.getInstance();if(mc.getOverlay()!=null)return;

  ticks++;
  if(ticks==5)mc.setScreen(new Preview(false));
  if(ticks==30||ticks==55||ticks==100)net.minecraft.client.Screenshot.grab(mc.gameDirectory,"tooltip-"+ticks+".png",mc.getMainRenderTarget(),m->{});
  if(ticks==70)mc.setScreen(new Preview(true));
  if(ticks==110){try{if(Math.abs(DollTooltip.sway(0)-DollTooltip.sway(2.8))>1e-9)throw new AssertionError("cycle discontinuity");Files.writeString(Path.of("tooltip-qa-results.txt"),"PASS production tooltip render compact/expanded, continuous sway cycle\n");}catch(Exception x){throw new RuntimeException(x);}mc.stop();}
 }
 private static final class Preview extends net.minecraft.client.gui.screens.Screen {
  private final boolean expanded;Preview(boolean expanded){super(net.minecraft.network.chat.Component.literal("Tooltip acceptance preview"));this.expanded=expanded;}
  @Override public void render(net.minecraft.client.gui.GuiGraphics g,int mx,int my,float partial){
   g.fill(0,0,width,height,0xff202b38);g.drawString(font,"CHENGRAN / "+(expanded?"SHIFT":"COMPACT"),8,8,0xffffffff,false);
   var stack=new net.minecraft.world.item.ItemStack(StarcrownDoll.ITEM.get());
   if(expanded)DollTooltip.draw(g,width/3,height/2,width,height,true,stack.getHoverName());
   else g.renderTooltip(font,stack,width/3,height/2);
  }
 }
}
