package de.cubbossa.serializedeffects;

import de.cubbossa.nbo.NBOFile;
import de.cubbossa.nbo.exceptions.NBOParseException;
import de.cubbossa.nbo.exceptions.NBOReferenceException;
import de.cubbossa.serializedeffects.effects.*;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

@Getter
@Setter
public class EffectHandler {

	public static record TextContext(String text, Player player, TagResolver resolver) {

	}

	@Getter
	private static EffectHandler instance;

	private final JavaPlugin plugin;
	private final BukkitAudiences audiences;
	private final MiniMessage miniMessage;
	private final Function<TextContext, Component> translator;

	private final Map<String, Map<String, Object>> effects;
	private final Map<UUID, Collection<EffectPlayer>> cooldownLocks;
	private final Map<UUID, Map<EffectPlayer, Queue<Runnable>>> cooldownQueues;

	public EffectHandler(JavaPlugin plugin, BukkitAudiences audiences, MiniMessage miniMessage, Function<TextContext, Component> messageSupplier) {
		instance = this;
		this.plugin = plugin;
		this.audiences = audiences;
		this.miniMessage = miniMessage;
		this.translator = messageSupplier;
		this.effects = new HashMap<>();
		this.cooldownLocks = new HashMap<>();
		this.cooldownQueues = new HashMap<>();
	}

	public void clearCache(File file) {
		effects.remove(file.getPath());
	}

	public void playEffect(File file, String name, Player player, Location location, Object... args) {
		EffectPlayer effect = getEffect(file, name);
		if(effect != null) {
			playEffect(effect, player, location, args);
		}
	}

	public void playEffect(EffectPlayer effect, Player player, Location location, Object... args) {
		if(effect == null) {
			return;
		}
		EffectContext context = new EffectContext(player, location, effect);
		var locks = cooldownLocks.computeIfAbsent(player.getUniqueId(), uuid -> new HashSet<>());
		if (locks.contains(effect)) {
			var inner = cooldownQueues.computeIfAbsent(player.getUniqueId(), (uuid) -> new HashMap<>());
			inner.computeIfAbsent(effect, player1 -> new LinkedList<>()).add(() -> {
				cooldownLocks.computeIfAbsent(player.getUniqueId(), uuid -> new HashSet<>()).add(effect);
				effect.play(context, args);
			});
		} else {
			effect.play(context, args);
		}
	}

	public void lockEffect(Player player, EffectPlayer effect) {
		var locks = cooldownLocks.computeIfAbsent(player.getUniqueId(), uuid -> new HashSet<>());
		locks.add(effect);
	}

	public void freeEffect(Player player, EffectPlayer effect) {
		cooldownLocks.computeIfAbsent(player.getUniqueId(), uuid -> new HashSet<>()).remove(effect);
		Runnable nextTask = cooldownQueues.computeIfAbsent(player.getUniqueId(), uuid -> new HashMap<>()).computeIfAbsent(effect, player1 -> new LinkedList<>())
				.poll();
		if (nextTask != null) {
			nextTask.run();
		}
	}

	public EffectPlayer getEffect(File file, String name) {
		return effects.computeIfAbsent(file.getPath(), f -> {
			try {
				return NBOFile.loadFile(new File(f), NBOFile.DEFAULT_SERIALIZER
						.registerMapSerializer(Cooldown.class, Cooldown::deserialize, Cooldown::serialize)
						.registerMapSerializer(ActionBarPlayer.class, ActionBarPlayer::deserialize, ActionBarPlayer::serialize)
						.registerMapSerializer(EffectPlayer.class, EffectPlayer::deserialize, EffectPlayer::serialize)
						.registerMapSerializer(ParticleLinePlayer.class, ParticleLinePlayer::deserialize, ParticleLinePlayer::serialize)
						.registerMapSerializer(ParticlePlayer.class, ParticlePlayer::deserialize, ParticlePlayer::serialize)
						.registerMapSerializer(ResetActionBar.class, ResetActionBar::deserialize, ResetActionBar::serialize)
						.registerMapSerializer(ResetTitle.class, ResetTitle::deserialize, ResetTitle::serialize)
						.registerMapSerializer(SoundPlayer.class, SoundPlayer::deserialize, SoundPlayer::serialize)
						.registerMapSerializer(TitlePlayer.class, TitlePlayer::deserialize, TitlePlayer::serialize)
						.registerMapSerializer(MessagePlayer.class, MessagePlayer::deserialize, MessagePlayer::serialize)
						.registerMapSerializer(WorldEffectPlayer.class, WorldEffectPlayer::deserialize, WorldEffectPlayer::serialize)
				).getReferenceObjects();
			} catch (IOException | NBOReferenceException | ClassNotFoundException | NBOParseException e) {
				throw new RuntimeException(e);
			}
		}).get(name) instanceof EffectPlayer player ? player : null;
	}
}
