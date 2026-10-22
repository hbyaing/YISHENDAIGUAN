# 1.6.0 隔离联测
不要在日常存档中运行测试。将 tools/qa/runtime.gradle 的内容临时追加到 build.gradle 后执行 `gradlew build qaJar`，可构建独立的 YiShenCombatQA 测试附加包。
将正式模组JAR、官方秘法涡流0.6.8和测试附加包放入独立Forge1.20.1实例的mods，进入临时单人存档。在游戏目录创建UTF-8无BOM的qa-command.txt，内容为两行：`resume` 和 `combat`。测试会修改该测试玩家的模式、装备和部分状态。
等待av-combat-results.txt末尾IMMEDIATE_PASS，以及100tick后的av-combat-live-results.txt末尾ALL_PASS。之后单独写入inspect可记录客户端状态，写入quit可正常关闭客户端。
该附加包仅供隔离验收；发布JAR不含该包，默认build.gradle也未启用此sourceSet。详细攻击入口清单见docs/arcane-hook-manifest-1.6.0.json，实际版本与哈希见docs/arcane-tested-1.6.0.json。第三方JAR需自行从官方发布处取得。
