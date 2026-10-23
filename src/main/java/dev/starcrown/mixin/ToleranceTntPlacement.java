package dev.starcrown.mixin;
import dev.starcrown.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(BlockItem.class)
public abstract class ToleranceTntPlacement {
 @Inject(method={"place(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/InteractionResult;","m_40576_(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/InteractionResult;"},at=@At("HEAD"),remap=false,require=1)
 private void before(BlockPlaceContext ctx,CallbackInfoReturnable<InteractionResult> ci){if(ctx.getLevel() instanceof ServerLevel level&&YuleTolerance.active(ctx.getPlayer())&&((BlockItem)(Object)this).getBlock()==Blocks.TNT)YuleHazards.get(level).claim(ctx.getClickedPos(),ctx.getPlayer().getUUID(),true);}
 @Inject(method={"place(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/InteractionResult;","m_40576_(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/InteractionResult;"},at=@At("RETURN"),remap=false,require=1)
 private void after(BlockPlaceContext ctx,CallbackInfoReturnable<InteractionResult> ci){if(!ci.getReturnValue().consumesAction()&&ctx.getLevel() instanceof ServerLevel level&&YuleTolerance.active(ctx.getPlayer())&&((BlockItem)(Object)this).getBlock()==Blocks.TNT)YuleHazards.get(level).forgetTnt(ctx.getClickedPos());}
}
