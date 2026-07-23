import {formatDistance} from 'date-fns';

type TimeAgoProps = {
  date: Date;
}

const TimeAgo = ({ date }: TimeAgoProps) => {
  const displayTime = formatDistance(date, new Date(), { addSuffix: true });

  return (
    <span>
      {displayTime}
    </span>
  );
};

export default TimeAgo;