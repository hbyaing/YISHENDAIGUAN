package dev.starcrownqa;
import dev.starcrown.*;
import net.minecraft.gametest.framework.*;
import net.minecraftforge.gametest.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
@GameTestHolder("starcrownqa") @PrefixGameTestTemplate(false)
public final class MiningRangeTests {
 @GameTest(template="empty",timeoutTicks=150)
 public static void fourCubeSizes(GameTestHelper h){
  var l=h.getLevel();var p=new ServerPlayer(l.getServer(),l,new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(),"mining-range"));var c=new net.minecraft.network.Connection(net.minecraft.network.protocol.PacketFlow.SERVERBOUND);new io.netty.channel.embedded.EmbeddedChannel(c);c.setProtocol(net.minecraft.network.ConnectionProtocol.PLAY);p.connection=new net.minecraft.server.network.ServerGamePacketListenerImpl(l.getServer(),c,p);l.addNewPlayer(p);var center=h.absolutePos(new BlockPos(7,7,7));p.moveTo(center,0,0);p.setGameMode(net.minecraft.world.level.GameType.CREATIVE);p.setItemSlot(EquipmentSlot.HEAD,new ItemStack(StarcrownDoll.ITEM.get()));DivineSeven.settings(p).putBoolean("area_mine",true);
  try{for(int index=0;index<4;index++){
   DivineSeven.action(p,18,index);int r=index+1;h.assertTrue(DivineSeven.miningRadius(p)==r,"selected radius persisted");
   var edge=center.offset(r,0,0);var corner=center.offset(r,r,r);var outside=center.offset(r+1,0,0);var chest=center.above();var bedrock=center.below();
   for(var pos:new BlockPos[]{center,edge,corner,outside})l.setBlockAndUpdate(pos,Blocks.STONE.defaultBlockState());l.setBlockAndUpdate(chest,Blocks.CHEST.defaultBlockState());l.setBlockAndUpdate(bedrock,Blocks.BEDROCK.defaultBlockState());
   p.gameMode.destroyBlock(center);h.assertTrue(l.getBlockState(edge).isAir()&&l.getBlockState(corner).isAir(),"cube covers edge and 3D corner for "+(r*2+1));h.assertTrue(l.getBlockState(outside).is(Blocks.STONE),"outside cube retained");h.assertTrue(l.getBlockState(chest).is(Blocks.CHEST)&&l.getBlockState(bedrock).is(Blocks.BEDROCK),"containers and unbreakable blocks retained");
   for(var pos:new BlockPos[]{center,edge,corner,outside,chest,bedrock})l.setBlockAndUpdate(pos,Blocks.AIR.defaultBlockState());
  }
  DivineSeven.action(p,18,99);h.assertTrue(DivineSeven.miningRadius(p)==4,"invalid range is bounded");var nbt=net.minecraft.nbt.TagParser.parseTag(DivineSeven.settings(p).toString());h.assertTrue(nbt.getInt("mining_range")==3,"selected range survives serialization");h.succeed();
  }catch(Exception e){throw new RuntimeException(e);}finally{DivineAnchor.unlock(p);p.setItemSlot(EquipmentSlot.HEAD,ItemStack.EMPTY);l.removePlayerImmediately(p,Entity.RemovalReason.DISCARDED);c.disconnect(net.minecraft.network.chat.Component.literal("done"));}
 }
}
