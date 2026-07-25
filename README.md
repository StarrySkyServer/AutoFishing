# AutoFishing

AutoFishing 是一个运行在 **Nukkit-MOT**（Minecraft 基岩版服务端）上的自动钓鱼插件。玩家钓到鱼后，插件会自动完成收杆、经济结算、钓竿耐久消耗，并在短暂延迟后重新抛竿，整个过程无需手动操作。

## 这个插件是什么

一个用 Java 17 编写的 Nukkit-MOT 服务器插件，通过轮询玩家的鱼钩状态实现钓鱼自动化。它依赖 EconomyAPI 经济系统，使用 okaeri-configs 管理配置，玩家设置按 UUID 持久化保存，服务器重启后依然生效。

核心机制：插件每 30 tick（约 1.5 秒）检查一次所有在线玩家的鱼钩，发现 `caught`（上钩）状态后，依次执行经济扣费/奖励、收杆生成掉落物、消耗钓竿耐久、10 tick 后重新抛竿，形成持续钓鱼循环。

## 作用

- **解放双手**：开启后无需手动收杆和抛竿，挂机即可持续钓鱼。
- **经济平衡**：每次钓鱼可配置扣除费用（防止无限刷物），也可配置为反向奖励玩家，或完全免费。
- **范围控制**：通过世界白名单限制自动钓鱼只在指定世界生效，避免在生存主城等场景被滥用。
- **按需启用**：每个玩家独立开关，可选择"仅潜行时启用"，不影响其他玩家的正常钓鱼体验。
- **耐久保护**：自动消耗钓竿耐久，耗尽时自动停止并清空该格物品，不会刷出损坏道具。

## 怎么用

### 安装

1. 确保服务端为 Nukkit-MOT，并已安装 EconomyAPI 插件（硬依赖，缺少则无法加载）。
2. 将 `AutoFishing-1.0-SNAPSHOT.jar` 放入服务端 `plugins/` 目录。
3. 启动服务端，插件会在 `plugins/AutoFishing/` 下自动生成 `config.yml` 和 `players.yml`。

### 配置

编辑 `plugins/AutoFishing/config.yml`：

```yaml
# 每次自动钓鱼的经济消耗
#   0   = 免费
#   -1  = 每次获得 1 经济
#   >0  = 每次扣除对应数值
cost: 0

# 允许使用自动钓鱼的世界列表
allow-worlds:
  - "world"
  - "the_end"
```

- `cost`：`0` 免费；正数表示每次扣除该金额；负数表示每次奖励玩家对应金额的绝对值。
- `allow-worlds`：只在这些世界生效，其他世界自动跳过。

修改保存后，OP 或控制台执行 `/reautofish` 热重载，无需重启服务器。

### 玩家使用

在游戏内执行指令 `/autofishing`，会弹出图形化设置面板：

- **启用自动钓鱼**：开启后钓到鱼自动收杆并重抛。开关文字会显示当前每次的经济消耗。
- **仅潜行时启用**：勾选后只有潜行状态下才触发自动钓鱼，不勾选则只要开启就持续生效。

设置保存后后台异步写入文件，玩家会收到"已保存设置"提示。

### 指令一览

| 指令 | 权限 | 作用 |
| --- | --- | --- |
| `/autofishing` | 所有玩家 | 打开设置面板，切换自动钓鱼与潜行模式 |
| `/reautofish` | OP / 控制台 | 重载 `config.yml` 配置 |

### 从源码构建

仓库现已包含 `pom.xml`，但 Nukkit-MOT 与 EconomyAPI 以本地 jar 形式依赖，需在项目根目录创建 `lib/` 目录并放入 `Nukkit-MOT.jar` 与 `EconomyAPI.jar`：

```bash
git clone https://github.com/StarrySkyServer/AutoFishing.git
cd AutoFishing
mkdir lib
# 将 Nukkit-MOT.jar 和 EconomyAPI.jar 拷贝到 lib/ 目录
mvn clean package
```

产物为 `target/AutoFishing-1.0-SNAPSHOT.jar`（已通过 shade 插件打包 okaeri-configs 依赖）。
