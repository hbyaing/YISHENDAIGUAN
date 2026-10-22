package dev.starcrownqa;

import dev.starcrown.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.nio.file.*;
import java.util.*;

@Mod.EventBusSubscriber(modid="starcrownqa",value=net.minecraftforge.api.distmarker.Dist.CLIENT)
public final class YuleQa {
 static net.minecraft.world.entity.Mob testPig;
 static int ticks;static boolean started,closed;static final List<String> out=new ArrayList<>();
 static void log(String text){out.add(text);try{Files.write(Path.of("yule-results.txt"),out);}catch(Exception e){throw new RuntimeException(e);}}
 static void shot(String name){var mc=Minecraft.getInstance();net.minecraft.client.Screenshot.grab(mc.gameDirectory,name+".png",mc.getMainRenderTarget(),message->{});log("CAPTURE "+name);}
 static void server(java.util.function.Consumer<net.minecraft.server.level.ServerPlayer> action){var mc=Minecraft.getInstance();var server=mc.getSingleplayerServer();var id=mc.player.getUUID();server.execute(()->action.accept(server.getPlayerList().getPlayer(id)));}
 @SubscribeEvent public static void tick(TickEvent.ClientTickEvent event){
  if(event.phase!=TickEvent.Phase.END||!Files.exists(Path.of("enable-yule-qa")))return;
  var mc=Minecraft.getInstance();mc.options.pauseOnLostFocus=false;
  if(mc.player==null||mc.getSingleplayerServer()==null)return;
  try{
   if(!started){started=true;mc.setScreen(null);mc.mouseHandler.releaseMouse();mc.options.setCameraType(net.minecraft.client.CameraType.FIRST_PERSON);
    server(p->{var level=p.getServer().overworld();DivineAnchor.unlock(p);p.setItemSlot(EquipmentSlot.HEAD,ItemStack.EMPTY);p.setGameMode(net.minecraft.world.level.GameType.CREATIVE);
     for(int x=-6;x<=6;x++)for(int z=-4;z<=8;z++){level.setBlockAndUpdate(new BlockPos(x,80,z),net.minecraft.world.level.block.Blocks.STONE.defaultBlockState());for(int y=81;y<91;y++)level.setBlockAndUpdate(new BlockPos(x,y,z),net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());}
     level.setBlockAndUpdate(new BlockPos(-2,81,0),StarcrownDoll.DOLL.get().defaultBlockState().setValue(net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING,net.minecraft.core.Direction.SOUTH));
     level.setDayTime(6000);level.destroyBlock(new BlockPos(0,81,0),false);level.setBlockAndUpdate(new BlockPos(0,81,0),StarcrownDoll.YULE_DOLL.get().defaultBlockState().setValue(net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING,net.minecraft.core.Direction.SOUTH));
     p.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.NIGHT_VISION,2000,0,false,false));
     p.getAbilities().flying=false;p.onUpdateAbilities();p.teleportTo(level,.5,81,4.5,180,12);p.getInventory().selected=0;p.setItemSlot(EquipmentSlot.MAINHAND,new ItemStack(StarcrownDoll.YULE_ITEM.get()));p.setItemSlot(EquipmentSlot.OFFHAND,ItemStack.EMPTY);
    });
   }
   ticks++;
   if(ticks==60){for(String frame:List.of("half","closed")){var model=mc.getModelManager().getModel(new net.minecraft.resources.ResourceLocation("starcrowndoll","block/doll_blink_"+frame));if(model==mc.getModelManager().getMissingModel())throw new AssertionError("missing blink model "+frame);}log("PASS both additional blink models baked");if(DollRenderer.blinkFrame(2)!=2||DollRenderer.blinkFrame(20)!=0)throw new AssertionError("blink timing");log("PASS blink has open and closed intervals");}
   if(ticks==62){
    var compact=new ArrayList<net.minecraft.network.chat.Component>();var expanded=new ArrayList<net.minecraft.network.chat.Component>();DollClient.yuleTooltip(compact,false);DollClient.yuleTooltip(expanded,true);
    if(compact.size()!=1||!compact.get(0).getString().contains("Shift")||expanded.size()!=4)throw new AssertionError("Shift tooltip states");log("PASS compact Shift hint and four expanded operation lines");
    var stack=new ItemStack(StarcrownDoll.YULE_ITEM.get());var lines=stack.getTooltipLines(mc.player,net.minecraft.world.item.TooltipFlag.NORMAL);if(lines.stream().noneMatch(c->c.getString().equals(net.minecraft.client.resources.language.I18n.get("tooltip.starcrowndoll.yule.divinity"))))throw new AssertionError("missing leisure divinity");log("PASS Yule name and leisure divinity tooltip");
   }
   if(ticks==65){for(String name:List.of("body","head","head_half","head_closed","right_arm","left_arm")){var model=mc.getModelManager().getModel(new net.minecraft.resources.ResourceLocation("starcrowndoll","block/yule_part_"+name));if(model==mc.getModelManager().getMissingModel())throw new AssertionError("missing articulated part "+name);}log("PASS six articulated part models baked");
    var pose=new com.mojang.blaze3d.vertex.PoseStack();DollAnimation.headMotion(pose,20,.5,9./16,10./16);var pivot=pose.last().pose().transformPosition(new org.joml.Vector3f(.5f,9f/16,10f/16));if(pivot.distance(new org.joml.Vector3f(.5f,9f/16,10f/16))>1e-5||Math.abs(pose.last().pose().m02())<.05)throw new AssertionError("head pivot/yaw");log("PASS head yaw changes around fixed neck pivot");}
   if(ticks==130)mc.options.keyJump.setDown(true);
   if(ticks==131)mc.options.keyJump.setDown(false);
   if(ticks==136){if(mc.player.onGround()||DollAnimation.jumpAmount(mc.player,1)<.5f)throw new AssertionError("held doll arms did not rise during real jump");log("PASS real player jump raises both doll arms");shot("right-jump");}
   if(ticks==175){if(!mc.player.onGround()||DollAnimation.jumpAmount(mc.player,1)>.01f)throw new AssertionError("arms did not lower after landing");log("PASS arms lower after actual landing");}
   if(ticks>100&&ticks<240&&!closed&&DollRenderer.blinkFrame(mc.level.getGameTime()+mc.getFrameTime())==2){shot("right-blink");closed=true;}
   if(ticks==220)shot("right-hand");
   if(ticks==240)server(p->{p.setItemSlot(EquipmentSlot.MAINHAND,ItemStack.EMPTY);p.setItemSlot(EquipmentSlot.OFFHAND,new ItemStack(StarcrownDoll.YULE_ITEM.get()));});
   if(ticks==280)shot("left-hand");
   if(ticks==280)server(p->{var level=p.serverLevel();var pos=new BlockPos(0,81,0);var be=(StarcrownDoll.DollEntity)level.getBlockEntity(pos);if(!StarcrownDoll.TYPE.get().isValid(((net.minecraft.world.level.block.entity.BlockEntity)be).getBlockState()))throw new AssertionError("invalid Yule BE type");level.getBlockState(pos).use(level,p,net.minecraft.world.InteractionHand.MAIN_HAND,new net.minecraft.world.phys.BlockHitResult(net.minecraft.world.phys.Vec3.atCenterOf(pos),net.minecraft.core.Direction.UP,pos,false));
    testPig=net.minecraft.world.entity.EntityType.PIG.create(level);testPig.setPos(4,81,0);level.addFreshEntity(testPig);if(!be.isPlaying()||!DollDance.controlled(testPig))throw new AssertionError("Yule music control");log("PASS shared block entity type, right-click music and mob control");});
   if(ticks==310){var be=mc.level.getBlockEntity(new BlockPos(0,81,0));Class<?> c=Class.forName("dev.starcrown.DollMusic");var f=c.getDeclaredField("ACTIVE");f.setAccessible(true);var ref=(java.lang.ref.WeakReference<?>)((Map<?,?>)f.get(null)).get(be);var sound=(net.minecraft.client.resources.sounds.SoundInstance)(ref==null?null:ref.get());if(sound==null||!sound.isLooping()||!mc.getSoundManager().isActive(sound)||!sound.getLocation().getPath().equals("yule_music"))throw new AssertionError("Yule positional looping sound");log("PASS Yule looping audio and its own subtitle event active");}
   if(ticks==320)server(p->{var level=p.serverLevel();var pos=new BlockPos(0,81,0);var be=(StarcrownDoll.DollEntity)level.getBlockEntity(pos);be.toggleMusic();if(DollDance.controlled(testPig)||!testPig.isEffectiveAi())throw new AssertionError("Yule release");log("PASS manual music stop restores mob AI");be.toggleMusic();level.destroyBlock(pos,true);if(DollDance.controlled(testPig))throw new AssertionError("Yule break release");var drops=level.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,new net.minecraft.world.phys.AABB(pos).inflate(2));if(drops.stream().noneMatch(i->i.getItem().is(StarcrownDoll.YULE_ITEM.get())))throw new AssertionError("Yule loot");log("PASS breaking Yule source releases mobs and drops correct doll");for(var item:drops)item.discard();testPig.discard();level.setBlockAndUpdate(pos,StarcrownDoll.YULE_DOLL.get().defaultBlockState().setValue(net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING,net.minecraft.core.Direction.SOUTH));});
   if(ticks==300)server(p->{p.setItemSlot(EquipmentSlot.OFFHAND,ItemStack.EMPTY);p.teleportTo(p.serverLevel(),.5,81,2.4,180,12);});
   if(ticks==340)shot("placed-front");
   if(ticks==360)server(p->p.teleportTo(p.serverLevel(),2.4,81,.5,90,12));
   if(ticks==400)shot("placed-side");
   if(ticks==420){server(p->{p.setItemSlot(EquipmentSlot.HEAD,new ItemStack(StarcrownDoll.YULE_ITEM.get()));p.teleportTo(p.serverLevel(),.5,81,4.5,180,0);});mc.options.setCameraType(net.minecraft.client.CameraType.THIRD_PERSON_FRONT);}
   if(ticks==430)mc.options.keyJump.setDown(true);
   if(ticks==431)mc.options.keyJump.setDown(false);
   if(ticks==436)shot("headwear-jump");
   if(ticks==460)shot("headwear");
   if(ticks==480){server(p->p.setItemSlot(EquipmentSlot.MAINHAND,new ItemStack(StarcrownDoll.YULE_ITEM.get())));net.minecraft.world.item.CreativeModeTabs.tryRebuildTabContents(mc.level.enabledFeatures(),true,mc.level.registryAccess());
    var contents=StarcrownDoll.TAB.get().getDisplayItems();if(contents.stream().noneMatch(i->i.is(StarcrownDoll.ITEM.get()))||contents.stream().noneMatch(i->i.is(StarcrownDoll.YULE_ITEM.get())))throw new AssertionError("creative tab contents");log("PASS original and Yule dolls both in standalone creative tab");
    var screen=new net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen(mc.player,mc.level.enabledFeatures(),true);mc.setScreen(screen);
    for(var method:screen.getClass().getDeclaredMethods())if(method.getParameterCount()==1&&method.getParameterTypes()[0]==net.minecraft.world.item.CreativeModeTab.class&&method.getReturnType()==void.class){method.setAccessible(true);method.invoke(screen,StarcrownDoll.TAB.get());break;}}
   if(ticks==500)shot("inventory");
   if(ticks==520){log("PASS screenshots completed; inspect visual evidence separately");log("ALL_PASS");mc.setScreen(null);mc.stop();}
  }catch(Throwable t){log("FAIL "+t);t.printStackTrace();mc.stop();}
 }
}
