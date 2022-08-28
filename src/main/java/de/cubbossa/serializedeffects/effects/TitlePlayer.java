package de.cubbossa.serializedeffects.effects;

import de.cubbossa.serializedeffects.EffectContext;
import de.cubbossa.serializedeffects.EffectHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TitlePlayer extends EffectPlayer {

	public String title;
	public String subTitle;
	public int fadeInTicks = 20;
	public int stayTicks = 60;
	public int fadeOutTicks = 20;

	public TitlePlayer withText(String title) {
		this.title = title;
		return this;
	}

	public TitlePlayer withSubTitle(String subTitle) {
		this.subTitle = subTitle;
		return this;
	}

	public TitlePlayer withTimings(int fadeInTicks, int stayTicks, int fadeOutTicks) {
		this.fadeInTicks = fadeInTicks;
		this.stayTicks = stayTicks;
		this.fadeOutTicks = fadeOutTicks;
		return this;
	}

	@Override
	public void play(EffectContext context, Object... args) {
		TagResolver resolver = args.length > 0 && args[0] instanceof TagResolver r ? r : TagResolver.empty();
		EffectHandler.getInstance().getAudiences().player(context.player()).showTitle(Title.title(
				EffectHandler.getInstance().getTranslator().apply(new EffectHandler.TextContext(title, context.player(), resolver)),
				EffectHandler.getInstance().getTranslator().apply(new EffectHandler.TextContext(subTitle, context.player(), resolver)),
				Title.Times.times(Duration.ofMillis(fadeInTicks * 50L), Duration.ofMillis(stayTicks * 50L), Duration.ofMillis(fadeOutTicks * 50L))));
		super.play(context, args);
	}

	@Override
	public EffectPlayer clone() {
		var effect = new TitlePlayer().withText(title).withSubTitle(subTitle).withTimings(fadeInTicks, stayTicks, fadeOutTicks);
		getEffectPlayers(false).forEach((effectPlayer, integer) -> effect.addEffect(integer, effectPlayer.clone()));
		return effect;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> ret = new LinkedHashMap<>();
		ret.put("title", title);
		ret.put("subtitle", subTitle);
		ret.put("fade-in-ticks", fadeInTicks);
		ret.put("stay-ticks", stayTicks);
		ret.put("fade-out-ticks", fadeOutTicks);
		var subEffects = super.serialize();
		if (!subEffects.isEmpty()) {
			ret.put("effects", subEffects);
		}
		return ret;
	}

	public static TitlePlayer deserialize(Map<String, Object> values) {
		var ret = new TitlePlayer(
				(String) values.getOrDefault("title", "Title"),
				(String) values.getOrDefault("subtitle", "Subtitle"),
				(int) values.getOrDefault("fade-in-ticks", 20),
				(int) values.getOrDefault("stay-ticks", 60),
				(int) values.getOrDefault("fade-out-ticks", 20));
		EffectPlayer.deserialize(values).getEffectPlayers(false).forEach((key, value) -> ret.addEffect(value, key));
		return ret;
	}

	@Override
	public String toString() {
		return "TitlePlayer{" +
				"title=" + title +
				", subTitle=" + subTitle +
				", fadeInTicks=" + fadeInTicks +
				", stayTicks=" + stayTicks +
				", fadeOutTicks=" + fadeOutTicks +
				'}';
	}
}
