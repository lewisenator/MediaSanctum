# MediaSanctum

A self-hosted library manager for ebooks and audiobooks — browse your collection, read or listen in the browser, and pick up right where you left off. Book and author metadata is enriched via the [Hardcover](https://hardcover.app) API.

<!--
Screenshots — drop images into a `docs/screenshots/` folder and update the paths below.
Suggested shots: login, library/book grid, book details, in-browser reader, audiobook player, author page.
-->
## Screenshots

| Library | Book Details |
|---|---|
| _screenshot coming soon_ | _screenshot coming soon_ |

| Reader | Audiobook Player |
|---|---|
| _screenshot coming soon_ | _screenshot coming soon_ |

---

## Features

- Book and author library with search
- Series browsing
- In-browser ebook reader with per-user reading progress
- Audiobook player with HTTP range-based streaming and per-user listening progress
- Author/book metadata enrichment via the Hardcover API (rate-limited and retried with Resilience4j)
- JWT-based authentication, with an admin user auto-provisioned from configuration on first boot
- SQLite storage with Flyway-managed schema migrations

---

## Tech Stack

### Backend
- **Java 25** with **Spring Boot 4** (Web MVC, Security, Validation, Actuator, Data JPA)
- **SQLite** (via `sqlite-jdbc` + Hibernate community dialects), schema managed by **Flyway**
- **JWT authentication** (Nimbus JOSE JWT)
- **Resilience4j** rate limiting/retry around the Hardcover GraphQL client
- **Spring Boot DevTools** — live reload during development

### Frontend
- **React 19** with **TypeScript**
- **TanStack Router**
- **Tailwind CSS 4**
- **Vite 8**

### Tooling / CI
- Gradle multi-project build with Checkstyle, PMD, SpotBugs, and JaCoCo coverage
- GitHub Actions CI (backend tests + coverage summary, frontend typecheck + tests), Dependabot-managed dependencies

---

## Prerequisites

- JDK 25+
- Node.js 22+ and npm

---

## Configuration

Copy the example environment file and fill in real values:

```bash
cp Backend/.env.example Backend/.env
```

| Variable | Description |
|---|---|
| `CONFIG` | Directory for app config and logs |
| `DATA` | Directory for library data (SQLite database, covers, uploaded book/audiobook files) |
| `ADMIN_EMAIL` / `ADMIN_PASSWORD` | Credentials for the admin user, auto-created (or refreshed) on startup |
| `JWT_SECRET` | 256-bit secret used to sign JWTs — generate one with `openssl rand -base64 32` |
| `HARDCOVER_API_KEY` | API key for the [Hardcover](https://hardcover.app) metadata API |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile, e.g. `local` |

These are read as OS environment variables (there's no `.env` loader wired into the app), so export them into your shell before running the backend:

```bash
set -a
source Backend/.env
set +a
```

---

## Development

### Backend

Run the Spring Boot dev server (port 8000 by default):

```bash
./gradlew :Backend:bootRun
```

Or build a runnable jar:

```bash
./gradlew :Backend:build
java -jar Backend/build/libs/Backend-0.0.1-SNAPSHOT.jar
```

### Frontend

Install dependencies and start the Vite dev server (port 3000):

```bash
cd Frontend
npm install
npm run dev
```

Or via Gradle (runs `npm install` then `npm run build`):

```bash
./gradlew :Frontend:buildFrontend
```

---

## Building Everything

Build both subprojects from the root:

```bash
./gradlew build
```

This compiles the backend, runs all tests, and produces a production frontend bundle in `Frontend/dist/`.
