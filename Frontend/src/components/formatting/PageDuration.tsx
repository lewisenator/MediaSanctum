type PageDurationProps = {
  pagesLeft?: number;
  className?: string;
  prefix?: string;
  suffix?: string;
};

const PPM = 1.5;

const PageDuration = (
  {pagesLeft, className, prefix, suffix}: PageDurationProps
) => {
  if (!pagesLeft || pagesLeft < 0) return (
    <span>N/A</span>
  );

  let minutesLeft = pagesLeft / PPM;
  let hoursLeft: number | undefined;
  if (minutesLeft >= 60) {
    hoursLeft = Math.floor(minutesLeft / 60);
    minutesLeft = Math.ceil(minutesLeft % 60);
  } else {
    minutesLeft = Math.ceil(minutesLeft);
  }

  return (
    <span className={className}>
      {prefix && (
        <span className="mr-1">{prefix}</span>
      )}
      {hoursLeft && (
        <span className="mr-1">{`${hoursLeft}h`}</span>
      )}
      <span>{`${minutesLeft}m`}</span>
      {suffix && (
        <span className="ml-1">{suffix}</span>
      )}
    </span>
  );
};

export default PageDuration;