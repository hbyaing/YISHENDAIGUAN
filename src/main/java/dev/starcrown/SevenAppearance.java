package dev.starcrown;
/** Client-local appearance only; never changes server gameplay. */
public final class SevenAppearance {
 public static double opacity=.25,stars=1.5,character=.8,size=1.12,speed=1,trailStrength=.3,glow=36,textSpeed=2;
 public static boolean trail=true,flow=true,showCharacter=true;
 private static final java.nio.file.Path FILE=net.minecraftforge.fml.loading.FMLPaths.CONFIGDIR.get().resolve("starcrowndoll-panel.json");
 static {try{var j=com.google.gson.JsonParser.parseString(java.nio.file.Files.readString(FILE)).getAsJsonObject();opacity=number(j,"opacity",opacity,.25,.98);stars=number(j,"stars",stars,0,1.5);character=number(j,"character",character,0,1);size=number(j,"size",size,.65,1.4);speed=number(j,"speed",speed,.35,2.5);trailStrength=number(j,"trailStrength",trailStrength,0,.55);glow=number(j,"glow",glow,0,36);textSpeed=number(j,"textSpeed",textSpeed,.5,3);trail=bool(j,"trail",trail);flow=bool(j,"flow",flow);showCharacter=bool(j,"showCharacter",showCharacter);}catch(Exception ignored){}}
 private static double number(com.google.gson.JsonObject j,String key,double fallback,double min,double max){if(!j.has(key))return fallback;double v=j.get(key).getAsDouble();return Double.isFinite(v)?Math.max(min,Math.min(max,v)):fallback;}
 private static boolean bool(com.google.gson.JsonObject j,String key,boolean fallback){return j.has(key)?j.get(key).getAsBoolean():fallback;}
 public static void save(){try{var j=new com.google.gson.JsonObject();j.addProperty("opacity",opacity);j.addProperty("stars",stars);j.addProperty("character",character);j.addProperty("size",size);j.addProperty("speed",speed);j.addProperty("trailStrength",trailStrength);j.addProperty("glow",glow);j.addProperty("textSpeed",textSpeed);j.addProperty("trail",trail);j.addProperty("flow",flow);j.addProperty("showCharacter",showCharacter);java.nio.file.Files.createDirectories(FILE.getParent());java.nio.file.Files.writeString(FILE,new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(j));}catch(Exception e){com.mojang.logging.LogUtils.getLogger().warn("Unable to save panel appearance",e);}}
 private SevenAppearance(){}
}
