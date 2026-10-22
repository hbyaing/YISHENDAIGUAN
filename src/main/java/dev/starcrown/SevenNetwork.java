package dev.starcrown;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.*;
import net.minecraftforge.network.simple.SimpleChannel;
public final class SevenNetwork {
 public static final SimpleChannel CHANNEL=NetworkRegistry.newSimpleChannel(new ResourceLocation(StarcrownDoll.ID,"seven"),()->"1",s->s.equals("1"),s->s.equals("1"));
 public record Action(int id,int value){static void encode(Action a,FriendlyByteBuf b){b.writeVarInt(a.id);b.writeVarInt(a.value);}static Action decode(FriendlyByteBuf b){return new Action(b.readVarInt(),b.readVarInt());}static void handle(Action a,Supplier<NetworkEvent.Context> supplier){var context=supplier.get();context.enqueueWork(()->{var p=context.getSender();if(p==null||a.id<0||a.id>17||a.value<0||a.value>7)return;long now=p.serverLevel().getGameTime();var data=p.getPersistentData();if(data.getLong("starcrown_last_action")==now)return;data.putLong("starcrown_last_action",now);DivineSeven.action(p,a.id,a.value);sync(p);});context.setPacketHandled(true);}}
 public record State(boolean domain,boolean night,boolean mine,boolean players,int speed){
  static void encode(State s,FriendlyByteBuf b){b.writeBoolean(s.domain);b.writeBoolean(s.night);b.writeBoolean(s.mine);b.writeBoolean(s.players);b.writeVarInt(s.speed);}
  static State decode(FriendlyByteBuf b){return new State(b.readBoolean(),b.readBoolean(),b.readBoolean(),b.readBoolean(),b.readVarInt());}
  static void handle(State s,Supplier<NetworkEvent.Context> supplier){var c=supplier.get();c.enqueueWork(()->net.minecraftforge.fml.DistExecutor.unsafeRunWhenOn(net.minecraftforge.api.distmarker.Dist.CLIENT,()->()->SevenClient.receive(s)));c.setPacketHandled(true);}
 }
 public static void sync(net.minecraft.server.level.ServerPlayer p){var d=DivineSeven.settings(p);CHANNEL.send(PacketDistributor.PLAYER.with(()->p),new State(d.getBoolean("domain"),!d.getBoolean("night_off"),d.getBoolean("area_mine"),!d.getBoolean("players_off"),d.contains("speed")?d.getInt("speed"):1));}
 public static void init(){CHANNEL.registerMessage(1,State.class,State::encode,State::decode,State::handle,java.util.Optional.of(NetworkDirection.PLAY_TO_CLIENT));CHANNEL.registerMessage(0,Action.class,Action::encode,Action::decode,Action::handle,java.util.Optional.of(NetworkDirection.PLAY_TO_SERVER));}
}
