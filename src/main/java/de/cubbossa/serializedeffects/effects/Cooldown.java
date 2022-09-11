package de.cubbossa.serializedeffects.effects;

import de.cubbossa.serializedeffects.EffectContext;
import de.cubbossa.serializedeffects.EffectHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Cooldown extends EffectPlayer {

	private int ticks;

	public Cooldown withDuration(int ticks) {
		this.ticks = ticks;
		return this;
	}

	@Override
	public void play(EffectContext context, Object... args) {
		EffectHandler.getInstance().lockEffect(context.player(), context.root());
		Bukkit.getScheduler().runTaskLater(EffectHandler.getInstance().getPlugin(), () -> EffectHandler.getInstance().freeEffect(context.player(), context.root()), ticks);
		super.play(context, args);
	}

	@Override
	public EffectPlayer clone() {
		var effect = new Cooldown().withDuration(ticks);
		getEffectPlayers(false).forEach((effectPlayer, integer) -> effect.addEffect(integer, effectPlayer.clone()));
		return effect;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> ret = new LinkedHashMap<>();
		ret.put("duration", ticks);
		var subEffects = super.serialize();
		if (!subEffects.isEmpty()) {
			ret.put("effects", subEffects);
		}
		return ret;
	}

	public static Cooldown deserialize(Map<String, Object> values) {
		Cooldown ret = new Cooldown((int) values.getOrDefault("ticks", 1));
		EffectPlayer.deserialize(values).getEffectPlayers(false).forEach((key, value) -> ret.addEffect(value, key));
		return ret;
	}

	@Override
	public String toString() {
		return "Cooldown{" +
				"ticks=" + ticks +
				'}';
	}
}
