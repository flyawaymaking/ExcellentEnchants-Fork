package su.nightexpress.excellentenchants.enchantment.weapon;

import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentenchants.EnchantsPlugin;
import su.nightexpress.excellentenchants.enchantment.EnchantData;
import su.nightexpress.excellentenchants.api.EnchantPriority;
import su.nightexpress.excellentenchants.api.Modifier;
import su.nightexpress.excellentenchants.api.enchantment.component.EnchantComponent;
import su.nightexpress.excellentenchants.api.enchantment.meta.Period;
import su.nightexpress.excellentenchants.api.enchantment.meta.Probability;
import su.nightexpress.excellentenchants.api.enchantment.type.AttackEnchant;
import su.nightexpress.excellentenchants.enchantment.GameEnchantment;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.wrapper.UniParticle;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class HotbarScrambleEnchant extends GameEnchantment implements AttackEnchant {

    private static final int HOTBAR_SLOTS = 9;
    private static final Random RANDOM = new Random();

    private Modifier duration;
    private Modifier scrambleChance;

    public HotbarScrambleEnchant(@NotNull EnchantsPlugin plugin, @NotNull File file, @NotNull EnchantData data) {
        super(plugin, file, data);
        this.addComponent(EnchantComponent.PROBABILITY, Probability.addictive(3, 2));
        this.addComponent(EnchantComponent.PERIODIC, Period.ofSeconds(15));
    }

    @Override
    protected void loadAdditional(@NotNull FileConfig config) {
    }

    private void scrambleHotbar(@NotNull Player player) {
        PlayerInventory inventory = player.getInventory();
        ItemStack[] hotbar = new ItemStack[HOTBAR_SLOTS];

        // Copy current hotbar
        for (int i = 0; i < HOTBAR_SLOTS; i++) {
            hotbar[i] = inventory.getItem(i);
        }

        // Shuffle the hotbar items
        List<ItemStack> hotbarList = Arrays.asList(hotbar);
        Collections.shuffle(hotbarList);

        // Apply shuffled items back to hotbar
        for (int i = 0; i < HOTBAR_SLOTS; i++) {
            inventory.setItem(i, hotbarList.get(i));
        }

        // Update inventory
        player.updateInventory();
    }

    @Override
    @NotNull
    public EnchantPriority getAttackPriority() {
        return EnchantPriority.NORMAL;
    }

    @Override
    public boolean onAttack(@NotNull EntityDamageByEntityEvent event, @NotNull LivingEntity damager, @NotNull LivingEntity victim, @NotNull ItemStack weapon, int level) {
        Player playerVictim = (Player) victim;
        scrambleHotbar(playerVictim);

        if (this.hasVisualEffects()) {
            UniParticle.of(Particle.WITCH)
                .play(victim.getEyeLocation(), 0.7, 0.5, 25);
        }

        return true;
    }
}
