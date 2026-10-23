package dev.starcrown;
import java.util.*;
import net.minecraft.nbt.*;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.server.MinecraftServer;
public final class GenerosityRewards extends SavedData {
 private final Set<UUID> players=new HashSet<>();
 public static GenerosityRewards get(MinecraftServer server){return server.overworld().getDataStorage().computeIfAbsent(GenerosityRewards::load,GenerosityRewards::new,"starcrowndoll_generosity");}
 public static GenerosityRewards load(CompoundTag tag){var data=new GenerosityRewards();for(var t:tag.getList("players",Tag.TAG_COMPOUND)){var e=(CompoundTag)t;if(e.hasUUID("id"))data.players.add(e.getUUID("id"));}return data;}
 public boolean has(UUID id){return players.contains(id);}
 public void grant(UUID id){if(players.add(id))setDirty();}
 @Override public CompoundTag save(CompoundTag tag){var list=new ListTag();for(var id:players){var t=new CompoundTag();t.putUUID("id",id);list.add(t);}tag.put("players",list);return tag;}
}
