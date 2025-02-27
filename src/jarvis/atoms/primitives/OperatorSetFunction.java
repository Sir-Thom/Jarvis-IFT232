package jarvis.atoms.primitives;

import jarvis.atoms.*;
import jarvis.interpreter.JarvisInterpreter;

public class OperatorSetFunction extends PrimitiveOperationAtom {

    @Override
    protected void init() {
        argCount = 2;
    }

    @Override
    protected AbstractAtom execute(JarvisInterpreter ji, ObjectAtom self) {    
        if (ji.getArgCount() < 2) {
            throw new IllegalArgumentException("La méthode 'set' nécessite deux arguments.");
        }
        AbstractAtom nomAttributAtom = ji.getArg();
        AbstractAtom nouvelleValeur = ji.getArg();

        if (!(nomAttributAtom instanceof StringAtom)) {
            throw new IllegalArgumentException("Le nom de l'attribut doit être une chaîne.");
        }
        String nomAttribut = ((StringAtom) nomAttributAtom).getValue();

        ListAtom attributs = self.getAllAttributes();
        int index = attributs.find(new StringAtom(nomAttribut));
        if (index == -1) {
            throw new IllegalArgumentException("Attribut '" + nomAttribut + "' introuvable.");
        }

        self.getValues().set(index, nouvelleValeur);
        return nouvelleValeur;
    }

    @Override
    public String makeKey() {
        return "OperatorSetFunction";
    }
}