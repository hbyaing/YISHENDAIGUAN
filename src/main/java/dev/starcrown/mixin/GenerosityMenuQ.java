package dev.starcrown.mixin;
import dev.starcrown.YuleGenerosity;
import net.minecraft.world.inventory.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
@Mixin(AbstractContainerMenu.class)
public abstract class GenerosityMenuQ {
 @Redirect(method={"doClick","m_150430_"},at=@At(value="INVOKE",target="Lnet/minecraft/world/entity/player/Player;drop(Lnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/world/entity/item/ItemEntity;",remap=false),remap=false,require=0)
 private ItemEntity menu(Player p,ItemStack stack,boolean flag,int slot,int button,ClickType type,Player context){return type==ClickType.THROW?YuleGenerosity.manual(p,()->p.drop(stack,flag)):p.drop(stack,flag);}
 @Redirect(method={"doClick","m_150430_"},at=@At(value="INVOKE",target="Lnet/minecraft/world/entity/player/Player;m_36176_(Lnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/world/entity/item/ItemEntity;",remap=false),remap=false,require=0)
 private ItemEntity menuProduction(Player p,ItemStack stack,boolean flag,int slot,int button,ClickType type,Player context){return type==ClickType.THROW?YuleGenerosity.manual(p,()->p.drop(stack,flag)):p.drop(stack,flag);}
}
