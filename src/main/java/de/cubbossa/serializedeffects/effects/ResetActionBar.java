package de.cubbossa.serializedeffects.effects;

import de.cubbossa.serializedeffects.EffectContext;
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
	public void play(EffectContext context, Object... args) {
		EffectHandler.getInstance().getAudiences().player(context.player()).sendActionBar(Component.empty());
		super.play(context, args);
	}
}
