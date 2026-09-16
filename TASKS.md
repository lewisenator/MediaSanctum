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
