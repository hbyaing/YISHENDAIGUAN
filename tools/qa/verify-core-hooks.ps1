param(
 [Parameter(Mandatory=$true)][string]$MinecraftJar,
 [Parameter(Mandatory=$true)][string]$Javap
)
$ErrorActionPreference='Stop'
$projectRoot=[IO.Path]::GetFullPath((Join-Path $PSScriptRoot '../..'))
$targets=@{
 'DiligenceSleep'='net.minecraft.server.level.ServerPlayer'
 'CharityItem'='net.minecraft.world.entity.item.ItemEntity'
 'CharityInventory'='net.minecraft.world.entity.player.Inventory'
 'CharitySlot'='net.minecraft.world.inventory.Slot'
 'CharityOrb'='net.minecraft.world.entity.ExperienceOrb'
 'TemperanceStack'='net.minecraft.world.item.ItemStack'
 'TemperancePlayer'='net.minecraft.world.entity.player.Player'
 'TemperanceFood'='net.minecraft.world.food.FoodData'
 'TemperanceSlot'='net.minecraft.world.inventory.Slot'
 'TemperanceMining'='net.minecraft.server.level.ServerPlayerGameMode'
 'CreativeUnequip'='net.minecraft.server.network.ServerGamePacketListenerImpl'
 'InventoryAnchor'='net.minecraft.world.entity.player.Inventory'
 'ChastityPlayer'='net.minecraft.world.entity.player.Player'
 'ChastityAnimal'='net.minecraft.world.entity.animal.Animal'
 'ChastityPet'='net.minecraft.world.entity.TamableAnimal'
 'ChastityHorse'='net.minecraft.world.entity.animal.horse.AbstractHorse'
 'ChastityPotion'='net.minecraft.world.item.PotionItem'
 'ChastityTradeSlot'='net.minecraft.world.inventory.Slot'
 'ChastityTrade'='net.minecraft.world.inventory.MerchantMenu'
 'ChastityUse'='net.minecraft.world.entity.LivingEntity'
 'LivingGuard'='net.minecraft.world.entity.LivingEntity'
 'PlayerGuard'='net.minecraft.world.entity.player.Player'
 'ServerPlayerGuard'='net.minecraft.server.level.ServerPlayer'
 'VitalDataGuard'='net.minecraft.network.syncher.SynchedEntityData'
 'EntityGuard'='net.minecraft.world.entity.Entity'
 'ServerLevelGuard'='net.minecraft.server.level.ServerLevel'
 'MobStillness'='net.minecraft.world.entity.Mob'
 'HumilityLiving'='net.minecraft.world.entity.LivingEntity'
 'HumilityPlayerDamage'=@('net.minecraft.world.entity.player.Player','net.minecraft.server.level.ServerPlayer')
 'HumilityTargeting'='net.minecraft.world.entity.ai.targeting.TargetingConditions'
 'HumilitySensing'='net.minecraft.world.entity.ai.sensing.Sensing'
 'HumilityBrain'='net.minecraft.world.entity.ai.Brain'
 'HumilityShrieker'='net.minecraft.world.level.block.entity.SculkShriekerBlockEntity'
 'HumilityDragon'='net.minecraft.world.entity.boss.enderdragon.phases.DragonStrafePlayerPhase'
 'HumilityWither'='net.minecraft.world.entity.boss.wither.WitherBoss'
 'HumilityPlayerRenderer'='net.minecraft.client.renderer.entity.player.PlayerRenderer'
}
$checked=0
foreach($mixin in $targets.Keys){
 foreach($target in @($targets[$mixin])){
 $dump=& $Javap -classpath $MinecraftJar -p -s $target
 if($LASTEXITCODE -ne 0){throw "javap failed: $target"}
 $methods=@();$methodName=$null
 foreach($line in $dump){
  if($line -match '\s([^\s(]+)\([^)]*\).*;\s*$'){
   $methodName=$Matches[1]
   if($methodName -eq $target){$methodName='<init>'}
  }elseif($line -match '^\s+descriptor: (\(.*)$'){
   if($null -ne $methodName){$methods+=($methodName+$Matches[1])}
   $methodName=$null
  }
 }
 $source=[IO.File]::ReadAllText((Join-Path $projectRoot "src/main/java/dev/starcrown/mixin/$mixin.java"))
 $injections=[regex]::Matches($source,'@(?:Inject|ModifyVariable)\(method=(\{[^}]+\}|"[^"]+")[^\r\n]*require=([1-9][0-9]*)\)')
 foreach($injection in $injections){
  $selectors=[regex]::Matches($injection.Groups[1].Value,'"([^"]+)"') | ForEach-Object { $_.Groups[1].Value }
  $found=@($methods | Where-Object { $signature=$_; @($selectors | Where-Object { if($_.Contains('(')){$signature -eq $_}else{$signature.StartsWith($_+'(')} }).Count -gt 0 })
  if($found.Count -lt [int]$injection.Groups[2].Value){throw "Missing required hook in ${target}: $($selectors -join ', ')"}
  $checked++
 }
 }
}
if($checked -ne 77){throw "Expected 77 required hook groups, inspected $checked"}
Write-Output "PASS: $checked required hooks match declared method descriptors in $MinecraftJar"
