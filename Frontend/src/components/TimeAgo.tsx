import {formatDistance, subDays} from 'date-fns';

type TimeAgoProps = {
  date: Date;
}

const TimeAgo = ({ date }: TimeAgoProps) => {
  const pastDate = subDays(date, 5);
  const displayTime = formatDistance(pastDate, new Date(), { addSuffix: true });

  return (
    <span>
      {displayTime}
    </span>
  );
};

export default TimeAgo;