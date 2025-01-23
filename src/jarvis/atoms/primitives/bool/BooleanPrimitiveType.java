package jarvis.atoms.primitives.bool;

import java.util.ArrayList;

import jarvis.atoms.AbstractAtom;
import jarvis.atoms.BoolAtom;
import jarvis.atoms.ObjectAtom;
import jarvis.interpreter.JarvisInterpreter;

public class BooleanPrimitiveType extends BooleanPrimitiveOperation {

	@Override
	public String makeKey() {

		return "BooleanPrimitiveType";
	}

	@Override
	protected AbstractAtom calculateResult(JarvisInterpreter ji, BoolAtom val1, BoolAtom val2) {
	
		throw new UnsupportedOperationException("BooleanPrimitiveType a besoin d'un seul argument.");
	}

	@Override
	protected AbstractAtom execute(JarvisInterpreter ji, ObjectAtom self) {
	
		boolean bool1 = ((BoolAtom) self.message("value")).getValue();

		ArrayList<AbstractAtom> data = new ArrayList<AbstractAtom>();
		data.add(new BoolAtom(bool1));

		return new ObjectAtom(((ObjectAtom) ji.getEnvironment().get("bool")), data, ji);
	}
}