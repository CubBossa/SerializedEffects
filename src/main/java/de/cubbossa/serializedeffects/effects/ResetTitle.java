package de.cubbossa.serializedeffects.effects;

import de.cubbossa.serializedeffects.EffectContext;

import java.util.Map;

public class ResetTitle extends EffectPlayer {

	public static ResetTitle deserialize(Map<String, Object> values) {
		var ret = new ResetTitle();
		EffectPlayer.deserialize(values).getEffectPlayers(false).forEach((key, value) -> ret.addEffect(value, key));
		return ret;
	}

	@Override
	public void play(EffectContext context, Object... args) {
		context.player().resetTitle();
		super.play(context, args);
	}
}
