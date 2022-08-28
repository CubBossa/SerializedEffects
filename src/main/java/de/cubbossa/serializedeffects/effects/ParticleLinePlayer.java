package de.cubbossa.serializedeffects.effects;

import de.cubbossa.serializedeffects.EffectContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ParticleLinePlayer extends ParticlePlayer {

	private float length;
	private float distance;

	public ParticleLinePlayer(Particle particle, int amount, Vector offset, float length, float distance) {
		super(particle, amount, offset);
		this.length = length;
		this.distance = distance;
	}

	public ParticleLinePlayer withLength(float length) {
		this.length = length;
		return this;
	}

	public ParticleLinePlayer withDistance(float distance) {
		this.distance = distance;
		return this;
	}

	@Override
	public void play(EffectContext context, Object... args) {
		for (float i = 0; i < length; i += distance) {
			super.play(new EffectContext(context.player(), context.location().clone().add(context.location().getDirection().normalize().multiply(i)), context.root()), args);
		}
	}

	public Map<String, Object> serialize() {
		Map<String, Object> ret = super.serialize();
		ret.put("length", length);
		ret.put("distance", distance);
		return ret;
	}

	public static ParticleLinePlayer deserialize(Map<String, Object> values) {
		var ret = new ParticleLinePlayer(Particle.valueOf((String) values.get("particle")),
				(int) values.getOrDefault("amount", 1),
				(Vector) values.getOrDefault("offset", new Vector()),
				(float) values.getOrDefault("length", 1),
				(float) values.getOrDefault("distance", 0.1f));
		ret.withMotion((Vector) values.getOrDefault("motion", new Vector()));
		ret.withData(values.getOrDefault("data", null));
		EffectPlayer.deserialize(values).getEffectPlayers(false).forEach((key, value) -> ret.addEffect(value, key));
		return ret;
	}
}
