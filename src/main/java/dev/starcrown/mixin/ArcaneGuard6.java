package dev.starcrown.mixin;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Pseudo
@Mixin(targets="com.erchien.arcanevortex.Help.DropLootHelp",priority=2000,remap=false)
public abstract class ArcaneGuard6 {
 @Inject(method="dropLoot(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/player/Player;Z)V",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av0(net.minecraft.world.entity.Entity a0,net.minecraft.world.entity.player.Player a1,boolean a2,CallbackInfo ci){if(dev.starcrown.DivineCombat.reject(a0,"AV.Help.DropLootHelp.dropLoot")){ci.cancel();}}
}
