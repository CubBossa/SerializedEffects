package de.cubbossa.serializedeffects;

import de.cubbossa.nbo.NBOFile;
import de.cubbossa.nbo.exceptions.NBOParseException;
import de.cubbossa.nbo.exceptions.NBOReferenceException;
import de.cubbossa.serializedeffects.effects.*;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class EffectHandler {

	@Getter
	private static EffectHandler instance;

	private JavaPlugin plugin;
	private BukkitAudiences audiences;
	private MiniMessage miniMessage;

	private final Map<File, Map<String, Object>> effects;

	public EffectHandler(JavaPlugin plugin, BukkitAudiences audiences, MiniMessage miniMessage) {
		instance = this;
		this.plugin = plugin;
		this.audiences = audiences;
		this.miniMessage = miniMessage;
		this.effects = new HashMap<>();
	}

	public void clearCache(File file) {
		effects.remove(file);
	}

	public EffectPlayer getEffect(File file, String name) {
		return effects.computeIfAbsent(file, f -> {
			try {
				return NBOFile.loadFile(f, NBOFile.DEFAULT_SERIALIZER
						.registerMapSerializer(ActionBarPlayer.class, ActionBarPlayer::deserialize, ActionBarPlayer::serialize)
						.registerMapSerializer(EffectPlayer.class, EffectPlayer::deserialize, EffectPlayer::serialize)
						.registerMapSerializer(ParticleLinePlayer.class, ParticleLinePlayer::deserialize, ParticleLinePlayer::serialize)
						.registerMapSerializer(ParticlePlayer.class, ParticlePlayer::deserialize, ParticlePlayer::serialize)
						.registerMapSerializer(ResetActionBar.class, ResetActionBar::deserialize, ResetActionBar::serialize)
						.registerMapSerializer(ResetTitle.class, ResetTitle::deserialize, ResetTitle::serialize)
						.registerMapSerializer(SoundPlayer.class, SoundPlayer::deserialize, SoundPlayer::serialize)
						.registerMapSerializer(TitlePlayer.class, TitlePlayer::deserialize, TitlePlayer::serialize)
						.registerMapSerializer(WorldEffectPlayer.class, WorldEffectPlayer::deserialize, WorldEffectPlayer::serialize)
				).getReferenceObjects();
			} catch (IOException | NBOReferenceException | ClassNotFoundException | NBOParseException e) {
				throw new RuntimeException(e);
			}
		}).get(name) instanceof EffectPlayer player ? player : null;
	}
}
