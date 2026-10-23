package dev.starcrownqa;
import dev.starcrown.*;
import java.nio.file.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.effect.*;
@Mod.EventBusSubscriber(modid="starcrownqa",value=net.minecraftforge.api.distmarker.Dist.CLIENT)
public final class PanelRuntimeQa {
 static int tick;static volatile boolean failed;
 static void check(boolean ok,String text){try{Files.writeString(Path.of("panel-runtime-results.txt"),(ok?"PASS ":"FAIL ")+text+"\n",StandardOpenOption.CREATE,StandardOpenOption.APPEND);}catch(Exception e){throw new RuntimeException(e);}if(!ok)failed=true;}
 static void server(java.util.function.Consumer<net.minecraft.server.level.ServerPlayer> action){var mc=net.minecraft.client.Minecraft.getInstance();var id=mc.player.getUUID();mc.getSingleplayerServer().execute(()->{try{action.accept(mc.getSingleplayerServer().getPlayerList().getPlayer(id));}catch(Throwable t){check(false,t.toString());}});}
 @SubscribeEvent public static void client(TickEvent.ClientTickEvent event)throws Exception {
  if(event.phase!=TickEvent.Phase.END||!Files.exists(Path.of("enable-panel-runtime-qa")))return;var mc=net.minecraft.client.Minecraft.getInstance();mc.options.pauseOnLostFocus=false;if(mc.player==null||mc.getSingleplayerServer()==null)return;tick++;
  if(tick==1){Files.writeString(Path.of("panel-runtime-results.txt"),"");mc.options.guiScale().set(2);mc.resizeDisplay();mc.setScreen(null);mc.mouseHandler.releaseMouse();server(p->{DivineAnchor.unlock(p);p.setItemSlot(EquipmentSlot.HEAD,ItemStack.EMPTY);DivineWill.update(p,false);p.removeAllEffects();p.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);p.setItemSlot(EquipmentSlot.HEAD,new ItemStack(StarcrownDoll.ITEM.get()));});}
  if(tick==35)SevenNetwork.CHANNEL.sendToServer(new SevenNetwork.Action(18,3));
  if(tick==55)server(p->check(DivineSeven.miningIndex(p)==3,"mining range packet reaches server and stores 9x9x9"));
  if(tick==65)SevenNetwork.CHANNEL.sendToServer(new SevenNetwork.EffectAction(net.minecraftforge.registries.ForgeRegistries.MOB_EFFECTS.getKey(MobEffects.DIG_SPEED),false));
  if(tick==85){server(p->check(!PotionFilter.enabled(p,MobEffects.DIG_SPEED)&&!p.hasEffect(MobEffects.DIG_SPEED),"potion packet still filters effects"));SevenClient.openPanel();}
  if(tick==100){var field=SevenClient.class.getDeclaredField("state");field.setAccessible(true);var state=(SevenNetwork.State)field.get(null);check(state.mining()==3,"server range synchronized to custom UI");check(mc.screen.children().stream().anyMatch(w->w instanceof net.minecraft.client.gui.components.Button b&&b.getMessage().getString().equals("9×9×9")),"range buttons exist in actual in-game panel");}
  if(tick==110)net.minecraft.client.Screenshot.grab(mc.gameDirectory,"panel-runtime-main.png",mc.getMainRenderTarget(),m->{});
  if(tick==120){for(var widget:java.util.List.copyOf(mc.screen.children()))if(widget instanceof net.minecraft.client.gui.components.Button b&&b.getMessage().getString().equals("3×3×3")){b.onPress();break;}}
  if(tick==140){server(p->check(DivineSeven.miningIndex(p)==0,"custom range button changes actual gameplay setting"));var screen=mc.screen;var field=screen.getClass().getDeclaredField("page");field.setAccessible(true);field.setInt(screen,3);screen.resize(mc,screen.width,screen.height);for(var widget:java.util.List.copyOf(screen.children()))if(widget instanceof net.minecraft.client.gui.components.EditBox box){box.setValue("急迫");break;}}
  if(tick==155){check(mc.screen.children().stream().anyMatch(w->w instanceof net.minecraft.client.gui.components.Button b&&b.getMessage().getString().equals("○ 急迫")),"search page shows synchronized disabled potion");}
  if(tick==160)net.minecraft.client.Screenshot.grab(mc.gameDirectory,"panel-runtime-potions.png",mc.getMainRenderTarget(),m->{});
  if(tick==170){mc.screen.onClose();check(Files.exists(net.minecraftforge.fml.loading.FMLPaths.CONFIGDIR.get().resolve("starcrowndoll-panel.json")),"appearance saved to client config");}
  if(tick==180){check(!failed,"all live panel checks passed");mc.stop();}
 }
}
