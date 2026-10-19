# Upstream investigation / contribution handoff

Checked on 2026-10-20:

- Official project: https://modrinth.com/mod/guide-shelf
- API: https://api.modrinth.com/v2/project/guide-shelf
- Project ID `8E0L1gA7`, author `Catomon`.
- `source_url: null`, `issues_url: null`; license `LicenseRef-All-Rights-Reserved`.
- Old display URL `/mod/patchouli-library` returns 404; use the current slug.
- GitHub public repository search for “Guide Shelf” + Minecraft and code search for `io.github.catomon.guideshelf` returned no matches.
- `Catomon` public repositories did not contain Guide Shelf. Do not assume that similarly named repositories are this mod.
- `myondev/PatchouliLibrary` was inspected and excluded: Unity project, not this Minecraft mod.

## Once the actual source is available

1. Confirm maintainer identity, intended branch, contribution/license requirements and public source link.
2. Fork that repository, preserve its license and history, create a topic branch.
3. Move `OptionalGuides.append` into native discovery **before** the ignored-ID filter; replace Mixin redirection with an ordinary source call.
4. Add `OptionalGuides.open` at the start of the native opener; retain all unrelated handlers.
5. Keep optional APIs isolated; preserve native progression and parent screens. Translate only new labels.
6. Test each provider absent/present, resource reloads, ignored entries, pins, cross-wiki page reset and UI return navigation.
7. Open a focused PR describing exact tested versions. Do not include local binary patches, mod binaries, private pack assets or the unrelated KubeJS integrations.

No fork or PR was created without a confirmed upstream. This standalone addon is the deliverable in the meantime.

## References

API signatures were checked against user-installed Oracle 1.4.0, GuideME 21.1.19 and the installed Ponder library. Guide Shelf original 1.3.6 and 1.3.7 downloads were SHA-512 verified against Modrinth version metadata. These binaries remain local/ignored and are not redistributed.

All implementation in `io.github.hsunxl.guideshelf` is original addon code under MIT; Guide Shelf and provider licenses remain unchanged. Standard Gradle wrapper: Apache-2.0 (https://github.com/gradle/gradle/blob/v8.8.0/LICENSE).
