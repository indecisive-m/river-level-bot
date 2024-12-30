import { Events } from "discord.js";

export const name = Events.ClientReady;

export const once = true;

export async function execute(client) {
  const channel = client.channels
    .fetch("1321994435185213483")
    .then((channel) => channel.send("hello"));
}
