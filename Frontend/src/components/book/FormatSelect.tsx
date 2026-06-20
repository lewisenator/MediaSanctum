import { CiHeadphones } from "react-icons/ci";
import { PiBookLight } from "react-icons/pi";

type FormatSelectProps = {
  selectedFormat: string;
  setSelectedFormat: (selectedFormat: string) => void;
};

const FormatSelect = (
  {
    selectedFormat,
    setSelectedFormat,
  }: FormatSelectProps
) => {
  return (
    <div
      className="flex flex-row mt-8 bg-surfaceAlt border border-border p-1 rounded-lg max-w-53"
    >
          <span
            onClick={() => setSelectedFormat("ebook")}
            className={`inline-flex justify-center items-center py-2 px-3 m-0 text-sm hover:bg-surface rounded-md cursor-pointer text-textDim ${selectedFormat === 'ebook' && 'drop-shadow-sm text-text!'}`}
            style={{
              backgroundColor: selectedFormat === 'ebook' ? 'var(--c-surface)' : 'var(--c-surfaceAlt)',
            }}
          >
            <PiBookLight className={`mr-1 ${selectedFormat === 'ebook' && 'text-accent'}`} /> Ebook
          </span>
      <span
        onClick={() => setSelectedFormat("audiobook")}
        className={`inline-flex justify-center items-center py-2 px-3 m-0 text-sm hover:bg-surface rounded-md cursor-pointer text-textDim ${selectedFormat === 'audiobook' && 'drop-shadow-sm text-text!'}`}
        style={{
          backgroundColor: selectedFormat === 'audiobook' ? 'var(--c-surface)' : 'var(--c-surfaceAlt)',
        }}
      >
            <CiHeadphones className={`mr-1 ${selectedFormat === 'audiobook' && 'text-accent'}`} /> Audiobook
          </span>
    </div>
  );
};

export default FormatSelect;