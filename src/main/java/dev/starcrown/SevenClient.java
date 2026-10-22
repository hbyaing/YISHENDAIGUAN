package dev.starcrown;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
@Mod.EventBusSubscriber(modid=StarcrownDoll.ID,value=Dist.CLIENT)
public final class SevenClient {
 private static SevenNetwork.State state=new SevenNetwork.State(false,true,false,true,1);
 public static void receive(SevenNetwork.State next){state=next;if(Minecraft.getInstance().screen instanceof Panel panel)panel.refresh();}
 public static void openPanel(){Minecraft.getInstance().setScreen(new Panel());}
 private static final KeyMapping OPEN=new KeyMapping("key.starcrowndoll.seven",com.mojang.blaze3d.platform.InputConstants.Type.KEYSYM,org.lwjgl.glfw.GLFW.GLFW_KEY_H,"key.categories.starcrowndoll");
 @Mod.EventBusSubscriber(modid=StarcrownDoll.ID,value=Dist.CLIENT,bus=Mod.EventBusSubscriber.Bus.MOD)
 public static final class Setup {@SubscribeEvent public static void keys(net.minecraftforge.client.event.RegisterKeyMappingsEvent e){e.register(OPEN);}}
 @SubscribeEvent public static void tick(TickEvent.ClientTickEvent e){if(e.phase==TickEvent.Phase.END)while(OPEN.consumeClick()){var mc=Minecraft.getInstance();if(mc.player!=null&&mc.screen==null&&DivineWill.wearing(mc.player))mc.setScreen(new Panel());}}
 private static final class Panel extends Screen {
  int page,slot;Panel(){super(Component.literal("成然玩偶 · 至高神性"));if(Minecraft.getInstance().getConnection()!=null)SevenNetwork.CHANNEL.sendToServer(new SevenNetwork.Action(16,0));}void refresh(){rebuildWidgets();}
  @Override public boolean isPauseScreen(){return false;}
  void action(int id,int value){SevenNetwork.CHANNEL.sendToServer(new SevenNetwork.Action(id,value));}
  void button(int column,int row,String text,Runnable run){addRenderableWidget(Button.builder(Component.literal(text),b->run.run()).bounds(width/2-Math.min(170,(width-24)/2)-4+column*(Math.min(170,(width-24)/2)+8),68+row*Math.max(20,Math.min(27,(height-140)/4)),Math.min(170,(width-24)/2),22).build());}
  @Override protected void init(){for(int i=0;i<3;i++){final int index=i;addRenderableWidget(Button.builder(Component.literal(new String[]{"神性与领域","群星归途","岁月与恢复"}[i]),b->{page=index;rebuildWidgets();}).bounds(12+i*((width-24)/3),39,(width-24)/3-4,22).build());}
   if(page==0){button(0,0,"万象领域："+(state.domain()?"开启":"关闭"),()->action(0,0));button(1,0,"驱逐领域周围敌人",()->action(15,0));for(int i=0;i<5;i++){final int index=i;button(i%2,1+i/2,(state.speed()==i?"✓ ":"")+"移动速度 ×"+new String[]{"1","1.5","2","3","5"}[i],()->action(1,index));}button(1,3,"夜视："+(state.night()?"开启":"关闭"),()->action(2,0));button(0,4,"范围采掘："+(state.mine()?"开启":"关闭"),()->action(3,0));}
   else if(page==1){button(0,0,"准星安全瞬移",()->{action(4,0);onClose();});button(1,0,"返回传送前位置",()->{action(7,0);onClose();});button(0,1,"返回死亡地点",()->{action(8,0);onClose();});button(1,1,"锚点槽位："+(slot+1),()->{slot=(slot+1)%8;rebuildWidgets();});button(0,2,"记录当前锚点",()->action(5,slot));button(1,2,"返回所选锚点",()->{action(6,slot);onClose();});}
   else {button(0,0,"预览战场修复范围",()->{action(10,0);onClose();});button(1,0,"恢复记录中的战场",()->{action(9,0);onClose();});button(0,1,"玩家庇护："+(state.players()?"开启":"关闭"),()->action(11,0));button(1,1,"准星目标 加入 / 移出友方",()->{action(12,0);onClose();});button(0,2,"准星敌人：旧伤重现",()->{action(13,0);onClose();});button(1,2,"领取没收的玩偶",()->action(17,0));}
   addRenderableWidget(Button.builder(Component.literal("关闭"),b->onClose()).bounds(width/2-55,height-33,110,20).build());
  }
  @Override public void render(GuiGraphics g,int x,int y,float partial){g.fill(0,0,width,height,0xd0101025);g.drawCenteredString(font,title,width/2,17,0xe5d9ff);super.render(g,x,y,partial);g.drawCenteredString(font,"准星目标由打开面板前的朝向决定；仅佩戴时可用",width/2,height-46,0xa9b7d7);}
 }
}
