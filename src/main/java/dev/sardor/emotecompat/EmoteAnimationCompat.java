package dev.sardor.emotecompat;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * Bridges Emotecraft to the public EMF animation pause-condition API.
 * Neither optional mod is a compile-time dependency.
 */
public final class EmoteAnimationCompat implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("emotecraft-anim-compat");

    @Override
    public void onInitializeClient() {
        FabricLoader loader = FabricLoader.getInstance();
        boolean hasEmotecraft = loader.isModLoaded("emotecraft");
        boolean hasEmf = loader.isModLoaded("entity_model_features");

        if (!hasEmotecraft || !hasEmf) {
            LOGGER.info("Compatibility inactive (Emotecraft: {}, EMF: {})", hasEmotecraft, hasEmf);
            return;
        }

        try {
            Class<?> api = Class.forName("traben.entity_model_features.EMFAnimationApi");
            Method register = api.getMethod("registerPauseCondition", Function.class);

            // The EMF API accepts a function of EMFEntity. Use Object at the
            // reflection boundary so that the optional mod is not required
            // on the compilation classpath.
            Function<Object, Boolean> condition = EmoteDetection::isEmoting;
            Object result = register.invoke(null, condition);
            if (Boolean.FALSE.equals(result)) {
                LOGGER.warn("EMF did not accept the pause condition");
            } else {
                LOGGER.info("Installed EMF pause condition for active Emotecraft emotes");
            }
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException
                 | InvocationTargetException | LinkageError error) {
            LOGGER.warn("Could not register EMF pause condition; compatibility is inactive", error);
        }
    }
}
