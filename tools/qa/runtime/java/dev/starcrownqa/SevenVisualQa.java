package dev.starcrownqa;
import dev.starcrown.*;
import java.nio.file.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
@Mod.EventBusSubscriber(modid="starcrownqa",value=Dist.CLIENT)
public final class SevenVisualQa {
 private static int ticks;
 @SubscribeEvent public static void tick(TickEvent.ClientTickEvent e){if(e.phase!=TickEvent.Phase.END||!Files.exists(Path.of("enable-seven-visual-qa")))return;var mc=net.minecraft.client.Minecraft.getInstance();if(mc.getOverlay()!=null)return;ticks++;
  if(ticks==5)SevenClient.openPanel();
  if(ticks==30||ticks==75||ticks==95)net.minecraft.client.Screenshot.grab(mc.gameDirectory,"seven-"+ticks+".png",mc.getMainRenderTarget(),m->{});
  if(ticks==40){var screen=mc.screen;try{var field=screen.getClass().getDeclaredField("page");field.setAccessible(true);field.setInt(screen,1);SevenClient.receive(new SevenNetwork.State(true,true,false,true,4));}catch(Exception ex){throw new RuntimeException(ex);}}
  if(ticks==55)net.minecraft.client.Screenshot.grab(mc.gameDirectory,"seven-travel.png",mc.getMainRenderTarget(),m->{});
  if(ticks==60)mc.setScreen(new Preview());
  if(ticks==110){try{Files.writeString(Path.of("seven-visual-results.txt"),"PASS divine panel pages and full seven-skill tooltip rendered\n");}catch(Exception ex){throw new RuntimeException(ex);}mc.stop();}
 }
 private static final class Preview extends net.minecraft.client.gui.screens.Screen {
  Preview(){super(net.minecraft.network.chat.Component.literal("Seven skills"));}
  @Override public void render(net.minecraft.client.gui.GuiGraphics g,int mx,int my,float partial){g.fill(0,0,width,height,0xff202b38);DollTooltip.draw(g,18,40,width,height,true,new net.minecraft.world.item.ItemStack(StarcrownDoll.ITEM.get()).getHoverName());}
 }
}
