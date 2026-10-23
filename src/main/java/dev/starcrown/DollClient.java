package dev.starcrown;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraft.client.*;
import net.minecraft.client.particle.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.core.particles.SimpleParticleType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
@Mod.EventBusSubscriber(modid=StarcrownDoll.ID,bus=Mod.EventBusSubscriber.Bus.MOD,value=Dist.CLIENT)
public class DollClient {
 public static void yuleTooltip(java.util.List<net.minecraft.network.chat.Component> lines,boolean expanded){
  if(expanded){
   lines.add(net.minecraft.network.chat.Component.translatable("tooltip.starcrowndoll.yule.virtues").withStyle(net.minecraft.ChatFormatting.GOLD));
   var description=net.minecraft.network.chat.Component.translatable("tooltip.starcrowndoll.yule.humility").withStyle(net.minecraft.ChatFormatting.GRAY);
   lines.add(net.minecraft.network.chat.Component.translatable("tooltip.starcrowndoll.yule.humility.name").withStyle(net.minecraft.ChatFormatting.BOLD).append(description.withStyle(net.minecraft.network.chat.Style.EMPTY.withColor(net.minecraft.ChatFormatting.GRAY).withBold(false))));
   lines.add(net.minecraft.network.chat.Component.translatable("tooltip.starcrowndoll.yule.tolerance.name").withStyle(net.minecraft.ChatFormatting.BOLD).append(net.minecraft.network.chat.Component.translatable("tooltip.starcrowndoll.yule.tolerance").withStyle(net.minecraft.network.chat.Style.EMPTY.withColor(net.minecraft.ChatFormatting.GRAY).withBold(false))));
   lines.add(net.minecraft.network.chat.Component.translatable("tooltip.starcrowndoll.yule.generosity.name").withStyle(net.minecraft.ChatFormatting.BOLD).append(net.minecraft.network.chat.Component.translatable("tooltip.starcrowndoll.yule.generosity").withStyle(net.minecraft.network.chat.Style.EMPTY.withColor(net.minecraft.ChatFormatting.GRAY).withBold(false))));
   for(String key:new String[]{"yule.animation","wear","music","yule.feast","place"})lines.add(net.minecraft.network.chat.Component.translatable("tooltip.starcrowndoll."+key).withStyle(net.minecraft.ChatFormatting.GRAY));
  }
  else lines.add(net.minecraft.network.chat.Component.translatable("tooltip.starcrowndoll.shift").withStyle(net.minecraft.network.chat.Style.EMPTY.withColor(0x999999)));
 }
 @SubscribeEvent public static void musicSetup(net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent e){e.enqueueWork(()->StarcrownDoll.musicListener=DollMusic::toggle);}
 @SubscribeEvent public static void particles(RegisterParticleProvidersEvent e){e.registerSpriteSet(StarcrownDoll.STAR.get(),Provider::new);}
 @SubscribeEvent public static void layers(EntityRenderersEvent.AddLayers e){for(String skin:e.getSkins()){
  var renderer=e.getSkin(skin);if(renderer instanceof net.minecraft.client.renderer.entity.player.PlayerRenderer pr)pr.addLayer(new HeadLayer(pr));
 }}
 static class HeadLayer extends RenderLayer<AbstractClientPlayer,PlayerModel<AbstractClientPlayer>> {
  HeadLayer(net.minecraft.client.renderer.entity.RenderLayerParent<AbstractClientPlayer,PlayerModel<AbstractClientPlayer>> p){super(p);}
  public void render(PoseStack p,MultiBufferSource b,int light,AbstractClientPlayer player,float a,float c,float partial,float age,float yaw,float pitch){
   if(!StarcrownDoll.isDoll(player.getItemBySlot(EquipmentSlot.HEAD))||player.isInvisible())return;
   p.pushPose();getParentModel().getHead().translateAndRotate(p);p.translate(0,-.51,0);p.mulPose(Axis.ZP.rotationDegrees(180));p.scale(.52f,.52f,.52f);
   p.pushPose();p.translate(-.5,0,-.5);DollRenderer.renderBody(StarcrownDoll.dollState(player.getItemBySlot(EquipmentSlot.HEAD)),p,b,light,net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,DollAnimation.jumpAmount(player,partial));p.popPose();
   DollRenderer.renderEffects((player.level().getGameTime()+partial)/20.,p,b,player.getItemBySlot(EquipmentSlot.HEAD).is(StarcrownDoll.ITEM.get()));p.popPose();
  }
 }
 static class Star extends TextureSheetParticle {
  int grounded;
  Star(ClientLevel l,double x,double y,double z,double dx,double dy,double dz,SpriteSet sprites){super(l,x,y,z,dx,dy,dz);xd=dx;yd=dy;zd=dz;gravity=.012f;friction=.985f;hasPhysics=true;lifetime=200;quadSize=.035f+random.nextFloat()*.025f;rCol=.8f;gCol=.3f;bCol=1;pickSprite(sprites);}
  public ParticleRenderType getRenderType(){return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;}
  protected int getLightColor(float partial){return 15728880;}
  public void tick(){super.tick();if(onGround){grounded++;xd*=.6;zd*=.6;}alpha=Math.min(1,age/6f)*Math.min(1,(lifetime-age)/25f)*(grounded>0?Math.max(0,1-grounded/24f):1);if(grounded>=24)remove();}
 }
 record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType>{public Particle createParticle(SimpleParticleType type,ClientLevel l,double x,double y,double z,double dx,double dy,double dz){return new Star(l,x,y,z,dx,dy,dz,sprites);}}
 @Mod.EventBusSubscriber(modid=StarcrownDoll.ID,value=Dist.CLIENT)
 public static class WorldEvents {
  @SubscribeEvent public static void yuleTooltipWidth(RenderTooltipEvent.GatherComponents event){if(event.getItemStack().is(StarcrownDoll.YULE_ITEM.get()))event.setMaxWidth(Math.min(340,event.getScreenWidth()-24));}
  @SubscribeEvent public static void humilitySizes(TickEvent.ClientTickEvent event){if(event.phase==TickEvent.Phase.END&&Minecraft.getInstance().level!=null)for(var player:Minecraft.getInstance().level.players())YuleHumility.updateSize(player);}
  @SubscribeEvent public static void tooltip(net.minecraftforge.event.entity.player.ItemTooltipEvent e){
   if(!StarcrownDoll.isDoll(e.getItemStack()))return;
   if(e.getItemStack().is(StarcrownDoll.YULE_ITEM.get())){
    yuleTooltip(e.getToolTip(),net.minecraft.client.gui.screens.Screen.hasShiftDown());
    return;
   }
   if(net.minecraft.client.gui.screens.Screen.hasShiftDown()){
    for(String key:DollTooltip.KEYS)e.getToolTip().add(net.minecraft.network.chat.Component.translatable("tooltip.starcrowndoll."+key).withStyle(net.minecraft.ChatFormatting.GRAY));
   }else e.getToolTip().add(net.minecraft.network.chat.Component.translatable("tooltip.starcrowndoll.shift").withStyle(net.minecraft.network.chat.Style.EMPTY.withColor(0x999999)));
  }

  @SubscribeEvent public static void tick(TickEvent.PlayerTickEvent e){
   var p=e.player;if(e.phase!=TickEvent.Phase.END||!p.level().isClientSide||p.tickCount%4!=0||!p.getItemBySlot(EquipmentSlot.HEAD).is(StarcrownDoll.ITEM.get()))return;
   // Spawn in world space; particles no longer follow the wearer after emission.
   StarcrownDoll.emit(p.level(),p.getX(),p.getY()+p.getBbHeight()+.64,p.getZ(),.23);
  }
 }
}

