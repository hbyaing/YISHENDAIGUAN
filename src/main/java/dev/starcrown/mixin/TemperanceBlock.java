package dev.starcrown.mixin;
import dev.starcrown.YuleTemperance;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
@Mixin(BlockItem.class)
public abstract class TemperanceBlock {
 @Group(name="consume",min=1)
 @Redirect(method={"place","m_40576_"},at=@At(value="INVOKE",target="Lnet/minecraft/world/item/ItemStack;shrink(I)V"),remap=false,require=0)
 private void consume0(ItemStack s,int n,net.minecraft.world.item.context.BlockPlaceContext context){if(!YuleTemperance.active(context.getPlayer()))s.shrink(n);}
 @Group(name="consume",min=1)
 @Redirect(method={"place","m_40576_"},at=@At(value="INVOKE",target="Lnet/minecraft/world/item/ItemStack;m_41774_(I)V"),remap=false,require=0)
 private void consume1(ItemStack s,int n,net.minecraft.world.item.context.BlockPlaceContext context){if(!YuleTemperance.active(context.getPlayer()))s.shrink(n);}
}
