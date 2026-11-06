package dev.sardor.emotecompat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Reads Emotecraft's client player state without depending on its internals
 * at compile time. In the pause callback, EMF may pass a non-player entity;
 * those entities must never be suppressed.
 */
final class EmoteDetection {
    private static final String[] INTERFACES = {
        "io.github.kosmx.emotes.main.mixinFunctions.IPlayerEntity",
        "io.github.kosmx.emotes.executor.emotePlayer.IEmotePlayerEntity"
    };

    private static volatile Accessor accessor;
    private static volatile boolean initialized;

    private EmoteDetection() {}

    static boolean isEmoting(Object emfEntity) {
        if (emfEntity == null) return false;
        Accessor candidate = getAccessor();
        if (candidate == null || !candidate.type.isInstance(emfEntity)) return false;

        try {
            return Boolean.TRUE.equals(candidate.method.invoke(emfEntity));
        } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException ex) {
            return false;
        }
    }

    private static Accessor getAccessor() {
        if (initialized) return accessor;
        synchronized (EmoteDetection.class) {
            if (initialized) return accessor;
            for (String interfaceName : INTERFACES) {
                try {
                    Class<?> playerInterface = Class.forName(interfaceName);
                    Method method = playerInterface.getMethod("isPlayingEmote");
                    if (method.getReturnType() != boolean.class
                            && method.getReturnType() != Boolean.class) continue;
                    accessor = new Accessor(playerInterface, method);
                    break;
                } catch (ClassNotFoundException | NoSuchMethodException | LinkageError ignored) {
                    // The other Emotecraft interface may still exist.
                }
            }
            initialized = true;
            return accessor;
        }
    }

    private record Accessor(Class<?> type, Method method) {}
}
