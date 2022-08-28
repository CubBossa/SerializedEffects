package de.cubbossa.serializedeffects.effects;

import de.cubbossa.serializedeffects.EffectContext;
import de.cubbossa.serializedeffects.EffectHandler;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.stream.Collectors;

public class EffectPlayer {

    private final HashMap<EffectPlayer, Integer> effects;

    public EffectPlayer() {
        this.effects = new LinkedHashMap<>();
    }

    @Override
    public EffectPlayer clone() {
        EffectPlayer effect = new EffectPlayer();
        effects.forEach((effectPlayer, integer) -> effect.addEffect(integer, effectPlayer.clone()));
        return effect;
    }

    public Map<EffectPlayer, Integer> getEffectPlayers(boolean deep) {
        if (deep) {
            Map<EffectPlayer, Integer> players = new HashMap<>();
            effects.keySet().forEach(effectPlayer -> players.putAll(effectPlayer.getEffectPlayers(true)));
            players.putAll(effects);
            return players;
        }
        return new HashMap<>(effects);
    }

    public void addEffect(int delay, EffectPlayer effect) {
        effects.put(effect, delay);
    }

    public void removeEffect(EffectPlayer effect) {
        effects.remove(effect);
    }

    public EffectPlayer withEffect(EffectPlayer effect) {
        return withEffect(0, effect);
    }

    public EffectPlayer withEffect(int delay, EffectPlayer effect) {
        effects.put(effect, delay);
        return this;
    }

    public void play(EffectContext context, Object... args) {
        for (Map.Entry<EffectPlayer, Integer> entry : effects.entrySet()) {
            if (entry.getValue() > 0) {
                Bukkit.getScheduler().runTaskLater(EffectHandler.getInstance().getPlugin(), () -> entry.getKey().play(context, args), entry.getValue());
            } else {
                entry.getKey().play(context, context.location(), args);
            }
        }
    }

    public Map<String, Object> serialize() {
        Map<String, Object> ret = new LinkedHashMap<>();
        var all = getEffectPlayers(false);
        all.values().stream().distinct().sorted().forEach(integer -> {
            ret.put("delay_" + integer, all.entrySet().stream().filter(e -> e.getValue().equals(integer)).map(Map.Entry::getKey).collect(Collectors.toList()));
        });
        return ret;
    }

    public static EffectPlayer deserialize(Map<String, Object> values) {
        EffectPlayer effectPlayer = new EffectPlayer();
        for (var entry : values.entrySet().stream()
                .filter(e -> e.getKey().startsWith("delay_"))
                .filter(e -> e.getValue() instanceof List)
                .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), (List<EffectPlayer>) e.getValue()))
                .collect(Collectors.toList())) {
            entry.getValue().forEach(e -> effectPlayer.addEffect(Integer.parseInt(entry.getKey().replace("delay_", "")), e));
        }
        return effectPlayer;
    }
}
