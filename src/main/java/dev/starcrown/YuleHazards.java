package dev.starcrown;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.tags.FluidTags;

/** Persisted provenance stays harmless even after the owner logs out or removes the doll. */
public final class YuleHazards extends SavedData {
 private final Map<BlockPos,UUID> blocks=new HashMap<>(),tnt=new HashMap<>();
 public static YuleHazards get(ServerLevel level){return level.getDataStorage().computeIfAbsent(YuleHazards::load,YuleHazards::new,"starcrowndoll_tolerance");}
 public static YuleHazards load(CompoundTag tag){var data=new YuleHazards();read(tag,"blocks",data.blocks);read(tag,"tnt",data.tnt);return data;}
 private static void read(CompoundTag tag,String key,Map<BlockPos,UUID> target){for(var entry:tag.getList(key,Tag.TAG_COMPOUND)){var t=(CompoundTag)entry;if(t.hasUUID("owner"))target.put(BlockPos.of(t.getLong("pos")),t.getUUID("owner"));}}
 private static ListTag write(Map<BlockPos,UUID> entries){var list=new ListTag();entries.forEach((p,id)->{var t=new CompoundTag();t.putLong("pos",p.asLong());t.putUUID("owner",id);list.add(t);});return list;}
 @Override public CompoundTag save(CompoundTag tag){tag.put("blocks",write(blocks));tag.put("tnt",write(tnt));return tag;}
 public void claim(BlockPos pos,UUID owner,boolean explosive){var map=explosive?tnt:blocks;if(!owner.equals(map.put(pos.immutable(),owner)))setDirty();}
 public UUID owner(BlockPos pos){return blocks.get(pos);}
 public UUID consumeTnt(BlockPos pos){UUID id=tnt.remove(pos);if(id!=null)setDirty();return id;}
 public void forgetTnt(BlockPos pos){if(tnt.remove(pos)!=null)setDirty();}
 public void changed(ServerLevel level,BlockPos pos,net.minecraft.world.level.block.state.BlockState state){
  if(!state.is(Blocks.TNT))forgetTnt(pos);
  if(state.isAir()){if(blocks.remove(pos)!=null)setDirty();return;}
  // Fire and fluid descendants inherit provenance; ordinary neighboring blocks do not.
  if(!blocks.containsKey(pos)&&(state.is(net.minecraft.tags.BlockTags.FIRE)||state.getFluidState().is(FluidTags.LAVA)||state.getFluidState().is(FluidTags.WATER))){
   for(var direction:net.minecraft.core.Direction.values()){var neighbor=pos.relative(direction);UUID id=blocks.get(neighbor);if(id!=null){claim(pos,id,false);break;}}
  }
 }
 public UUID contact(net.minecraft.world.entity.Entity entity){
  if(blocks.isEmpty())return null;
  var box=entity.getBoundingBox().inflate(.12);
  double cells=(box.getXsize()+2)*(box.getYsize()+3)*(box.getZsize()+2);
  if(blocks.size()<cells){for(var entry:blocks.entrySet()){var pos=entry.getKey();if(pos.getX()>=net.minecraft.util.Mth.floor(box.minX)&&pos.getX()<=net.minecraft.util.Mth.floor(box.maxX)&&pos.getY()>=net.minecraft.util.Mth.floor(box.minY)-1&&pos.getY()<=net.minecraft.util.Mth.floor(box.maxY)&&pos.getZ()>=net.minecraft.util.Mth.floor(box.minZ)&&pos.getZ()<=net.minecraft.util.Mth.floor(box.maxZ))return entry.getValue();}return null;}
  for(var pos:BlockPos.betweenClosed(net.minecraft.util.Mth.floor(box.minX),net.minecraft.util.Mth.floor(box.minY)-1,net.minecraft.util.Mth.floor(box.minZ),net.minecraft.util.Mth.floor(box.maxX),net.minecraft.util.Mth.floor(box.maxY),net.minecraft.util.Mth.floor(box.maxZ))){UUID id=blocks.get(pos);if(id!=null)return id;}
  return null;
 }
}
