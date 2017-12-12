TestVTMListValue : TestVTMCollectionValue {
	*generateRandomValue{arg params;
		^[
			this.generateRandomInteger,
			this.generateRandomString,
			this.generateRandomDecimal;
		];
	}
}
