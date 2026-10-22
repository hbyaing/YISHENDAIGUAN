package dev.starcrown.mixin;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class CreativeUnequip {
 @Inject(method={"handleSetCreativeModeSlot(Lnet/minecraft/network/protocol/game/ServerboundSetCreativeModeSlotPacket;)V","m_5964_(Lnet/minecraft/network/protocol/game/ServerboundSetCreativeModeSlotPacket;)V"},at=@At("HEAD"),remap=false,require=1)
 private void headPacket(ServerboundSetCreativeModeSlotPacket packet,CallbackInfo ci){var p=((ServerGamePacketListenerImpl)(Object)this).player;if(p.getServer()!=null&&p.getServer().isSameThread()&&!packet.getItem().is(dev.starcrown.StarcrownDoll.ITEM.get()))dev.starcrown.DivineAnchor.creativeUnequip(p,packet.getSlotNum());}
}
