package dev.starcrown;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.fml.common.Mod;
@Mod.EventBusSubscriber(modid=StarcrownDoll.ID,value=Dist.CLIENT)
public final class DollMusicPriority {
 private static final Set<SoundInstance> OTHERS=Collections.newSetFromMap(new WeakHashMap<>());
 public static boolean exclusive(){return DollMusic.suppressing();}
 static void stopOthers(){
  var manager=Minecraft.getInstance().getSoundManager();manager.stop(null,SoundSource.MUSIC);
  for(var sound:List.copyOf(OTHERS))manager.stop(sound);
  OTHERS.clear();
 }
 public static boolean music(SoundInstance sound){
  if(sound instanceof DollMusic)return false;
  var path=sound.getLocation().getPath();
  return sound.getSource()==SoundSource.MUSIC||sound.getSource()==SoundSource.RECORDS||path.contains("music")||path.contains("bgm");
 }
 @SubscribeEvent(priority=EventPriority.LOWEST)
 public static void play(PlaySoundEvent event){
  if(event.getOriginalSound() instanceof DollMusic){event.setSound(event.getOriginalSound());return;}
  var sound=event.getSound();if(sound==null||!music(sound))return;
  if(DollMusic.suppressing())event.setSound(null);else OTHERS.add(sound);
 }
 @SubscribeEvent public static void tick(TickEvent.ClientTickEvent event){
  if(event.phase!=TickEvent.Phase.END)return;
  var mc=Minecraft.getInstance();if(mc.level==null){OTHERS.clear();return;}
  if(!DollMusic.suppressing())return;
  stopOthers();
 }
}
