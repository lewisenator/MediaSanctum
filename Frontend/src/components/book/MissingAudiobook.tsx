import { type Book } from '#/client/bookClient.ts';
import { FiUpload } from 'react-icons/fi';
import { useMutation } from '@tanstack/react-query';
import { uploadAudiobook } from '#/client/bookClient.ts';
import { useCallback } from 'react';
import { useDropzone } from 'react-dropzone';

type MissingAudiobookProps = {
  book: Book;
  setBook: (book: Book) => void;
};

const MissingAudiobook = (
  {book, setBook}: MissingAudiobookProps
) => {

  const { mutateAsync, isError, error, isPending } = useMutation({
    mutationFn: (file: File) => uploadAudiobook(book.id, file),
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
      <input {...getInputProps()} accept="audio/mp4,audio/x-m4b,.mp4" />
      <div
        className="text-xl font-semibold"
      >
        No Audiobook File Yet
      </div>
      <div
        className="font-display text-textDim"
      >
        Drag an MP4 file here, or choose one from your device to add the audiobook edition of this book.
      </div>
      <button
        className="btn btn-primary rounded-lg"
        disabled={isDragActive || isPending}
      >
        <FiUpload /> { isDragActive || isPending ? 'Uploading...' : 'Choose M4B file' }
      </button>
      <div
        className="text-sm tabular-nums text-textMute"
      >
        Accepts .m4b ⋅ up to 10GB
      </div>
      {isError && (
        <div className="font-display text-danger">
          {error.message}
        </div>
      )}
    </div>
  );
};

export default MissingAudiobook;