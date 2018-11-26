VTMUnitTest : UnitTest {
	classvar <>reportAllErrors = true;
	classvar <>checkFreeingResponderFuncs = true;
	classvar <>forceFreeingResponderFuncs = true;

	*runAll{arg runScripts = true;

		[
			VTMValue,
			VTMData,
			VTMDataManager,
			VTMNamedList,
			// VTMApplication, //buggy testing when using .wait andhaung etc.
		].do({arg cl;
			this.runTestForClass(cl, recursive: true);
		});
		if(runScripts, {
			VTMUnitTestScript.findTestScripts;
			VTMUnitTestScript.runAll;
		});

	}

	*runTestForClass{arg what, recursive = false;
		var testClasses;
		testClasses = what.asArray;

		if(recursive, {
			what.allSubclasses.do({arg subclass;
				testClasses = testClasses.add(subclass);
			});
		});
		testClasses.do({arg testClass;
			super.runTestClassForClass(testClass);
		})
	}

	setUp{
		"Setting up a VTMTest".postln;
	}

	tearDown{
		"Tearing down a VTMTest".postln;

		//This checks if any Responders have not been removed.
		if(this.class.checkFreeingResponderFuncs, {
			if(AbstractResponderFunc.allFuncProxies.isEmpty.not, {
				//and this is not listening to a VTM discovery port, indicating that it
				//is a VTM responder that is not to be freed.
				if(AbstractResponderFunc.allFuncProxies.includesKey('OSC unmatched') and: {
					AbstractResponderFunc.allFuncProxies['OSC unmatched'].every({arg resp;
						resp.recvPort != VTMLocalNetworkNode.discoveryBroadcastPort
					})
				}, {
					this.failed(currentMethod,
						"Some AbstractResponderFunc proxies were not freed after test.:\n\t:%".format(
							AbstractResponderFunc.allFuncProxies).postln;
					);
					if(this.class.forceFreeingResponderFuncs, {
						AbstractResponderFunc.allFuncProxies.do({arg funcs;
							funcs.do(_.free)
						});
					});

				});
			});
		});
	}


	run { | reset = true, report = true|
		var function;
		if(reset) { this.class.reset };
		if(report) { ("RUNNING UNIT TEST" + this).inform };
		this.class.forkIfNeeded {
			this.findTestMethods.do { |method|
				this.setUp;
				currentMethod = method;
				if(this.class.reportAllErrors, {
					try{
						this.perform(method.name);
					} {|err|
						this.failed(method,
							"ERROR: during test: \n\t%".format(err.errorString)
						)
					};
				}, {
					this.perform(method.name);
				});

				this.tearDown;
			};
			if(report) { this.class.report };
			nil
		};
	}

	*generateRandomSymbol{arg params;
		^this.generateRandomString(params).asSymbol;
	}

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
			result = result.addAll("/%".format(this.generateRandomString));
		}).asSymbol;
		^result;
	}

	*generateRandomString{arg params;
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

	*generateRandomBoolean {arg params;
		var chance = 0.5;
		if(params.notNil and: {params.isKindOf(Dictionary)}, {
			chance = params[\chance] ? 0.5;
		});
		^chance.coin;
	}

	*generateRandomInteger{arg params;
		^this.generateRandomDecimal(params).asInteger;
	}

	*generateRandomDecimal{arg params;
		var minVal = -2147483648.0;//32 bits random
		var maxVal = 2147483647.0;
		if(params.notNil, {
			minVal = params[\minVal] ? minVal;
			maxVal = params[\maxVal] ? maxVal;
		});
		^rrand(minVal, maxVal);
	}

	*generateRandomDictionary{arg params;
		var result;
		var size, minSize = 1, maxSize = 15;
		var dataTypes;
		var dictClass = IdentityDictionary;
		if(params.notNil, {
			size = params[\size];
			minSize = params[\minSize] ? minSize;
			maxSize = params[\maxSize] ? maxSize;
			dictClass = params[\classSymbol].asClass ? dictClass;
			dataTypes = params[\dataTypes];
		});
		if(size.isNil, {
			size = rrand(minSize, maxSize);
		});
		result = dictClass.new(size);
		if(dataTypes.isNil, {
			dataTypes = { [\Boolean, \Integer, \Decimal, \String].choose } ! size;
		});
		size.do({arg i;
			result.put(
				this.generateRandomSymbol,
				this.perform("generateRandom%".format(dataTypes[i]).asSymbol)
			);
		});
		^result;
	}

	*makeUML{arg outputFilepath;
		var path = outputFilepath ?? {"~/Desktop/test.dot".standardizePath};
		var dotfile;
		var vtmClasses;
		var escapeChars = {arg str;
			var result = str;
			[$\\, $", $>, $<, $|].do({arg it;
				result = result.escapeChar(it);
			});
			result;
		};

		var getInstVarDotString = {arg cls;
			var result;
			cls.classVarNames.do({arg instVarName;
				result = result ++ "% %\\l".format(
					"*", //getset
					instVarName
				);
			});
			// cls.privateInstVars.do({arg instVarName;
			// 	result = result ++ "% %\\l".format(
			// 		"-", //getset
			// 		instVarName
			// 	);
			// });

			cls.instVars.do({arg instVarName;
				result = result ++ "% %\\l".format(
					"-", //getset
					instVarName
				);
			});
			result;
		};
		var getMethodDotStringForClass = {arg cls;
			var result;
			cls.class.methods.do({arg it;
				var args = "";
				if(it.argNames.size > 1, {
					args = it.argumentString;
				});
				result = result ++ "* %( % )\\l".format(
					escapeChars.value(it.name.asString),
					escapeChars.value(args)
				);
				escapeChars.value(result);
			});

			cls.methods.reject({arg it;
				it.hasCorrespondingInstVar;
			}).do({arg it;
				var args = "";
				if(it.argNames.size > 1, {
					args = it.argumentString;
				});
				result = result ++ "%( % )%%\\l".format(
					escapeChars.value(it.name.asString),
					escapeChars.value(args),
					if(it.isOverridingSuperclass, {
						" @overrides %".format(it.findOverriddenMethod.ownerClass.name)
					}, {
						"";
					}),
					if(it.isExtensionMethod, {
						" @extension"
					}, {
						"";
					})
				);
			});
			result;
		};

		vtmClasses = Class.allClasses.select({arg item; "VTM".matchRegexp(item.name.asString)}).reject(_.isMetaClass);
		{
			var vtmFolder = PathName("~/github/blacksound/VTM/Classes".standardizePath);
			var vtmTestFolder = PathName("~/github/blacksound/VTMTesting/Classes".standardizePath);
			vtmClasses = Class.allClasses.select({arg class;
				var path = PathName(class.filenameSymbol.asString);
				vtmFolder.fullPath.matchRegexp(path.fullPath.asString) or: {
					vtmTestFolder.fullPath.matchRegexp(path.fullPath.asString)
				};
			}).reject(_.isMetaClass);
		}.value;

		dotfile = File.new(path, "w");


		if(dotfile.isOpen.not, {
			"Failed opening file".error.throw;
		});
		dotfile << "digraph G {" << "\n";
		dotfile << "\tfontname = \"Bitstream Vera Sans\"" << "\n";
		dotfile << "\tfontsize = 8" << "\n";
		dotfile << "\n";
		dotfile << "\trankdir = \"LR\"\n";
		dotfile << "\tnode [" << "\n";
		dotfile << "\t\tfontname = \"Bitstream Vera Sans\"" << "\n";
		dotfile << "\t\tfontsize = 8" << "\n";
		dotfile << "\t\tshape = \"record\"" << "\n";
		dotfile << "\t]" << "\n";
		dotfile << "\n";
		dotfile << "\tedge [" << "\n";
		dotfile << "\t\tfontname = \"Bitstream Vera Sans\"" << "\n";
		dotfile << "\t\tfontsize = 8" << "\n";
		dotfile << "\t]" << "\n";
		dotfile << "\n";
		vtmClasses.do({arg class;
			{arg cls;
				dotfile << "\t% [".format(class.name) << "\n";
				dotfile << "\t\tlabel = ";
				dotfile << "\"{%|%|%}\"".format(
					class.name,
					getInstVarDotString.value(cls),
					getMethodDotStringForClass.value(cls)
				);
				dotfile << "\n";
				dotfile << "\t]" << "\n";
			}.value(class);
		});
		//draw connections
		vtmClasses.do({arg class;
			if(vtmClasses.includes(class.superclass), {
				dotfile << "% -> % [dir=back]\n".format(class.superclass.name, class.name).postln;
			});
		});

		dotfile << "}" << "\n";

		"DONE".postln;
		dotfile.close();
		while({dotfile.isOpen}, {
			".".post;
		});
		"/usr/local/bin/dot % -Tpdf -O".format(path).unixCmd({arg returnCode ...args;
			var imgPath;
			if(returnCode != 0, {
				"Error running 'dot' program".warn;
			}, {
				imgPath = path ++ ".pdf";
				"open %".format(imgPath).postln.unixCmd;

			});
		});
	}
}
