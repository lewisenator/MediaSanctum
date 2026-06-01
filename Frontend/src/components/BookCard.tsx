import { type Book } from '#/client/mediaSanctumClient.ts';
import { BsBook } from "react-icons/bs";
import { Link } from '@tanstack/react-router';

type BookCardProps = {
  book: Book;
};

const BookCard = ({book}: BookCardProps) => {

  return (
    <Link to="/books/$bookId" params={{ bookId: book.id }} className="library-card group flex flex-col">
      <div className="library-card-cover min-h-2/4">
        {book.ebookEdition && book.ebookEdition.image ? (
          <img
            src={book.ebookEdition.image.url}
            alt={`${book.title} Cover`}
            className="cover-image w-full"
          />
        ) : (
          <div className="cover-image flex items-center justify-center h-40 min-h-40 max-w-30 text-center text-4xl shadow-none!">
            <BsBook />
          </div>
        )}
      </div>
      <div className="library-card-meta ">
        <div>
          <p className="library-card-title font-display font-medium text-md text-wrap tracking-tight overflow-hidden" title={book.title}>
            {book.title}
          </p>
          <div className="text-xs text-textDim mt-1 overflow-hidden">
            {book.author.name}
          </div>
        </div>
        <div className="flex flex-row justify-between mt-2">
          <span className="library-card-format bg-surfaceAlt! text-accent!">Missing</span>
          <span>{book.releaseYear}</span>
        </div>
      </div>
    </Link>
  );
};

export default BookCard;