{|test|
	var cond = Condition.new;
	var testSets = [
		(
			valueType: \integer, viewType: \textfield
		)
	];
	"Testing VTMValueView".postln;

	{
		var val = VTMIntegerValue();
		var view, win;
		var ctrlView = val.makeView();
		var str;
		win = Window.new();
		val.action = {|v|
			"integer value: %".format(v.value).postln;
		};
		str = "Should show text field control view.\n" ++
			"Should be able to change the value.\n";
		view = View().layout_(
			VLayout(
				StaticText().string_(str),
				ctrlView,
				nil,
				Button()
				.states_([["End test"]])
				.action_({
					cond.test = true;
					cond.signal;
					{win.close;}.defer;
				})
			)
		);
		win.layout_(HLayout(view));
		win.front;
	}.value;
	cond.wait;
	test.passed(nil, "super love");
}
