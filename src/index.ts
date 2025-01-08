export default {
	async scheduled(controller, env, ctx) {
		ctx.waitUntil(fetchRiverLevelFromAPI());
	},
	async fetch(request: Request): Promise<Response> {
		return new Response('Returning in local testing only', {
			status: 200,
		});
	},
} satisfies ExportedHandler<Env>;

const fetchRiverLevelFromAPI = async (): Promise<Response> => {
	const FLOODMONITORINGURL = 'https://environment.data.gov.uk/flood-monitoring/id/stations/52124/measures';
	const THRESHOLD = 6;
	const abortController = new AbortController();

	const cancelFetch = setTimeout(() => abortController.abort(), 10);

	try {
		const res = await fetch(FLOODMONITORINGURL, { signal: abortController.signal });

		if (!res.ok) {
			throw new Error('Fetch request failed');
		}

		clearTimeout(cancelFetch);

		const result: RootObject = await res.json();

		const items: Array<Item> = result.items;

		const dateTime = items[0]?.latestReading?.dateTime;
		const measure = items[0]?.latestReading?.value;

		const date = new Date(dateTime).toLocaleDateString();

		const time = new Date(dateTime).toLocaleTimeString();

		if (measure > THRESHOLD) {
			await fetch(
				'https://discord.com/api/webhooks/1323029259073093652/MCehx9lS5amarNdtg5iIdqmy9K0IkXxLnL4S0BI5GqhJsWLrRgOYMjlVoQk8x19JFF-l',
				{
					method: 'POST',
					headers: { 'Content-Type': 'application/json' },
					body: JSON.stringify({
						content: `The measurement at ${time} on ${date} is ${measure}m, this is above the ${THRESHOLD}m threshold`,
					}),
				}
			);

			return new Response('`The measurement at ${time} on ${date} is ${measure}m, this is above the ${THRESHOLD}m threshold`', {
				status: 200,
			});
		} else {
			return new Response(`The measurement at ${time} on ${date} is ${measure}m, this is below the ${THRESHOLD}m threshold`);
		}
	} catch (error) {
		if (error instanceof Error && error.name === 'AbortError') {
			return new Response('Fetch timed out', { status: 408 });
		}
		return new Response('Internal server error', { status: 500 });
	}
};
