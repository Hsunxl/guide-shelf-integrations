# Guide Shelf Integrations / 指南书架整合

Minecraft **1.21.1 / NeoForge 21.1.218+ / Java 21**，**客户端附加模组**，依赖原版 **Guide Shelf 1.3.6 或 1.3.7**。不是原版替代品，不重新分发原 JAR，不修改磁盘上的模组。开发运行使用与当前整合包一致的 NeoForge 21.1.259。

## 四类指南入口

| 系统 | 行为 |
|---|---|
| Patchouli | 保留原书架原生发现、书籍标识、打开逻辑及原生进度 |
| GuideME | 保留原生入口，每次发现时动态补录已注册指南，按 ID 去重，不硬编码整合包书籍列表 |
| Ponder | 新增 `ponder:library`，用原生索引界面与 ScreenOpener 返回链 |
| Oracle | 新增总目录与实际已加载 Wiki 的独立入口；切换 Wiki 清理旧页面引用，保留父界面 |

Ponder、Oracle、GuideME 都是可选项。没有安装的提供者不链接对应 API，不生成假 Wiki。入口在 **原版 `ignoredBookIds` 过滤之前**合并，因此忽略配置继续生效；已有条目优先，不覆盖原始书名、物品组件或固定 ID。原有 Modonomicon、工程师手册等入口不动。

仅聚合入口，不翻译原书正文，不设置解锁进度，不替换手持物品，不发送伪造物品使用包。

## 安装

1. 安装原版 Guide Shelf 1.3.6/1.3.7。
2. 把构建的 `guide-shelf-integrations-0.1.0.jar` 放入客户端 `mods`。
3. 可选安装对应指南提供者，重启游戏并打开书架。

不需要服务器安装本附加模组。既有 ATM10 二进制补丁中的同名 Ponder/Oracle ID 会去重，但建议新安装使用原版 Guide Shelf。**不要同时安装两个不同版本的 Guide Shelf**。本项目不迁移旧补丁的 Cyclops、漠光石板服务端 KubeJS 等额外整合，也不会清除它们；目前范围是上述四类系统。

原生 Guide Shelf 1.3.7 已有固定书籍持久化修复；本附加模组不接管固定配置。若希望保留当前整合包额外改动，先在测试实例验证迁移，不要直接覆盖现用 JAR。

## 构建

```shell
./gradlew build
```

默认通过 Modrinth Maven 获取原版作为编译/开发依赖，不将其打入 JAR。网络环境有 TLS 问题时可手动取得原版并指定：

```shell
./gradlew -PguideShelfJar=/absolute/path/guide_shelf-1.3.6.jar build
python tools/check_api.py --shelf /path/to/guide_shelf-1.3.6.jar --shelf /path/to/guide_shelf-1.3.7.jar
```

可选 ABI 验证参数：`--oracle <jar>`、`--ponder <ponder-library.jar>`、`--guideme <jar>`；只检查方法/字段/注入位置，不初始化游戏类。

## 为什么是附加模组，不是 fork / PR？

截至 2026-10-20，官方 Modrinth 项目 `guide-shelf` 未提供 `source_url` 或 `issues_url`，许可证是 All Rights Reserved。检查作者公开仓库、旧名 `patchouli-library` 和包名 `io.github.catomon.guideshelf` 后未找到可确认的上游源码。搜索到的同名 PatchouliLibrary 是无关 Unity 工程，未 fork。

因此本仓库只发布原创的入口适配代码和 Mixin，不把反编译的原模组当作开源源码上传。**当前没有上游 PR**。获得作者确认的 GitHub 源仓库及贡献方式后，可以把 `OptionalGuides` 的发现/打开逻辑改成源码调用并提交 PR；迁移检查表见 `UPSTREAM.md`。

## 验证边界

已构建并进行发现策略单测及原版 API/注入位置检查。客户端实际点击、返回链、资源重载和不同提供者组合仍需游戏内验收；ABI 检查不是 UI 验收。可选 API 改变时会记录日志并提示打开失败，不把兼容失败静默当成成功。

## English summary

A client-only, non-redistributive addon for Guide Shelf 1.3.6–1.3.7. Preserves native Patchouli/GuideME support, adds Ponder's library and dynamically discovered Oracle wikis, deduplicates stable IDs before the native ignored-book filter, and retains provider-owned UI/progression. No upstream fork or PR is claimed: the confirmed upstream source repository has not been located.
