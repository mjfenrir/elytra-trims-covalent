plugins {
    alias(libs.plugins.stonecutter)
    alias(libs.plugins.kotlin) apply false
    alias(ft.plugins.default) apply false
    alias(ft.plugins.fabric) apply false
}

val ci: String? = System.getenv("CI")
if (ci != null) sc active null else stonecutter active "26.3-fabric"

stonecutter parameters {
    val (version, loader) = current.project.split('-', limit = 2)
    properties.tags(version, loader)
    
    constants {
        match(loader, "fabric", "neoforge")
        this["trimica"] = false
        this["iris"] = false
        this["rvv"] = false
    }

    swaps {
        this["shader_in"] = when {
            current.parsed >= "26.3" -> "layout(location = $1) in "
            else -> "in "
        }

        this["shader_out"] = when {
            current.parsed >= "26.3" -> "layout(location = $1) out "
            else -> "out "
        }

        this["recipe_lambda"] = when {
            loader != "fabric" -> $$"\"lambda$prepare$1\";"
            current.parsed < "26.1" -> "\"method_64689\";"
            else -> $$"\"lambda$prepare$0\";"
        }
    }

    replacements {
        string(current.parsed > "26") {
            replace("classTweaker v2 named", "classTweaker v2 official")
            replace("GuiGraphics", "GuiGraphicsExtractor")
            replace("net.minecraft.client.resources.model.AtlasManager", "net.minecraft.client.resources.model.sprite.AtlasManager")
            replace("net/minecraft/client/resources/model/AtlasManager", "net/minecraft/client/resources/model/sprite/AtlasManager")
            replace("net.minecraft.client.resources.model.Material", "net.minecraft.client.resources.model.sprite.SpriteId")
        }

        regex(current.parsed >= "26.3") {
            replace(
                """com\.mojang\.blaze3d\.pipeline(?!\.RenderPipeline\b)""" to "com.mojang.renderpearl.api.pipeline",
                """com\.mojang\.renderpearl\.api\.pipeline(?!\.RenderPipeline\b)""" to "com.mojang.blaze3d.pipeline"
            )
        }

        string(current.parsed in "26.3"..<"26.4") {
            replace("com.mojang.blaze3d.pipeline.RenderPipeline", "com.mojang.renderpearl.api.pipeline.RenderPipeline")
        }

        string(current.parsed >= "26.3") {
            replace("com/mojang/blaze3d/pipeline", "com/mojang/renderpearl/api/pipeline")
            replace("com.mojang.blaze3d.PrimitiveTopology", "com.mojang.renderpearl.api.pipeline.PrimitiveTopology")

            replace("#moj_import", "#include")
        }

        string(current.parsed >= "26.4") {
            replace("net.minecraft.client.renderer.RenderPipelines", "net.minecraft.client.renderer.pipeline.RenderPipelines")
        }
    }
}

stonecutter handlers {
    inherit("aw", "toml")
}

stonecutter tasks {
    val ordering = versionComparator
        .thenComparingInt { if (it.metadata.project.endsWith("fabric")) 1 else 0 }

    order("publishModrinth", ordering)
    order("publishCurseforge", ordering)
}
