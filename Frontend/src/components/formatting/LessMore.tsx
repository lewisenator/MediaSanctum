import { useState } from 'react';

type LessMoreProps = {
  text: string;
  limit?: number;
  mdLimit?: number;
  lgLimit?: number;
  xlLimit?: number;
  className?: string;
};

const LessMore = (
  {
    text,
    limit = 100,
    mdLimit: mdLimitProvided,
    lgLimit: lgLimitProvided,
    xlLimit: xlLimitProvided,
    className = "",
  }: LessMoreProps
) => {
  const [isExpanded, setIsExpanded] = useState(false);

  const mdLimit = mdLimitProvided || limit;
  const lgLimit = lgLimitProvided || mdLimit;
  const xlLimit = xlLimitProvided || lgLimit;


  type SizeSettings = {
    styles: string;
    limit: number;
  }

  const sizes: SizeSettings[] = [
    { styles: 'md:hidden', limit },
    { styles: 'hidden md:block lg:hidden', limit: mdLimit },
    { styles: 'hidden lg:block xl:hidden', limit: lgLimit },
    { styles: 'hidden xl:block', limit: xlLimit },
  ];

  return (
    <>
    {sizes.map(({styles, limit: textLimit}: SizeSettings, index) => (
        <div key={index} className={`${className} ${styles}`}>
          { (isExpanded || text.length <= textLimit) ? text : `${text.substring(0, textLimit)}...`}
          { text.length > textLimit && (
            <a
              onClick={() => setIsExpanded(!isExpanded)}
              className="ml-2 text-accent hover:cursor-pointer"
            >
              {isExpanded ? 'less' : 'more'}
            </a>
          )}
        </div>
    ))}
    </>
  );
};

export default LessMore;