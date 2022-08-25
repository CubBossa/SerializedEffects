package de.cubbossa.serializedeffects.effects;

import de.cubbossa.serializedeffects.EffectHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;

public class ResetActionBar extends EffectPlayer {

	public static ResetActionBar deserialize(Map<String, Object> values) {
		var ret = new ResetActionBar();
		EffectPlayer.deserialize(values).getEffectPlayers(false).forEach((key, value) -> ret.addEffect(value, key));
		return ret;
	}

	@Override
	public void play(Player player, Location location, Object... args) {
		EffectHandler.getInstance().getAudiences().player(player).sendActionBar(Component.empty());
		super.play(player, location, args);;
	}
}
