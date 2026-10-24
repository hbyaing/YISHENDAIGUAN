package dev.starcrownqa;
import dev.starcrown.*;
import java.nio.file.*;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** CPU submission timings, not GPU frame times or a gameplay TPS benchmark. */
@Mod.EventBusSubscriber(modid="starcrownqa",value=Dist.CLIENT)
public final class TooltipPerformanceQa {
 private static boolean started;
 @SubscribeEvent public static void tick(TickEvent.ClientTickEvent event){
  if(started||event.phase!=TickEvent.Phase.END||!Files.exists(Path.of("enable-tooltip-performance-qa")))return;
  var mc=Minecraft.getInstance();if(mc.getOverlay()!=null)return;started=true;mc.setScreen(new Benchmark());
 }
 private static final class Benchmark extends Screen {
  int frame,stage;final List<Long> times=new ArrayList<>();final List<String> results=new ArrayList<>();
  Benchmark(){super(Component.literal("Tooltip CPU performance QA"));}
  @Override public void render(GuiGraphics g,int mx,int my,float partial){
   g.fill(0,0,width,height,0xff303040);
   boolean yule=stage>=2,expanded=stage%2==1;
   var stack=new ItemStack(yule?StarcrownDoll.YULE_ITEM.get():StarcrownDoll.ITEM.get());
   long before=System.nanoTime();
   if(yule)YuleItemTooltip.draw(g,width/3,height/2,width,height,expanded,stack.getHoverName());
   else DollTooltip.draw(g,width/3,height/2,width,height,expanded,stack.getHoverName());
   g.flush();long elapsed=System.nanoTime()-before;
   if(frame++>=40)times.add(elapsed);
   if(frame==160){
    Collections.sort(times);double average=times.stream().mapToLong(Long::longValue).average().orElseThrow()/1e6;
    results.add((yule?"Yule":"Chengran")+" "+(expanded?"expanded":"compact")+" samples="+times.size()+" mean_ms="+average+" median_ms="+times.get(times.size()/2)/1e6+" p95_ms="+times.get((int)(times.size()*.95))/1e6);
    try{Files.write(Path.of("tooltip-performance-results.txt"),results);}catch(Exception e){throw new RuntimeException(e);}
    times.clear();frame=0;if(++stage==4)Minecraft.getInstance().stop();
   }
  }
 }
}
