import { addBook, type BookResult } from '#/client/mediaSanctumClient.ts';
import { FaPlus } from "react-icons/fa6";
import { GoBook } from "react-icons/go";
import { useMutation } from '@tanstack/react-query';
import { useNavigate } from '@tanstack/react-router';

export type BookSearchResultProps = {
  book: BookResult;
};

const BookSearchResult = ({ book }: BookSearchResultProps) => {
  const navigate = useNavigate();

  const { mutateAsync, isError, error, isPending } = useMutation({
    mutationFn: (hardcoverId: string) => addBook(hardcoverId),
    onSuccess: () => {
      navigate({
        to: '/books'
      });
    },
  });

  const addButtonClicked = async () => {
    try {
      await mutateAsync(book.hardcoverId);
    } catch (error) {
      console.error('Error adding book:', error);
    }
  };

  return (
    <div key={book.hardcoverId} className="flex items-center gap-3 px-3 py-2 hover:bg-surfaceAlt">
      <div className="flex flex-row gap-2 flex-1">
        { book.imageUrl ? (
          <img
            className="object-cover h-20 max-w-14 shrink-0"
            src={book.imageUrl}
            alt={book.title}
          />
        ) : (
          <div className="flex items-center justify-center h-20 w-14 bg-surfaceAlt text-center text-xl border border-border">
            <GoBook/>
          </div>
        )}
        <div className="min-w-0 flex-1">
          <p className="truncate text-md font-display font-medium text-text mt-0.5">
            {`${book.title}${book.releaseYear ? ` - ${book.releaseYear}` : ''}`}
          </p>
          <p className="truncate text-sm italic font-light text-textDim mt-1">
            {book.authors.join(', ')}
          </p>
        </div>
      </div>
      { isError ? (
        <p className="text-sm text-danger">
          {error.message}
        </p>
      ) : (
        <button
          className="btn btn-secondary text-xs! text-textDim! shrink-0"
          onClick={addButtonClicked}
          disabled={isPending}
        >
          <FaPlus /> { isPending ? 'Adding...' : 'Add'}
        </button>
      )}

    </div>
  );
};

export default BookSearchResult;