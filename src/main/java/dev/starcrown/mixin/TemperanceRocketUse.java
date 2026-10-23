package dev.starcrown.mixin;
import dev.starcrown.YuleTemperance;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
@Mixin(FireworkRocketItem.class)
public abstract class TemperanceRocketUse {
 @Group(name="consume",min=1)
 @Redirect(method={"use","m_7203_"},at=@At(value="INVOKE",target="Lnet/minecraft/world/item/ItemStack;shrink(I)V"),remap=false,require=0)
 private void consume0(ItemStack s,int n,net.minecraft.world.level.Level l,net.minecraft.world.entity.player.Player p,net.minecraft.world.InteractionHand hand){if(!YuleTemperance.active(p))s.shrink(n);}
 @Group(name="consume",min=1)
 @Redirect(method={"use","m_7203_"},at=@At(value="INVOKE",target="Lnet/minecraft/world/item/ItemStack;m_41774_(I)V"),remap=false,require=0)
 private void consume1(ItemStack s,int n,net.minecraft.world.level.Level l,net.minecraft.world.entity.player.Player p,net.minecraft.world.InteractionHand hand){if(!YuleTemperance.active(p))s.shrink(n);}
}
