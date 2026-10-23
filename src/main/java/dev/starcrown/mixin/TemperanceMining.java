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
 @Shadow(aliases={"f_9245_"},remap=false) @Final protected ServerPlayer player;
 @Shadow(aliases={"f_9244_"},remap=false) protected ServerLevel level;
 @Unique private final java.util.Deque<Block> temperance$blocks=new java.util.ArrayDeque<>();
 @Unique private final java.util.Deque<Boolean> temperance$active=new java.util.ArrayDeque<>();
 @Inject(method={"destroyBlock(Lnet/minecraft/core/BlockPos;)Z","m_9280_(Lnet/minecraft/core/BlockPos;)Z"},at=@At("HEAD"),remap=false,require=1)
 private void start(BlockPos pos,CallbackInfoReturnable<Boolean> c){temperance$blocks.push(level.getBlockState(pos).getBlock());temperance$active.push(YuleTemperance.active(player));}
 @Inject(method={"destroyBlock(Lnet/minecraft/core/BlockPos;)Z","m_9280_(Lnet/minecraft/core/BlockPos;)Z"},at=@At("RETURN"),remap=false,require=1)
 private void end(BlockPos pos,CallbackInfoReturnable<Boolean> c){var b=temperance$blocks.pop();boolean active=temperance$active.pop();if(c.getReturnValue()&&active&&b!=net.minecraft.world.level.block.Blocks.AIR){YuleTemperance.mined(player,pos,b);dev.starcrown.YuleDiligence.reward(player,1);}}
}
