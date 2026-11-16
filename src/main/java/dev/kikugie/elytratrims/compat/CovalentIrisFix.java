package dev.kikugie.elytratrims.compat;

import dev.kikugie.elytratrims.ModData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Covalent TEST patch: backport of upstream registerIrisCompat() (4.x-26.3) to 4.9.3/26.2. */
public final class CovalentIrisFix {
    private static final Logger LOG = LoggerFactory.getLogger("elytratrims/covalent-irisfix");
    private CovalentIrisFix() {}

    public static void register() {
        try {
            if (!ModData.INSTANCE.isModLoaded("iris")) return;
            Impl.run();
            LOG.info("[Covalent] Assigned elytratrims:pipeline/elytra_translucent to Iris ENTITIES_TRANSLUCENT / SHADOW_ENTITIES");
        } catch (Throwable t) {
            LOG.warn("[Covalent] Could not register Elytra Trims pipeline with Iris; trims may not render with shaders", t);
        }
    }

    /** Separate class so Iris types are only resolved when Iris is present. */
    private static final class Impl {
        static void run() {
            net.irisshaders.iris.api.v0.IrisApi api = net.irisshaders.iris.api.v0.IrisApi.getInstance();
            api.assignPipeline(dev.kikugie.elytratrims.render.ElytraRenderPipelines.ELYTRA_TRANSLUCENT,
                    net.irisshaders.iris.api.v0.IrisProgram.ENTITIES_TRANSLUCENT);
            api.assignPipelineShadow(dev.kikugie.elytratrims.render.ElytraRenderPipelines.ELYTRA_TRANSLUCENT,
                    net.irisshaders.iris.api.v0.IrisShadowProgram.SHADOW_ENTITIES);
        }
    }
}
