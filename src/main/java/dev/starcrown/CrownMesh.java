package dev.starcrown;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Vector3f;
/** One local anchor for block, item and player-head renderers. Units are blocks. */
public final class CrownMesh {
 static final float CX=0,CZ=.125f,BASE=1.067f,R=.291f;
 static final java.util.List<float[]> VERTICES=new java.util.ArrayList<>();
 static void render(PoseStack p,MultiBufferSource buffers){
  if(VERTICES.isEmpty())build(null,null);
  var v=buffers.getBuffer(RenderType.entityCutoutNoCull(DollRenderer.TEX));
  for(float[] q:VERTICES)v.vertex(p.last().pose(),q[0],q[1],q[2]).color(q[3],q[4],q[5],1).uv(.5f,.5f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(p.last().normal(),q[6],q[7],q[8]).endVertex();
 }
 static void build(PoseStack p,VertexConsumer v){
  band(v,p,R,BASE,.014f,.024f);
  band(v,p,R+.002f,BASE+.03f,.006f,.008f);
  // Fine hanging scallops and round gemstone settings.
  for(int i=0;i<96;i++){
   double a=i*Math.PI/48,b=(i+1)*Math.PI/48;
   float y=BASE+.048f+(float)Math.abs(Math.sin(a*5))*.025f,Y=BASE+.048f+(float)Math.abs(Math.sin(b*5))*.025f;
   rod(v,p,pt(a,R,y),pt(b,R,Y),.0028f,1,.79f,.42f);
  }
  for(int i=0;i<10;i++){
   double a=-Math.PI/2+i*Math.PI/5;float x=(float)Math.cos(a)*R,z=CZ+(float)Math.sin(a)*R;
   float height=i%2==0?.215f:.135f;
   rod(v,p,new Vector3f(x,BASE+.025f,z),new Vector3f(x,BASE+height-.028f,z),.004f,.94f,.73f,.38f);
   diamond(v,p,x,BASE+height-.013f,z,.009f,.047f,.009f,1,.87f,.6f);
   star(v,p,x,BASE+height-.047f,z,.025f,.042f,.007f,1,.85f,.55f);
   diamond(v,p,x,BASE+.048f,z,.009f,.014f,.01f,.49f,.18f,1);
  }
  // Front marquise jewel, a gold surround and eight actual jewel facets.
  diamond(v,p,0,BASE+.162f,CZ-R-.006f,.068f,.117f,.015f,1,.84f,.51f);
  diamond(v,p,0,BASE+.162f,CZ-R-.022f,.049f,.09f,.02f,.76f,.12f,1);
  star(v,p,0,BASE+.293f,CZ-R-.006f,.026f,.04f,.009f,1,.91f,.72f);
  diamond(v,p,0,BASE+.144f,CZ+R+.006f,.044f,.082f,.014f,1,.83f,.5f);
  diamond(v,p,0,BASE+.144f,CZ+R+.021f,.03f,.06f,.017f,.65f,.15f,1);
  for(int i=0;i<20;i++){
   double a=i*Math.PI/10;float x=(float)Math.cos(a)*R,z=CZ+(float)Math.sin(a)*R;
   diamond(v,p,x,BASE+.008f,z,.0045f,.007f,.005f,1,.92f,.72f);
  }
 }
 static Vector3f pt(double a,float r,float y){return new Vector3f((float)Math.cos(a)*r,y,CZ+(float)Math.sin(a)*r);}
 static void band(VertexConsumer v,PoseStack p,float r,float y,float thick,float h){
  float[][] profile={{r-thick,y+h*.2f},{r-thick*.65f,y},{r+thick*.65f,y},{r+thick,y+h*.2f},{r+thick,y+h*.8f},{r+thick*.65f,y+h},{r-thick*.65f,y+h},{r-thick,y+h*.8f}};
  for(int i=0;i<96;i++)for(int j=0;j<profile.length;j++){
   int k=(j+1)%profile.length;double a=i*Math.PI/48,b=(i+1)*Math.PI/48;
   float shade=.75f+.25f*(float)(.5+.5*Math.cos(a-.7));
   quad(v,p,pt(a,profile[j][0],profile[j][1]),pt(b,profile[j][0],profile[j][1]),pt(b,profile[k][0],profile[k][1]),pt(a,profile[k][0],profile[k][1]),shade,.81f*shade,.48f*shade);
  }
 }
 static void diamond(VertexConsumer v,PoseStack p,float x,float y,float z,float w,float h,float d,float r,float g,float b){
  Vector3f[] points={new Vector3f(x,y+h,z),new Vector3f(x+w,y,z),new Vector3f(x,y-h,z),new Vector3f(x-w,y,z)};
  for(int side=-1;side<=1;side+=2)for(int i=0;i<4;i++){
   float shade=new float[]{1,.76f,.52f,.89f}[i];tri(v,p,points[i],points[(i+1)%4],new Vector3f(x,y,z+side*d),r*shade,g*shade,b*shade);
  }
 }
 static void star(VertexConsumer v,PoseStack p,float x,float y,float z,float w,float h,float d,float r,float g,float b){
  Vector3f center=new Vector3f(x,y,z-d);
  Vector3f[] points=new Vector3f[8];for(int i=0;i<8;i++){double a=i*Math.PI/4;float radius=i%2==0?1:.24f;points[i]=new Vector3f(x+(float)Math.sin(a)*w*radius,y+(float)Math.cos(a)*h*radius,z);}
  for(int i=0;i<8;i++)tri(v,p,points[i],points[(i+1)%8],center,r,g*(i%2==0?1:.8f),b);
 }
 static void rod(VertexConsumer v,PoseStack p,Vector3f a,Vector3f b,float radius,float r,float g,float bl){
  Vector3f axis=new Vector3f(b).sub(a).normalize(),u=new Vector3f(axis).cross(Math.abs(axis.y)<.9?new Vector3f(0,1,0):new Vector3f(1,0,0)).normalize(),w=new Vector3f(axis).cross(u).normalize();
  for(int i=0;i<6;i++){
   double x=i*Math.PI/3,y=(i+1)*Math.PI/3;
   Vector3f o=new Vector3f(u).mul((float)Math.cos(x)*radius).add(new Vector3f(w).mul((float)Math.sin(x)*radius)),q=new Vector3f(u).mul((float)Math.cos(y)*radius).add(new Vector3f(w).mul((float)Math.sin(y)*radius));
   quad(v,p,new Vector3f(a).add(o),new Vector3f(b).add(o),new Vector3f(b).add(q),new Vector3f(a).add(q),r,g,bl);
  }
 }
 static void tri(VertexConsumer v,PoseStack p,Vector3f a,Vector3f b,Vector3f c,float r,float g,float bl){quad(v,p,a,b,c,c,r,g,bl);}
 static void quad(VertexConsumer v,PoseStack p,Vector3f a,Vector3f b,Vector3f c,Vector3f d,float r,float g,float bl){
  Vector3f n=new Vector3f(b).sub(a).cross(new Vector3f(c).sub(a)).normalize();
  for(Vector3f q:new Vector3f[]{a,b,c,d})VERTICES.add(new float[]{q.x,q.y,q.z,r,g,bl,n.x,n.y,n.z});
 }
}
