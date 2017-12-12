+ String {
	*generateRandom{arg params;
		var chars;
		var minLength, maxLength, noSpaces, noNumbers, noAlphas, onlyAlphaNumeric;
		minLength = 1;
		maxLength = 15;
		noSpaces = true;
		noAlphas = false;
		noNumbers = false;
		onlyAlphaNumeric = true;

		if(params.notNil and: { params.isKindOf(Dictionary) }, {
			minLength = params[\minLength] ? minLength;
			maxLength = params[\maxLength] ? maxLength;
			noSpaces = params[\noSpaces] ? noSpaces;
			noAlphas = params[\noAlphas] ? noAlphas;
			noNumbers = params[\noNumbers] ? noNumbers;
			onlyAlphaNumeric = params[\onlyAlphaNumeric] ? onlyAlphaNumeric;
		});

		chars = (0..127).collect(_.asAscii);

		if(onlyAlphaNumeric, {
			chars = chars.select({arg it; it.isAlphaNum || (it == Char.space); });
		});
		if(noSpaces, {
			chars = chars.reject({arg it; it == Char.space; });
		});
		if(noNumbers, {
			chars = chars.reject({arg it; it.isDecDigit; });
		});
		if(noAlphas, {
			chars = chars.reject({arg it; it.isAlpha; });
		});
		^String.newFrom(
			rrand(minLength, maxLength).collect({
				chars.choose;
			})
		);
	}
}

+ Symbol {
	*generateRandom{ arg params; ^String.generateRandom(params).asSymbol; }

	*generateRandomPath{arg params;
		var numLevels;
		var minLevels = 1;
		var maxLevels = 3;
		var result;
		if(params.notNil, {
			numLevels = params[\numLevels];
			minLevels = params[\minLevels] ? minLevels;
			maxLevels = params[\maxLevels] ? maxLevels;
		});
		if(numLevels.isNil, {
			numLevels = rrand(minLevels, maxLevels);
		});
		numLevels.collect({arg i;
			result = result.addAll("/%".format(this.generateRandom));
		}).asSymbol;
		^result;
	}
}

+ Boolean {
	*generateRandom{arg params;
		var chance = 0.5;
		if(params.notNil and: {params.isKindOf(Dictionary)}, {
			chance = params[\chance] ? 0.5;
		});
		^chance.coin;
	}
}

+ Integer {
	*generateRandom{arg params;
		^Float.generateRandom(params).asInteger;
	}
}

+ Float {
	*generateRandom{arg params;
		var minVal = -2147483648.0;//32 bits random
		var maxVal = 2147483647.0;
		if(params.notNil, {
			minVal = params[\minVal] ? minVal;
			maxVal = params[\maxVal] ? maxVal;
		});
		^rrand(minVal, maxVal);
	}
}

+ Dictionary {
	*generateRandom{arg params;
		var result;
		var size, minSize = 1, maxSize = 15;
		var depth, minDepth = 1, maxDepth = 1;
		var valueClasses;
		if(params.notNil, {
			size = params[\size];
			minSize = params[\minSize] ? minSize;
			maxSize = params[\maxSize] ? maxSize;
			valueClasses = params[\valueClasses];
			depth = params[\depth];
			minDepth = params[\minDepth] ? minDepth;
			maxDepth = params[\maxDepth] ? maxDepth;
		});
		if(size.isNil, {
			size = rrand(minSize, maxSize);
		});
		if(depth.isNil, {
			depth = rrand(minDepth, maxDepth);
		});
		result = this.new(size);
		valueClasses = valueClasses ? Object.randomGeneratorClasses;
		if(depth > 1, {valueClasses = valueClasses ++ this});
		size.do({arg i;
			var valClass = valueClasses.choose;
			var valParams;
			if(valParams.isNil, {valParams = ();}, { valParams = params.deepCopy; });
			if(valClass == this, {
				valParams.put(\depth, depth - 1);
			});
			result.put(
				this.generateRandomKey,
				valClass.generateRandom(valParams);
			);
		});
		^result;
	}

	*generateRandomKey{arg params;
		^String.generateRandom(params);
	}
}

+ IdentityDictionary {
	*generateRandomKey{arg params;
		^super.generateRandomKey(params).asSymbol;
	}
}

+ Collection {
	*generateRandom{arg params;
		var result;
		var size, minSize = 1, maxSize = 15;
		var depth, minDepth = 1, maxDepth = 1;
		var valueClasses, valueClass;
		if(params.notNil, {
			size = params[\size];
			minSize = params[\minSize] ? minSize;
			maxSize = params[\maxSize] ? maxSize;
			valueClasses = params[\valueClasses];
			depth = params[\depth];
			minDepth = params[\minDepth] ? minDepth;
			maxDepth = params[\maxDepth] ? maxDepth;
			valueClass = params[\valueClass];
		});
		if(size.isNil, {
			size = rrand(minSize, maxSize);
		});
		if(depth.isNil, {
			depth = rrand(minDepth, maxDepth);
		});
		result = this.new(size);
		valueClasses = valueClasses ? Object.randomGeneratorClasses;
		if(depth > 1, {valueClasses = valueClasses ++ this});
		size.do({arg i;
			var valClass = valueClass ?? {valueClasses.choose;};
			var valParams;
			if(valParams.isNil, {valParams = ();}, { valParams = params.deepCopy; });
			if(valClass == this, {
				valParams.put(\depth, depth - 1);
			});
			result = result.add( valClass.generateRandom(valParams) );
		});
		^result;
	}

}

+ Object {
	*randomGeneratorClasses{
		^[/*Boolean,*/ Integer, Float, String, Symbol];
	}

	*generateRandomObject{
		^this.randomGeneratorClasses.choose.generateRandom;
	}
}


