package dev.starcrownqa;

import dev.starcrown.*;
import java.nio.file.*;
import java.util.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

/** Opt-in screenshots in an isolated client, never in the normal gameplay JAR. */
@Mod.EventBusSubscriber(modid="starcrownqa",value=Dist.CLIENT)
public final class HumilityVisualQa {
 private static int ticks;private static boolean scheduled;private static final List<String> RESULTS=new ArrayList<>();
 private static void log(String text){RESULTS.add(text);try{Files.write(Path.of("humility-visual-results.txt"),RESULTS);}catch(Exception ex){throw new RuntimeException(ex);}}
 private static void check(boolean ok,String text){if(!ok)throw new AssertionError(text);log("PASS "+text);}
 private static void shot(String filename){var mc=net.minecraft.client.Minecraft.getInstance();net.minecraft.client.Screenshot.grab(mc.gameDirectory,filename,mc.getMainRenderTarget(),message->{});}
 @SubscribeEvent public static void tick(TickEvent.ClientTickEvent event){
  if(event.phase!=TickEvent.Phase.END||!Files.exists(Path.of("enable-humility-visual-qa")))return;
  var mc=net.minecraft.client.Minecraft.getInstance();if(mc.player==null||mc.getSingleplayerServer()==null)return;
  try{
   if(!scheduled){scheduled=true;var id=mc.player.getUUID();mc.getSingleplayerServer().execute(()->{
    var p=mc.getSingleplayerServer().getPlayerList().getPlayer(id);var level=p.serverLevel();
    level.getGameRules().getRule(net.minecraft.world.level.GameRules.RULE_DOMOBSPAWNING).set(false,level.getServer());level.setDayTime(6000);
    var oldEntities=new ArrayList<net.minecraft.world.entity.Entity>();level.getAllEntities().forEach(oldEntities::add);
    for(var entity:oldEntities)if(entity instanceof net.minecraft.world.entity.Mob||entity instanceof net.minecraft.world.entity.decoration.ArmorStand)entity.discard();
    for(int x=-6;x<=6;x++)for(int z=-6;z<=6;z++)level.setBlockAndUpdate(new net.minecraft.core.BlockPos(x,100,z),net.minecraft.world.level.block.Blocks.SMOOTH_STONE.defaultBlockState());
    p.setGameMode(net.minecraft.world.level.GameType.CREATIVE);p.teleportTo(level,.5,101,.5,0,0);p.setItemSlot(EquipmentSlot.HEAD,new ItemStack(StarcrownDoll.YULE_ITEM.get()));p.getInventory().setItem(0,new ItemStack(StarcrownDoll.YULE_ITEM.get()));
    if(net.minecraftforge.fml.ModList.get().isLoaded("arcanevortex"))try{
     for(String name:new String[]{"com.erchien.arcanevortex.Core.CoreEntity.HealthMethodHelper","com.erchien.arcanevortex.Agent.HealthMethod.AgentHealthMethodHelper","com.erchien.arcanevortex.NativeAgent.NativeAgentHealthMethod"}){
      var type=Class.forName(name);type.getMethod("forceSetHealth",Object.class,float.class,float.class).invoke(null,p,0f,0f);
      if((Boolean)type.getMethod("isModified",Object.class).invoke(null,p)||p.getHealth()<=0)throw new AssertionError("third-party health override accepted");
     }
     log("PASS installed third-party forced-health APIs rejected for Yule wearer");
    }catch(Throwable error){log("FAIL third-party compatibility: "+error);}
    var stand=new net.minecraft.world.entity.decoration.ArmorStand(level,1.7,101,.5);level.addFreshEntity(stand);
   });return;}
   if(!YuleHumility.active(mc.player))return;
   ticks++;
   if(ticks==20){
    check(Math.abs(mc.player.getBbHeight()-.5)<.001,"client hitbox height is 0.5; actual="+mc.player.getBbHeight());check(mc.player.getEyeHeight()<.5,"client camera eye height adjusted");
    var pose=new com.mojang.blaze3d.vertex.PoseStack();var renderer=mc.getEntityRenderDispatcher().getRenderer(mc.player);java.lang.reflect.Method scale=null;
    for(String name:new String[]{"scale","m_7546_"})try{scale=net.minecraft.client.renderer.entity.player.PlayerRenderer.class.getDeclaredMethod(name,net.minecraft.client.player.AbstractClientPlayer.class,com.mojang.blaze3d.vertex.PoseStack.class,float.class);break;}catch(NoSuchMethodException ignored){}
    if(scale==null)throw new AssertionError("player scale method missing");scale.setAccessible(true);scale.invoke(renderer,mc.player,pose,0f);
    check(Math.abs(pose.last().pose().m00()-.9375f*YuleHumility.SCALE)<.001,"production player renderer scale applied");
    mc.options.setCameraType(net.minecraft.client.CameraType.THIRD_PERSON_FRONT);
   }
   if(ticks==60)shot("humility-half-height.png");
   if(ticks==80){
    var lines=new ArrayList<net.minecraft.network.chat.Component>();DollClient.yuleTooltip(lines,true);
    check(lines.get(0).getString().equals("七美德"),"expanded header is 七美德");check(lines.get(1).getStyle().isBold(),"谦逊 heading is bold");
    mc.setScreen(new Preview(lines));
   }
   if(ticks==105)shot("humility-shift-tooltip.png");
   if(ticks==125){log(RESULTS.stream().anyMatch(s->s.startsWith("FAIL"))?"FAILED":"ALL_PASS");mc.stop();}
  }catch(Throwable error){log("FAIL "+error);error.printStackTrace();mc.stop();}
 }
 private static final class Preview extends net.minecraft.client.gui.screens.Screen {
  private final List<net.minecraft.network.chat.Component> lines;
  Preview(List<net.minecraft.network.chat.Component> expanded){super(net.minecraft.network.chat.Component.literal("余乐玩偶 · Shift 展开预览"));lines=new ArrayList<>();lines.add(new ItemStack(StarcrownDoll.YULE_ITEM.get()).getHoverName());lines.addAll(expanded);}
  @Override public boolean isPauseScreen(){return false;}
  @Override public void render(net.minecraft.client.gui.GuiGraphics graphics,int mouseX,int mouseY,float partial){
   graphics.fill(0,0,width,height,0xff202b38);graphics.drawString(font,title,16,16,0xffe9ddbd);
   graphics.renderTooltip(font,lines,Optional.empty(),new ItemStack(StarcrownDoll.YULE_ITEM.get()),16,52);
  }
 }
}
