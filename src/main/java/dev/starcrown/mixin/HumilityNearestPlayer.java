package dev.starcrown.mixin;
import dev.starcrown.YuleHumility;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.EntityGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import java.util.function.Predicate;
import org.spongepowered.asm.mixin.Mixin;
/** Explicit Level override: Mixin 0.8.5 cannot inject into EntityGetter's default method. */
@Mixin(Level.class)
public abstract class HumilityNearestPlayer implements EntityGetter {
 @Override public Player getNearestPlayer(double x,double y,double z,double range,Predicate<Entity> predicate){
  Player nearest=null;double best=Double.MAX_VALUE;
  for(var player:players()){
   if(YuleHumility.active(player)||(predicate!=null&&!predicate.test(player)))continue;
   double distance=player.distanceToSqr(x,y,z);
   if((range<0||distance<range*range)&&distance<best){nearest=player;best=distance;}
  }
  return nearest;
 }
}
