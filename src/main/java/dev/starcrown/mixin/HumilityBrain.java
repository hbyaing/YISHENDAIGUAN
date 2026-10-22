package dev.starcrown.mixin;
import dev.starcrown.YuleHumility;
import net.minecraft.world.entity.ai.Brain;
import java.util.Optional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
@Mixin(Brain.class)
public abstract class HumilityBrain {
 @ModifyVariable(method={"setMemoryInternal(Lnet/minecraft/world/entity/ai/memory/MemoryModuleType;Ljava/util/Optional;)V","m_21941_(Lnet/minecraft/world/entity/ai/memory/MemoryModuleType;Ljava/util/Optional;)V"},at=@At("HEAD"),argsOnly=true,ordinal=0,remap=false,require=1)
 private Optional<?> humilityMemory(Optional<?> next){return YuleHumility.memory(next);}
}
