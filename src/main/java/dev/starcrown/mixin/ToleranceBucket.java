package dev.starcrown.mixin;
import dev.starcrown.*;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(BucketItem.class)
public abstract class ToleranceBucket {
 @Inject(method="emptyContents(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/BlockHitResult;Lnet/minecraft/world/item/ItemStack;)Z",at=@At("RETURN"),remap=false,require=1)
 private void placed(Player player,Level level,BlockPos pos,BlockHitResult hit,ItemStack stack,CallbackInfoReturnable<Boolean> ci){if(ci.getReturnValue()&&level instanceof ServerLevel server&&YuleTolerance.active(player)&&!server.getFluidState(pos).isEmpty())YuleHazards.get(server).claim(pos,player.getUUID(),false);}
}
