package de.cubbossa.serializedeffects.effects;

import de.cubbossa.serializedeffects.EffectHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActionBarPlayer extends EffectPlayer {

	private String text;

	public ActionBarPlayer withText(String text) {
		this.text = text;
		return this;
	}

	@Override
	public void play(Player player, Location location, Object... args) {
		TagResolver resolver = args.length > 0 && args[0] instanceof TagResolver r ? r : TagResolver.empty();
		EffectHandler.getInstance().getAudiences().player(player).sendActionBar(
				EffectHandler.getInstance().getMiniMessage().deserialize(text, resolver));
		super.play(player, location, args);
	}

	@Override
	public EffectPlayer clone() {
		var effect = new ActionBarPlayer().withText(text);
		getEffectPlayers(false).forEach((effectPlayer, integer) -> effect.addEffect(integer, effectPlayer.clone()));
		return effect;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> ret = new LinkedHashMap<>();
		ret.put("text", text);
		var subEffects = super.serialize();
		if (!subEffects.isEmpty()) {
			ret.put("effects", subEffects);
		}
		return ret;
	}

	public static ActionBarPlayer deserialize(Map<String, Object> values) {
		ActionBarPlayer ret = new ActionBarPlayer((String) values.getOrDefault("text", "Title"));
		EffectPlayer.deserialize(values).getEffectPlayers(false).forEach((key, value) -> ret.addEffect(value, key));
		return ret;
	}

	@Override
	public String toString() {
		return "ActionBarPlayer{" +
				"text=" + text +
				'}';
	}
}
