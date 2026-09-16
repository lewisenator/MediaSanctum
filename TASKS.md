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

## Done

- [x] PR #42 (Spring Boot 4.1.0→4.1.1 backend bump) — merged 2026-09-16.
  Root cause of cascading Dependabot CI failures across ~16 PRs: `HARDCOVER_API_KEY`
  existed in the `ci` GitHub Actions environment but not in Dependabot secrets,
  so every Dependabot-triggered run had no key and NPE'd. Fixed by adding the key
  to repo Dependabot secrets.
