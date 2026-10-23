package dev.starcrownqa;
import dev.starcrown.*;
import java.nio.file.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
@Mod.EventBusSubscriber(modid="starcrownqa",value=net.minecraftforge.api.distmarker.Dist.CLIENT)
public final class HandsMusicQa {
 static int ticks;static SoundInstance background,record,dollSound;static BlockPos pos=new BlockPos(0,81,0);
 static void log(String s){try{Files.writeString(Path.of("hands-music-results.txt"),s+"\n",StandardOpenOption.CREATE,StandardOpenOption.APPEND);}catch(Exception e){throw new RuntimeException(e);}}
 static void check(boolean b,String s){if(!b)throw new AssertionError(s);log("PASS "+s);}
 static void server(java.util.function.Consumer<net.minecraft.server.level.ServerPlayer> action){var mc=Minecraft.getInstance();var id=mc.player.getUUID();mc.getSingleplayerServer().execute(()->{try{action.accept(mc.getSingleplayerServer().getPlayerList().getPlayer(id));}catch(Throwable t){log("FAIL "+t);}});}
 static void shot(String name){var mc=Minecraft.getInstance();net.minecraft.client.Screenshot.grab(mc.gameDirectory,name+".png",mc.getMainRenderTarget(),m->{});}
 static net.minecraftforge.client.event.sound.PlaySoundEvent soundEvent(SoundInstance sound){return new net.minecraftforge.client.event.sound.PlaySoundEvent(null,sound);}
 @SubscribeEvent public static void played(net.minecraftforge.client.event.sound.PlaySoundEvent e){if(e.getOriginalSound().getClass().getName().equals("dev.starcrown.DollMusic"))dollSound=e.getOriginalSound();}
 @SubscribeEvent public static void tick(TickEvent.ClientTickEvent event)throws Exception{
  if(event.phase!=TickEvent.Phase.END||!Files.exists(Path.of("enable-hands-music-qa")))return;
  var mc=Minecraft.getInstance();if(mc.player==null)return;ticks++;
  try{
   if(ticks==1)Files.writeString(Path.of("hands-music-results.txt"),"");
   if(ticks==115){mc.setScreen(null);mc.options.setCameraType(net.minecraft.client.CameraType.FIRST_PERSON);mc.options.getSoundSourceOptionInstance(SoundSource.MUSIC).set(1.0);mc.options.getSoundSourceOptionInstance(SoundSource.RECORDS).set(1.0);server(p->{p.setItemSlot(EquipmentSlot.MAINHAND,ItemStack.EMPTY);p.setItemSlot(EquipmentSlot.OFFHAND,ItemStack.EMPTY);});}
   if(ticks==140){check(mc.player.isInvisible(),"invisibility flag retained during first-person rendering");shot("hand-empty");server(p->p.setItemSlot(EquipmentSlot.MAINHAND,new ItemStack(StarcrownDoll.ITEM.get())));}
   if(ticks==170){shot("hand-doll");server(p->p.setItemSlot(EquipmentSlot.MAINHAND,MapItem.create(p.level(),0,0,(byte)0,false,false)));}
   if(ticks==200){shot("hand-map");server(p->p.setItemSlot(EquipmentSlot.MAINHAND,ItemStack.EMPTY));background=SimpleSoundInstance.forMusic(SoundEvents.MUSIC_GAME.value());record=SimpleSoundInstance.forRecord(SoundEvents.MUSIC_DISC_CAT,mc.player.position());mc.getSoundManager().play(background);mc.getSoundManager().play(record);}
   if(ticks==220){check(mc.getSoundManager().isActive(background)&&mc.getSoundManager().isActive(record),"background music and record active before doll");server(p->{p.serverLevel().setBlockAndUpdate(pos,StarcrownDoll.DOLL.get().defaultBlockState());((StarcrownDoll.DollEntity)p.serverLevel().getBlockEntity(pos)).toggleMusic();});}
   if(ticks==260){check(DollMusicPriority.exclusive(),"doll music priority active");check(dollSound!=null&&mc.getSoundManager().isActive(dollSound),"Chengran streamed loop actively playing");check(dollSound.getVolume()==2&&dollSound.isLooping(),"music has boosted spatial volume and loops");check(!mc.getSoundManager().isActive(background)&&!mc.getSoundManager().isActive(record),"already playing background music and records stopped");
    for(var s:new SoundInstance[]{SimpleSoundInstance.forMusic(SoundEvents.MUSIC_GAME.value()),SimpleSoundInstance.forRecord(SoundEvents.MUSIC_DISC_CAT,mc.player.position()),new SimpleSoundInstance(new net.minecraft.resources.ResourceLocation("examplemod","boss_bgm"),SoundSource.HOSTILE,1,1,net.minecraft.util.RandomSource.create(),false,0,SoundInstance.Attenuation.NONE,0,0,0,true)}){var e=soundEvent(s);DollMusicPriority.play(e);check(e.getSound()==null,"new competing music suppressed: "+s.getLocation());}
    var foot=soundEvent(SimpleSoundInstance.forUI(SoundEvents.STONE_STEP,1));DollMusicPriority.play(foot);check(foot.getSound()!=null,"ordinary effects retained");var own=soundEvent(dollSound);own.setSound(null);DollMusicPriority.play(own);check(own.getSound()==dollSound,"doll music wins playback event priority");
    server(p->{((StarcrownDoll.DollEntity)p.serverLevel().getBlockEntity(pos)).toggleMusic();p.serverLevel().setBlockAndUpdate(pos,StarcrownDoll.YULE_DOLL.get().defaultBlockState());((StarcrownDoll.DollEntity)p.serverLevel().getBlockEntity(pos)).toggleMusic();});
   }
   if(ticks==300){check(dollSound!=null&&dollSound.getLocation().equals(StarcrownDoll.YULE_MUSIC.get().getLocation())&&mc.getSoundManager().isActive(dollSound),"Yule streamed loop also prioritized");server(p->p.teleportTo(100.5,81,4.5));}
   if(ticks==330){check(!DollMusicPriority.exclusive(),"outside 32 block hearing range releases priority");server(p->p.teleportTo(.5,81,4.5));}
   if(ticks==360){check(DollMusicPriority.exclusive(),"returning to music restores priority");server(p->p.serverLevel().destroyBlock(pos,false));}
   if(ticks==390){check(!DollMusicPriority.exclusive(),"breaking music source releases priority");var e=soundEvent(SimpleSoundInstance.forMusic(SoundEvents.MUSIC_GAME.value()));DollMusicPriority.play(e);check(e.getSound()!=null,"normal music allowed again");check(mc.options.getSoundSourceVolume(SoundSource.MUSIC)==1&&mc.options.getSoundSourceVolume(SoundSource.RECORDS)==1,"volume sliders unchanged by priority policy");log("ALL_PASS");mc.stop();}
  }catch(Throwable t){log("FAIL "+t);t.printStackTrace();mc.stop();}
 }
}
