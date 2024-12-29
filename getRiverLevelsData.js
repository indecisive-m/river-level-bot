const url =
  "https://environment.data.gov.uk/flood-monitoring/id/stations/52124/measures";

export default async function fetchRiverLevelsData() {
  const res = await fetch(url);
  const result = await res.json();

  const items = result.items;

  const dateTime = items[0]?.latestReading?.dateTime;
  const measure = items[0]?.latestReading?.value;

  const date = new Date(dateTime).toLocaleDateString();

  const time = new Date(dateTime).toLocaleTimeString();

  return `The measurement at ${time} on ${date} is ${measure}m`;
}

fetchRiverLevelsData();
