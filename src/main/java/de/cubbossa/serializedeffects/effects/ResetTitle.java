package de.cubbossa.serializedeffects.effects;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;

public class ResetTitle extends EffectPlayer {

	public static ResetTitle deserialize(Map<String, Object> values) {
		var ret = new ResetTitle();
		EffectPlayer.deserialize(values).getEffectPlayers(false).forEach((key, value) -> ret.addEffect(value, key));
		return ret;
	}

	@Override
	public void play(Player player, Location location, Object... args) {
		player.resetTitle();
		super.play(player, location, args);;
	}
}
