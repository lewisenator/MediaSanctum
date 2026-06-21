type AudioDurationProps = {
  seconds: number;
  className?: string;
};

const MINUTE_IN_SECONDS = 60;
const HOUR_IN_SECONDS = MINUTE_IN_SECONDS * 60;

const AudioDuration = (
  {seconds: inputSeconds, className = ""}: AudioDurationProps
) => {
  if (!inputSeconds) return <span className="tabular-nums">N/A</span>

  let seconds = inputSeconds;

  let hours: number | undefined;
  if (seconds > HOUR_IN_SECONDS) {
    hours = Math.floor(seconds / HOUR_IN_SECONDS);
    seconds = seconds % HOUR_IN_SECONDS;
  }

  let minutes: number | undefined;
  if (seconds > MINUTE_IN_SECONDS) {
    minutes = Math.floor(seconds / MINUTE_IN_SECONDS);
    seconds = seconds % MINUTE_IN_SECONDS;
  }

  return (
    <span className={className}>
      {hours && (
        <span className="mr-2">{hours}h</span>
      )}
      {minutes && (
        <span className="mr-2">{minutes}m</span>
      )}
      <span>{Math.floor(seconds)}s</span>
    </span>
  );
};

export default AudioDuration;