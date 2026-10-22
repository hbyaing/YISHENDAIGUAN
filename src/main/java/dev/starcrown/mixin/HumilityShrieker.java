package dev.starcrown.mixin;
import dev.starcrown.YuleHumility;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.SculkShriekerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(SculkShriekerBlockEntity.class)
public abstract class HumilityShrieker {
 @Inject(method={"tryGetPlayer(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/server/level/ServerPlayer;","m_222861_(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/server/level/ServerPlayer;"},at=@At("RETURN"),cancellable=true,remap=false,require=1)
 private static void humilityShriek(Entity source,CallbackInfoReturnable<ServerPlayer> ci){if(YuleHumility.active(ci.getReturnValue()))ci.setReturnValue(null);}
}
