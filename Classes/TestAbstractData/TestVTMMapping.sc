TestVTMMapping : TestVTMElement {

	*generateRandomParameter{arg key, params;
		var result;
		result = super.generateRandomParameter(key, params);
		result = switch(key,
			\source, { this.generateRandomPath; },
			\destination, { this.generateRandomPath; },
			\when, {this.generateRandomString},//TODO: Change 'when'
			//	to whatever we'll use to express conditionals
			\type, { [\forwarding, \subscription, \bind, \exclusiveBind].choose; },
			result
		);
		^result;
	}
}
