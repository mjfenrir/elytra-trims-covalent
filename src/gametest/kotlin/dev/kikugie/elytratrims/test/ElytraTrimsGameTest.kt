@file:Suppress("UnstableApiUsage")

package dev.kikugie.elytratrims.test

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext
import net.fabricmc.fabric.api.client.gametest.v1.screenshot.TestScreenshotComparisonAlgorithm
import net.fabricmc.fabric.api.client.gametest.v1.screenshot.TestScreenshotComparisonOptions
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState
import net.minecraft.world.entity.player.ChatVisiblity

object ElytraTrimsGameTest : FabricClientGameTest {
    override fun runTest(context: ClientGameTestContext): Unit = context.runInWorld {
        //? if <26.1
        //clientWorld.waitForChunksRender()
        //? else {
        //~ if >=26.2 clientLevel -> connection
        connection.waitForChunksRender()
        //?}
        //? if >=26.2
        context.runOnClient<Throwable> { if (!it.gui.hud.isHidden) it.gui.hud.toggle() }
        //? else
        //context.runOnClient<Throwable> { it.options.hideGui = true }

        runModelTest(context, "elytra_model")
        runModelTest(context, "elytra_model_trim")
        runModelTest(context, "elytra_model_shield")
        runModelTest(context, "elytra_model_banner")
        runModelTest(context, "elytra_model_gradient")
        runModelTest(context, "elytra_model_glow")
        runModelTest(context, "elytra_model_cosmic")
        // FIXME: because of the timing differences, the exact frame may be different
        // runModelTest(context, "elytra_model_badapple")
    }

    private fun TestSingleplayerContext.runModelTest(context: ClientGameTestContext, name: String) {
        server.runCommand("execute at @p run place template elytratrims-test:$name ~ ~ ~2")
        context.waitTicks(10)
        context.assertScreenshotEquals(TestScreenshotComparisonOptions
            .of(name)
            .withAlgorithm(TestScreenshotComparisonAlgorithm.meanSquaredDifference(0.001F)))

        server.runCommand("execute at @p run setblock ~ ~ ~2 air")
        server.runCommand("kill @e[type=armor_stand]")
    }
}

private inline fun ClientGameTestContext.runInWorld(action: TestSingleplayerContext.() -> Unit) {
    runOnClient<Throwable> {
        it.options.chatVisibility().set(ChatVisiblity.HIDDEN)
        it.options.simulationDistance().set(5)
    }

    worldBuilder().adjustSettings {
        it.gameMode = WorldCreationUiState.SelectedGameMode.CREATIVE
    }.create().use { it.action() }
}
