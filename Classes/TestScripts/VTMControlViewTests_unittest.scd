{|test|
	var cond = Condition.new;
	var window;
	var testSelectorView;
	var testMenu;
	var startTestButton, stopTestButton, doAllTestsButton;
	var closeTestWindowButton;
	var testView;
	var currentTestView;
	var startTest;
	var buildTestView;
	var stopTest;
	var testSets;
	var makeTestName;

	///////////--Data//////////////////////////////////////////
	testSets = [
			(mode: \attribute, type: \integer, viewType: \textfield),
			(mode: \attribute, type: \integer, viewType: \slider),
			(mode: \attribute, type: \integer, viewType: \numbox),
	];

	///////////--Functions ////////////////////////////////////
	makeTestName = {|it|
		var result = "% type: % mode: %".format(
			it[\viewType], it[\type], it[\mode]
		);
		result;
	};

	buildTestView = {|testIndex|
		var result;
		var testSet = testSets[testIndex];
		var testName = makeTestName.value(testSet);
		var controlView;
		var control = VTMControl.makeFromDescription('test', (
			type: testSet[\type],
			mode: testSet[\mode]
		));
		if(control.isNil, {
			test.failed(nil,
				"Failed to make control object: %".format(testSet)
			);
		}, {
			control.action_({|v|
				"Changed value: %[%]".format(v.value, v.class).postln;
			});
			controlView = control.makeView(settings: (viewType: testSet[\viewType]));
			controlView.addAction({{control.free}.defer}, \onClose);
			if(controlView.notNil, {
				result = View()
				.layout_(
					VLayout(
						StaticText().string_(testName),
						nil,
						controlView
					)
				)
				.background_(Color.green);
			}, {
				test.failed(nil,
					"Failed to make control view: %".format(testSet)
				);
			});
		});
		result;
	};

	startTest = {|testIndex|
		"Starting test at index: %".format(testIndex).postln;
		"\t%".format(testSets[testIndex]).postln;
		{
			if(currentTestView.notNil, {
				currentTestView.remove;
			});
			currentTestView = buildTestView.value(testIndex);
			testView.layout_(
				VLayout(currentTestView)
			);
		}.defer;
	};

	stopTest = {
		if(currentTestView.notNil, {
			currentTestView.remove;
			currentTestView = nil;
		});
	};

	///////////--Windows, Views, Buttons, menus etc.///////////

	window = Window.new("Testing VTMControlView");
	testMenu = PopUpMenu()
	.items_( testSets.collect(makeTestName.value(_)) );

	startTestButton = Button()
	.states_([["Start test"]])
	.action_({ startTest.value( testMenu.value ) });

	stopTestButton = Button()
	.states_([["Stop current test"]])
	.action_({ "Stop current test".postln; stopTest.value; });

	doAllTestsButton = Button()
	.states_([["Do all tests"]])
	.action_({"Do all tests".postln;});

	closeTestWindowButton = Button()
	.states_([["Close test window"]])
	.action_({ window.close; });

	//View composition
	testSelectorView = View()
	.layout_(
		VLayout(
			StaticText().string_("Select test"),
			testMenu,
			startTestButton,
			stopTestButton,
			doAllTestsButton,
			nil,
			closeTestWindowButton
		)
	)
	.minSize_(Size(300,400))
	.background_(Color.blue);

	testView = View()
	.minSize_(Size(300,400))
	.background_(Color.red);

	window.layout_(
		HLayout( testSelectorView, testView )
	);

	window.front;

	// {
	// 	var val = VTMIntegerValue();
	// 	var view, win;
	// 	var ctrlView = val.makeView();
	// 	var str;
	// 	win = Window.new();
	// 	val.action = {|v|
	// 		"integer value: %".format(v.value).postln;
	// 	};
	// 	str = "Should show text field control view.\n" ++
	// 		"Should be able to change the value.\n";
	// 	view = View().layout_(
	// 		VLayout(
	// 			StaticText().string_(str),
	// 			ctrlView,
	// 			nil,
	// 			Button()
	// 			.states_([["End test"]])
	// 			.action_({
	// 				cond.test = true;
	// 				cond.signal;
	// 				{win.close;}.defer;
	// 			})
	// 		)
	// 	);
	// 	win.layout_(HLayout(view));
	// 	win.front;
	// }.value;
	// cond.wait;
	test.passed(nil, "super love");
}
