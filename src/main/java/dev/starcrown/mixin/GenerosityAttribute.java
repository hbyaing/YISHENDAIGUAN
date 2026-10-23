package dev.starcrown.mixin;
import dev.starcrown.YuleGenerosity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(AttributeInstance.class)
public abstract class GenerosityAttribute {
 @Inject(method={"getValue()D","m_22135_()D"},at=@At("RETURN"),cancellable=true,remap=false,require=1)
 private void doubled(CallbackInfoReturnable<Double> ci){var a=(AttributeInstance)(Object)this;double value=dev.starcrown.YuleDiligence.attributeValue(a,ci.getReturnValue());if(YuleGenerosity.boosted(a)&&Double.isFinite(value))value=Math.max(-Double.MAX_VALUE,Math.min(Double.MAX_VALUE,value*2));ci.setReturnValue(value);}
}
