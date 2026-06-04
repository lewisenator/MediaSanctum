import { useCallback, useState } from 'react';
import { FiUpload } from 'react-icons/fi';
import { useDropzone } from 'react-dropzone';
import { useMutation } from '@tanstack/react-query';
import { type Book, type BookFile, uploadEbook } from '#/client/mediaSanctumClient.ts';
import { Link } from '@tanstack/react-router';
import { BsBook } from "react-icons/bs";
import { IoCloudDownloadOutline } from "react-icons/io5";


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
  const { mutateAsync, isError, error, isPending } = useMutation({
    mutationFn: (file: File) => uploadEbook(bookId, file),
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

  const [downloading, setDownloading] = useState<boolean>(false);
  const downloadBook = async () => {
    if (!bookFile) return;
    setDownloading(true);
    try {
      const response = await fetch(bookFile.url);
      const bookFileBlob: Blob = await response.blob();
      const url = window.URL.createObjectURL(bookFileBlob);
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', bookFile.filename);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.log(error);
    }
    setDownloading(false);
  }

  console.log(`Rendering format for bookId: ${bookId} format: ${format} hasBook: ${bookFile !== null}`);
  console.log(`bookFile: ${JSON.stringify(bookFile)}`);

  return (
    <div className="py-4 px-5 flex flex-col">
      <div className="flex flex-row justify-between items-center flex-wrap gap-3">
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
          <div className="flex flex-row gap-3 flex-wrap">
            <Link
              to='/books/reader/$bookId'
              params={{ bookId: bookId }}
              className="btn btn-secondary text-xs! px-3 py-2 font-ui shrink-0"
            >
              <BsBook /> Read
            </Link>
            <button
              disabled={downloading}
              onClick={downloadBook}
              className="btn btn-secondary text-xs! px-3 py-2 font-ui shrink-0"
            >
              <IoCloudDownloadOutline /> {downloading ? 'Downloading...' : 'Download'}
            </button>
          </div>
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
        { bookFile
          ? `Click to download or read ${bookFile.filename}`
          : 'Files missing - drop a file anywhere on the page or use the buttons above.'
        }

      </div>
    </div>
  );
};

export default BookFormat;