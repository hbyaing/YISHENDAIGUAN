package dev.starcrown.mixin;
import dev.starcrown.YuleTemperance;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
@Mixin(BowItem.class)
public abstract class TemperanceBow {
 @Redirect(method={"releaseUsing","m_5551_"},at=@At(value="INVOKE",target="Lnet/minecraft/world/item/ArrowItem;isInfinite(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;)Z"),remap=false,require=1)
 private boolean infinite(ArrowItem arrow,ItemStack ammo,ItemStack bow,Player p){return YuleTemperance.active(p)||arrow.isInfinite(ammo,bow,p);}
}
