TestVTMPreset : VTMUnitTest {

	*generateRandomAttribute{arg key, params;
		var result;
		result = super.generateRandomAttribute(key, params);
		switch(key,
			\values, {
				var parameterDeclarationArray = params;
				if(parameterDeclarationArray.isNil, {
					[
						\boolean,
						\timecode,
						\string,
						\integer,
						\decimal,
						\dictionary,
						\list,
						\array,
						\schema,
						\tuple
					].do({arg paramType;
						result = result.addAll([
							this.generateRandomSymbol,
							TestVTMValue.testclassForType(paramType).generateRandomValue
						]);
					});
				}, {
					parameterDeclarationArray.do({arg parameterDeclaration;
						if(parameterDeclaration.isKindOf(Dictionary).not, {
							parameterDeclaration = (
								name: this.generateRandomSymbol,
								type: parameterDeclaration
							);
						});
						result = result.addAll([
							parameterDeclaration[\name] ? this.generateRandomSymbol,
							TestVTMValue.testclassForType(parameterDeclaration[\type]).generateRandomValue(
								parameterDeclaration
							)
						]);
					});
				});
			}
		);
		^result;
	}
}
