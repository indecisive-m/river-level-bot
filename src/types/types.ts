interface RootObject {
	'@context': string;
	meta: Meta;
	items: Item[];
}

interface Item {
	'@id': string;
	datumType: string;
	label: string;
	latestReading: LatestReading;
	notation: string;
	parameter: string;
	parameterName: string;
	period: number;
	qualifier: string;
	station: string;
	stationReference: string;
	unit: string;
	unitName: string;
	valueType: string;
}

interface LatestReading {
	'@id': string;
	date: string;
	dateTime: string;
	measure: string;
	value: number;
}

interface Meta {
	publisher: string;
	licence: string;
	documentation: string;
	version: string;
	comment: string;
	hasFormat: string[];
}
