package dev.starcrown.mixin;
import dev.starcrown.YuleDiligence;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(ServerPlayer.class)
public abstract class DiligenceSleep {
 @Inject(method={"startSleepInBed(Lnet/minecraft/core/BlockPos;)Lcom/mojang/datafixers/util/Either;","m_7720_(Lnet/minecraft/core/BlockPos;)Lcom/mojang/datafixers/util/Either;"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void sleep(BlockPos pos,CallbackInfoReturnable<com.mojang.datafixers.util.Either<Player.BedSleepingProblem,net.minecraft.util.Unit>> c){if(YuleDiligence.active((ServerPlayer)(Object)this))c.setReturnValue(com.mojang.datafixers.util.Either.left(Player.BedSleepingProblem.OTHER_PROBLEM));}
 @Inject(method={"startSleeping(Lnet/minecraft/core/BlockPos;)V","m_5802_(Lnet/minecraft/core/BlockPos;)V"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void forcedSleep(BlockPos pos,CallbackInfo c){if(YuleDiligence.active((ServerPlayer)(Object)this))c.cancel();}
 @Inject(method={"setRespawnPosition(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/core/BlockPos;FZZ)V","m_9158_(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/core/BlockPos;FZZ)V"},at=@At("HEAD"),cancellable=true,remap=false,require=1)
 private void spawn(net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dimension,BlockPos pos,float angle,boolean forced,boolean send,CallbackInfo c){if(YuleDiligence.bedSpawn((ServerPlayer)(Object)this,dimension,pos,forced))c.cancel();}
}
