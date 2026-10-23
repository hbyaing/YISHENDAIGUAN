package dev.starcrown.mixin;
import dev.starcrown.YuleHazards;
import net.minecraft.world.level.material.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(FlowingFluid.class)
public abstract class ToleranceFluid {
 @Inject(method={"spreadTo(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/material/FluidState;)V","m_6364_(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/material/FluidState;)V"},at=@At("RETURN"),remap=false,require=1)
 private void flow(LevelAccessor world,BlockPos pos,BlockState state,Direction direction,FluidState fluid,CallbackInfo ci){if(world instanceof ServerLevel level&&!level.getFluidState(pos).isEmpty()){var data=YuleHazards.get(level);var owner=data.owner(pos.relative(direction.getOpposite()));if(owner!=null)data.claim(pos,owner,false);}}
}
