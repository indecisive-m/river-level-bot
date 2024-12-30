import { EmbedBuilder, WebhookClient } from "discord.js";

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

  if (measure > 2) {
    const webhookClient = new WebhookClient({
      url: " https://discord.com/api/webhooks/1323029259073093652/MCehx9lS5amarNdtg5iIdqmy9K0IkXxLnL4S0BI5GqhJsWLrRgOYMjlVoQk8x19JFF-l",
    });

    webhookClient.send({
      content: `The measurement at ${time} on ${date} is ${measure}m`,
      username: "River Level Webhook",
    });
  }

  return;
}

fetchRiverLevelsData();
