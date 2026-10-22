package dev.starcrown.mixin;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Pseudo
@Mixin(targets="com.erchien.arcanevortex.Content.Item.EntityRemover.SetAllBooleanField",priority=2000,remap=false)
public abstract class ArcaneGuard10 {
 @Inject(method="setAllBooleanFields(Lnet/minecraft/world/entity/Entity;ZLnet/minecraft/world/entity/player/Player;)Z",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av0(net.minecraft.world.entity.Entity a0,boolean a1,net.minecraft.world.entity.player.Player a2,CallbackInfoReturnable<Boolean> ci){if(dev.starcrown.DivineCombat.rejectCompatibility(a0,"AV.Content.Item.EntityRemover.SetAllBooleanField.setAllBooleanFields")){ci.setReturnValue(false);}}
 @Inject(method="invertAllBooleanFields(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/player/Player;)Z",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av1(net.minecraft.world.entity.Entity a0,net.minecraft.world.entity.player.Player a1,CallbackInfoReturnable<Boolean> ci){if(dev.starcrown.DivineCombat.rejectCompatibility(a0,"AV.Content.Item.EntityRemover.SetAllBooleanField.invertAllBooleanFields")){ci.setReturnValue(false);}}
 @Inject(method="modifyBooleanFields(Lnet/minecraft/world/entity/Entity;ZZLnet/minecraft/world/entity/player/Player;)Z",at=@At("HEAD"),cancellable=true,remap=false,require=0)
 private static void av2(net.minecraft.world.entity.Entity a0,boolean a1,boolean a2,net.minecraft.world.entity.player.Player a3,CallbackInfoReturnable<Boolean> ci){if(dev.starcrown.DivineCombat.rejectCompatibility(a0,"AV.Content.Item.EntityRemover.SetAllBooleanField.modifyBooleanFields")){ci.setReturnValue(false);}}
}
