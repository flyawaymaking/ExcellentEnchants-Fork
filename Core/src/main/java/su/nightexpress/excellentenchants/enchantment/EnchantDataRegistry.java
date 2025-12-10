package su.nightexpress.excellentenchants.enchantment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentenchants.api.*;
import su.nightexpress.excellentenchants.api.item.ItemSetId;
import su.nightexpress.excellentenchants.api.wrapper.EnchantDefinition;
import su.nightexpress.excellentenchants.api.wrapper.EnchantDistribution;
import su.nightexpress.excellentenchants.api.wrapper.TradeType;
import su.nightexpress.excellentenchants.bridge.DistributionConfig;
import su.nightexpress.excellentenchants.enchantment.armor.*;
import su.nightexpress.excellentenchants.enchantment.bow.*;
import su.nightexpress.excellentenchants.enchantment.fishing.*;
import su.nightexpress.excellentenchants.enchantment.tool.*;
import su.nightexpress.excellentenchants.enchantment.universal.*;
import su.nightexpress.excellentenchants.enchantment.weapon.*;
import su.nightexpress.nightcore.bridge.registry.NightRegistry;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.FileUtil;

import java.util.Arrays;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static su.nightexpress.excellentenchants.api.EnchantsPlaceholders.*;

public class EnchantDataRegistry {

    private static final NightRegistry<String, EnchantData> REGISTRY = new NightRegistry<>();

    private static final int COMMON    = 10;
    private static final int UNCOMMON  = 5;
    private static final int RARE      = 2;
    private static final int VERY_RARE = 1;

    private final Path        enchantsDir;
    private final Set<String> disabledEnchants;
    private final boolean isPaper;

    private EnchantDataRegistry(@NotNull Path dataDir, boolean isPaper) {
        this.enchantsDir = Path.of(dataDir.toString(), EnchantFiles.DIR_ENCHANTS);
        this.disabledEnchants = new HashSet<>();
        this.isPaper = isPaper;
    }

    public static void initialize(@NotNull Path dataDir, boolean isPaper) {
        if (REGISTRY.isFrozen()) throw new IllegalStateException("Registry is already initialized");

        EnchantDataRegistry dataRegistry = new EnchantDataRegistry(dataDir, isPaper);
        dataRegistry.load();
        REGISTRY.freeze();
    }

    public static void clear() {
        REGISTRY.unfreeze();
        REGISTRY.clear();
    }

    @Nullable
    public static EnchantData getDataById(@NotNull String id) {
        return REGISTRY.byKey(id);
    }

    @NotNull
    public static Map<String, EnchantData> getMap() {
        return REGISTRY.map();
    }

    public static boolean isPresent(@NotNull String id) {
        return REGISTRY.lookup(id).isPresent();
    }

    private void load() {
        this.loadDisabledFolder();

        this.loadDefaults().forEach((id, data) -> {
            if (this.disabledEnchants.contains(id)) return;

            Path file = Path.of(this.enchantsDir.toString(), FileConfig.withExtension(id));
            boolean exists = Files.exists(file);

            FileConfig config = FileConfig.load(file);
            EnchantDefinition definition = data.getDefinition();
            EnchantDistribution distribution = data.getDistribution();

            if (!exists) {
                config.set("Definition", definition);
                config.set("Distribution", distribution);
            }

            definition = EnchantDefinition.read(config, "Definition");
            distribution = EnchantDistribution.read(config, "Distribution");

            REGISTRY.register(id, new EnchantData(definition, distribution, data.getProvider(), data.isCurse()));

            config.saveChanges();
        });
    }

    private void loadDisabledFolder() {
        this.disabledEnchants.addAll(DistributionConfig.DISABLED_GLOBAL.get());

        Path path = Path.of(this.enchantsDir.toString(), EnchantFiles.DIR_DISABLED);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            }
            catch (IOException exception) {
                exception.printStackTrace();
                return;
            }
        }

        FileUtil.findYamlFiles(path.toString()).forEach(file -> {
            String name = file.getFileName().toString();
            this.disabledEnchants.add(name.substring(0, name.length() - FileConfig.EXTENSION.length()));
        });
    }

    @NotNull
    private Map<String, EnchantData> loadDefaults() {
        Map<String, EnchantData> map = new LinkedHashMap<>();

        this.loadDefaultArmors(map);
        this.loadDefaultBow(map);
        this.loadDefaultFishing(map);
        this.loadDefaultTool(map);
        this.loadDefaultUniversal(map);
        this.loadDefaultWeapon(map);

        return map;
    }

    private void addData(@NotNull Map<String, EnchantData> map,
                         @NotNull String id,
                         @NotNull EnchantDefinition definition,
                         @NotNull EnchantDistribution distribution,
                         @NotNull EnchantProvider<?> provider) {
        this.addData(map, id, definition, distribution, provider, false);
    }

    private void addData(@NotNull Map<String, EnchantData> map,
                         @NotNull String id,
                         @NotNull EnchantDefinition definition,
                         @NotNull EnchantDistribution distribution,
                         @NotNull EnchantProvider<?> provider,
                         boolean curse) {
        map.put(id, new EnchantData(definition, distribution, provider, curse));
    }

    private void loadDefaultArmors(@NotNull Map<String, EnchantData> map) {
        this.addData(map, EnchantId.COLD_STEEL, EnchantDefinition.builder("Усталость", 3)
            .description(TRIGGER_CHANCE + "% шанс наложить " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER + " (" + EFFECT_DURATION + "с.) на атакующего.")
            .weight(UNCOMMON)
            .supportedItems(ItemSetId.CHESTPLATE_ELYTRA)
            .primaryItems(ItemSetId.CHESTPLATE)
            .build(), EnchantDistribution.regular(TradeType.SNOW_COMMON), ColdSteelEnchant::new);

        this.addData(map, EnchantId.DARKNESS_CLOAK, EnchantDefinition.builder("Плащ тьмы", 3)
            .description(TRIGGER_CHANCE + "% шанс наложить " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER + " (" + EFFECT_DURATION + "с.) на атакующего.")
            .weight(UNCOMMON)
            .supportedItems(ItemSetId.CHESTPLATE_ELYTRA)
            .primaryItems(ItemSetId.CHESTPLATE)
            .build(), EnchantDistribution.regular(TradeType.SAVANNA_COMMON), DarknessCloakEnchant::new);

        this.addData(map, EnchantId.ELEMENTAL_PROTECTION, EnchantDefinition.builder("Элементальная защита", 5)
            .description("Уменьшает урон от зелий и стихий на " + GENERIC_AMOUNT + "%.")
            .weight(COMMON)
            .items(ItemSetId.ARMOR)
            .build(), EnchantDistribution.regular(TradeType.SWAMP_COMMON), ElementalProtectionEnchant::new);

        this.addData(map, EnchantId.FIRE_SHIELD, EnchantDefinition.builder("Раскалённый", 4)
            .description(TRIGGER_CHANCE + "% шанс поджечь атакующих с задержкой 2с.")
            .weight(UNCOMMON)
            .supportedItems(ItemSetId.CHESTPLATE_ELYTRA)
            .primaryItems(ItemSetId.CHESTPLATE)
            .build(), EnchantDistribution.regular(TradeType.DESERT_COMMON), FireShieldEnchant::new);

        this.addData(map, EnchantId.FIRE_RESIST, EnchantDefinition.builder("Обсидиановый щит", 1)
                .description("Даёт постоянный " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER)
                .weight(VERY_RARE)
                .supportedItems(ItemSetId.CHESTPLATE_ELYTRA)
                .primaryItems(ItemSetId.CHESTPLATE)
                .build(), EnchantDistribution.treasure(TradeType.TAIGA_SPECIAL), FireResistEnchant::new);

        this.addData(map, EnchantId.FLAME_WALKER, EnchantDefinition.builder("Лаваход", 2)
            .description(Arrays.asList("Позволяет ходить по лаве", "и даёт иммунитет к урону от магмы."))
            .weight(VERY_RARE)
            .items(ItemSetId.BOOTS)
            .exclusives(EnchantKeys.FROST_WALKER)
            .build(), EnchantDistribution.treasure(TradeType.DESERT_SPECIAL), FlameWalkerEnchant::new);

        this.addData(map, EnchantId.HARDENED, EnchantDefinition.builder("Защищённый", 2)
            .description(Arrays.asList(TRIGGER_CHANCE + "% шанс получить " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER + " (" + EFFECT_DURATION + "с.)", "при получении урона (раз в 5 секунд)."))
            .weight(VERY_RARE)
            .supportedItems(ItemSetId.CHESTPLATE_ELYTRA)
            .primaryItems(ItemSetId.CHESTPLATE)
            .build(), EnchantDistribution.regular(TradeType.PLAINS_COMMON), HardenedEnchant::new);

        this.addData(map, EnchantId.ICE_SHIELD, EnchantDefinition.builder("Ледяной щит", 5)
            .description(Arrays.asList(TRIGGER_CHANCE + "% шанс заморозить и наложить", EFFECT_TYPE + " " + EFFECT_AMPLIFIER + " (" + EFFECT_DURATION + "с.) на атакующего."))
            .weight(RARE)
            .supportedItems(ItemSetId.CHESTPLATE_ELYTRA)
            .primaryItems(ItemSetId.CHESTPLATE)
            .build(), EnchantDistribution.regular(TradeType.SNOW_COMMON), IceShieldEnchant::new);

        this.addData(map, EnchantId.JUMPING, EnchantDefinition.builder("Пружины", 3)
            .description("Даёт постоянный " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER)
            .weight(RARE)
            .items(ItemSetId.BOOTS)
            .build(), EnchantDistribution.regular(TradeType.PLAINS_COMMON), JumpingEnchant::new);

        this.addData(map, EnchantId.KAMIKADZE, EnchantDefinition.builder("Камикадзе", 3)
            .description(TRIGGER_CHANCE + "% шанс взорваться после смерти.")
            .weight(UNCOMMON)
            .supportedItems(ItemSetId.CHESTPLATE_ELYTRA)
            .primaryItems(ItemSetId.CHESTPLATE)
            .build(), EnchantDistribution.regular(TradeType.JUNGLE_COMMON), KamikadzeEnchant::new);

        this.addData(map, EnchantId.NIGHT_VISION, EnchantDefinition.builder("Светящийся", 1)
            .description("Даёт постоянный " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER)
            .weight(VERY_RARE)
            .items(ItemSetId.HELMET)
            .build(), EnchantDistribution.treasure(TradeType.TAIGA_SPECIAL), NightVisionEnchant::new);

        this.addData(map, EnchantId.REBOUND, EnchantDefinition.builder("Желейные ноги", 2)
            .description("Эффект приземления на слизистый блок.")
            .weight(RARE)
            .items(ItemSetId.BOOTS)
            .exclusives(EnchantKeys.FEATHER_FALLING)
            .build(), EnchantDistribution.treasure(TradeType.SWAMP_COMMON), ReboundEnchant::new);

        this.addData(map, EnchantId.REGROWTH, EnchantDefinition.builder("Ангел", 5)
            .description(Arrays.asList(TRIGGER_CHANCE + "% шанс восстановить ХП если вас атакуют", "(раз в 6 секунд)."))
            .weight(RARE)
            .supportedItems(ItemSetId.CHESTPLATE_ELYTRA)
            .primaryItems(ItemSetId.CHESTPLATE)
            .build(), EnchantDistribution.treasure(TradeType.JUNGLE_SPECIAL), RegrowthEnchant::new);

        this.addData(map, EnchantId.SATURATION, EnchantDefinition.builder("Импланты", 3)
            .description(TRIGGER_CHANCE + "% шанс восстановить голод.")
            .weight(VERY_RARE)
            .items(ItemSetId.HELMET)
            .build(), EnchantDistribution.regular(TradeType.SAVANNA_SPECIAL), SaturationEnchant::new);

        this.addData(map, EnchantId.SPEED, EnchantDefinition.builder("Ускорение", 3)
            .description("Даёт постоянный эффект " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER)
            .weight(VERY_RARE)
            .items(ItemSetId.BOOTS)
            .build(), EnchantDistribution.regular(TradeType.DESERT_SPECIAL), SpeedyEnchant::new);

        this.addData(map, EnchantId.STOPPING_FORCE, EnchantDefinition.builder("Останавливатель", 3)
            .description(TRIGGER_CHANCE + "% шанс уменьшить отдачу от удара на " + GENERIC_AMOUNT + "%.")
            .weight(UNCOMMON)
            .items(ItemSetId.LEGGINGS)
            .build(), EnchantDistribution.regular(TradeType.PLAINS_COMMON), StoppingForceEnchant::new);

        this.addData(map, EnchantId.WATER_BREATHING, EnchantDefinition.builder("Водный", 1)
            .description("Даёт постоянный эффект " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER)
            .weight(RARE)
            .items(ItemSetId.HELMET)
            .build(), EnchantDistribution.treasure(TradeType.PLAINS_SPECIAL), WaterBreathingEnchant::new);
    }

    private void loadDefaultBow(@NotNull Map<String, EnchantData> map) {
        this.addData(map, EnchantId.BOMBER, EnchantDefinition.builder("ТНТ Лук", 4)
            .description("Стреляет подожжёным ТНТ (на " + GENERIC_TIME + "с) вместо стрел.")
            .weight(VERY_RARE)
            .items(ItemSetId.BOW_CROSSBOW)
            .exclusives(
                EnchantKeys.create(EnchantId.ENDER_BOW), EnchantKeys.create(EnchantId.GHAST),
                EnchantKeys.FLAME, EnchantKeys.PUNCH, EnchantKeys.POWER
            )
            .build(), EnchantDistribution.treasure(TradeType.DESERT_SPECIAL), BomberEnchant::new);

        this.addData(map, EnchantId.CONFUSING_ARROWS, EnchantDefinition.builder("Стрелы тошноты", 3)
            .description(TRIGGER_CHANCE + "% шанс вызвать " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER + " (" + EFFECT_DURATION + "с.)")
            .weight(COMMON)
            .items(ItemSetId.BOW_CROSSBOW)
            .exclusives(EnchantKeys.create(EnchantId.ENDER_BOW), EnchantKeys.create(EnchantId.GHAST), EnchantKeys.create(EnchantId.BOMBER))
            .build(), EnchantDistribution.regular(TradeType.SWAMP_COMMON), ConfusingArrowsEnchant::new);

        this.addData(map, EnchantId.DARKNESS_ARROWS, EnchantDefinition.builder("Стрелы тьмы", 3)
            .description(TRIGGER_CHANCE + "% шанс вызвать " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER + " (" + EFFECT_DURATION + "с.)")
            .weight(UNCOMMON)
            .items(ItemSetId.BOW_CROSSBOW)
            .exclusives(EnchantKeys.create(EnchantId.ENDER_BOW), EnchantKeys.create(EnchantId.GHAST), EnchantKeys.create(EnchantId.BOMBER))
            .build(), EnchantDistribution.regular(TradeType.SNOW_COMMON), DarknessArrowsEnchant::new);

        this.addData(map, EnchantId.DRAGONFIRE_ARROWS, EnchantDefinition.builder("Драконьи стрелы", 3)
            .description(Arrays.asList(TRIGGER_CHANCE + "% шанс, вызвать эффект драконьего", "(Р=" + GENERIC_RADIUS + ", " + GENERIC_DURATION + "с) раз в 3 с. ."))
            .weight(UNCOMMON)
            .items(ItemSetId.BOW_CROSSBOW)
            .exclusives(EnchantKeys.create(EnchantId.ENDER_BOW), EnchantKeys.create(EnchantId.GHAST), EnchantKeys.create(EnchantId.BOMBER))
            .build(), EnchantDistribution.regular(TradeType.SWAMP_SPECIAL), DragonfireArrowsEnchant::new);

        this.addData(map, EnchantId.ELECTRIFIED_ARROWS, EnchantDefinition.builder("Электрические стрелы", 3)
            .description(TRIGGER_CHANCE + "% шанс поразить молнией аз в 3с.")
            .weight(RARE)
            .items(ItemSetId.BOW_CROSSBOW)
            .exclusives(EnchantKeys.create(EnchantId.ENDER_BOW), EnchantKeys.create(EnchantId.GHAST), EnchantKeys.create(EnchantId.BOMBER))
            .build(), EnchantDistribution.regular(TradeType.PLAINS_COMMON), ElectrifiedArrowsEnchant::new);

        this.addData(map, EnchantId.ENDER_BOW, EnchantDefinition.builder("Эндер лук", 1)
            .description("Стреляет жемчужинами Эндера вместо стрел.")
            .weight(VERY_RARE)
            .items(ItemSetId.BOW)
            .exclusives(EnchantKeys.create(EnchantId.BOMBER), EnchantKeys.create(EnchantId.GHAST), EnchantKeys.FLAME, EnchantKeys.PUNCH, EnchantKeys.POWER)
            .build(), EnchantDistribution.treasure(TradeType.PLAINS_SPECIAL), EnderBowEnchant::new);

        this.addData(map, EnchantId.EXPLOSIVE_ARROWS, EnchantDefinition.builder("Разрывные стрелы", 5)
            .description(TRIGGER_CHANCE + "% шанс выстрелить разрывной стрелой с задержкой 5с.")
            .weight(COMMON)
            .items(ItemSetId.BOW_CROSSBOW)
            .exclusives(EnchantKeys.create(EnchantId.ENDER_BOW), EnchantKeys.create(EnchantId.GHAST), EnchantKeys.create(EnchantId.BOMBER))
            .build(), EnchantDistribution.regular(TradeType.TAIGA_COMMON), ExplosiveArrowsEnchant::new);

        this.addData(map, EnchantId.FLARE, EnchantDefinition.builder("Факел", 1)
            .description("Ставит факел в месте падения стрелы.")
            .weight(COMMON)
            .items(ItemSetId.BOW)
            .exclusives(EnchantKeys.create(EnchantId.ENDER_BOW), EnchantKeys.create(EnchantId.GHAST), EnchantKeys.create(EnchantId.BOMBER))
            .build(), EnchantDistribution.treasure(TradeType.SNOW_COMMON), FlareEnchant::new);

        this.addData(map, EnchantId.GHAST, EnchantDefinition.builder("Гаст", 1)
            .description("Стреляет огненными шарами вместо стрел.")
            .weight(RARE)
            .items(ItemSetId.BOW_CROSSBOW)
            .exclusives(EnchantKeys.create(EnchantId.ENDER_BOW), EnchantKeys.create(EnchantId.GHAST), EnchantKeys.FLAME, EnchantKeys.PUNCH, EnchantKeys.POWER)
            .build(), EnchantDistribution.treasure(TradeType.DESERT_COMMON), GhastEnchant::new);

        this.addData(map, EnchantId.HOVER, EnchantDefinition.builder("Левитация", 3)
            .description(TRIGGER_CHANCE + "% шанс вызвать " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER + " (" + EFFECT_DURATION + "с.)")
            .weight(UNCOMMON)
            .items(ItemSetId.BOW_CROSSBOW)
            .exclusives(EnchantKeys.create(EnchantId.ENDER_BOW), EnchantKeys.create(EnchantId.GHAST), EnchantKeys.create(EnchantId.BOMBER))
            .build(), EnchantDistribution.regular(TradeType.PLAINS_SPECIAL), HoverEnchant::new);

        this.addData(map, EnchantId.LINGERING, EnchantDefinition.builder("Оседающий", 3)
            .description(Arrays.asList(TRIGGER_CHANCE + "% вероятность вызвать", "оседающий эффект."))
            .weight(COMMON)
            .items(ItemSetId.BOW_CROSSBOW)
            .exclusives(EnchantKeys.create(EnchantId.ENDER_BOW), EnchantKeys.create(EnchantId.GHAST), EnchantKeys.create(EnchantId.BOMBER))
            .build(), EnchantDistribution.regular(TradeType.SAVANNA_COMMON), LingeringEnchant::new);

        this.addData(map, EnchantId.POISONED_ARROWS, EnchantDefinition.builder("Ядовитые стрелы", 3)
            .description(TRIGGER_CHANCE + "% шанс вызвать " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER + " (" + EFFECT_DURATION + "с.)")
            .weight(UNCOMMON)
            .items(ItemSetId.BOW_CROSSBOW)
            .exclusives(EnchantKeys.create(EnchantId.ENDER_BOW), EnchantKeys.create(EnchantId.GHAST), EnchantKeys.create(EnchantId.BOMBER))
            .build(), EnchantDistribution.regular(TradeType.SWAMP_COMMON), PoisonedArrowsEnchant::new);

        this.addData(map, EnchantId.SNIPER, EnchantDefinition.builder("Снайпер", 5)
            .description("Увеличивает скорость снаряда на " + GENERIC_AMOUNT + "%")
            .weight(RARE)
            .items(ItemSetId.BOW_CROSSBOW)
            .build(), EnchantDistribution.regular(TradeType.PLAINS_SPECIAL), SniperEnchant::new);

        this.addData(map, EnchantId.VAMPIRIC_ARROWS, EnchantDefinition.builder("Стрелы Вампира", 3)
            .description(TRIGGER_CHANCE + "% шанс восстановить " + GENERIC_AMOUNT + "❤ при попадании.")
            .weight(VERY_RARE)
            .items(ItemSetId.BOW_CROSSBOW)
            .exclusives(EnchantKeys.create(EnchantId.ENDER_BOW), EnchantKeys.create(EnchantId.GHAST), EnchantKeys.create(EnchantId.BOMBER))
            .build(), EnchantDistribution.regular(TradeType.SWAMP_SPECIAL), VampiricArrowsEnchant::new);

        this.addData(map, EnchantId.WITHERED_ARROWS, EnchantDefinition.builder("Стрелы Иссушения", 5)
            .description(TRIGGER_CHANCE + "% шанс получить эффект " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER + " (" + EFFECT_DURATION + "с.)")
            .weight(UNCOMMON)
            .items(ItemSetId.BOW_CROSSBOW)
            .exclusives(EnchantKeys.create(EnchantId.ENDER_BOW), EnchantKeys.create(EnchantId.GHAST), EnchantKeys.create(EnchantId.BOMBER))
            .build(), EnchantDistribution.regular(TradeType.SNOW_SPECIAL), WitheredArrowsEnchant::new);
    }

    private void loadDefaultFishing(@NotNull Map<String, EnchantData> map) {
        if (this.isPaper) {
            this.addData(map, EnchantId.AUTO_REEL, EnchantDefinition.builder("Подсечка", 2)
                .description(Arrays.asList(TRIGGER_CHANCE + "% шанс автоматически смотать", "удочку при поклёвке."))
                .weight(RARE)
                .items(ItemSetId.FISHING_ROD)
                .build(), EnchantDistribution.treasure(TradeType.JUNGLE_SPECIAL), AutoReelEnchant::new);
        }

        this.addData(map, EnchantId.DOUBLE_CATCH, EnchantDefinition.builder("Двойной улов", 3)
            .description(Arrays.asList("Увеличивает количество улова в 2 раза", "с шансом " + TRIGGER_CHANCE + "%."))
            .weight(UNCOMMON)
            .items(ItemSetId.FISHING_ROD)
            .build(), EnchantDistribution.regular(TradeType.PLAINS_COMMON), DoubleCatchEnchant::new);

        this.addData(map, EnchantId.RIVER_MASTER, EnchantDefinition.builder("Хозяин реки", 3)
            .description("Увеличивает дальность заброса.")
            .weight(COMMON)
            .items(ItemSetId.FISHING_ROD)
            .build(), EnchantDistribution.regular(TradeType.PLAINS_COMMON), RiverMasterEnchant::new);

        this.addData(map, EnchantId.SEASONED_ANGLER, EnchantDefinition.builder("Опытный рыболов", 3)
            .description(Arrays.asList("Увеличивает количество опыта,", "получаемого за рыбалку, на " + GENERIC_AMOUNT + "%."))
            .weight(COMMON)
            .items(ItemSetId.FISHING_ROD)
            .build(), EnchantDistribution.regular(TradeType.PLAINS_COMMON), SeasonedAnglerEnchant::new);

        this.addData(map, EnchantId.SURVIVALIST, EnchantDefinition.builder("Выживальщик", 1)
            .description("Автоматически готовит рыбу с шансом " + TRIGGER_CHANCE + "%.")
            .weight(UNCOMMON)
            .items(ItemSetId.FISHING_ROD)
            .build(), EnchantDistribution.treasure(TradeType.SNOW_SPECIAL), SurvivalistEnchant::new);
    }

    private void loadDefaultTool(@NotNull Map<String, EnchantData> map) {
        this.addData(map, EnchantId.BLAST_MINING, EnchantDefinition.builder("Взрывной шахтёр", 5)
            .description(TRIGGER_CHANCE + "% шанс выкопать блоки взрывом.")
            .weight(RARE)
            .items(ItemSetId.PICKAXE)
            .exclusives(EnchantKeys.create(EnchantId.VEINMINER), EnchantKeys.create(EnchantId.TUNNEL))
            .build(), EnchantDistribution.regular(TradeType.PLAINS_COMMON), BlastMiningEnchant::new);

        this.addData(map, EnchantId.GLASSBREAKER, EnchantDefinition.builder("Разрушитель стекла", 1)
            .description("Мгновенно разбивает стекло")
            .weight(COMMON)
            .supportedItems(ItemSetId.TOOL)
            .primaryItems(ItemSetId.MINING_TOOLS)
            .build(), EnchantDistribution.regular(TradeType.DESERT_COMMON), GlassbreakerEnchant::new);

        this.addData(map, EnchantId.HASTE, EnchantDefinition.builder("Спешка", 3)
            .description("Даёт постоянный эффект " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER + ".")
            .weight(VERY_RARE)
            .supportedItems(ItemSetId.TOOL)
            .primaryItems(ItemSetId.MINING_TOOLS)
            .build(), EnchantDistribution.regular(TradeType.SAVANNA_COMMON), HasteEnchant::new);

        this.addData(map, EnchantId.LUCKY_MINER, EnchantDefinition.builder("Опытный шахтёр", 5)
            .description(TRIGGER_CHANCE + "% шанс получить больше опыта с руд.")
            .weight(UNCOMMON)
            .items(ItemSetId.PICKAXE)
            .build(), EnchantDistribution.regular(TradeType.JUNGLE_COMMON), LuckyMinerEnchant::new);

        this.addData(map, EnchantId.REPLANTER, EnchantDefinition.builder("Пересадка", 1)
            .description("Пересаживает посевы когда вы их ломаете.")
            .weight(RARE)
            .items(ItemSetId.HOE)
            .build(), EnchantDistribution.regular(TradeType.PLAINS_COMMON), ReplanterEnchant::new);

        if (this.isPaper) {
            this.addData(map, EnchantId.SILK_CHEST, EnchantDefinition.builder("Шёлковый сундук", 1)
                .description("Сохраните содержимое сундука внутри при поломке.")
                .weight(COMMON)
                .supportedItems(ItemSetId.MINING_TOOLS)
                .primaryItems(ItemSetId.AXE)
                .exclusives(EnchantKeys.create(EnchantId.SMELTER))
                .build(), EnchantDistribution.regular(TradeType.SAVANNA_COMMON), SilkChestEnchant::new);
        }

        this.addData(map, EnchantId.SILK_SPAWNER, EnchantDefinition.builder("Шёлковый спавнер", 3)
            .description(TRIGGER_CHANCE + "% шанс добыть спавнер.")
            .weight(VERY_RARE)
            .supportedItems(ItemSetId.MINING_TOOLS)
            .primaryItems(ItemSetId.PICKAXE)
            .exclusives(EnchantKeys.create(EnchantId.SMELTER))
            .build(), EnchantDistribution.treasure(TradeType.JUNGLE_SPECIAL), SilkSpawnerEnchant::new);

        this.addData(map, EnchantId.SMELTER, EnchantDefinition.builder("Плавка", 3)
            .description(TRIGGER_CHANCE + "% шанс переплавить добытый блок.")
            .weight(RARE)
            .supportedItems(ItemSetId.TOOL)
            .primaryItems(ItemSetId.MINING_TOOLS)
            .exclusives(EnchantKeys.create(EnchantId.SILK_SPAWNER), EnchantKeys.create(EnchantId.SILK_CHEST), EnchantKeys.SILK_TOUCH)
            .build(), EnchantDistribution.regular(TradeType.DESERT_COMMON), SmelterEnchant::new);

        this.addData(map, EnchantId.TELEKINESIS, EnchantDefinition.builder("Телепатия", 4)
            .description(TRIGGER_CHANCE + "% шанс поместить сломанные блоки сразу в инвентарь")
            .weight(UNCOMMON)
            .supportedItems(ItemSetId.TOOL)
            .primaryItems(ItemSetId.MINING_TOOLS)
            .build(), EnchantDistribution.treasure(TradeType.DESERT_SPECIAL), TelekinesisEnchant::new);

        this.addData(map, EnchantId.TREEFELLER, EnchantDefinition.builder("Дровосек", 5)
            .description(TRIGGER_CHANCE + "% шанс срубить дерево полностью.")
            .weight(VERY_RARE)
            .items(ItemSetId.AXE)
            .build(), EnchantDistribution.regular(TradeType.TAIGA_SPECIAL), TreefellerEnchant::new);

        this.addData(map, EnchantId.TUNNEL, EnchantDefinition.builder("Траншея", 5)
            .description(TRIGGER_CHANCE + "% шанс выкопать зону площадью 3х3.")
            .weight(VERY_RARE)
            .supportedItems(ItemSetId.MINING_TOOLS)
            .primaryItems(ItemSetId.PICKAXE)
            .exclusives(EnchantKeys.create(EnchantId.VEINMINER), EnchantKeys.create(EnchantId.BLAST_MINING))
            .build(), EnchantDistribution.regular(TradeType.SAVANNA_SPECIAL), TunnelEnchant::new);

        this.addData(map, EnchantId.VEINMINER, EnchantDefinition.builder("Добытчик жил", 5)
            .description(TRIGGER_CHANCE + "% шанс добыть всю руду жил.")
            .weight(VERY_RARE)
            .items(ItemSetId.PICKAXE)
            .exclusives(EnchantKeys.create(EnchantId.BLAST_MINING), EnchantKeys.create(EnchantId.TUNNEL))
            .build(), EnchantDistribution.regular(TradeType.PLAINS_SPECIAL), VeinminerEnchant::new);
    }

    private void loadDefaultUniversal(@NotNull Map<String, EnchantData> map) {
        this.addData(map, EnchantId.RESTORE, EnchantDefinition.builder("Прочный", 4)
            .description(Arrays.asList(TRIGGER_CHANCE + "% шанс предотвратить потерю прочности", "и восстановить часть утраченной,", "с задержкой 15с."))
            .weight(VERY_RARE)
            .items(ItemSetId.BREAKABLE)
            .build(), EnchantDistribution.regular(TradeType.DESERT_COMMON), RestoreEnchant::new);

        this.addData(map, EnchantId.SOULBOUND, EnchantDefinition.builder("Связанный", 3)
            .description(TRIGGER_CHANCE + "% шанс сохранить предмет при смерти.")
            .weight(VERY_RARE)
            .items(ItemSetId.BREAKABLE)
            .exclusives(EnchantKeys.VANISHING_CURSE)
            .build(), EnchantDistribution.treasure(TradeType.DESERT_SPECIAL), SoulboundEnchant::new);
    }

    private void loadDefaultWeapon(@NotNull Map<String, EnchantData> map) {
        this.addData(map, EnchantId.BANE_OF_NETHERSPAWN, EnchantDefinition.builder("Незерлинг", 5)
            .description("Наност дополнительно " + GENERIC_DAMAGE + "❤ урона мобам из Ада.")
            .weight(COMMON)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.PLAINS_COMMON), BaneOfNetherspawnEnchant::new);

        this.addData(map, EnchantId.BLINDNESS, EnchantDefinition.builder("Слепой", 3)
            .description(TRIGGER_CHANCE + "% шанс наложить " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER + " (" + EFFECT_DURATION + "с.) на цель.")
            .weight(UNCOMMON)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.TAIGA_COMMON), BlindnessEnchant::new);

        this.addData(map, EnchantId.CONFUSION, EnchantDefinition.builder("Конфуз", 4)
            .description(TRIGGER_CHANCE + "% шанс наложить " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER + " (" + EFFECT_DURATION + "с.) на цель.")
            .weight(COMMON)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.SNOW_COMMON), ConfusionEnchant::new);

        this.addData(map, EnchantId.CURE, EnchantDefinition.builder("Излечение", 3)
            .description(Arrays.asList(TRIGGER_CHANCE + "% шанс вылечить зомбированных пиглинов и", "зомби-жителей деревни при атаке."))
            .weight(COMMON)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.SAVANNA_COMMON), CureEnchant::new);

        this.addData(map, EnchantId.CUTTER, EnchantDefinition.builder("Разоружение", 6)
            .description(TRIGGER_CHANCE + "% шанс снять случайную часть брони с противника")
            .weight(VERY_RARE)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.PLAINS_COMMON), CutterEnchant::new);

        this.addData(map, EnchantId.DECAPITATOR, EnchantDefinition.builder("Обезглавливатель", 2)
            .description(TRIGGER_CHANCE + "% шанс получить голову игрока или моба.")
            .weight(COMMON)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.treasure(TradeType.SNOW_SPECIAL), DecapitatorEnchant::new);

        this.addData(map, EnchantId.DOUBLE_STRIKE, EnchantDefinition.builder("Двойной удар", 3)
                .description(TRIGGER_CHANCE + "% шанс нанести двойной удар.")
                .weight(VERY_RARE)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.TAIGA_COMMON), DoubleStrikeEnchant::new);

        this.addData(map, EnchantId.EXHAUST, EnchantDefinition.builder("Голодный", 4)
            .description(TRIGGER_CHANCE + "% шанс наложить " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER + " (" + EFFECT_DURATION + "с.) на цель.")
            .weight(COMMON)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.PLAINS_COMMON), ExhaustEnchant::new);

        this.addData(map, EnchantId.HOTBAR_SCRAMBLE, EnchantDefinition.builder("Перемешка", 3)
            .description(TRIGGER_CHANCE + "% шанс перемешать хотбар противника")
            .weight(VERY_RARE)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.DESERT_COMMON), HotbarScrambleEnchant::new);

        this.addData(map, EnchantId.ICE_ASPECT, EnchantDefinition.builder("Ледяной аспект", 3)
            .description(TRIGGER_CHANCE + "% шанс наложить " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER + " (" + EFFECT_DURATION + "с.) на цель.")
            .weight(UNCOMMON)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.SNOW_COMMON), IceAspectEnchant::new);

        this.addData(map, EnchantId.INFERNUS, EnchantDefinition.builder("Инферно", 3)
            .description(TRIGGER_CHANCE + "% шанс поджечь врага запущенным трезубцем")
            .weight(UNCOMMON)
            .items(ItemSetId.TRIDENT)
            .build(), EnchantDistribution.regular(TradeType.SAVANNA_COMMON), InfernusEnchant::new);

        this.addData(map, EnchantId.NIMBLE, EnchantDefinition.builder("Проворный", 1)
            .description(Arrays.asList("Перемещает выпавшие предметы", "непосредственно в инвентарь."))
            .weight(RARE)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.JUNGLE_COMMON), NimbleEnchant::new);

        this.addData(map, EnchantId.PARALYZE, EnchantDefinition.builder("Паралич", 4)
            .description(TRIGGER_CHANCE + "% шанс наложить " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER + " (" + EFFECT_DURATION + "с.) на цель.")
            .weight(UNCOMMON)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.TAIGA_COMMON), ParalyzeEnchant::new);

        this.addData(map, EnchantId.RAGE, EnchantDefinition.builder("Берсерк", 5)
                .description(Arrays.asList(TRIGGER_CHANCE + "% шанс получить " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER + " в бою.", "(раз в 5 секунд)."))
                .weight(UNCOMMON)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.DESERT_COMMON), RageEnchant::new);

        this.addData(map, EnchantId.ROCKET, EnchantDefinition.builder("Ракета", 3)
            .description(TRIGGER_CHANCE + "% шанс подбросить врага.")
            .weight(UNCOMMON)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.treasure(TradeType.JUNGLE_COMMON), RocketEnchant::new);

//        this.addData(map, EnchantId.SURPRISE, EnchantDefinition.builder("Surprise", 3)
//            .description(TRIGGER_CHANCE + "% chance to apply random potion effect to enemy on hit.")
//            .weight(UNCOMMON)
//            .supportedItems(ItemSetId.SWORDS_AXES)
//            .primaryItems(ItemSetId.SWORD)
//            .build(), EnchantDistribution.regular(TradeType.JUNGLE_COMMON));

        this.addData(map, EnchantId.SWIPER, EnchantDefinition.builder("Вор", 3)
            .description(TRIGGER_CHANCE + "% шанс украсть " + GENERIC_AMOUNT + "опыта у игрока.")
            .weight(COMMON)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.SWAMP_COMMON), SwiperEnchant::new);

        this.addData(map, EnchantId.TEMPER, EnchantDefinition.builder("Закалка", 5)
            .description(Arrays.asList("Увеличивает урон на " + GENERIC_AMOUNT + "%", "за каждые потерянные " + GENERIC_RADIUS + "❤."))
            .weight(UNCOMMON)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.TAIGA_COMMON), TemperEnchant::new);

        this.addData(map, EnchantId.THRIFTY, EnchantDefinition.builder("Экономный", 3)
            .description(TRIGGER_CHANCE + "% шанс получить яйцо призыва моба.")
            .weight(VERY_RARE)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.treasure(TradeType.JUNGLE_SPECIAL), ThriftyEnchant::new);

        this.addData(map, EnchantId.THUNDER, EnchantDefinition.builder("Гром", 5)
            .description(Arrays.asList(TRIGGER_CHANCE + "% шанс поразить молнией нанеся " + GENERIC_DAMAGE + "❤", "дополнительного урона (раз в 3 секунды)."))
            .weight(VERY_RARE)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.PLAINS_COMMON), ThunderEnchant::new);

        this.addData(map, EnchantId.VAMPIRE, EnchantDefinition.builder("Вампир", 3)
            .description(Arrays.asList(TRIGGER_CHANCE + "% шанс восстановить " + GENERIC_AMOUNT + "❤ при атаке.", " (раз в 2 секунды)."))
            .weight(RARE)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.SAVANNA_COMMON), VampireEnchant::new);

        this.addData(map, EnchantId.VENOM, EnchantDefinition.builder("Ядовитый", 3)
            .description(TRIGGER_CHANCE + "% шанс наложить " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER + " (" + EFFECT_DURATION + "с.) на цель.")
            .weight(COMMON)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.SWAMP_COMMON), VenomEnchant::new);

        this.addData(map, EnchantId.VILLAGE_DEFENDER, EnchantDefinition.builder("Защитник деревни", 3)
            .description("Увеличивает урон по разбойникам на " + GENERIC_AMOUNT + "❤.")
            .weight(COMMON)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.PLAINS_COMMON), VillageDefenderEnchant::new);

        this.addData(map, EnchantId.WISDOM, EnchantDefinition.builder("Мудрость", 5)
            .description("Мобы сбрасывают больше опыта (x" + GENERIC_MODIFIER + ").")
            .weight(UNCOMMON)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.DESERT_COMMON), WisdomEnchant::new);

        this.addData(map, EnchantId.WITHER, EnchantDefinition.builder("Иссушение", 5)
            .description(TRIGGER_CHANCE + "% шанс наложить " + EFFECT_TYPE + " " + EFFECT_AMPLIFIER + " (" + EFFECT_DURATION + "с.) на цель.")
            .weight(UNCOMMON)
            .supportedItems(ItemSetId.SWORDS_AXES)
            .primaryItems(ItemSetId.SWORD)
            .build(), EnchantDistribution.regular(TradeType.SNOW_COMMON), WitherEnchant::new);
    }
}
