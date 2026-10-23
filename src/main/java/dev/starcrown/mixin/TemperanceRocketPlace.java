package dev.starcrown.mixin;
import dev.starcrown.YuleTemperance;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
@Mixin(FireworkRocketItem.class)
public abstract class TemperanceRocketPlace {
 @Group(name="consume",min=1)
 @Redirect(method={"useOn","m_6225_"},at=@At(value="INVOKE",target="Lnet/minecraft/world/item/ItemStack;shrink(I)V"),remap=false,require=0)
 private void consume0(ItemStack s,int n,net.minecraft.world.item.context.UseOnContext context){if(!YuleTemperance.active(context.getPlayer()))s.shrink(n);}
 @Group(name="consume",min=1)
 @Redirect(method={"useOn","m_6225_"},at=@At(value="INVOKE",target="Lnet/minecraft/world/item/ItemStack;m_41774_(I)V"),remap=false,require=0)
 private void consume1(ItemStack s,int n,net.minecraft.world.item.context.UseOnContext context){if(!YuleTemperance.active(context.getPlayer()))s.shrink(n);}
}
