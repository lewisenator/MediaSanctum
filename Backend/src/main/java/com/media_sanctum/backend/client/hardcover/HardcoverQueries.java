package com.media_sanctum.backend.client.hardcover;

public class HardcoverQueries {

    public static final String SEARCH_AUTHORS = """
            query SearchAuthors($q: String!) {
                search(query: $q, query_type: "author", per_page: 25, page: 1) {
                    results
                }
            }
            """;

    public static final String SEARCH_BOOKS = """
            query SearchBooks($q: String!) {
                search(query: $q, query_type: "book", per_page: 25, page: 1) {
                    results
                }
            }
            """;

    public static final String GET_AUTHOR = """
            query GetAuthor($q: Int!) {
              authors_by_pk(id: $q) {
                id
                canonical_id
                name
                title
                name_personal
                alternate_names
                slug
                bio
                born_year
                death_year
                books_count
                users_count
                cached_image
                links
              }
            }
            """;

    public static final String GET_BOOK = """
            query GetBook($q: Int!) {
                books_by_pk(id: $q) {
                    id
                    canonical_id
                    headline
                    title
                    slug
                    subtitle
                    description
                    release_year
                    pages
                    audio_seconds
                    rating
                    ratings_count
                    taggings {
                        id
                        tag {
                            id
                            tag
                            tag_category {
                                id
                                category
                            }
                        }
                    }
                    default_cover_edition {
                      id
                      canonical_id
                      book_id
                      asin
                      cached_image
                      isbn_10
                      isbn_13
                      language {
                        code2
                      }
                      country {
                        code2
                      }
                    }
                    default_audio_edition {
                      id
                      canonical_id
                      book_id
                      asin
                      cached_image
                      isbn_10
                      isbn_13
                      audio_seconds
                      language {
                        code2
                      }
                      country {
                        code2
                      }
                    }
                    contributions {
                      author {
                        id
                        name
                      }
                      contributable_type
                      contribution
                      id
                    }
                    featured_book_series {
                      id
                      position
                      series {
                          id
                          canonical_id
                          name
                          slug
                          is_completed
                          description
                          primary_books_count
                      }
                    }
                }
            }
            """;
}
