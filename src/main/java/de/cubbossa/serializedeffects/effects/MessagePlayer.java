package de.cubbossa.serializedeffects.effects;

import de.cubbossa.serializedeffects.EffectContext;
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
public class MessagePlayer extends EffectPlayer {

	private String text;

	public MessagePlayer withText(String text) {
		this.text = text;
		return this;
	}

	@Override
	public void play(EffectContext context, Object... args) {
		TagResolver resolver = args.length > 0 && args[0] instanceof TagResolver r ? r : TagResolver.empty();
		EffectHandler.getInstance().getAudiences().player(context.player()).sendMessage(
				EffectHandler.getInstance().getTranslator().apply(new EffectHandler.TextContext(text, context.player(), resolver)));
		super.play(context, args);
	}

	@Override
	public EffectPlayer clone() {
		var effect = new MessagePlayer().withText(text);
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

	public static MessagePlayer deserialize(Map<String, Object> values) {
		MessagePlayer ret = new MessagePlayer((String) values.getOrDefault("text", "Text"));
		EffectPlayer.deserialize(values).getEffectPlayers(false).forEach((key, value) -> ret.addEffect(value, key));
		return ret;
	}

	@Override
	public String toString() {
		return "MessagePlayer{" +
				"text=" + text +
				'}';
	}
}
