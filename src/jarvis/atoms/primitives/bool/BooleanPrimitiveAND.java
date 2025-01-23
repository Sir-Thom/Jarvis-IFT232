package jarvis.atoms.primitives.bool;

import java.util.ArrayList;

import jarvis.atoms.AbstractAtom;
import jarvis.atoms.BoolAtom;
import jarvis.atoms.ObjectAtom;
import jarvis.interpreter.JarvisInterpreter;

public class BooleanPrimitiveAND extends BooleanPrimitiveOperation {

	@Override
	public String makeKey() {

		return "BooleanPrimitiveAND";
	}

	@Override
	protected AbstractAtom calculateResult(JarvisInterpreter ji, BoolAtom val1, BoolAtom val2) {
	
		boolean result = val1.getValue() && val2.getValue();


		ArrayList<AbstractAtom> data = new ArrayList<AbstractAtom>();
		data.add(new BoolAtom(result));

		return new ObjectAtom(((ObjectAtom) ji.getEnvironment().get("bool")), data, ji);
	}

}
