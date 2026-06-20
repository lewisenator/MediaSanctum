import { type Book, uploadEbook } from '#/client/bookClient.ts';
import { FiUpload } from 'react-icons/fi';
import { useCallback } from 'react';
import { useMutation } from '@tanstack/react-query';
import { useDropzone } from 'react-dropzone';

type MissingEbookProps = {
  book: Book;
  setBook: (book: Book) => void;
};

const MissingEbook = (
  {setBook, book}: MissingEbookProps
) => {

  const { mutateAsync, isError, error, isPending } = useMutation({
    mutationFn: (file: File) => uploadEbook(book.id, file),
    onSuccess: async (data) => {
      if (data) {
        setBook(data);
      }
    }
  });

  const onDrop = useCallback((acceptedFiles: File[]) => {
    acceptedFiles.forEach((file: File) => {
      const reader = new FileReader();
      reader.onabort = () => console.log('file reading was aborted');
      reader.onerror = () => console.log('file reading has failed');
      reader.onload = async () => {
        // Do whatever you want with the file contents
        const binaryStr = reader.result;
        console.log(binaryStr);

        try {
          await mutateAsync(file);
        } catch (error) {
          console.error('Error adding book:', error);
        }
      }
      reader.readAsArrayBuffer(file);
    })
  }, []);

  const {getRootProps, getInputProps, isDragActive} = useDropzone({onDrop});

  return (
    <div
      className="bg-surfaceAlt mt-4 p-10 rounded-xl border border-border flex flex-col flex-wrap gap-3 items-center"
      {...getRootProps()}
    >
      <input {...getInputProps()} accept="application/epub+zip,.epub,application/x-mobipocket-ebook,.mobi" />
      <div
        className="text-xl font-semibold"
      >
        No Ebook File Yet
      </div>
      <div
        className="font-display text-textDim"
      >
        Drag an EPUB file here, or choose one from your device to add the ebook edition of this book.
      </div>
      <button
        className="btn btn-primary rounded-lg"
        disabled={isDragActive || isPending}
      >
        <FiUpload /> { isDragActive || isPending ? 'Uploading...' : 'Choose EPUB file' }
      </button>
      <div
        className="text-sm tabular-nums text-textMute"
      >
        Accepts .epub ⋅ up to 2GB
      </div>
      {isError && (
        <div className="font-display text-danger">
          {error.message}
        </div>
      )}
    </div>
  );
};

export default MissingEbook;