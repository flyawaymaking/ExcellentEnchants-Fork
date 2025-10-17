package su.nightexpress.excellentenchants.enchantment.universal;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentenchants.EnchantsPlugin;
import su.nightexpress.excellentenchants.api.EnchantData;
import su.nightexpress.excellentenchants.api.EnchantPriority;
import su.nightexpress.excellentenchants.api.EnchantsPlaceholders;
import su.nightexpress.excellentenchants.api.Modifier;
import su.nightexpress.excellentenchants.api.enchantment.component.EnchantComponent;
import su.nightexpress.excellentenchants.api.enchantment.meta.Period;
import su.nightexpress.excellentenchants.api.enchantment.meta.Probability;
import su.nightexpress.excellentenchants.api.enchantment.type.DurabilityEnchant;
import su.nightexpress.excellentenchants.enchantment.GameEnchantment;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.bukkit.NightSound;
import su.nightexpress.nightcore.util.wrapper.UniParticle;

import java.io.File;

public class RestoreEnchant extends GameEnchantment implements DurabilityEnchant {

    private Modifier amount;

    public RestoreEnchant(@NotNull EnchantsPlugin plugin, @NotNull File file, @NotNull EnchantData data) {
        super(plugin, file, data);
        this.addComponent(EnchantComponent.PROBABILITY, Probability.addictive(0, 6));
        this.addComponent(EnchantComponent.PERIODIC, Period.ofSeconds(15));
    }

    @Override
    protected void loadAdditional(@NotNull FileConfig config) {
    }

    public double getAmount(int level) {
        return this.amount.getValue(level);
    }

    @Override
    @NotNull
    public EnchantPriority getItemDamagePriority() {
        return EnchantPriority.MONITOR;
    }

    @Override
    public boolean onItemDamage(@NotNull PlayerItemDamageEvent event, @NotNull Player player, @NotNull ItemStack itemStack, int level) {
        if (!(itemStack.getItemMeta() instanceof Damageable damageable)) return false;

        int damage = event.getDamage();
        int currentDamage = damageable.getDamage();
        int maxDurability = itemStack.getType().getMaxDurability();

        event.setCancelled(true);

        int damageToRestore = damage;

        int newDamage = Math.max(0, currentDamage - damageToRestore);

        damageable.setDamage(newDamage);

        if (currentDamage + damage < maxDurability) {
            damageable.removeEnchant(this.getBukkitEnchantment());
            if (this.hasVisualEffects()) {
                NightSound.of(Sound.ITEM_TOTEM_USE).play(event.getPlayer());
                UniParticle.of(Particle.HEART)
                    .play(player.getEyeLocation(), 0.5, 0.5, 10);
            }
        }

        itemStack.setItemMeta(damageable);

        return true;
    }
}
