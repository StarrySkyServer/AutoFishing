# AutoFishing

## 介绍

AutoFishing 是一个运行在 **Nukkit-MOT**（Minecraft 基岩版服务端）上的自动钓鱼插件。玩家钓到鱼后，插件会自动完成收杆、经济结算、钓竿耐久消耗，并在短暂延迟后重新抛竿，整个过程无需手动操作。依赖 EconomyAPI 经济系统，使用 okaeri-configs 管理配置，玩家设置按 UUID 持久化保存，服务器重启后依然生效。

## 安装

1. 确保服务端为 Nukkit-MOT，并已安装 EconomyAPI 插件。
2. 将 `AutoFishing-1.0-SNAPSHOT.jar` 放入服务端 `plugins/` 目录。
3. 启动服务端，插件会在 `plugins/AutoFishing/` 下自动生成 `config.yml` 和 `players.yml`。

## 玩家使用

在游戏内执行指令 `/autofishing`，会弹出图形化设置面板：

- **启用自动钓鱼**：开启后钓到鱼自动收杆并重抛。开关文字会显示当前每次的经济消耗。
- **仅潜行时启用**：勾选后只有潜行状态下才触发自动钓鱼，不勾选则只要开启就持续生效。

### 指令一览

| 指令 | 权限 | 作用 |
| --- | --- | --- |
| `/autofishing` | 所有玩家 | 打开设置面板，切换自动钓鱼与潜行模式 |
| `/reautofish` | OP / 控制台 | 重载 `config.yml` 配置 |

### 配置文件
~\plugins\AutoFishing\config.yml
```yaml
################################
#   AutoFishing Configuration   #
################################
# 每次自动钓鱼的经济消耗
#   0   = 免费
#   -1  = 每次获得 1 经济
#   >0  = 每次扣除对应数值
cost: 3.0
# 允许使用自动钓鱼的世界列表
allowWorlds:
- resource
