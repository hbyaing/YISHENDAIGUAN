package dev.starcrown.mixin;
import dev.starcrown.YuleTemperance;
import net.minecraft.server.level.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(ServerPlayerGameMode.class)
public abstract class TemperanceMining {
 @Unique private ServerPlayer temperance$player;
 @Inject(method="<init>(Lnet/minecraft/server/level/ServerPlayer;)V",at=@At("RETURN"),remap=false,require=1)
 private void bindOwner(ServerPlayer owner,CallbackInfo c){temperance$player=owner;}
 @Unique private final java.util.Deque<Block> temperance$blocks=new java.util.ArrayDeque<>();
 @Unique private final java.util.Deque<Boolean> temperance$active=new java.util.ArrayDeque<>();
 @Inject(method={"destroyBlock(Lnet/minecraft/core/BlockPos;)Z","m_9280_(Lnet/minecraft/core/BlockPos;)Z"},at=@At("HEAD"),remap=false,require=1)
 private void start(BlockPos pos,CallbackInfoReturnable<Boolean> c){temperance$blocks.push(temperance$player.serverLevel().getBlockState(pos).getBlock());temperance$active.push(YuleTemperance.active(temperance$player));}
 @Inject(method={"destroyBlock(Lnet/minecraft/core/BlockPos;)Z","m_9280_(Lnet/minecraft/core/BlockPos;)Z"},at=@At("RETURN"),remap=false,require=1)
 private void end(BlockPos pos,CallbackInfoReturnable<Boolean> c){var b=temperance$blocks.pop();boolean active=temperance$active.pop();if(c.getReturnValue()&&active&&b!=net.minecraft.world.level.block.Blocks.AIR){YuleTemperance.mined(temperance$player,pos,b);dev.starcrown.YuleDiligence.reward(temperance$player,1);}}
}
