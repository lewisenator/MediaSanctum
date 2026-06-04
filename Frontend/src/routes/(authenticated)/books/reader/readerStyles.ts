import { type IReactReaderStyle, ReactReaderStyle } from 'react-reader';

export const readerStyles: IReactReaderStyle = {
  ...ReactReaderStyle,
  titleArea: {
    ...ReactReaderStyle.titleArea,
    background: "var(--c-surfaceAlt)",
    color: "var(--c-text)",
    display: "none",
  },
  container: {
    ...ReactReaderStyle.container,
  },
  arrow: {
    ...ReactReaderStyle.arrow,
    display: "none",
  },
  arrowHover: {
    ...ReactReaderStyle.arrowHover,
    display: "none",
  },
  reader: {
    ...ReactReaderStyle.reader,
    color: "var(--c-text)",
    background: "var(--c-bg)",
    inset: "0"
  },
  readerArea: {
    ...ReactReaderStyle.readerArea,
    backgroundColor: "var(--c-surface)",
    transition: undefined,
  },
  tocArea: {
    ...ReactReaderStyle.tocArea,
    background: "var(--c-surface)",
    color: "var(--c-text)",
  },
  tocButtonExpanded: {
    ...ReactReaderStyle.tocButtonExpanded,
    background: "var(--c-surfaceAlt)",
  },
  tocButtonBar: {
    ...ReactReaderStyle.tocButtonBar,
    background: "var(--c-text)",
  },
  tocButton: {
    ...ReactReaderStyle.tocButton,
    background: "var(--c-surfaceAlt)",
  },
  tocBackground: {
    ...ReactReaderStyle.tocBackground,

  },
  toc: {
    ...ReactReaderStyle.toc,
    background: "var(--c-surfaceAlt)",
  },
};