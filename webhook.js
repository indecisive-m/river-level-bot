const { EmbedBuilder, WebhookClient } = require("discord.js");
const { webhookId, webhookToken } = require("./config.json");

const webhookClient = new WebhookClient({
  url: " https://discord.com/api/webhooks/1323029259073093652/MCehx9lS5amarNdtg5iIdqmy9K0IkXxLnL4S0BI5GqhJsWLrRgOYMjlVoQk8x19JFF-l",
});

webhookClient.send({
  content: "Webhook",
  username: "River Level Webhook",
});
