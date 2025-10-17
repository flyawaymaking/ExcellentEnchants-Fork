# 🧙‍♂️ ExcellentEnchants — Модифицированная версия

> ⚠️ **Важное уведомление о форке**  
> Это **не официальный релиз** плагина [ExcellentEnchants](https://github.com/nulli0n/ExcellentEnchants-spigot).  
> Данная версия является **модифицированной сборкой** с дополнительным функционалом и изменениями.  
> Все авторские права на оригинальный код и дизайн принадлежат разработчикам **nulli0n**.

> 📋 **Лицензия GPL-3.0**  
> Данный проект распространяется под лицензией GPL-3.0.  
> Исходный код доступен в соответствии с условиями лицензии.

> ✅ **Информация о тестировании**  
> Версия протестирована на **Minecraft 1.21.8 (Paper)**.  
> Совместимость с другими версиями **не гарантируется**.

---

## ✨ Добавленный функционал

### 🎯 Новые команды
- **Выдача случайной книги по весу** - получение случайного зачарования определённого веса
- **Просмотр всех весов** - список всех доступных весов зачарований с примерами
- **Выдача всех книг веса** - получение всех зачарований определённого веса

### 🔮 Новые зачарования
- **Постоянный Night Vision** для нагрудника
- **Перемешивание хотбара** противника для оружия

### ⚡ Изменения баланса
- **Удалены все проклятья**
- **Переработана механика способностей** - добавлены шансы срабатывания
- **Оптимизирована система весов** зачарований

### 🎨 Система весов зачарований
```
Weight 1 - §6§lЛегендарный чар
Weight 2 - §d§lЭпический чар  
Weight 5 - §b§lЭлитный чар
Weight 10 - §a§lУникальный чар
```

---

## 🚀 Использование новых команд

### Выдача случайной книги по весу
```bash
/eenchants givebyweight <игрок> <weight>
```

### Просмотр всех доступных весов
```bash
/eenchants weightlist
```

### Выдача всех книг определённого веса
```bash
/eenchants giveallbyweight <игрок> <weight>
```

---

## 📝 Технические детали

### Изменения в коде:
- Добавлены 3 новые команды управления зачарованиями по весу
- Реализована система группировки зачарований по весу из definition
- Добавлены вспомогательные методы для получения всех книг одного Weight
- Интегрирована система красивого отображения весов с цветовым кодированием

---

# About
<div align="center">
  <img src="https://nightexpressdev.com/excellentenchants/header.png">

<a href="https://discord.gg/EwNFGsnGaW"><img src="https://img.shields.io/discord/903053383475277844?style=for-the-badge&label=Discord&color=%2333a8ff"></a>&nbsp;
<a href="https://ko-fi.com/nightexpress"><img src="https://img.shields.io/badge/donate-%E2%9D%A4%EF%B8%8F_to_support-dff33?style=for-the-badge"></a>&nbsp;
<a href="https://nightexpressdev.com/excellentenchants/"><img src="https://img.shields.io/badge/wiki-documentation-ff9c33?style=for-the-badge"></a>

<a href="https://modrinth.com/plugin/excellentenchants"><img src="https://nightexpressdev.com/img/badge/modrinth.svg"></a>&nbsp;
<a href="https://spigotmc.org/resources/61693/"><img src="https://nightexpressdev.com/img/badge/spigotmc.svg"></a>&nbsp;
<a href="https://hangar.papermc.io/NightExpress/ExcellentEnchants"><img src="https://nightexpressdev.com/img/badge/hangar.svg"></a>

**ExcellentEnchants** is a lightweight and modern enchantments plugin.<br>
Provides **75+ vanilla-like** enchantments for your server, where each enchantment has its own configuration file.

<img src="https://nightexpressdev.com/img/excellentenchants/sc_enchanting.gif">
<img src="https://nightexpressdev.com/img/excellentenchants/sc_anvils.gif">
<br>
<img src="https://nightexpressdev.com/img/excellentenchants/sc_villagers.gif">
<img src="https://nightexpressdev.com/img/excellentenchants/sc_creative.gif">
</div>

# Features

- [**Seemless Integration**](https://nightexpressdev.com/excellentenchants/features/compatibility/). Data-driven enchantments that are fully compatible with commands, game mechanics and other plugins.
- **Anvils** support. Combine new enchantments with vanilla ones with no issues!
- **Grindstone** support. Grindstone new enchantments just like vanilla ones!
- [**Enchanting Table**](https://nightexpressdev.com/excellentenchants/features/distribution/) support. Obtain new enchantments through villager trades!
- [**Villager Trades**](https://nightexpressdev.com/excellentenchants/features/distribution/) support. Obtain new enchantments through villager trades!
- [**Random Loot**](https://nightexpressdev.com/excellentenchants/features/distribution/) support. Found new enchantments in dungeon chests!
- [**Fishing**](https://nightexpressdev.com/excellentenchants/features/distribution/) support. Fish enchanted books containing new enchantments!
- [**Mob Equipment**](https://nightexpressdev.com/excellentenchants/features/distribution/) support. Mobs can spawn with items enchanted with new enchantments!
- **Enchanted Books** support. Get enchanted books with new enchantments right from the Creative menu!
- **Highly Customizable**. Edit attributes of any enchantment!
- **Overpowering**. Enchants are allowed to scale beyond their max levels!
- **Colored Tooltip**. Customize the item tooltip color for new enchantments!
- [**Description Tooltip**](https://nightexpressdev.com/excellentenchants/features/description/). Display enchantment summary in item's tooltips!
- [**Disable Enchantments**](https://nightexpressdev.com/excellentenchants/features/disabling/). Disable custom enchantments globally or per world!
- [**New Curses**](https://nightexpressdev.com/excellentenchants/features/enchants/). Did you ever wanted more curses? We have ones!
- [**Item Sets**](https://nightexpressdev.com/excellentenchants/features/item-sets/). Create your own item sets for new enchantments!
- **Axes Support**. Sword enchantments are applicable for axes!
- **Crossbows Support**. Allows bow enchantments for crossbows by default!
- **Elytra Support**. Allows chestplate enchantments for elytras by default!
- **Exclusives**. Customize list of incompatible enchantments for each enchantment!
- **Enchantments GUI**. Customizable GUI for players to browse all the custom enchantments!
- **Visual Effects**. Enchantments comes with particle effects and sounds!
- [**Enchant Charges**](https://nightexpressdev.com/excellentenchants/features/charges/). Unique feature that brings new mechanics to the custom enchantments!
- [**PlaceholderAPI**](https://nightexpressdev.com/excellentenchants/hooks/placeholder-api/) support.

# Dependencies
**REQUIRED:**
- **Java Version:** 21 or newer.
- **Server Software:** Spigot / Paper / Purpur
- **Server Version:** 1.21.4 or newer.
- [nightcore](https://nightexpressdev.com/nightcore/) - Plugin engine.

**OPTIONAL:**
- [ProtocolLib](https://ci.dmulloy2.net/job/ProtocolLib/) or [PacketEvents](https://spigotmc.org/resources/80279/) - For dynamic enchant description.

# Donate
If you like my work or enjoy using my plugins, feel free to [Buy me a coffee](https://ko-fi.com/nightexpress) :) Thank you! 🧡