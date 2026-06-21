type TimeProps = {
  milliseconds: number;
  className?: string;
  showHoursMinutesSeconds?: boolean;
};

const MS_IN_SECOND = 1000;
const MS_IN_MINUTE = MS_IN_SECOND * 60;
const MS_IN_HOUR = MS_IN_MINUTE * 60;

const Time = (
  {
    milliseconds: millisecondsInput,
    className = "",
    showHoursMinutesSeconds = false,
  }: TimeProps
) => {

  let milliseconds = millisecondsInput;

  let hours: number | undefined;
  if (showHoursMinutesSeconds) {
    hours = 0;
  }
  if (milliseconds > MS_IN_HOUR) {
    hours = Math.floor(milliseconds / MS_IN_HOUR);
    milliseconds = milliseconds % MS_IN_HOUR;
  }

  let minutes: number | undefined;
  if (showHoursMinutesSeconds) {
    minutes = 0;
  }
  if (milliseconds > MS_IN_MINUTE) {
    minutes = Math.floor(milliseconds / MS_IN_MINUTE);
    milliseconds = milliseconds % MS_IN_MINUTE;
  }

  let seconds: number | undefined;
  if (milliseconds > MS_IN_SECOND) {
    seconds = Math.floor(milliseconds / MS_IN_SECOND);
  } else {
    seconds = 0;
  }

  return (
    <span className={className}>
      {hours !== undefined && (
        <>
          <span className="hours">{hours}</span>
          <span className="separator">:</span>
        </>
      )}

      {minutes !== undefined && (
        <>
          <span className="minutes">{minutes.toString().padStart(2, "0")}</span>
          <span className="separator">:</span>
        </>
      )}

      <span className="seconds">{seconds.toString().padStart(2, "0")}</span>
    </span>
  );
};

export default Time;