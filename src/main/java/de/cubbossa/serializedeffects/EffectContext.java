package de.cubbossa.serializedeffects;

import de.cubbossa.serializedeffects.effects.EffectPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public record EffectContext(Player player, Location location, EffectPlayer root) {
}
