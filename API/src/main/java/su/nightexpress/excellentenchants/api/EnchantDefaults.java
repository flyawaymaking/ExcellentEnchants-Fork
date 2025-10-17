package su.nightexpress.excellentenchants.api;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentenchants.api.config.ConfigBridge;
import su.nightexpress.excellentenchants.api.config.DistributionConfig;
import su.nightexpress.excellentenchants.api.item.ItemSetId;
import su.nightexpress.excellentenchants.api.wrapper.EnchantDefinition;
import su.nightexpress.excellentenchants.api.wrapper.EnchantDistribution;
import su.nightexpress.excellentenchants.api.wrapper.TradeType;
import su.nightexpress.nightcore.config.FileConfig;

import java.util.Arrays;
import java.io.File;

import static su.nightexpress.excellentenchants.api.EnchantsPlaceholders.*;

public class EnchantDefaults {

    private static final int COMMON = 10;
    private static final int UNCOMMON = 5;
    private static final int RARE = 2;
    private static final int VERY_RARE = 1;

    public static void load(@NotNull File dataDir) {
        String dirPath = dataDir.getAbsolutePath() + ConfigBridge.DIR_ENCHANTS;

        loadDefaults();

        EnchantRegistry.getDataMap().forEach((id, data) -> {
            // Skip disabled enchantments to keep the directory clean.
            if (DistributionConfig.isDisabled(id)) return;

            File file = new File(dirPath, id + FileConfig.EXTENSION);
            boolean exists = file.exists();

            FileConfig config = new FileConfig(file);
            EnchantDefinition definition = data.getDefinition();
            EnchantDistribution distribution = data.getDistribution();

            if (!exists) {
                config.set("Definition", definition);
                config.set("Distribution", distribution);
                config.saveChanges();
                return;
            }

            definition = EnchantDefinition.read(config, "Definition");
            distribution = EnchantDistribution.read(config, "Distribution");

            EnchantRegistry.addData(id, definition, distribution, data.isCurse());

            config.saveChanges();
        });
    }

    private static void loadDefaults() {
        loadDefaultArmors();
        loadDefaultBow();
        loadDefaultFishing();
        loadDefaultTool();
        loadDefaultUniversal();
        loadDefaultWeapon();
    }

    private static void loadDefaultArmors() {
        EnchantRegistry.addData(EnchantId.COLD_STEEL, EnchantDefinition.builder("Усталось", 3)
            .description(TRIGGER_CHANCE + "% шанс наложить " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER + " (" + EFFECT_DURATION + "с.) на атакующего.")
            .weight(UNCOMMON)
            .supportedItems(ItemSetId.CHESTPLATE_ELYTRA)
            .primaryItems(ItemSetId.CHESTPLATE)
            .build(), EnchantDistribution.regular(TradeType.SNOW_COMMON));

        EnchantRegistry.addData(EnchantId.DARKNESS_CLOAK, EnchantDefinition.builder("Плащ тьмы", 3)
            .description(TRIGGER_CHANCE + "% шанс наложить " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER + " (" + EFFECT_DURATION + "с.) на атакующего.")
            .weight(UNCOMMON)
            .supportedItems(ItemSetId.CHESTPLATE_ELYTRA)
            .primaryItems(ItemSetId.CHESTPLATE)
            .build(), EnchantDistribution.regular(TradeType.SAVANNA_COMMON));

        EnchantRegistry.addData(EnchantId.ELEMENTAL_PROTECTION, EnchantDefinition.builder("Элементальная защита", 5)
            .description("Уменьшает урон от зелий и стихий на " + GENERIC_AMOUNT + "%.")
            .weight(COMMON)
            .items(ItemSetId.ARMOR)
            .build(), EnchantDistribution.regular(TradeType.SWAMP_COMMON));

        EnchantRegistry.addData(EnchantId.FIRE_SHIELD, EnchantDefinition.builder("Раскалённый", 4)
            .description(TRIGGER_CHANCE + "% шанс поджечь атакующих с задержкой 2с.")
            .weight(UNCOMMON)
            .supportedItems(ItemSetId.CHESTPLATE_ELYTRA)
            .primaryItems(ItemSetId.CHESTPLATE)
            .build(), EnchantDistribution.regular(TradeType.DESERT_COMMON));

        EnchantRegistry.addData(EnchantId.FIRE_RESIST, EnchantDefinition.builder("Обсидиановый щит", 1)
            .description("Даёт постоянный " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER)
            .weight(VERY_RARE)
            .supportedItems(ItemSetId.CHESTPLATE_ELYTRA)
            .primaryItems(ItemSetId.CHESTPLATE)
            .build(), EnchantDistribution.treasure(TradeType.TAIGA_SPECIAL));

        EnchantRegistry.addData(EnchantId.FLAME_WALKER, EnchantDefinition.builder("Лаваход", 2)
            .description(Arrays.asList("Позволяет ходить по лаве", "и даёт иммунитет к урону от магмы."))
            .weight(VERY_RARE)
            .items(ItemSetId.BOOTS)
            .exclusives(EnchantKeys.FROST_WALKER)
            .build(), EnchantDistribution.treasure(TradeType.DESERT_SPECIAL));

        EnchantRegistry.addData(EnchantId.HARDENED, EnchantDefinition.builder("Защищённый", 2)
            .description(Arrays.asList(TRIGGER_CHANCE + "% шанс получить " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER + " (" + EFFECT_DURATION + "с.)", "при получении урона (раз в 5 секунд)."))
            .weight(VERY_RARE)
            .supportedItems(ItemSetId.CHESTPLATE_ELYTRA)
            .primaryItems(ItemSetId.CHESTPLATE)
            .build(), EnchantDistribution.regular(TradeType.PLAINS_COMMON));

        EnchantRegistry.addData(EnchantId.ICE_SHIELD, EnchantDefinition.builder("Ледяной щит", 5)
            .description(Arrays.asList(TRIGGER_CHANCE + "% шанс заморозить и наложить", EFFECT_TYPE + " " + EFFECT_AMPLIFIER + " (" + EFFECT_DURATION + "с.) на атакующего."))
            .weight(RARE)
            .supportedItems(ItemSetId.CHESTPLATE_ELYTRA)
            .primaryItems(ItemSetId.CHESTPLATE)
            .build(), EnchantDistribution.regular(TradeType.SNOW_COMMON));

        EnchantRegistry.addData(EnchantId.JUMPING, EnchantDefinition.builder("Пружины", 3)
            .description("Даёт постоянный " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER)
            .weight(RARE)
            .items(ItemSetId.BOOTS)
            .build(), EnchantDistribution.regular(TradeType.PLAINS_COMMON));

        EnchantRegistry.addData(EnchantId.KAMIKADZE, EnchantDefinition.builder("Камикадзе", 3)
            .description(TRIGGER_CHANCE + "% шанс взорваться после смерти.")
            .weight(UNCOMMON)
            .supportedItems(ItemSetId.CHESTPLATE_ELYTRA)
            .primaryItems(ItemSetId.CHESTPLATE)
            .build(), EnchantDistribution.regular(TradeType.JUNGLE_COMMON));

        EnchantRegistry.addData(EnchantId.NIGHT_VISION, EnchantDefinition.builder("Светящийся", 1)
            .description("Даёт постоянный " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER)
            .weight(VERY_RARE)
            .items(ItemSetId.HELMET)
            .build(), EnchantDistribution.treasure(TradeType.TAIGA_SPECIAL));

        EnchantRegistry.addData(EnchantId.REBOUND, EnchantDefinition.builder("Желейные ноги", 2)
            .description("Эффект приземления на слизистый блок.")
            .weight(RARE)
            .items(ItemSetId.BOOTS)
            .exclusives(EnchantKeys.FEATHER_FALLING)
            .build(), EnchantDistribution.treasure(TradeType.SWAMP_COMMON));

        EnchantRegistry.addData(EnchantId.REGROWTH, EnchantDefinition.builder("Ангел", 5)
            .description(Arrays.asList(TRIGGER_CHANCE + "% шанс восстановить ХП если вас атакуют", "(раз в 6 секунд)."))
            .weight(RARE)
            .supportedItems(ItemSetId.CHESTPLATE_ELYTRA)
            .primaryItems(ItemSetId.CHESTPLATE)
            .build(), EnchantDistribution.treasure(TradeType.JUNGLE_SPECIAL));

        EnchantRegistry.addData(EnchantId.SATURATION, EnchantDefinition.builder("Импланты", 3)
            .description(TRIGGER_CHANCE + "% шанс восстановить голод.")
            .weight(VERY_RARE)
            .items(ItemSetId.HELMET)
            .build(), EnchantDistribution.regular(TradeType.SAVANNA_SPECIAL));

        EnchantRegistry.addData(EnchantId.SPEED, EnchantDefinition.builder("Ускорение", 3)
            .description("Даёт постоянный эффект " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER)
            .weight(VERY_RARE)
            .items(ItemSetId.BOOTS)
            .build(), EnchantDistribution.regular(TradeType.DESERT_SPECIAL));

        EnchantRegistry.addData(EnchantId.STOPPING_FORCE, EnchantDefinition.builder("Останавливатель", 3)
            .description(TRIGGER_CHANCE + "% шанс уменьшить отдачу от удара на " + GENERIC_AMOUNT + "%.")
            .weight(UNCOMMON)
            .items(ItemSetId.LEGGINGS)
            .build(), EnchantDistribution.regular(TradeType.PLAINS_COMMON));

        EnchantRegistry.addData(EnchantId.WATER_BREATHING, EnchantDefinition.builder("Водный", 1)
            .description("Даёт постоянный эффект " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER)
            .weight(RARE)
            .items(ItemSetId.HELMET)
            .build(), EnchantDistribution.treasure(TradeType.PLAINS_SPECIAL));
    }

    private static void loadDefaultBow() {
        EnchantRegistry.addData(EnchantId.BOMBER, EnchantDefinition.builder("ТНТ Лук", 4)
            .description("Стреляет подожжёной ТНТ (на " + GENERIC_TIME + "с) вместо стрел.")
            .weight(VERY_RARE)
            .items(ItemSetId.BOW_CROSSBOW)
            .exclusives(
                EnchantKeys.custom(EnchantId.ENDER_BOW), EnchantKeys.custom(EnchantId.GHAST),
                EnchantKeys.FLAME, EnchantKeys.PUNCH, EnchantKeys.POWER
            )
            .build(), EnchantDistribution.treasure(TradeType.DESERT_SPECIAL));

        EnchantRegistry.addData(EnchantId.CONFUSING_ARROWS, EnchantDefinition.builder("Стрелы тошноты", 3)
            .description(TRIGGER_CHANCE + "% шанс вызвать " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER + " (" + EFFECT_DURATION + "с.)")
            .weight(COMMON)
            .items(ItemSetId.BOW_CROSSBOW)
            .exclusives(EnchantKeys.custom(EnchantId.ENDER_BOW), EnchantKeys.custom(EnchantId.GHAST), EnchantKeys.custom(EnchantId.BOMBER))
            .build(), EnchantDistribution.regular(TradeType.SWAMP_COMMON));

        EnchantRegistry.addData(EnchantId.DARKNESS_ARROWS, EnchantDefinition.builder("Стрелы тьмы", 3)
            .description(TRIGGER_CHANCE + "% шанс вызвать " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER + " (" + EFFECT_DURATION + "с.)")
            .weight(UNCOMMON)
            .items(ItemSetId.BOW_CROSSBOW)
            .exclusives(EnchantKeys.custom(EnchantId.ENDER_BOW), EnchantKeys.custom(EnchantId.GHAST), EnchantKeys.custom(EnchantId.BOMBER))
            .build(), EnchantDistribution.regular(TradeType.SNOW_COMMON));

        EnchantRegistry.addData(EnchantId.DRAGONFIRE_ARROWS, EnchantDefinition.builder("Драконьи стрелы", 3)
            .description(Arrays.asList(TRIGGER_CHANCE + "% шанс, вызвать эффект драконьего", "(Р=" + GENERIC_RADIUS + ", " + GENERIC_DURATION + "с) раз в 3 с. ."))
            .weight(UNCOMMON)
            .items(ItemSetId.BOW_CROSSBOW)
            .exclusives(EnchantKeys.custom(EnchantId.ENDER_BOW), EnchantKeys.custom(EnchantId.GHAST), EnchantKeys.custom(EnchantId.BOMBER))
            .build(), EnchantDistribution.regular(TradeType.SWAMP_SPECIAL));

        EnchantRegistry.addData(EnchantId.ELECTRIFIED_ARROWS, EnchantDefinition.builder("Электрические стрелы", 3)
            .description(TRIGGER_CHANCE + "% шанс поразить молнией аз в 3с.")
            .weight(RARE)
            .items(ItemSetId.BOW_CROSSBOW)
            .exclusives(EnchantKeys.custom(EnchantId.ENDER_BOW), EnchantKeys.custom(EnchantId.GHAST), EnchantKeys.custom(EnchantId.BOMBER))
            .build(), EnchantDistribution.regular(TradeType.PLAINS_COMMON));

        EnchantRegistry.addData(EnchantId.ENDER_BOW, EnchantDefinition.builder("Эндер лук", 1)
            .description("Стреляет жемчужинами Эндера вместо стрел.")
            .weight(VERY_RARE)
            .items(ItemSetId.BOW)
            .exclusives(EnchantKeys.custom(EnchantId.BOMBER), EnchantKeys.custom(EnchantId.GHAST), EnchantKeys.FLAME, EnchantKeys.PUNCH, EnchantKeys.POWER)
            .build(), EnchantDistribution.treasure(TradeType.PLAINS_SPECIAL));

        EnchantRegistry.addData(EnchantId.EXPLOSIVE_ARROWS, EnchantDefinition.builder("Разрывные стрелы", 5)
            .description(TRIGGER_CHANCE + "% шанс выстрелить разрывной стрелой с задержкой 5с.")
            .weight(COMMON)
            .items(ItemSetId.BOW_CROSSBOW)
            .exclusives(EnchantKeys.custom(EnchantId.ENDER_BOW), EnchantKeys.custom(EnchantId.GHAST), EnchantKeys.custom(EnchantId.BOMBER))
            .build(), EnchantDistribution.regular(TradeType.TAIGA_COMMON));

        EnchantRegistry.addData(EnchantId.FLARE, EnchantDefinition.builder("Факел", 1)
            .description("Ставит факел в месте падения стрелы.")
            .weight(COMMON)
            .items(ItemSetId.BOW)
            .exclusives(EnchantKeys.custom(EnchantId.ENDER_BOW), EnchantKeys.custom(EnchantId.GHAST), EnchantKeys.custom(EnchantId.BOMBER))
            .build(), EnchantDistribution.treasure(TradeType.SNOW_COMMON));

        EnchantRegistry.addData(EnchantId.GHAST, EnchantDefinition.builder("Гаст", 1)
            .description("Стреляет огненными шарами вместо стрел.")
            .weight(RARE)
            .items(ItemSetId.BOW_CROSSBOW)
            .exclusives(EnchantKeys.custom(EnchantId.ENDER_BOW), EnchantKeys.custom(EnchantId.GHAST), EnchantKeys.FLAME, EnchantKeys.PUNCH, EnchantKeys.POWER)
            .build(), EnchantDistribution.treasure(TradeType.DESERT_COMMON));

        EnchantRegistry.addData(EnchantId.HOVER, EnchantDefinition.builder("Левитация", 3)
            .description(TRIGGER_CHANCE + "% шанс вызвать " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER + " (" + EFFECT_DURATION + "с.)")
            .weight(UNCOMMON)
            .items(ItemSetId.BOW_CROSSBOW)
            .exclusives(EnchantKeys.custom(EnchantId.ENDER_BOW), EnchantKeys.custom(EnchantId.GHAST), EnchantKeys.custom(EnchantId.BOMBER))
            .build(), EnchantDistribution.regular(TradeType.PLAINS_SPECIAL));

        EnchantRegistry.addData(EnchantId.LINGERING, EnchantDefinition.builder("Оседающий", 3)
            .description(Arrays.asList(TRIGGER_CHANCE + "% вероятность вызвать", "оседающий эффект."))
            .weight(COMMON)
            .items(ItemSetId.BOW_CROSSBOW)
            .exclusives(EnchantKeys.custom(EnchantId.ENDER_BOW), EnchantKeys.custom(EnchantId.GHAST), EnchantKeys.custom(EnchantId.BOMBER))
            .build(), EnchantDistribution.regular(TradeType.SAVANNA_COMMON));

        EnchantRegistry.addData(EnchantId.POISONED_ARROWS, EnchantDefinition.builder("Ядовитые стрелы", 3)
            .description(TRIGGER_CHANCE + "% шанс вызвать " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER + " (" + EFFECT_DURATION + "с.)")
            .weight(UNCOMMON)
            .items(ItemSetId.BOW_CROSSBOW)
            .exclusives(EnchantKeys.custom(EnchantId.ENDER_BOW), EnchantKeys.custom(EnchantId.GHAST), EnchantKeys.custom(EnchantId.BOMBER))
            .build(), EnchantDistribution.regular(TradeType.SWAMP_COMMON));

        EnchantRegistry.addData(EnchantId.SNIPER, EnchantDefinition.builder("Снайпер", 5)
            .description("Увеличивает скорость снаряда на " + GENERIC_AMOUNT + "%")
            .weight(RARE)
            .items(ItemSetId.BOW_CROSSBOW)
            .build(), EnchantDistribution.regular(TradeType.PLAINS_SPECIAL));

        EnchantRegistry.addData(EnchantId.VAMPIRIC_ARROWS, EnchantDefinition.builder("Стрелы вамира", 3)
            .description(TRIGGER_CHANCE + "% шанс восстановить " + GENERIC_AMOUNT + "❤ при попадании.")
            .weight(VERY_RARE)
            .items(ItemSetId.BOW_CROSSBOW)
            .exclusives(EnchantKeys.custom(EnchantId.ENDER_BOW), EnchantKeys.custom(EnchantId.GHAST), EnchantKeys.custom(EnchantId.BOMBER))
            .build(), EnchantDistribution.regular(TradeType.SWAMP_SPECIAL));

        EnchantRegistry.addData(EnchantId.WITHERED_ARROWS, EnchantDefinition.builder("Стрелы Иссушения", 5)
            .description(TRIGGER_CHANCE + "% шанс получить эффект " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER + " (" + EFFECT_DURATION + "с.)")
            .weight(UNCOMMON)
            .items(ItemSetId.BOW_CROSSBOW)
            .exclusives(EnchantKeys.custom(EnchantId.ENDER_BOW), EnchantKeys.custom(EnchantId.GHAST), EnchantKeys.custom(EnchantId.BOMBER))
            .build(), EnchantDistribution.regular(TradeType.SNOW_SPECIAL));
    }

    private static void loadDefaultFishing() {
        EnchantRegistry.addData(EnchantId.AUTO_REEL, EnchantDefinition.builder("Автокатушка", 2)
            .description(Arrays.asList(TRIGGER_CHANCE + "% шанс автоматически смотать", "удочку при поклёвке."))
            .weight(RARE)
            .items(ItemSetId.FISHING_ROD)
            .build(), EnchantDistribution.treasure(TradeType.JUNGLE_SPECIAL));

        EnchantRegistry.addData(EnchantId.DOUBLE_CATCH, EnchantDefinition.builder("Двойной улов", 3)
            .description(Arrays.asList("Увеличивает количество улова в 2 раза", "с шансом " + TRIGGER_CHANCE + "%."))
            .weight(UNCOMMON)
            .items(ItemSetId.FISHING_ROD)
            .build(), EnchantDistribution.regular(TradeType.PLAINS_COMMON));

        EnchantRegistry.addData(EnchantId.RIVER_MASTER, EnchantDefinition.builder("Хозяин реки", 3)
            .description("Увеличивает дальность заброса.")
            .weight(COMMON)
            .items(ItemSetId.FISHING_ROD)
            .build(), EnchantDistribution.regular(TradeType.PLAINS_COMMON));

        EnchantRegistry.addData(EnchantId.SEASONED_ANGLER, EnchantDefinition.builder("Опытный рыболов", 3)
            .description(Arrays.asList("Увеличивает количество опыта,", "получаемого за рыбалку, на " + GENERIC_AMOUNT + "%."))
            .weight(COMMON)
            .items(ItemSetId.FISHING_ROD)
            .build(), EnchantDistribution.regular(TradeType.PLAINS_COMMON));

        EnchantRegistry.addData(EnchantId.SURVIVALIST, EnchantDefinition.builder("Выживальщик", 2)
            .description("Автоматически готовит рыбу с шансом " + TRIGGER_CHANCE + "%.")
            .weight(UNCOMMON)
            .items(ItemSetId.FISHING_ROD)
            .build(), EnchantDistribution.treasure(TradeType.SNOW_SPECIAL));
    }

    private static void loadDefaultTool() {
        EnchantRegistry.addData(EnchantId.BLAST_MINING, EnchantDefinition.builder("Взрывной шахтёр", 5)
            .description(TRIGGER_CHANCE + "% шанс выкопать блоки взрывом.")
            .weight(RARE)
            .items(ItemSetId.PICKAXE)
            .exclusives(EnchantKeys.custom(EnchantId.VEINMINER), EnchantKeys.custom(EnchantId.TUNNEL))
            .build(), EnchantDistribution.regular(TradeType.PLAINS_COMMON));

        EnchantRegistry.addData(EnchantId.GLASSBREAKER, EnchantDefinition.builder("Разрушитель стекла", 1)
            .description("Мгновенно разбивает стекло")
            .weight(COMMON)
            .supportedItems(ItemSetId.TOOL)
            .primaryItems(ItemSetId.MINING_TOOLS)
            .build(), EnchantDistribution.regular(TradeType.DESERT_COMMON));

        EnchantRegistry.addData(EnchantId.HASTE, EnchantDefinition.builder("Спешка", 3)
            .description("Даёт постоянный эффект " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER + ".")
            .weight(VERY_RARE)
            .supportedItems(ItemSetId.TOOL)
            .primaryItems(ItemSetId.MINING_TOOLS)
            .build(), EnchantDistribution.regular(TradeType.SAVANNA_COMMON));

        EnchantRegistry.addData(EnchantId.LUCKY_MINER, EnchantDefinition.builder("Опытный", 5)
            .description(TRIGGER_CHANCE + "% шанс получить больше опыта с руд.")
            .weight(UNCOMMON)
            .items(ItemSetId.PICKAXE)
            .build(), EnchantDistribution.regular(TradeType.JUNGLE_COMMON));

        EnchantRegistry.addData(EnchantId.REPLANTER, EnchantDefinition.builder("Пересадка", 1)
            .description("Пересаживает посевы когда вы их ломаете.")
            .weight(RARE)
            .items(ItemSetId.HOE)
            .build(), EnchantDistribution.regular(TradeType.PLAINS_COMMON));

        EnchantRegistry.addData(EnchantId.SILK_CHEST, EnchantDefinition.builder("Шёлковый сундук", 1)
            .description("Сохраните содержимое сундука внутри при поломке.")
            .weight(COMMON)
            .supportedItems(ItemSetId.MINING_TOOLS)
            .primaryItems(ItemSetId.AXE)
            .exclusives(EnchantKeys.custom(EnchantId.SMELTER))
            .build(), EnchantDistribution.regular(TradeType.SAVANNA_COMMON));

        EnchantRegistry.addData(EnchantId.SILK_SPAWNER, EnchantDefinition.builder("Шёлковый спавнер", 6)
            .description(TRIGGER_CHANCE + "% шанс добыть спавнер.")
            .weight(VERY_RARE)
            .supportedItems(ItemSetId.MINING_TOOLS)
            .primaryItems(ItemSetId.PICKAXE)
            .exclusives(EnchantKeys.custom(EnchantId.SMELTER))
            .build(), EnchantDistribution.treasure(TradeType.JUNGLE_SPECIAL));

        EnchantRegistry.addData(EnchantId.SMELTER, EnchantDefinition.builder("Плавка", 3)
            .description(TRIGGER_CHANCE + "% шанс переплавить добытый блок.")
            .weight(RARE)
            .supportedItems(ItemSetId.TOOL)
            .primaryItems(ItemSetId.MINING_TOOLS)
            .exclusives(EnchantKeys.custom(EnchantId.SILK_SPAWNER), EnchantKeys.custom(EnchantId.SILK_CHEST), EnchantKeys.SILK_TOUCH)
            .build(), EnchantDistribution.regular(TradeType.DESERT_COMMON));

        EnchantRegistry.addData(EnchantId.TELEKINESIS, EnchantDefinition.builder("Телепатия", 4)
            .description(TRIGGER_CHANCE + "% шанс поместить сломанные блоки сразу в инвентарь")
            .weight(UNCOMMON)
            .supportedItems(ItemSetId.TOOL)
            .primaryItems(ItemSetId.MINING_TOOLS)
            .build(), EnchantDistribution.treasure(TradeType.DESERT_SPECIAL));

        EnchantRegistry.addData(EnchantId.TREEFELLER, EnchantDefinition.builder("Дровосек", 5)
            .description(TRIGGER_CHANCE + "% шанс срубить дерево полностью.")
            .weight(VERY_RARE)
            .items(ItemSetId.AXE)
            .build(), EnchantDistribution.regular(TradeType.TAIGA_SPECIAL));

        EnchantRegistry.addData(EnchantId.TUNNEL, EnchantDefinition.builder("Траншея", 5)
            .description(TRIGGER_CHANCE + "% шанс выкопать зону площадью 3х3.")
            .weight(VERY_RARE)
            .supportedItems(ItemSetId.MINING_TOOLS)
            .primaryItems(ItemSetId.PICKAXE)
            .exclusives(EnchantKeys.custom(EnchantId.VEINMINER), EnchantKeys.custom(EnchantId.BLAST_MINING))
            .build(), EnchantDistribution.regular(TradeType.SAVANNA_SPECIAL));

        EnchantRegistry.addData(EnchantId.VEINMINER, EnchantDefinition.builder("Добытчик жил", 5)
            .description(TRIGGER_CHANCE + "% шанс добыть всю руду жил.")
            .weight(VERY_RARE)
            .items(ItemSetId.PICKAXE)
            .exclusives(EnchantKeys.custom(EnchantId.BLAST_MINING), EnchantKeys.custom(EnchantId.TUNNEL))
            .build(), EnchantDistribution.regular(TradeType.PLAINS_SPECIAL));
    }

    private static void loadDefaultUniversal() {
        EnchantRegistry.addData(EnchantId.RESTORE, EnchantDefinition.builder("Прочный", 4)
            .description(Arrays.asList(TRIGGER_CHANCE + "% шанс предотвратить потерю прочности", "и восстановить часть утраченной,", "с задержкой 15с."))
            .weight(VERY_RARE)
            .items(ItemSetId.BREAKABLE)
            .build(), EnchantDistribution.regular(TradeType.DESERT_COMMON));

        EnchantRegistry.addData(EnchantId.SOULBOUND, EnchantDefinition.builder("Связанный", 3)
            .description(TRIGGER_CHANCE + "% шанс сохранить предмет при смерти.")
            .weight(VERY_RARE)
            .items(ItemSetId.BREAKABLE)
            .exclusives(EnchantKeys.VANISHING_CURSE)
            .build(), EnchantDistribution.treasure(TradeType.DESERT_SPECIAL));
    }

    private static void loadDefaultWeapon() {
        EnchantRegistry.addData(EnchantId.BANE_OF_NETHERSPAWN, EnchantDefinition.builder("Незерлинг", 5)
            .description("Наност дополнительно " + GENERIC_DAMAGE + "❤ урона мобам из Ада.")
            .weight(COMMON)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.PLAINS_COMMON));

        EnchantRegistry.addData(EnchantId.BLINDNESS, EnchantDefinition.builder("Слепой", 3)
            .description(TRIGGER_CHANCE + "% шанс наложить " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER + " (" + EFFECT_DURATION + "с.) на цель.")
            .weight(UNCOMMON)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.TAIGA_COMMON));

        EnchantRegistry.addData(EnchantId.CONFUSION, EnchantDefinition.builder("Конфуз", 4)
            .description(TRIGGER_CHANCE + "% шанс наложить " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER + " (" + EFFECT_DURATION + "с.) на цель.")
            .weight(COMMON)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.SNOW_COMMON));

        EnchantRegistry.addData(EnchantId.CURE, EnchantDefinition.builder("Излечение", 3)
            .description(Arrays.asList(TRIGGER_CHANCE + "% шанс вылечить зомбированных пиглинов и", "зомби-жителей деревни при атаке."))
            .weight(COMMON)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.SAVANNA_COMMON));

        EnchantRegistry.addData(EnchantId.CUTTER, EnchantDefinition.builder("Разоружение", 6)
            .description(TRIGGER_CHANCE + "% шанс снять случайную часть брони с противника")
            .weight(VERY_RARE)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.PLAINS_COMMON));

        EnchantRegistry.addData(EnchantId.DECAPITATOR, EnchantDefinition.builder("Обезглавливатель", 2)
            .description(TRIGGER_CHANCE + "% шанс получить голову игрока или моба.")
            .weight(COMMON)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.treasure(TradeType.SNOW_SPECIAL));

        EnchantRegistry.addData(EnchantId.DOUBLE_STRIKE, EnchantDefinition.builder("Двойной удар", 3)
            .description(TRIGGER_CHANCE + "% шанс нанести двойной удар.")
            .weight(VERY_RARE)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.TAIGA_COMMON));

        EnchantRegistry.addData(EnchantId.EXHAUST, EnchantDefinition.builder("Голодный", 4)
            .description(TRIGGER_CHANCE + "% шанс наложить " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER + " (" + EFFECT_DURATION + "с.) на цель.")
            .weight(COMMON)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.PLAINS_COMMON));

        EnchantRegistry.addData(EnchantId.HOTBAR_SCRAMBLE, EnchantDefinition.builder("Перемешка", 3)
            .description(TRIGGER_CHANCE + "% шанс перемешать хотбар противника")
            .weight(VERY_RARE)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.DESERT_COMMON));

        EnchantRegistry.addData(EnchantId.ICE_ASPECT, EnchantDefinition.builder("Ледяной аспект", 3)
            .description(TRIGGER_CHANCE + "% шанс наложить " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER + " (" + EFFECT_DURATION + "с.) на цель.")
            .weight(UNCOMMON)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.SNOW_COMMON));

        EnchantRegistry.addData(EnchantId.INFERNUS, EnchantDefinition.builder("Инферно", 3)
            .description(TRIGGER_CHANCE + "% шанс поджечь врага запущенным трезубцем")
            .weight(UNCOMMON)
            .items(ItemSetId.TRIDENT)
            .build(), EnchantDistribution.regular(TradeType.SAVANNA_COMMON));

        EnchantRegistry.addData(EnchantId.NIMBLE, EnchantDefinition.builder("Проворный", 1)
            .description(Arrays.asList("Перемещает выпавшие предметы", "непосредственно в инвентарь."))
            .weight(RARE)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.JUNGLE_COMMON));

        EnchantRegistry.addData(EnchantId.PARALYZE, EnchantDefinition.builder("Паралич", 4)
            .description(TRIGGER_CHANCE + "% шанс наложить " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER + " (" + EFFECT_DURATION + "с.) на цель.")
            .weight(UNCOMMON)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.TAIGA_COMMON));

        EnchantRegistry.addData(EnchantId.RAGE, EnchantDefinition.builder("Берсерк", 5)
            .description(Arrays.asList(TRIGGER_CHANCE + "% шанс получить " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER + " в бою.", "(раз в 5 секунд)."))
            .weight(UNCOMMON)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.DESERT_COMMON));

        EnchantRegistry.addData(EnchantId.ROCKET, EnchantDefinition.builder("Ракета", 3)
            .description(TRIGGER_CHANCE + "% шанс подбросить врага.")
            .weight(UNCOMMON)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.treasure(TradeType.JUNGLE_COMMON));

        EnchantRegistry.addData(EnchantId.SWIPER, EnchantDefinition.builder("Вор", 3)
            .description(TRIGGER_CHANCE + "% шанс украсть " + GENERIC_AMOUNT + "опыта у игрока.")
            .weight(COMMON)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.SWAMP_COMMON));

        EnchantRegistry.addData(EnchantId.TEMPER, EnchantDefinition.builder("Закалка", 5)
            .description(Arrays.asList("Увеличивает урон на " + GENERIC_AMOUNT + "%", "за каждые потерянные " + GENERIC_RADIUS + "❤."))
            .weight(UNCOMMON)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.TAIGA_COMMON));

        EnchantRegistry.addData(EnchantId.THRIFTY, EnchantDefinition.builder("Экономный", 6)
            .description(TRIGGER_CHANCE + "% шанс получить яйцо призыва моба.")
            .weight(VERY_RARE)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.treasure(TradeType.JUNGLE_SPECIAL));

        EnchantRegistry.addData(EnchantId.THUNDER, EnchantDefinition.builder("Гром", 5)
            .description(Arrays.asList(TRIGGER_CHANCE + "% шанс поразить молнией нанеся " + GENERIC_DAMAGE + "❤", "дополнительного урона (раз в 3 секунды)."))
            .weight(VERY_RARE)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.PLAINS_COMMON));

        EnchantRegistry.addData(EnchantId.VAMPIRE, EnchantDefinition.builder("Вампир", 3)
            .description(Arrays.asList(TRIGGER_CHANCE + "% шанс восстановить " + GENERIC_AMOUNT + "❤ при атаке.", " (раз в 2 секунды)."))
            .weight(RARE)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.SAVANNA_COMMON));

        EnchantRegistry.addData(EnchantId.VENOM, EnchantDefinition.builder("Ядовитый", 3)
            .description(TRIGGER_CHANCE + "% шанс наложить " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER + " (" + EFFECT_DURATION + "с.) на цель.")
            .weight(COMMON)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.SWAMP_COMMON));

        EnchantRegistry.addData(EnchantId.VILLAGE_DEFENDER, EnchantDefinition.builder("Защитник деревни", 3)
            .description("Увеличивает урон по разбойникам на " + GENERIC_AMOUNT + "❤.")
            .weight(COMMON)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.PLAINS_COMMON));

        EnchantRegistry.addData(EnchantId.WISDOM, EnchantDefinition.builder("Мудрость", 5)
            .description("Мобы сбрасывают больше опыта (x" + GENERIC_MODIFIER + ").")
            .weight(UNCOMMON)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.DESERT_COMMON));

        EnchantRegistry.addData(EnchantId.WITHER, EnchantDefinition.builder("Иссушение", 5)
            .description(TRIGGER_CHANCE + "% шанс наложить " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER + " (" + EFFECT_DURATION + "с.) на цель.")
            .weight(UNCOMMON)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.SNOW_COMMON));
    }
}
