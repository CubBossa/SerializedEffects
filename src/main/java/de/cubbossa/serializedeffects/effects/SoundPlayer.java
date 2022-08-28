package de.cubbossa.serializedeffects.effects;

import de.cubbossa.serializedeffects.EffectContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SoundPlayer extends EffectPlayer {

    private Sound sound = Sound.ENTITY_EGG_THROW;
    private float volume = 1f;
    private float pitch = 1f;

    public SoundPlayer withSound(Sound sound) {
        this.sound = sound;
        return this;
    }

    public SoundPlayer withVolume(float volume) {
        this.volume = volume;
        return this;
    }

    public SoundPlayer withPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    public void play(EffectContext context, Object... args) {
        context.player().playSound(context.location(), sound, volume, pitch);
        super.play(context, args);
    }

    @Override
    public SoundPlayer clone() {
        var effect = new SoundPlayer().withSound(sound).withVolume(volume).withPitch(pitch);
        getEffectPlayers(false).forEach((effectPlayer, integer) -> effect.addEffect(integer, effectPlayer.clone()));
        return effect;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> ret = new LinkedHashMap<>();
        ret.put("sound", sound.getKey().toString());
        ret.put("volume", volume);
        ret.put("pitch", pitch);
        var subEffects = super.serialize();
        if (!subEffects.isEmpty()) {
            ret.put("effects", subEffects);
        }
        return ret;
    }

    public static SoundPlayer deserialize(Map<String, Object> values) {
        var ret = new SoundPlayer(Registry.SOUNDS.get(NamespacedKey.fromString((String) values.get("sound"))),
                (Float) values.getOrDefault("volume", 1),
                (Float) values.getOrDefault("pitch", 1));
        EffectPlayer.deserialize(values).getEffectPlayers(false).forEach((key, value) -> ret.addEffect(value, key));
        return ret;
    }

    @Override
    public String toString() {
        return "SoundPlayer{" +
                "sound: " + sound +
                ", volume: " + volume +
                ", pitch: " + pitch +
                '}';
    }
}
