package su.nightexpress.excellentenchants.command;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.block.ShulkerBox;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentenchants.EnchantsPlugin;
import su.nightexpress.excellentenchants.enchantment.EnchantRegistry;
import su.nightexpress.excellentenchants.api.EnchantsPlaceholders;
import su.nightexpress.excellentenchants.api.enchantment.CustomEnchantment;
import su.nightexpress.excellentenchants.config.Config;
import su.nightexpress.excellentenchants.config.Lang;
import su.nightexpress.excellentenchants.config.Perms;
import su.nightexpress.excellentenchants.enchantment.GameEnchantment;
import su.nightexpress.excellentenchants.util.EnchantUtils;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.Commands;
import su.nightexpress.nightcore.commands.builder.HubNodeBuilder;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.util.*;
import su.nightexpress.nightcore.util.bridge.RegistryType;
import su.nightexpress.nightcore.util.random.Rnd;

import java.util.*;
import java.util.stream.Collectors;

public class BaseCommands {

    private final EnchantsPlugin plugin;

    public BaseCommands(@NotNull EnchantsPlugin plugin) {
        this.plugin = plugin;
    }

    public void load(@NotNull HubNodeBuilder builder) {
        builder.branch(Commands.literal("reload")
            .description(CoreLang.COMMAND_RELOAD_DESC)
            .permission(Perms.COMMAND_RELOAD)
            .executes((context, arguments) -> {
                this.plugin.doReload(context.getSender());
                return true;
            })
        );

        builder.branch(Commands.literal("book")
            .description(Lang.COMMAND_BOOK_DESC)
            .permission(Perms.COMMAND_BOOK)
            .withArguments(
                CommandArguments.enchantArgument(CommandArguments.ENCHANT),
                CommandArguments.levelArgument(CommandArguments.LEVEL).optional(),
                Arguments.player(CommandArguments.PLAYER).optional()
            )
            .withFlags(CommandArguments.FLAG_CHARGED)
            .executes(this::giveEnchantBook)
        );

        builder.branch(Commands.literal("randombook")
            .description(Lang.COMMAND_RANDOM_BOOK_DESC)
            .permission(Perms.COMMAND_RANDOM_BOOK)
            .withArguments(Arguments.player(CommandArguments.PLAYER).optional())
            .withFlags(CommandArguments.FLAG_CUSTOM, CommandArguments.FLAG_CHARGED)
            .executes(this::giveRandomBook)
        );

        builder.branch(Commands.literal("enchant")
            .description(Lang.COMMAND_ENCHANT_DESC)
            .permission(Perms.COMMAND_ENCHANT)
            .withArguments(
                CommandArguments.enchantArgument(CommandArguments.ENCHANT),
                CommandArguments.levelArgument(CommandArguments.LEVEL),
                Arguments.player(CommandArguments.PLAYER).optional(),
                CommandArguments.slotArgument(CommandArguments.SLOT).optional()
            )
            .withFlags(CommandArguments.FLAG_CHARGED)
            .executes(this::enchantItem)
        );

        builder.branch(Commands.literal("disenchant")
            .description(Lang.COMMAND_DISENCHANT_DESC)
            .permission(Perms.COMMAND_DISENCHANT)
            .withArguments(
                CommandArguments.enchantArgument(CommandArguments.ENCHANT),
                Arguments.player(CommandArguments.PLAYER).optional(),
                CommandArguments.slotArgument(CommandArguments.SLOT).optional()
            )
            .executes(this::disenchantItem)
        );

        builder.branch(Commands.literal("list")
            .playerOnly()
            .description(Lang.COMMAND_LIST_DESC)
            .permission(Perms.COMMAND_LIST)
            .withArguments(Arguments.player(CommandArguments.PLAYER).permission(Perms.COMMAND_LIST_OTHERS).optional())
            .executes(this::openList)
        );

        if (Config.isChargesEnabled()) {
            builder.branch(Commands.literal("givefuel")
                .playerOnly()
                .description(Lang.COMMAND_GIVE_FUEL_DESC)
                .permission(Perms.COMMAND_GIVE_FUEL)
                .withArguments(
                    CommandArguments.customEnchantArgument(CommandArguments.ENCHANT)
                        .suggestions((reader, context) -> EnchantRegistry.getRegistered().stream().filter(CustomEnchantment::isChargeable).map(CustomEnchantment::getId).toList()),
                    Arguments.integer(CommandArguments.AMOUNT, 1).suggestions((rader, context) -> Lists.newList("1", "8", "16", "32", "64")).optional(),
                    Arguments.player(CommandArguments.PLAYER).optional()
                )
                .executes(this::giveFuel)
            );
        }

        builder.branch(Commands.literal("randomenchant")
            .description("Получить случайную книгу зачарования по weight")
            .permission(Perms.COMMAND_BOOK)
            .withArguments(
                    Arguments.integer("weight", 1).suggestions((rader, context) -> Lists.newList("1", "2", "5", "10")),
                    Arguments.player(CommandArguments.PLAYER).optional()
            )
            .executes(this::giveRandomEnchantByWeight)
        );

        builder.branch(Commands.literal("allenchants")
            .description("Получить все книги зачарований по weight")
            .permission(Perms.COMMAND_BOOK)
            .withArguments(Arguments.integer("weight", 1).suggestions((rader, context) -> Lists.newList("1", "2", "5", "10")))
            .executes(this::giveAllEnchantsByWeight)
        );

        builder.branch(Commands.literal("weightlist")
            .description("Показать список доступных weight")
            .permission(Perms.COMMAND_LIST)
            .executes(this::showWeightList)
        );
    }

    private int getLevel(@NotNull Enchantment enchantment, @NotNull ParsedArguments arguments) {
        int level = arguments.getInt(CommandArguments.LEVEL, -1);
        if (level <= 0) {
            level = EnchantUtils.randomLevel(enchantment);
        }
        return level;
    }

    private boolean giveEnchantBook(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        if (!context.isPlayer() && !arguments.contains(CommandArguments.PLAYER)) {
            context.printUsage();
            return false;
        }

        Player player = arguments.contains(CommandArguments.PLAYER) ? arguments.getPlayer(CommandArguments.PLAYER) : context.getPlayerOrThrow();

        boolean charged = context.hasFlag(CommandArguments.FLAG_CHARGED);
        Enchantment enchantment = arguments.getEnchantment(CommandArguments.ENCHANT);
        int level = getLevel(enchantment, arguments);

        return this.giveBook(context.getSender(), player, enchantment, level, charged);
    }

    private boolean giveRandomBook(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        if (!context.isPlayer() && !arguments.contains(CommandArguments.PLAYER)) {
            context.printUsage();
            return false;
        }

        Player player = arguments.contains(CommandArguments.PLAYER) ? arguments.getPlayer(CommandArguments.PLAYER) : context.getPlayerOrThrow();

        boolean custom = context.hasFlag(CommandArguments.FLAG_CUSTOM);
        boolean charged = context.hasFlag(CommandArguments.FLAG_CHARGED);
        Enchantment enchantment = Rnd.get(custom ? EnchantRegistry.getRegisteredBukkit() : BukkitThing.getAll(RegistryType.ENCHANTMENT));
        int level = EnchantUtils.randomLevel(enchantment);

        return this.giveBook(context.getSender(), player, enchantment, level, charged);
    }

    private boolean giveBook(@NotNull CommandSender sender, @NotNull Player player, @NotNull Enchantment enchantment, int level, boolean charged) {
        ItemStack itemStack = new ItemStack(Material.ENCHANTED_BOOK);
        if (charged) {
            EnchantUtils.restoreCharges(itemStack, enchantment, level);
        }

        EnchantUtils.add(itemStack, enchantment, level, true);
        Players.addItem(player, itemStack);

        Lang.ENCHANTED_BOOK_GAVE.message().send(sender, replacer -> replacer
            .replace(EnchantsPlaceholders.GENERIC_ENCHANT, LangUtil.getSerializedName(enchantment))
            .replace(EnchantsPlaceholders.GENERIC_LEVEL, NumberUtil.toRoman(level))
            .replace(EnchantsPlaceholders.forPlayer(player))
        );

        return true;
    }

    private boolean enchantItem(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        if (!context.isPlayer() && !arguments.contains(CommandArguments.PLAYER)) {
            context.printUsage();
            return false;
        }

        Player player = arguments.contains(CommandArguments.PLAYER) ? arguments.getPlayer(CommandArguments.PLAYER) : context.getPlayerOrThrow();
        EquipmentSlot slot = arguments.getOr(CommandArguments.SLOT, EquipmentSlot.class, EquipmentSlot.HAND);

        ItemStack itemStack = EntityUtil.getItemInSlot(player, slot);
        if (itemStack == null || itemStack.getType().isAir()) {
            context.send(Lang.COMMAND_ENCHANT_ERROR_NO_ITEM);
            return false;
        }

        boolean charged = context.hasFlag(CommandArguments.FLAG_CHARGED);
        Enchantment enchantment = arguments.getEnchantment(CommandArguments.ENCHANT);
        int level = getLevel(enchantment, arguments);

        EnchantUtils.add(itemStack, enchantment, level, true);

        if (charged) {
            EnchantUtils.restoreCharges(itemStack, enchantment, level);
        }

        context.send(context.getSender() == player ? Lang.COMMAND_ENCHANT_DONE_SELF : Lang.COMMAND_ENCHANT_DONE_OTHERS, replacer -> replacer
            .replace(EnchantsPlaceholders.forPlayer(player))
            .replace(EnchantsPlaceholders.GENERIC_ITEM, ItemUtil.getNameSerialized(itemStack))
            .replace(EnchantsPlaceholders.GENERIC_ENCHANT, LangUtil.getSerializedName(enchantment))
            .replace(EnchantsPlaceholders.GENERIC_LEVEL, NumberUtil.toRoman(level))
        );

        return true;
    }

    private boolean disenchantItem(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        if (!context.isPlayer() && !arguments.contains(CommandArguments.PLAYER)) {
            context.printUsage();
            return false;
        }

        Player player = arguments.contains(CommandArguments.PLAYER) ? arguments.getPlayer(CommandArguments.PLAYER) : context.getPlayerOrThrow();
        EquipmentSlot slot = arguments.getOr(CommandArguments.SLOT, EquipmentSlot.class, EquipmentSlot.HAND);

        ItemStack itemStack = EntityUtil.getItemInSlot(player, slot);
        if (itemStack == null || itemStack.getType().isAir()) {
            context.send(Lang.COMMAND_ENCHANT_ERROR_NO_ITEM);
            return false;
        }

        Enchantment enchantment = arguments.getEnchantment(CommandArguments.ENCHANT);
        EnchantUtils.remove(itemStack, enchantment);

        context.send(context.getSender() == player ? Lang.COMMAND_DISENCHANT_DONE_SELF : Lang.COMMAND_DISENCHANT_DONE_OTHERS, replacer -> replacer
            .replace(EnchantsPlaceholders.forPlayer(player))
            .replace(EnchantsPlaceholders.GENERIC_ITEM, ItemUtil.getNameSerialized(itemStack))
            .replace(EnchantsPlaceholders.GENERIC_ENCHANT, LangUtil.getSerializedName(enchantment))
        );

        return true;
    }

    private boolean giveFuel(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        if (!context.isPlayer() && !arguments.contains(CommandArguments.PLAYER)) {
            context.printUsage();
            return false;
        }

        Player player = arguments.contains(CommandArguments.PLAYER) ? arguments.getPlayer(CommandArguments.PLAYER) : context.getPlayerOrThrow();
        CustomEnchantment enchantment = arguments.get(CommandArguments.ENCHANT, CustomEnchantment.class);
        int amount = arguments.getInt(CommandArguments.AMOUNT, 1);

        if (!enchantment.isChargeable()) {
            context.send(Lang.CHARGES_FUEL_BAD_ENCHANTMENT, replacer -> replacer.replace(EnchantsPlaceholders.GENERIC_NAME, enchantment.getDisplayName()));
            return false;
        }

        ItemStack fuel = enchantment.getFuel();
        fuel.setAmount(amount);

        Players.addItem(player, fuel);

        context.send(Lang.CHARGES_FUEL_GAVE, replacer -> replacer
            .replace(EnchantsPlaceholders.GENERIC_AMOUNT, NumberUtil.format(amount))
            .replace(EnchantsPlaceholders.GENERIC_NAME, ItemUtil.getNameSerialized(fuel))
            .replace(EnchantsPlaceholders.forPlayer(player))
        );

        return true;
    }

    private boolean openList(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        if (!context.isPlayer() && !arguments.contains(CommandArguments.PLAYER)) {
            context.printUsage();
            return false;
        }

        Player player = arguments.contains(CommandArguments.PLAYER) ? arguments.getPlayer(CommandArguments.PLAYER) : context.getPlayerOrThrow();
        this.plugin.getEnchantManager().openEnchantsMenu(player);

        if (player != context.getSender()) {
            context.send(Lang.COMMAND_LIST_DONE_OTHERS, replacer -> replacer.replace(EnchantsPlaceholders.forPlayer(player)));
        }
        return true;
    }

    private String getWeightName(int weight) {
        return switch (weight) {
            case 1 -> "§6§lЛегендарный чар";
            case 2 -> "§d§lЭпический чар";
            case 5 -> "§b§lЭлитный чар";
            case 10 -> "§a§lУникальный чар";
            default -> "Weight: " + weight;
        };
    }

    private boolean giveRandomEnchantByWeight(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = arguments.contains(CommandArguments.PLAYER) ? arguments.getPlayer(CommandArguments.PLAYER) : context.getPlayerOrThrow();

        int targetWeight = arguments.getInt("weight", -1);

        if (targetWeight <= 0) {
            context.getSender().sendMessage("§cWeight должен быть положительным числом!");
            return false;
        }

        // Получаем все CustomEnchantment и фильтруем по weight из definition
        List<CustomEnchantment> enchantments = EnchantRegistry.getRegistered().stream()
            .filter(enchant -> enchant.getDefinition().getWeight() == targetWeight)
            .collect(Collectors.toList());

        if (enchantments.isEmpty()) {
            context.getSender().sendMessage("§cНе найдено зачарований с weight: " + targetWeight);
            showAvailableWeights(context.getSender());
            return false;
        }

        // Выбираем случайное зачарование
        CustomEnchantment enchant = Rnd.get(enchantments);
        int maxLevel = enchant.getDefinition().getMaxLevel();
        int randomLevel = Rnd.get(1, maxLevel);

        // Создаем и выдаем книгу
        ItemStack enchantedBook = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantUtils.add(enchantedBook, enchant.getBukkitEnchantment(), randomLevel, true);
        Players.addItem(player, enchantedBook);

        // Красивое сообщение как в других командах плагина
        if (context.getSender() == player) {
            // Сообщение для себя
            Lang.ENCHANTED_BOOK_GAVE.message().send(context.getSender(), replacer -> replacer
                .replace(EnchantsPlaceholders.GENERIC_ENCHANT, enchant.getDisplayName())
                .replace(EnchantsPlaceholders.GENERIC_LEVEL, NumberUtil.toRoman(randomLevel))
                .replace(EnchantsPlaceholders.forPlayer(player))
                .replace("%weight%", String.valueOf(targetWeight))
            );
        } else {
            // Сообщение когда выдаем другому игроку
            context.getSender().sendMessage("§aВы выдали книгу с зачарованием §e" +
                enchant.getDisplayName() + " " + NumberUtil.toRoman(randomLevel) +
                "§a игроку §e" + player.getName() + "§a (weight: " + targetWeight + ")");

            // Сообщение для получателя
            player.sendMessage("§aВы получили книгу с зачарованием §e" +
                enchant.getDisplayName() + " " + NumberUtil.toRoman(randomLevel) +
                "§a (" + getWeightName(targetWeight) + "§a)");
        }

        return true;
    }

    private boolean giveAllEnchantsByWeight(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = arguments.contains(CommandArguments.PLAYER) ? arguments.getPlayer(CommandArguments.PLAYER) : context.getPlayerOrThrow();

        int targetWeight = arguments.getInt("weight", -1);

        if (targetWeight <= 0) {
            context.getSender().sendMessage("§cWeight должен быть положительным числом!");
            return false;
        }

        // Получаем все CustomEnchantment и фильтруем по weight из definition
        List<CustomEnchantment> enchantments = EnchantRegistry.getRegistered().stream()
            .filter(enchant -> enchant.getDefinition().getWeight() == targetWeight)
            .toList();

        if (enchantments.isEmpty()) {
            context.getSender().sendMessage("§cНе найдено зачарований с weight: " + targetWeight);
            showAvailableWeights(context.getSender());
            return false;
        }

        int givenCount = 0;

        // Создаем книги для каждого зачарования
        for (CustomEnchantment enchant : enchantments) {
            int maxLevel = enchant.getDefinition().getMaxLevel();

            if (maxLevel == 1) {
                // Для зачарований с одним уровнем - просто книга
                ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
                EnchantUtils.add(book, enchant.getBukkitEnchantment(), 1, true);
                Players.addItem(player, book);
                givenCount++;
            } else {
                // Для зачарований с несколькими уровнями - создаем шалкербокс
                ItemStack shulkerBox = createShulkerBoxWithLevels((GameEnchantment) enchant, maxLevel);
                Players.addItem(player, shulkerBox);
                givenCount++;
            }
        }

        context.getSender().sendMessage("§aВыдано " + givenCount + " предметов с зачарованиями weight: " + targetWeight);
        return true;
    }

    private boolean showWeightList(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        // Получаем все зарегистрированные зачарования
        Set<CustomEnchantment> registered = EnchantRegistry.getRegistered();

        if (registered.isEmpty()) {
            context.getSender().sendMessage("§cЗачарования не загружены!");
            return false;
        }

        // Группируем зачарования по weight из definition
        Map<Integer, List<CustomEnchantment>> weightMap = registered.stream()
            .collect(Collectors.groupingBy(enchant -> enchant.getDefinition().getWeight()));

        context.getSender().sendMessage("§6=== Доступные weight зачарований ===");

        if (weightMap.isEmpty()) {
            context.getSender().sendMessage("§cНе найдено зачарований!");
            return false;
        }

        weightMap.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> {
                int weight = entry.getKey();
                int count = entry.getValue().size();
                context.getSender().sendMessage("§eWeight " + weight + "§7: §f" + count + " зачарований");

                // Показываем первые 3 зачарования для примера
                List<String> enchantNames = entry.getValue().stream()
                    .limit(3)
                    .map(CustomEnchantment::getDisplayName)
                    .collect(Collectors.toList());

                context.getSender().sendMessage("§8Примеры: §7" + String.join("§8, §7", enchantNames));
            });

        return true;
    }

    private void showAvailableWeights(@NotNull CommandSender sender) {
        Set<CustomEnchantment> registered = EnchantRegistry.getRegistered();
        Set<Integer> weights = registered.stream()
            .map(enchant -> enchant.getDefinition().getWeight())
            .collect(Collectors.toSet());

        if (!weights.isEmpty()) {
            sender.sendMessage("§7Доступные weight: " + weights.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(", ")));
        }
    }

    // Вспомогательные методы
    @NotNull
    private static ItemStack createEnchantedBook(@NotNull Enchantment enchantment, int level) {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantUtils.add(book, enchantment, level, true);
        return book;
    }

    @NotNull
    private static ItemStack createShulkerBoxWithLevels(@NotNull GameEnchantment enchant, int maxLevel) {
        Material shulkerMaterial = Material.SHULKER_BOX;
        ItemStack shulkerBox = new ItemStack(shulkerMaterial);

        BlockStateMeta meta = (BlockStateMeta) shulkerBox.getItemMeta();
        if (meta == null) return shulkerBox;

        ShulkerBox shulker = (ShulkerBox) meta.getBlockState();

        // Заполняем шалкербокс книгами всех уровней
        for (int level = 1; level <= maxLevel; level++) {
            ItemStack book = createEnchantedBook(enchant.getBukkitEnchantment(), level);
            shulker.getInventory().setItem(level - 1, book);
        }

        // Устанавливаем название шалкербокса
        String displayName = enchant.getDisplayName();
        meta.setDisplayName("§6" + displayName + " §7(Уровни 1-" + maxLevel + ")");

        // Устанавливаем лор
        List<String> lore = Lists.newList(
            "§7Содержит все уровни зачарования",
            "§7" + displayName,
            "",
            "§8Weight: " + enchant.getDefinition().getWeight()
        );
        meta.setLore(lore);

        meta.setBlockState(shulker);
        shulkerBox.setItemMeta(meta);

        return shulkerBox;
    }
}
