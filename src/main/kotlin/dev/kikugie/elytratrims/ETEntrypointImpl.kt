package dev.kikugie.elytratrims

import dev.kikugie.elytratrims.api.ETClientInitializer
import dev.kikugie.elytratrims.api.ETCommonInitializer
import dev.kikugie.elytratrims.api.render.ETRenderingAPI.registerDecorator
import dev.kikugie.elytratrims.recipe.cauldron.ETCauldronInteractions
import dev.kikugie.elytratrims.render.ElytraRenderPipelines
import dev.kikugie.elytratrims.render.impl.*

//? if <26.4
import dev.kikugie.elytratrims.debug.registerServerDebugCommands

fun clientInit(entrypoints: Iterable<ETClientInitializer>) {
    ElytraRenderPipelines.init()
    entrypoints.sortedByDescending(ETClientInitializer::priority)
        .forEach(ETClientInitializer::onInitializeClientET)
}

fun commonInit(entrypoints: Iterable<ETCommonInitializer>) {
    entrypoints.sortedByDescending(ETCommonInitializer::priority)
        .forEach(ETCommonInitializer::onInitializeCommonET)

    //? if fabric && <26.4 {
    if (ModData.isDevEnv) net.fabricmc.fabric.api.command.v2
        .CommandRegistrationCallback.EVENT.register(::registerServerDebugCommands)
    //?}
}

fun clientEP() {
    registerDecorator(ETGatewayRenderer)
    registerDecorator(ETColorRenderer)
    registerDecorator(ETBadAppleRenderer)
    registerDecorator(ETPatternsRenderer)
    registerDecorator(ETTrimsRenderer)
    // Covalent Iris fix: assign elytratrims:pipeline/elytra_translucent to Iris
    // ENTITIES_TRANSLUCENT / SHADOW_ENTITIES when Iris is loaded. Upstream 4.9.3
    // removed registerIrisCompat() on 2026-11-10 (commit 69643f9).
    dev.kikugie.elytratrims.compat.CovalentIrisFix.register()
}

fun commonEP() {
    ETCauldronInteractions.register()
}
