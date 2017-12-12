TestVTMDictionaryValue : TestVTMCollectionValue {
	*generateRandomValue{arg params;
		//This is a temporary solution
		^Dictionary[
			this.generateRandomString -> this.generateRandomInteger,
			this.generateRandomString -> this.generateRandomString,
			this.generateRandomString -> this.generateRandomDecimal;
		];
	}

}
