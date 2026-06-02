import { useCallback } from 'react';
import { FiUpload } from 'react-icons/fi';
import { useDropzone } from 'react-dropzone';
import { useMutation } from '@tanstack/react-query';
import { type Book, type BookFile, uploadEbook } from '#/client/mediaSanctumClient.ts';

type BookFormatProps = {
  bookId: string;
  format: string;
  setBook: (book: Book) => void;
  metric: string;
  amount?: number;
  bookFile?: BookFile
};

const BookFormat = (
  { bookId, format, setBook, metric, amount, bookFile }: BookFormatProps
) => {
  const { mutateAsync, isError, error, isPending, data } = useMutation({
    mutationFn: (file: File) => uploadEbook(bookId, file),
    onSuccess: () => {
      console.log('Upload successful');
      if (data !== undefined) {
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
    <div className="py-4 px-5 flex flex-col">
      <div className="flex flex-row justify-between items-center flex-wrap">
        <div className="flex flex-row">
          <p className="library-card-format">
            {format}
          </p>
          { /* Show X pages or X audiobook hours */ }
          { amount && metric && (
            <div className="ml-5 text-sm text-textDim gap-1 flex flex-row flex-wrap items-center">
              <span className="tabular-nums">{amount}</span>
              {metric}
            </div>
          )}
        </div>
        { bookFile ? (
          <p className="text-success">{bookFile.filename}</p>
        ) : (
          <div className="">
            { isError && (
              <p className="text-danger">{error?.message}</p>               )}
            <div {...getRootProps()}>
              <input {...getInputProps()} accept="application/epub+zip,.epub,application/x-mobipocket-ebook,.mobi" />
              {
                isDragActive ?
                  <p><FiUpload />Drop the files here ...</p> :
                  <button
                    className="btn btn-secondary text-xs! px-3 py-2 font-ui"
                    disabled={isDragActive || isPending}
                  >
                    <FiUpload /> { isDragActive || isPending ? 'Uploading...' : 'Upload' }
                  </button>
              }
            </div>
          </div>
        )}

      </div>
      <div className="basis-full text-xs italic text-textMute">
        Files missing - drop a file anywhere on the page or use the buttons above.
      </div>
    </div>
  );
};

export default BookFormat;