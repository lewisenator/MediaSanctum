type FileSizeProps = {
  bytes?: number;
};

const FileSize = (
  { bytes }: FileSizeProps
) => {

  if (!bytes) return <span className="tabular-nums">N/A</span>;

  const units = ["B", "KB", "MB", "GB", "TB"];

  let value = bytes;
  let unit = "B"

  let index = 0;
  while (index < units.length && value > 1024) {
    value = value / 1024;
    unit = units[++index];
  }


  return (
    <span className="tabular-nums">
      {Math.round(value)} {unit}
    </span>
  );
};

export default FileSize;