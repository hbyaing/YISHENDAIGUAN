package dev.starcrown;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Reading pagination never scales long descriptions down to fit the screen. */
@Mod.EventBusSubscriber(modid=StarcrownDoll.ID,value=Dist.CLIENT)
public final class TooltipReading {
 private static String owner="";
 private static Screen screen;
 private static long seen;
 private static int page,pages=1;
 private static boolean locked,shiftHeld,drawingPinned;
 private static int anchorX,anchorY;
 private static Component name=Component.empty();
 public static void track(int x,int y,Component title){if(!locked){anchorX=x;anchorY=y;name=title.copy();}}
 public static boolean pinned(){
  if(screen!=Minecraft.getInstance().screen){locked=false;shiftHeld=false;page=0;}
  return locked;
 }
 @SubscribeEvent public static void hideHover(RenderTooltipEvent.Pre event){if(pinned()&&!drawingPinned)event.setCanceled(true);}
 @SubscribeEvent public static void pinnedRender(ScreenEvent.Render.Post event){
  if(!pinned())return;
  var mc=Minecraft.getInstance();drawingPinned=true;
  try{
   if(owner.equals("yule"))YuleItemTooltip.draw(event.getGuiGraphics(),anchorX,anchorY,mc.getWindow().getGuiScaledWidth(),mc.getWindow().getGuiScaledHeight(),true,name);
   else DollTooltip.draw(event.getGuiGraphics(),anchorX,anchorY,mc.getWindow().getGuiScaledWidth(),mc.getWindow().getGuiScaledHeight(),true,name);
  }finally{drawingPinned=false;}
 }
 public record Reading<T>(List<T> lines,int page,int pages){}
 public static <T> Reading<T> select(List<T> lines,int header,int screenH,String id,boolean expanded){
  long now=System.nanoTime();Screen current=Minecraft.getInstance().screen;
  if(!owner.equals(id)||screen!=current||(!locked&&now-seen>300_000_000L)||!expanded)page=0;
  owner=id;screen=current;seen=now;
  if(!expanded){pages=1;return new Reading<>(lines,0,1);}
  int capacity=Math.max(1,(screenH-44)/13-header);
  int body=lines.size()-header;
  pages=Math.max(1,(body+capacity-1)/capacity);page=Math.max(0,Math.min(page,pages-1));
  List<T> result=new ArrayList<>(lines.subList(0,header));
  int from=header+page*capacity;result.addAll(lines.subList(from,Math.min(lines.size(),from+capacity)));
  return new Reading<>(result,page,pages);
 }
 private static boolean active(){return pinned()&&pages>1;}
 @SubscribeEvent public static void scroll(ScreenEvent.MouseScrolled.Pre event){
  if(!active()||event.getScrollDelta()==0)return;
  page=Math.max(0,Math.min(pages-1,page+(event.getScrollDelta()<0?1:-1)));event.setCanceled(true);
 }
 @SubscribeEvent public static void key(ScreenEvent.KeyPressed.Pre event){
  int shift=event.getKeyCode();
  if(shift==340||shift==344){
   if(shiftHeld)return;
   shiftHeld=true;
   if(pinned()||screen==event.getScreen()&&System.nanoTime()-seen<300_000_000L){locked=!locked;page=0;event.setCanceled(true);}
   return;
  }
  if(!active())return;
  int code=event.getKeyCode();if(code!=266&&code!=267&&code!=268&&code!=269)return;
  page=code==268?0:code==269?pages-1:Math.max(0,Math.min(pages-1,page+(code==267?1:-1)));event.setCanceled(true);
 }
 @SubscribeEvent public static void released(ScreenEvent.KeyReleased.Post event){if(event.getKeyCode()==340||event.getKeyCode()==344)shiftHeld=false;}
 public static Component footer(Reading<?> reading){return Component.translatable("tooltip.starcrowndoll.reading",reading.page()+1,reading.pages());}
 public static void text(GuiGraphics g,FormattedCharSequence line,int x,int y,int[] palette,double time){
  var glyphs=TooltipLayouts.glyphs(line);
  FormattedCharSequence colored=sink->{for(var glyph:glyphs){
   int color=DollTooltip.sample(palette,(float)(glyph.x()/260.-time*.5));
   // Pastel highlights stay bright across every part of the gradient, without an outline.
   int r=(((color>>16)&255)+255*2)/3,green=(((color>>8)&255)+255*2)/3,b=((color&255)+255*2)/3;
   if(!sink.accept(glyph.index(),glyph.style().withBold(false).withColor((r<<16)|(green<<8)|b),glyph.code()))return false;
  }return true;};
  g.drawString(Minecraft.getInstance().font,colored,x,y,0xffffff,false);
 }
}
