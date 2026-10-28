# Screens

Every route under `Frontend/src/routes`, its status and its states. Screenshots live in `docs/images/desktop` and `docs/images/mobile`; there is no `docs/design/reference`.

Status: **Built** = working screen; **Stub** = renders placeholder text only; **Layout** = no screen of its own, wraps children; **Redirect** = no UI.

State legend: ✅ implemented, ❌ not implemented, n/a not applicable.
App-wide: `router.tsx` sets no `pendingComponent` or `errorComponent`, so routes that use `useSuspenseQuery` have no loading or error UI (the loader blocks navigation, a failed loader shows the router default error).

## Route files

| File | URL | Status | Purpose |
| --- | --- | --- | --- |
| `__root.tsx` | (all) | Layout | Root outlet, devtools, 404 `NotFound` page |
| `index.tsx` | `/` | Redirect | Redirects to `/books` |
| `(unauthenticated)/route.tsx` | (group) | Layout | Wraps auth pages in `AuthLayout` |
| `(unauthenticated)/login.tsx` | `/login` | Built | Email and password sign-in |
| `(unauthenticated)/login.test.tsx` | n/a | Test | Tests for the login screen |
| `(authenticated)/route.tsx` | (group) | Layout | Auth guard, sends to `/login` without user or token |
| `(authenticated)/_sidebar/route.tsx` | (group) | Layout | Wraps pages in `SidebarLayout` (left menu, top bar) |
| `(authenticated)/_fullpage/route.tsx` | (group) | Layout | Wraps pages in `FullPageLayout` |
| `(authenticated)/_sidebar/books/index.tsx` | `/books` | Built | Book catalogue grid |
| `(authenticated)/_sidebar/books/search.tsx` | `/books/search` | Built | Search and add a book by title |
| `(authenticated)/_sidebar/books/$bookId/index.tsx` | `/books/:bookId` | Built | Book details, ebook and audiobook tabs |
| `(authenticated)/_sidebar/books/$bookId/listener.tsx` | `/books/:bookId/listener` | Built | Audiobook player |
| `(authenticated)/_fullpage/books/$bookId/reader.tsx` | `/books/:bookId/reader` | Built | EPUB reader with contents and settings panels |
| `(authenticated)/_fullpage/books/$bookId/-readerStyles.ts` | n/a | Helper | Reader styles (ignored by the router, no screen) |
| `(authenticated)/_sidebar/authors/index.tsx` | `/authors` | Built | Author catalogue grid |
| `(authenticated)/_sidebar/authors/search.tsx` | `/authors/search` | Stub | "Add New Author" target, prints route name |
| `(authenticated)/_sidebar/authors/$authorId.tsx` | `/authors/:authorId` | Stub | Author details, prints route name |
| `(authenticated)/_sidebar/series/index.tsx` | `/series` | Stub | Series list, prints a hello heading |
| `(authenticated)/_sidebar/status/index.tsx` | `/status` | Built (minimal) | Backend health text |

## Screen states

### 404 (`__root.tsx`)
- Populated: ✅ message and "Go Back Home" link. Other states n/a.

### Login (`/login`) — [desktop](docs/images/desktop/01_login_page.png), [mobile](docs/images/mobile/01_login_page.jpeg)
- Default form: ✅
- Submitting: ✅ button reads "Signing in..."; the button is not disabled, so double submits are possible.
- Validation error (empty fields): ✅ inline message.
- Server error: ✅ inline message from the API.
- Success: ✅ stores token and user, goes to `/books`.

### Books (`/books`) — [desktop](docs/images/desktop/02_book_index.png), [mobile](docs/images/mobile/02_book_index.jpeg)
- Loading: ❌ suspense query, no fallback.
- Populated: ✅ card grid with title count.
- Empty: ✅ "No books yet" card (copy mentions a button "at"; wording is off).
- Error: ❌
- Search box: ❌ renders but filtering is commented out.
- Related: [left-hand menu, mobile](docs/images/mobile/03_left_hand_menu.jpeg); theme picker ([desktop](docs/images/desktop/03_theme_picker.png), [mobile](docs/images/mobile/04_theme_picker.jpeg)) belong to the sidebar layout, not a route.

### Book search (`/books/search`) — [desktop](docs/images/desktop/04_book_search.png), [mobile](docs/images/mobile/05_book_search.jpeg)
- Idle (fewer than 2 characters): ✅ input only.
- Loading: ✅ "Loading..." text.
- Results: ✅ list of hits.
- No results: ❌ nothing shown for zero hits.
- Error: ✅ error message.

### Book details (`/books/:bookId`) — [desktop](docs/images/desktop/05_book_details.png), [mobile](docs/images/mobile/06_book_details.jpeg)
- Loading: ❌ suspense query, no fallback.
- Populated: ✅ cover, metadata, description, format tabs (swipe on mobile).
- Missing cover: ✅ icon placeholder.
- Ebook present / missing: ✅ `AvailableEbook` / `MissingEbook`; progress widget in [06_ebook_progress_widget](docs/images/desktop/06_ebook_progress_widget.png).
- Audiobook present / missing: ✅ `AvailableAudiobook` / `MissingAudiobook`.
- Not found or error: ❌
- Gap: the series link points back to the book itself, since there is no series detail page.

### Audiobook player (`/books/:bookId/listener`) — [desktop](docs/images/desktop/10_audiobook_player.png), [mobile](docs/images/mobile/10_audiobook_player.jpeg)
- Loading: ❌ suspense query.
- Populated: ✅ player controls, chapters, right-hand menu.
- Bookmarks and History panels: Stub (`components/listener/Bookmarks.tsx`, `History.tsx` are placeholders).
- Error: ❌

### EPUB reader (`/books/:bookId/reader`) — [desktop](docs/images/desktop/07_epub_reader.png), [mobile](docs/images/mobile/07_epub_reader.jpeg)
- Loading: ❌ route-level; book content load is handled inside the reader component only.
- Reading: ✅ progress saved to server and `localStorage`.
- Contents panel: ✅ [desktop](docs/images/desktop/08_epub_toc.png), [mobile](docs/images/mobile/08_epub_toc.jpeg)
- Settings panel: ✅ [desktop](docs/images/desktop/09_epub_settings.png), [mobile](docs/images/mobile/09_epub_settings.jpeg)
- Error (bad file, missing ebook): ❌

### Authors (`/authors`) — no screenshot
- Loading: ❌
- Populated: ✅ card grid.
- Empty: ✅ but the copy is copied from Books ("No books yet").
- Search box: ❌ renders, no filtering.
- Error: ❌

### Author search (`/authors/search`) — no screenshot
- Stub only; no states. Linked from "Add New Author".

### Author details (`/authors/:authorId`) — no screenshot
- Stub only; no states. Linked from the book details page.

### Series (`/series`) — no screenshot
- Stub only; no states. Reachable from the menu, no data client yet.

### Status (`/status`) — no screenshot
- Loading: ❌
- Populated: ✅ shows the status string; not wrapped in `Main`.
- Error: ❌ (the backend being down fails the loader).

## Gaps for planning
1. No loading or error UI anywhere except book search and login; add router default pending and error components.
2. Three stubs: author details, author search, series list.
3. Search boxes on Books and Authors do nothing.
4. Empty-state copy on Authors is wrong; Book search has no empty-result state.
5. Screens without any screenshot: authors, series, status.
