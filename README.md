# Elytra Trims (Covalent Iris Fix)

Unofficial fork of **Elytra Trims 4.9.3** (NeoForge, Minecraft 26.2) that restores Iris shader compatibility.

This is **not** an official KikuGie release, and KikuGie has not endorsed it.

## Credits and license

Original mod: **Elytra Trims** by **KikuGie**.

- Modrinth: https://modrinth.com/mod/elytra-trims
- Source: https://codeberg.org/KikuGie/elytra-trims
- Issues (upstream): https://codeberg.org/KikuGie/elytra-trims/issues

This tree is KikuGie's `4.x-26.3` source at commit `e66c57747919032836a3917e012b42d9d9ac76cd` (the 4.9.3 version bump, 11 Nov 2026 21:31 +01:00 / 2:31 PM America/Chicago), plus the Covalent Iris fix described below. See `UPSTREAM_COMMIT.txt`. The upstream README is in `README.upstream.md`.

License: **GNU Lesser General Public License version 3** (LGPL-3.0), the same as upstream. The complete license text is in `LICENSE`. You must keep this license, credit KikuGie, and publish the corresponding source of your changes (this tree) whenever you distribute the jar.

## What this fork changes

`modId` remains `elytratrims`. The published jar is `4.9.3-covalent.1`, for Minecraft 26.2 / NeoForge.

When Iris is loaded, the fork registers `elytratrims:pipeline/elytra_translucent` with Iris as `ENTITIES_TRANSLUCENT` and, for shadows, `SHADOW_ENTITIES`. Without that assignment, elytra trims and other Elytra Trims decorations often do not render while shaders are on.

This is the same pipeline registration KikuGie shipped as `registerIrisCompat()` and later removed from `clientEP()` on 10 Nov 2026 in commit `69643f9` ("Update stuff"), the day before the 4.9.3 bump. Official `elytratrims-neoforge-4.9.3-26.2.jar` therefore does not call it. Please ask KikuGie whether that removal was intentional before relying on this fork long term.

Implementation (matches the tested jar):

- New file: `src/main/java/dev/kikugie/elytratrims/compat/CovalentIrisFix.java` — runtime `isModLoaded("iris")` check, then `IrisApi.assignPipeline` / `assignPipelineShadow` in a nested class so Iris types are only resolved when Iris is present.
- `src/main/kotlin/dev/kikugie/elytratrims/ETEntrypointImpl.kt` — `CovalentIrisFix.register()` is called at the end of `clientEP()`.
- Unified diff: `patches/covalent-iris-fix.patch`
- The tested jar was produced by compiling `CovalentIrisFix` and ASM-inserting that static call before the `RETURN` in `ETEntrypointImplKt.clientEP` (`patches/PatchClientEP.java`), starting from official `elytratrims-neoforge-4.9.3-26.2.jar` (SHA-1 `829489604b683c50801b196fa28ca7ffb6ca8890`).

The jar intended for CurseForge is `../jars/elytratrims-neoforge-4.9.3-26.2-covalent.1.jar`. It is the tested irisfix jar with only metadata changes, plus this `LICENSE` file (the official/TEST jars did not include one). A clean Gradle/Stonecutter rebuild from this tree may not be byte-identical to that jar, but it is the corresponding source of the same change.

## Compatibility

- Minecraft 26.2 (NeoForge version range `[26.2, 26.3)`)
- Tested on NeoForge 26.2.0.93
- Iris is optional. If Iris is absent, the fix does nothing.
- The Iris hook is client-side. A dedicated server can keep official 4.9.3; Covalent already does that.

## Publishing notes (LGPL)

Suggested GitHub repo name: `mjfenrir/elytra-trims-covalent`. Create the repo yourself, push this directory, and link it from the CurseForge project description so recipients can get the corresponding source. This package was prepared only; it has not been pushed, and no GitHub repository has been created.

Do not point `updateJSONURL` at the official Modrinth updater, or players will be offered the official jar without the Iris fix.
