import { IoCloseOutline } from 'react-icons/io5';
import Slider from '#/components/widgets/Slider.tsx';
import Dropdown from "#/components/widgets/Dropdown.tsx";
import { fontOptions } from './fontOptions.ts';
import Checkbox from '#/components/widgets/Checkbox.tsx';
import ThemePicker from '#/components/widgets/ThemePicker.tsx';

type ReaderSettingsProps = {
  dismiss: () => void;

  fontSize: number;
  setFontSize: (fontSize: number) => void;

  lineHeight: number;
  setLineHeight: (fontSize: number) => void;

  font: string,
  setFont: (font: string) => void;

  paragraphSpacing: number;
  setParagraphSpacing: (paragraphSpacing: number) => void;

  pageMargins: number;
  setPageMargins: (pageMargins: number) => void;

  brightness: number;
  setBrightness: (brightness: number) => void;

  spread: boolean;
  setSpread: (spread: boolean) => void;
};

const ReaderSettings = (
  {
    dismiss,
    fontSize,
    setFontSize,
    lineHeight,
    setLineHeight,
    font,
    setFont,
    paragraphSpacing,
    setParagraphSpacing,
    pageMargins,
    setPageMargins,
    brightness,
    setBrightness,
    spread,
    setSpread,
  }: ReaderSettingsProps
) => {

  return (
    <>
      <div
        className="fixed top-11.75 bottom-10.25 left-0 z-30 flex w-full bg-black/10"
        onClick={dismiss}
      ></div>
      <div
        className="fixed top-11.75 bottom-10.25 right-0 z-30 flex w-64 flex-col
          shadow-[4px_0_6px_-2px_rgba(0,0,0,0.15)]
          transition-all duration-200 ease-in-out
          bg-surface border-b border-border
          "
      >
        <div className="px-5 py-3 flex flex-row justify-between items-center border-b border-border">
          <h1 className="font-display text-text font-semibold">
            Reader Settings
          </h1>
          <div>
            <button
              onClick={dismiss}
              className="inline-flex items-center justify-center text-lg font-bold bg-surface hover:bg-surfaceAlt transition w-7 h-7 rounded-md border border-border"
            >
              <IoCloseOutline />
            </button>
          </div>
        </div>
        <div className="overflow-y-scroll">
          <div className="py-2 px-4">
            <ThemePicker />
          </div>
          <div className="py-2 px-4">
            <Slider
              label="Brightness"
              stepSize={1}
              value={brightness}
              setValue={setBrightness}
              valueFormatter={(value) => `${value} %`}
              min={20}
              max={100}
            />
          </div>
          <div className="py-3 px-4">
            <Slider
              label="Font Size"
              stepSize={0.1}
              value={fontSize}
              setValue={setFontSize}
              valueFormatter={(value) => `${value}em`}
              min={0.6}
              max={2}
            />
          </div>
          <div className="py-3 px-4">
            <Dropdown
              value={font}
              setValue={setFont}
              label={"Font"}
              options={fontOptions}
              keyFormatter={(k: string, v: string) => (
                <p style={{
                  fontFamily: v
                }}>
                  {k}
                </p>
              )}
            />
          </div>
          <div className="py-3 px-4">
            <Slider
              label="Line Height"
              stepSize={0.1}
              value={lineHeight}
              setValue={setLineHeight}
              min={0.8}
              max={2.0}
            />
          </div>
          <div className="py-3 px-4">
            <Slider
              label="Paragraph Spacing"
              stepSize={0.1}
              value={paragraphSpacing}
              setValue={setParagraphSpacing}
              valueFormatter={(value) => `${value}em`}
              min={-0.4}
              max={2.0}
            />
          </div>
          <div className="py-3 px-4">
            <Slider
              label="Page Margins"
              stepSize={0.1}
              value={pageMargins}
              setValue={setPageMargins}
              valueFormatter={(value) => `${value}em`}
              min={0.0}
              max={3.0}
            />
          </div>
          <div className="py-3 px-4">
            <Checkbox
              label="2-page spread when wide"
              value={spread}
              setValue={setSpread}
            />
          </div>
        </div>
      </div>
    </>
  );
};

export default ReaderSettings;