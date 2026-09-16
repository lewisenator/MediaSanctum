# TASKS.md - MediaSanctum

Working task list for maintenance and feature-oriented work on this repo.

## Backlog

- [ ] Add rate-limit handling for the Hardcover API client. `SearchControllerTest`
  (searchAuthors_ok / searchBooks_ok) hits the live Hardcover API in CI with no
  mocking. Back-to-back CI runs against the same API key can trip rate limiting,
  and the app currently appears to swallow the upstream error and return an
  empty result set rather than surfacing/retrying it — causing spurious test
  failures that look like flaky tests. Fix should include either: (a) mocking
  Hardcover in `SearchControllerTest`, and/or (b) proper backoff/retry + error
  surfacing in the Hardcover client so a real rate-limit doesn't silently
  degrade to "no results". (Discovered 2026-09-16 while triaging PR #42.)
  UPDATE 2026-09-16: under sustained back-to-back CI load (re-running many
  Dependabot PRs in a short window) the same rate-limit condition also produced
  single unrelated-test failures (e.g. `BooksControllerTest.addBook_getBook_and_getBooks_ok`)
  on PR #43 and #44, both of which only bump `spotbugs`/`spotbugs-annotations`
  (build-tooling, zero runtime surface — ruled out as the cause). Working theory:
  a failed/slow Hardcover call during `SearchControllerTest` is polluting the
  shared Spring test context or H2 DB state for whichever test runs next, so the
  "random single failing test" symptom isn't confined to Search anymore. Strengthens
  the case for mocking Hardcover in tests rather than hitting the live API.

- [ ] PR #32 (`commons-codec` 1.22.0→1.22.1) — branch was deleted. While retriggering
  CI I closed the PR to reopen it (as done successfully for #31/#33/#34), but this
  repo has "automatically delete head branches" enabled and the branch was deleted
  before/during the close, so `gh pr reopen 32` and `@dependabot recreate` both
  failed ("Could not open the pull request" / dependabot says branch is gone).
  Dependabot will recreate this PR on its next scheduled run, or it can be
  triggered manually from the repo's Dependency graph → Dependabot page. No action
  needed here beyond waiting/manual trigger — not a regression, just bookkeeping
  fallout from CI-retriggering. (Switched to `@dependabot rebase` comments for all
  subsequent PRs to avoid repeating this.)

## Done

- [x] PR #42 (Spring Boot 4.1.0→4.1.1 backend bump) — merged 2026-09-16.
  Root cause of cascading Dependabot CI failures across ~16 PRs: `HARDCOVER_API_KEY`
  existed in the `ci` GitHub Actions environment but not in Dependabot secrets,
  so every Dependabot-triggered run had no key and NPE'd. Fixed by adding the key
  to repo Dependabot secrets.
- [x] PR #27 (`@testing-library/jest-dom` 6.9.1→7.0.0) — merged 2026-09-16.
  First CI attempt showed the known Hardcover rate-limit flake (SearchControllerTest);
  re-run came back fully green.
- [x] PR #31 (`typescript` 6.0.3→7.0.2) — merged 2026-09-16. Stale CI (pre-dates
  the HARDCOVER_API_KEY fix); reopened PR to retrigger CI, came back green.
- [x] PR #33 (`vite` 8.1.5→8.2.0) — merged 2026-09-16. Reopened to retrigger CI
  (green), hit a `package-lock.json` merge conflict against #31 after #31 merged,
  resolved with `@dependabot rebase`, re-passed, merged.
- [x] PR #34 (`lucide-react` 1.26.0→1.28.0) — merged 2026-09-16. Reopened to
  retrigger CI, came back green.
- [x] PR #35 (`@types/node` 25.9.5→26.1.2, dependabot bumped further to 26.5.1
  during rebase) — merged 2026-09-16. `@dependabot rebase` retrigger came back green.
- [x] PR #38 (`axios` 1.18.1→1.19.0, dependabot bumped further to 1.20.0 during
  rebase) — merged 2026-09-16. `@dependabot rebase` retrigger came back green.
- [x] PR #45 (`org.xerial:sqlite-jdbc` 3.53.2.0→3.53.4.0) — merged 2026-09-16.
  Stale CI (pre-dates the HARDCOVER_API_KEY fix, from Aug 31 with empty key);
  `@dependabot rebase` retrigger came back green.
- [x] PR #40 (`undici` 7.28.0→7.29.0) — merged 2026-09-16. `@dependabot rebase`
  retrigger came back green.
- [x] PR #39 (`@vitejs/plugin-react` 6.0.4→6.1.1) — merged 2026-09-16. First
  attempt hit the Hardcover flake (SearchControllerTest, 1/52 failed);
  `@dependabot rebase` retrigger came back green.
- [x] PR #43 (`spotbugs-annotations` 4.10.3→4.10.4) — merged 2026-09-16. First
  attempt hit a widened flake (`BooksControllerTest`, see backlog note above,
  ruled out as a real regression since this is a build-tooling-only bump);
  `@dependabot rebase` retrigger came back green.
- [x] PR #44 (`spotbugs` 6.5.9→6.5.11) — merged 2026-09-16. Same widened-flake
  pattern as #43 on first attempt; `@dependabot rebase` retrigger came back green.
- [x] PR #28 (`react-dropzone` 15.0.0→19.1.1, dependabot bumped further to
  20.1.1 during recreate) — merged 2026-09-16. Dependabot flagged the PR as
  "edited by someone other than Dependabot" (from an earlier `gh run rerun`)
  and refused to rebase; used `@dependabot recreate` instead, came back green.

## Skipped (not a regression)

- PR #36 (`@types/react-dom` 19.2.3→19.2.4) and PR #37 (`@types/react` 19.2.17→19.2.18)
  — Dependabot auto-closed both itself after the rebase request: "no longer
  updatable" (peer-dependency constraint elsewhere in the tree). No action taken;
  this is Dependabot's own decision, not a CI failure.

## Java 27 upgrade — BLOCKED (investigated 2026-09-16)

JDK 27 released 2026-09-15. Investigated bumping the backend; **not viable yet**.
Current pins: Java 25 toolchain (`Backend/build.gradle.kts` line 33,
`.github/workflows/ci.yml` java-version), Spring Boot 4.1.1, Gradle wrapper 9.4.1,
Lombok 1.18.46 (BOM-pinned via Boot), Hibernate ORM 7.4.5.Final, JUnit Jupiter 6.0.3.
No Dockerfile in repo (nothing to bump there).

Blockers, in dependency order (each gates the next):

1. **Gradle** — even latest *stable* Gradle (9.7.1) can't run on JDK 27; the compatibility
   matrix caps supported JVMs at 26. JDK 27 support only exists in `9.8.0-RC1`
   (pre-release, ~2026-09-08), not GA. Need a stable Gradle 9.8.x+ before the wrapper
   can be bumped. https://docs.gradle.org/current/userguide/compatibility.html
2. **Spring Boot** — 4.1.1's official system requirements certify up to Java 26 only,
   not 27. Need a Boot 4.1.x/4.2.x release that extends the baseline.
   https://docs.spring.io/spring-boot/system-requirements.html
3. **Lombok** — BOM-pinned 1.18.46 only added JDK26 support; JDK27 support landed in
   1.18.48 (2026-09-01). Easy fix (explicit version override) once #1/#2 clear —
   not itself blocking, just needs to move in lockstep.

Hibernate ORM 7.4.5.Final and JUnit Jupiter 6.0.3 showed no known Java 27 incompatibility
in research, but also no explicit certification — lower risk, unconfirmed.

Revisit once Gradle ships a stable 9.8.x+ GA and Spring Boot certifies Java 27
(historically Boot follows shortly after Gradle stabilizes support for a new JDK).
No PR opened — repo is correctly still on Java 25.

## Security alert triage — 2026-09-16 (round 2, post dependency-merge batch)

Re-pulled current state (the original 36-alert flag was stale/pre-merge): **24 open
Dependabot alerts** at start (3 critical, 14 high, 7 moderate), 0 open code-scanning
(CodeQL) alerts. No Dependabot-authored fix PRs existed for any of these 24 — all were
on transitive/build-tool dependencies Dependabot couldn't auto-resolve.

**Resolved 15 of 24** via 2 manually-authored PRs, both merged with fully green CI
(Backend/Frontend/CodeQL, no Hardcover-flake retrigger needed):
- [x] PR #48 — bumped `extra["tomcat.version"]` override in `Backend/build.gradle.kts`
  11.0.22→11.0.25. Closed 3 **critical** alerts on `org.apache.tomcat.embed:tomcat-embed-core`
  (#84 GHSA-gcx9-497g-6cp6, #85 GHSA-9xv2-5v5q-p794, #86 GHSA-h3x4-894j-xpx5).
- [x] PR #49 — widened `Frontend/package.json` overrides (`@xmldom/xmldom` ^0.8.13→^0.8.15,
  `vitest` ^4.1.8→^4.1.11) and regenerated `package-lock.json`. Closed 12 alerts: 10x
  `@xmldom/xmldom` (transitive via `react-reader`→`epub.js`) + vitest/`@vitest/mocker`
  GHSA-82fw-gwwq-j7x9. Note: local `npm install`/`audit fix`/`update` hit a real npm 10.9.8
  arborist bug (`Cannot read properties of null (reading 'edgesOut')`) resolving vitest
  4.1.11's new optional peer dep — worked around with `npm install --legacy-peer-deps` to
  regenerate the lockfile, then confirmed `npm ci` (what CI actually runs) installs clean
  from the result. Doesn't affect CI.

**9 alerts remain open** (verified live 2026-09-16) — all Java build-tool-only transitive
deps, not shipped in the runtime app, deliberately NOT auto-fixed (risk of breaking the
build tool or unclear upstream compatibility):

- [ ] `com.fasterxml.jackson.core:jackson-core`/`jackson-databind` (#3, #66, #33, #36, #39, #43
  — 4 high, 2 moderate) — old copy (2.14.2) pulled by `com.github.node-gradle:gradle-node-plugin:7.1.0`
  (the Frontend's Gradle→npm bridge plugin). The app's own Jackson is already well above the
  fixed threshold via `jackson-2-bom.version=2.21.5` — this is an isolated copy in the
  gradle-node-plugin's own classpath. Fix requires bumping that plugin; compatibility with the
  bumped version not evaluated.
- [ ] `org.codehaus.plexus:plexus-utils` (#7, high) and `commons-beanutils:commons-beanutils`
  (#2, high) — old copies (3.3.0 / 1.10.1) pulled via Checkstyle's (`com.puppycrawl.tools:checkstyle:10.21.4`)
  own tool classpath, separate from the app's runtime/test classpath where these are already
  correctly forced via `resolutionStrategy.eachDependency` in `Backend/build.gradle.kts`. The
  existing force doesn't reach Checkstyle's isolated tool configuration.
- [ ] `org.apache.commons:commons-lang3` (#4, moderate) — old copy (3.16.0) pulled transitively
  by the Spring Boot Gradle plugin itself (`spring-boot-buildpack-platform`/`spring-boot-loader-tools`/
  `spring-boot-gradle-plugin`, pinned at 4.1.1, via `commons-compress:1.27.1`). App's own
  direct `commons-lang3:3.20.0` already satisfies the fix; not controllable from this repo
  without an upstream Spring Boot Gradle plugin bump.

Common thread on all 9: fixing cleanly means either bumping `gradle-node-plugin` or
Checkstyle (risk: could trip `maxWarnings = 0` on newly-introduced lint rules) or waiting on
an upstream Spring Boot Gradle plugin release. None are runtime-exposed. Revisit if/when
those upstream projects ship compatible releases, or if risk tolerance changes.
