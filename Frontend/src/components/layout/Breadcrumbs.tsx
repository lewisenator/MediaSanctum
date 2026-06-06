import { type ReactElement } from 'react';
import { RxSlash } from 'react-icons/rx';
import { IoIosArrowBack } from 'react-icons/io';

type BreadcrumbsProps = {
  items: ReactElement[];
  className?: string;
};

export const breadcrumbClassName = "rounded-md hover:bg-surfaceAlt px-1 py-1";

const Breadcrumbs = (
  {
    className = "",
    items,
  }: BreadcrumbsProps
) => {
  return (
    <div
      className={`${className} flex flex-row items-center font-text text-textDim text-sm`}
    >
      <IoIosArrowBack />
      {items.map((element, index) => (
        <>
          { index !== 0 && (
            <RxSlash />
          )}
          {element}
        </>
      ))}
    </div>
  );
};

export default Breadcrumbs;