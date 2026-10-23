package dev.starcrown.mixin;
import dev.starcrown.YuleGenerosity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
@Mixin(ServerPlayer.class)
public abstract class GenerosityQ {
 @Redirect(method={"drop(Z)Z","m_182294_(Z)Z"},at=@At(value="INVOKE",target="Lnet/minecraftforge/common/ForgeHooks;onPlayerTossEvent(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/world/entity/item/ItemEntity;"),remap=false,require=1)
 private ItemEntity q(Player player,ItemStack stack,boolean flag){return YuleGenerosity.manual(player,()->net.minecraftforge.common.ForgeHooks.onPlayerTossEvent(player,stack,flag));}
}
