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
		if(this == Collection or: {this == SequenceableCollection}, {
			var collectionClass;
			collectionClass = this.allSubclasses.choose;
			^collectionClass.generateRandom(params);
		});
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

		valueClasses = valueClasses ? this.randomGeneratorClasses;
		if(depth > 1, {valueClasses = valueClasses ++ this});

		result = this.fill(size, {arg i;
			var valClass = valueClass ?? {valueClasses.choose;};
			var valParams;
			if(valParams.isNil, {valParams = ();}, { valParams = params.deepCopy; });
			if(valClass == this, {
				valParams.put(\depth, depth - 1);
			});
			valClass.generateRandom(valParams);
		});
		^result;
	}

}

+ RawArray {
	*generateRandom{arg params;
		var result;
		var size, minSize = 1, maxSize = 15;
		var valueClass, arrayClass;
		arrayClass = this;
		if(arrayClass == RawArray, {
			arrayClass = RawArray.allSubclasses.choose;
			^arrayClass.generateRandom(params);
		});
		if(params.notNil, {
			size = params[\size];
			minSize = params[\minSize] ? minSize;
			maxSize = params[\maxSize] ? maxSize;
		});
		if(size.isNil, {
			size = rrand(minSize, maxSize);
		});

		result = arrayClass.fill(size, {arg i;
			this.generateRandomSlotItem(params);
		});
		^result;
	}
}

+ Int8Array {
	*generateRandomSlotItem{arg params;
		var minVal = -128;
		var maxVal = 127;
		if(params.notNil, {
			minVal = params[\minVal] ? minVal;
			maxVal = params[\maxVal] ? maxVal;
		});
		^rrand(minVal, maxVal);
	}
}

+ Int16Array {
	*generateRandomSlotItem{arg params;
		var minVal = -32768;
		var maxVal = 32767;
		if(params.notNil, {
			minVal = params[\minVal] ? minVal;
			maxVal = params[\maxVal] ? maxVal;
		});
		^rrand(minVal, maxVal);
	}
}

+ Int32Array {
	*generateRandomSlotItem{arg params;
		^Integer.makeRandom32Bits;
	}
}

+ FloatArray {
	*generateRandomSlotItem{arg params;
		//these random ranges are not the best for floats but okay
		var maxVal = -1.0;
		var minVal = 1.0;
		if(params.notNil, {
			minVal = params[\minVal] ? minVal;
			maxVal = params[\maxVal] ? maxVal;
		});
		^rrand(minVal, maxVal);
	}
}

+ DoubleArray {
	*generateRandomSlotItem{arg params;
		//these random ranges are not the best for doubles but okay
		var maxVal = -1.0;
		var minVal = 1.0;
		if(params.notNil, {
			minVal = params[\minVal] ? minVal;
			maxVal = params[\maxVal] ? maxVal;
		});
		^rrand(minVal, maxVal);
	}
}

+ SymbolArray {
	*generateRandomSlotItem{arg params;
		^Symbol.generateRandom(params);
	}
}

+ Wavetable{
	*generateRandom{arg params;
		var size = 2 ** rrand(2,16);
		var newParams = IdentityDictionary.new;
		if(params.notNil, {
			size = params[\size] ? size;
			newParams = params.deepCopy;
		});
		newParams.put(\size, size);
		
		^Signal.generateRandom(newParams).asWavetable;
	}
}

+ SortedList {
	//only value types that can be sorted
	*randomGeneratorClasses{
		^[Integer, Float, String];
	}
}

+ Pair {
	*generateRandom{arg params;
		var result;
		var size, minSize = 1, maxSize = 15;
		var depth, minDepth = 1, maxDepth = 1;
		var valueClasses, valueClass;
		var makePair;
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
		valueClasses = valueClasses ? this.randomGeneratorClasses;
		result = this.new;
		makePair = {arg n, m;
			var pair;
			var linkDown, linkAcross;
			//The depth is for linkDown
			if((m-1) == 0, {
				linkDown = Object.generateRandomObject(params);
			}, {
				if((m / depth).coin, {
					linkDown = makePair.value(n, m-1);
				}, {
					linkDown = Object.generateRandomObject(params);
				});
			});
			//The size is for linkAcross
			if((n-1) == 0, {
				linkAcross = Object.generateRandomObject;
			}, {
				if((n / size).coin, {
					linkAcross = makePair.value(n-1, m);
				}, {
					linkAcross = Object.generateRandomObject;
				});
			});
			pair = Pair(linkDown, linkAcross);
			pair;
		};
		result = makePair.value(size,depth);
		^result;
	}
}

+ MultiLevelIdentityDictionary {
	*generateRandom{arg params;
		var result;
		result = IdentityDictionary.generateRandom(params);
		result = this.newFromIdentityDictionary(result);
		^result;
	}

	*newFromIdentityDictionary{arg dict;
		var convertLevel;
		var multiDict = MultiLevelIdentityDictionary.new;
		convertLevel = {arg d, parentKeys;
			d.keysValuesDo({arg key, val;
				if(val.isKindOf(IdentityDictionary), {
					convertLevel.value(val, parentKeys.add(key));
				}, {
					multiDict.put(*(parentKeys ++ [key] ++ [val]));
				});
			});
		};
		convertLevel.value(dict);
		^multiDict;
	}
}

+ Array2D {
	*generateRandom{arg params;
		var result;
		var numRows, numCols;
		var size, minSize, maxSize;
		var minRows = 1, maxRows = 15;
		var minCols = minRows, maxCols = maxRows;
		var valueClasses, valueClass;
		if(params.notNil, {
			size = params[\size];
			minSize = params[\minSize] ? minSize;
			maxSize = params[\maxSize] ? maxSize;
			valueClasses = params[\valueClasses];
			numRows = params[\numRows];
			minRows = params[\minRows] ? minRows;
			maxRows = params[\maxRows] ? maxRows;
			numCols = params[\numCols];
			minCols = params[\minCols] ? minCols;
			maxCols = params[\maxCols] ? maxCols;
			valueClass = params[\valueClass];
		});
		if(size.notNil, {
			numRows = size;
			numCols = size;
		}, {
			numRows = rrand(minRows, maxRows);
			numCols = rrand(minCols, maxCols);
		});
		result = Array2D.new(numRows, numCols);
		valueClasses = valueClasses ? this.randomGeneratorClasses;

		numRows.do({arg i;
			numCols.do({arg j;
				valueClass = valueClass ?? {valueClasses.choose};
				result.put(i, j, valueClass.generateRandom(params));
			});
		});
		^result;
	}

	*randomGeneratorClasses{
		^[Integer, Float, String, Symbol];
	}
}

+ Matrix {
	*generateRandom{arg params;
		var result;
		var numRows, numCols;
		var size, minSize, maxSize;
		var minRows = 1, maxRows = 15;
		var minCols = minRows, maxCols = maxRows;
		var valueClasses, valueClass;
		if(params.notNil, {
			size = params[\size];
			minSize = params[\minSize] ? minSize;
			maxSize = params[\maxSize] ? maxSize;
			valueClasses = params[\valueClasses];
			numRows = params[\numRows];
			minRows = params[\minRows] ? minRows;
			maxRows = params[\maxRows] ? maxRows;
			numCols = params[\numCols];
			minCols = params[\minCols] ? minCols;
			maxCols = params[\maxCols] ? maxCols;
			valueClass = params[\valueClass];
		});
		if(size.notNil, {
			numRows = size;
			numCols = size;
		}, {
			numRows = rrand(minRows, maxRows);
			numCols = rrand(minCols, maxCols);
		});
		valueClasses = valueClasses ? this.randomGeneratorClasses;
		result = Matrix.fill(numRows, numCols, {arg i, j;
			valueClass = valueClass ?? {valueClasses.choose};
			valueClass.generateRandom;
		});
		^result;
	}
	*randomGeneratorClasses{
		^[Integer, Float];
	}
}

+ Interval {
	*generateRandom{arg params;
		var valueClass;
		var start, minStart, maxStart;
		var end, minEnd, maxEnd;
		var step, range;
		var size = rrand(1, 15);
		valueClass = [Integer, Float].choose;
		minStart = valueClass.generateRandom;
		maxStart = rrand(minStart, minStart + valueClass.generateRandom.abs);
		minEnd = maxStart + valueClass.generateRandom.abs;
		maxEnd = rrand(minEnd, minEnd + valueClass.generateRandom.abs);
		start = rrand(minStart, maxStart);
		end = rrand(minEnd, maxEnd);
		#start, end = [start, end].sort;
		range = end - start;
		step = range / size;
		^Interval.new(start, end, step);
	}
}

+ Range {
	*generateRandom{arg params;
		var start, minStart = -1000, maxStart = 1000;
		var size, minSize = 0, maxSize = 1000;
		if(params.notNil, {
			size = params[\size];
			minSize = params[\minSize] ? minSize;
			maxSize = params[\maxSize] ? maxSize;
			start = params[\numStart];
			minStart = params[\minStart] ? minStart;
			maxStart = params[\maxStart] ? maxStart;
		});
		if(size.isNil, {
			size = rrand(minSize, maxSize);
		});
		if(start.isNil, {
			start = rrand(minStart, maxStart);
		});
		^Range(start, size);
	}
}

+ NamedList {
	//the interface the NamedList class is a bit
	//cumbersome and too "helpful" in a way.
	//Therefore a simple solution here.
	*generateRandom{arg params;
		var result, names;
		result = List.generateRandom((valueClass: Float));

		names = result.size.collect({
			Symbol.generateRandom;
		});
		^this.newUsing(result, names);
	}
}

+ TwoWayIdentityDictionary {
	*generateRandom{arg params;
		var result, dict;
		dict = IdentityDictionary.generateRandom(params);
		result = TwoWayIdentityDictionary.new;
		dict.keysValuesDo({arg key, val;
			result.put(key, val);
		});
		^result;
	}
}

+ Object {
	*randomGeneratorClasses{
		^[Boolean, Integer, Float, String, Symbol];
	}

	*generateRandomObject{
		^this.randomGeneratorClasses.choose.generateRandom;
	}
}


