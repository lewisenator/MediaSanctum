# MediaSanctum

Application for managing an ebook and audiobook library.

---

## Tech Stack

### Backend
- **Java 25** with **Spring Boot 4**
- **Spring Web MVC** — REST API
- **Spring Boot DevTools** — live reload during development

### Frontend
- **React 19** with **TypeScript**
- **TanStack Router**
- **Tailwind CSS 4**
- **Vite 8**

---

## Prerequisites

- JDK 25+
- Node.js 22+ and npm

---

## Development

### Backend

Run the Spring Boot dev server (port 8080 by default):

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
