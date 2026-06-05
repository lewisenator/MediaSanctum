import { useState } from 'react';

type LessMoreProps = {
  text: string;
  limit?: number;
  className?: string;
};

const LessMore = (
  {
    text,
    limit = 100,
    className = "",
  }: LessMoreProps
) => {
  const [isExpanded, setIsExpanded] = useState(false);

  return (
    <div className={className}>
      { isExpanded ? text : `${text.substring(0, limit)}...`}
      <a
        onClick={() => setIsExpanded(!isExpanded)}
        className="ml-2 text-accent hover:cursor-pointer"
      >
        {isExpanded ? 'less' : 'more'}
      </a>
    </div>
  );
};

export default LessMore;