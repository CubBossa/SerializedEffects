package de.cubbossa.serializedeffects.effects;

import de.cubbossa.serializedeffects.EffectContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParticlePlayer extends EffectPlayer {

	private Particle particle;
	private int amount;
	private Vector offset = new Vector(0, 0, 0);
	private Vector motion = new Vector(0, 0, 0);
	private Object data = null;

	public ParticlePlayer(Particle particle, int amount, Vector offset) {
		this.particle = particle;
		this.amount = amount;
		this.offset = offset;
	}

	@Override
	public void play(EffectContext context, Object... args) {
		Location location = context.location().clone().subtract(0, .3f, 0);
		Vector motion = this.motion.clone().multiply(location.getDirection());
		context.player().spawnParticle(particle, location.clone().add((Math.random() * 2 - 1) * offset.getX(), (Math.random() * 2 - 1) * offset.getY(), (Math.random() * 2 - 1) * offset.getZ()), amount, motion.getX(), motion.getY(), motion.getZ(), data);
		super.play(context, args);
	}

	public ParticlePlayer withParticle(Particle particle) {
		this.particle = particle;
		return this;
	}

	public ParticlePlayer withAmount(int amount) {
		this.amount = amount;
		return this;
	}

	public ParticlePlayer withOffset(Vector offset) {
		this.offset = offset;
		return this;
	}

	public ParticlePlayer withMotion(Vector motion) {
		this.motion = motion;
		return this;
	}

	public <T> ParticlePlayer withData(T data) {
		this.data = data;
		return this;
	}

	public Map<String, Object> serialize() {
		Map<String, Object> ret = new LinkedHashMap<>();
		ret.put("particle", particle.toString());
		ret.put("amount", amount);
		ret.put("offset", offset);
		ret.put("motion", motion);
		if (data != null) {
			ret.put("data", data);
		}
		var subEffects = super.serialize();
		if (!subEffects.isEmpty()) {
			ret.put("effects", subEffects);
		}
		return ret;
	}

	public static ParticlePlayer deserialize(Map<String, Object> values) {
		ParticlePlayer ret = new ParticlePlayer(Particle.valueOf((String) values.get("particle")),
				(int) values.getOrDefault("amount", 1),
				(Vector) values.getOrDefault("offset", new Vector()),
				(Vector) values.getOrDefault("motion", new Vector()),
				values.getOrDefault("data", null));
		EffectPlayer.deserialize(values).getEffectPlayers(false).forEach((key, value) -> ret.addEffect(value, key));
		return ret;
	}
}
