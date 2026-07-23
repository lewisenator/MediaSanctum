import type { Author } from '#/client/authorClient.ts';
import { Link } from '@tanstack/react-router';
import { RxPerson } from "react-icons/rx";

type AuthorCardProps = {
  author: Author;
};

const AuthorCard = (
  {author}: AuthorCardProps
) => {
  return (
    <Link to="/authors/$authorId" params={{ authorId: author.id }} className="library-card group flex flex-col" title={author.name}>
      <div className="library-card-cover min-h-2/4">
        {author.image ? (
          <img
            src={author.image.url}
            alt={`${author.name} Image`}
            className="cover-image w-full"
          />
        ) : (
          <div className="cover-image flex items-center justify-center h-40 min-h-40 max-w-30 text-center text-4xl shadow-none!">
            <RxPerson />
          </div>
        )}
      </div>
      <div className="library-card-meta ">
        <div>
          <p className="library-card-title font-display font-medium text-md text-wrap tracking-tight overflow-hidden" title={author.name}>
            {author.name}
          </p>
          <div className="text-xs text-textDim mt-1 overflow-hidden">
            {`${author.libraryBooksCount} ${author.libraryBooksCount !== 1 ? 'books' : 'book'}`}
          </div>
        </div>
        <div className="flex flex-row justify-between mt-2">
          <span className="tabular-nums font-mono text-textMute text-xs">
            {author.bornYear && (
              <span className="flex flex-row gap-1">
                <span className="font-mono text-accent">{author.bornYear}</span>
                <span>-</span>
                <span className="font-mono text-accent">
                  {author.deathYear ? (
                    author.deathYear
                  ) : (
                    "Present"
                  )}
                </span>
              </span>
            )}
          </span>
        </div>
      </div>
    </Link>
  );
};

export default AuthorCard;