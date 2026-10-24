# 进入世界阻塞修复（1.20.12）

## 原因

正式版 Forge 47.4.26 在创建/加载世界、首次加载 ServerPlayerGameMode 时发生致命 Mixin 错误：`Non-private field cannot be aliased. Found f_9245_`。TemperanceMining 用 aliases 映射 protected 的 player/level 字段，开发映射环境字段名能直接匹配，正式 SRG 环境则进入 Mixin 禁止的非私有字段别名路径。此前开发 GameTest 与纯渲染验证没有覆盖这一正式包进入世界路径。

## 修复

移除两处 Shadow/aliases，改在 ServerPlayerGameMode(ServerPlayer) 构造方法返回处保存私有 Unique 玩家引用，执行采掘前通过公开 serverLevel() 读取世界。保留实际成功采掘计数、勤勉奖励与嵌套调用上下文，不依赖非私有字段别名。

## 验证

- 正式 JAR 构建成功，45 项 Forge GameTest 全部通过。
- 77 组正式 SRG 方法描述符检查通过，包含新构造方法挂钩。
- 使用用户安装的 Forge 47.4.26、完整第三方模组与重新混淆的正式 JAR，在隔离目录的测试存档副本中加载世界成功。生产环境验收代码位于 tools/qa/runtime/java/dev/starcrownqa/ProductionMappingQa.java。
- 在正式环境新建 ServerPlayerGameMode，实际破坏石头，验证同区块计数恰好增加1，勤勉成长也恰好增加1。结果为 ALL_PASS。
- 测试过程中遇到的实验性设置 Proceed 页面为正常确认提示，与原致命错误无关。
- 没有改动用户正式存档。旧 JAR 备份在实例 codex-mod-backups/ 下，QA 模组没有安装到正式 mods 目录。
