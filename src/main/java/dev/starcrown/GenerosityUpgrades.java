package dev.starcrown;
import com.google.gson.*;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.*;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.*;
/** Pricing is world-persistent; resource reloads must not reset saved prices. */
public final class GenerosityUpgrades extends SimpleJsonResourceReloadListener {
 public GenerosityUpgrades(){super(new Gson(),"generosity_upgrades");}
 @Override protected void apply(Map<ResourceLocation,JsonElement> files,ResourceManager manager,ProfilerFiller profiler){}
 public static Item result(ItemStack stack){return GenerosityEmc.result(stack,net.minecraft.util.RandomSource.create());}
}
