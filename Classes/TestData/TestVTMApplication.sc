/*
Test setup with three applications running on the same computer.
*/
TestVTMApplication : TestVTMContext {

	test_StartApplication{}

	test_InitModuleHost{}

	test_InitSceneOwner{}

	test_InitHardwareSetup{}

	test_FilePaths{
		//Project paths, global paths, and project paths
	}

	test_DeclaredLibrary{
			var result, cond, modEnv;
			var moduleDefinition;
			var library, app, module;
			result = IdentitySet.new;
			cond = false;
			modEnv = Environment.make{
				~init = {|self|
					result.add(\init);
				};
				~free = {
					result.add(\free);
				}
			};
			moduleDefinition = VTMContextDefinition(modEnv, 'testModule');
			library = VTMDefinitionLibrary.newWithDefinitions([moduleDefinition]);
			app = VTMApplication(
				'myTestApp', 
				declaration: (
					definitionLibrary: library
				), 
				definition: Environment.make{
					~modules = [
						\myTestModule -> (definition: \testModule)
					];
				}, 
				onInit: {
					cond = true;
				}
			);
			this.wait(cond);
			cond = false;
			this.assert(
				app.modules.hasItemNamed(\myTestModule),
				"A module with the right name exists in test app"
			);
			module = app.modules[\myTestModule];
			this.assert(
				module.notNil,
				"Found the module in test app"
			);
			this.assert(
				app.findDefinition(\testModule).notNil,
				"Has context def from inline library"
			);
			// this.assertEquals(app.modules.items.names, ['test']);
			app.on(\didFree, {
				cond = true;
			});
			app.free;
			this.wait(cond);
			this.assertEquals(
				result,
				IdentitySet[\init, \free],
				"Ran the module def init and free functions"
			);
	}

	//test_RegisterNetworkApplicationsOnStartup{
	//	var result, aaa, bbb, ccc;
	//	var cond = false;
	//	aaa = VTMApplication.new('aaa', onRunning: {cond = true;});
	//	this.wait(cond, maxTime: 1.0);
	//	cond = false;
	//	bbb = VTMApplication.new('bbb', onRunning: {cond = true;});
	//	this.wait(cond, maxTime: 1.0);
	//	cond = false;
	//	ccc = VTMApplication.new('ccc', onRunning: {cond = true;});
	//	this.wait(cond, maxTime: 1.0);
	//	cond = false;
	//	//The application should now have registered eachother as application proxies.
	//	result = nil;
	//	result = result.add(aaa.network.applicationProxies.collect(_.name).includesAll([\bbb, \ccc]));
	//	result = result.add(bbb.network.applicationProxies.collect(_.name).includesAll([\aaa, \ccc]));
	//	result = result.add(ccc.network.applicationProxies.collect(_.name).includesAll([\bbb, \aaa]));
	//	cond = {
	//		[
	//			aaa.network.applicationProxies.collect(_.name).includesAll([\bbb, \ccc]),
	//			bbb.network.applicationProxies.collect(_.name).includesAll([\aaa, \ccc]),
	//			ccc.network.applicationProxies.collect(_.name).includesAll([\bbb, \aaa])
	//		].every({arg it; it;});
	//	};
	//	this.wait(
	//		cond,
	//		"Applications did not register eachother correctly",
	//		1.0
	//	);

	//	aaa.quit;
	//	cond = {
	//		[
	//			bbb.network.applicationProxies.collect(_.name).matchItem(\aaa).not,
	//			ccc.network.applicationProxies.collect(_.name).matchItem(\aaa).not
	//		].every({arg item; item});
	//	};
	//	this.wait(
	//		cond,
	//		"Application 'bbb' and 'ccc' did get got notified of 'aaa' quit and removed its ApplicationProxy for it.",
	//		maxTime: 1.0
	//	);

	//	bbb.quit;
	//	ccc.quit;
	//}

	test_LoadApplication{}

	test_StartAppFromFolder{
		//App is defined in the folder aaa.
	}

}
