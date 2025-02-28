package jarvis.atoms;

import jarvis.interpreter.JarvisInterpreter;

import java.util.ArrayList;

/*
 * Cette classe implante l'objet de base.
 * L'interpr�teur comprend un objet comme
 * une simple liste de valeurs.
 * L'organisation de ses donn�es est sp�cifi�e
 * par la classe. Celle-ci peut �tre retrouv�e
 * via le lien classReference.
 */
public class ObjectAtom extends AbstractAtom {

	/*
	 * Si vous ajoutez des champs � JarvisClass
	 * ces constantes doivent le refl�ter.
	 * Elles sont utilis�es pour retrouver
	 * les membres d'une classe.
	 * 
	 */
	public static final int ATTRIBUTE_FIELD =0;
	public static final int METHOD_FIELD =1;
	public static final int SUPER_FIELD =2;
	/*
	 * R�f�rence � la classe de cet objet.
	 */
	private ObjectAtom classReference;
	private ArrayList<AbstractAtom> values;
	
	//R�f�rence utile pour faire des reverse lookup
	private JarvisInterpreter ji;

	

	// Constructeur d'objet g�n�rique
	// Utilis� comme raccourci par les fonctions tricheuses.
	public ObjectAtom(ObjectAtom theClass, ArrayList<AbstractAtom> vals,JarvisInterpreter ji) {

		classReference = theClass;

		values = new ArrayList<AbstractAtom>();
		values.addAll(vals);
		
		this.ji=ji;
	}
	
	@Override
	public AbstractAtom interpretNoPut(JarvisInterpreter ji) {	
		return this;
	}

	public ObjectAtom getJarvisClass() {
		return classReference;
	}
	
	
	//Cas sp�cial o� le selecteur n'est pas encore encapsul� dans un atome
	//Support� pour all�ger la syntaxe.
	public AbstractAtom message(String selector) {
		
		return message(new StringAtom(selector));
		
	}
	
    //H�RITAGE
	//VARIABLESCLASSE
	/*
	 * Algorithme de gestion des messages.
	 * Ce bout de code a pour responsabilit� de d�terminer si le message
	 * concerne un attribut ou une m�thode. 
	 * Pour implanter l'h�ritage, cet algorithme doit n�cessairement �tre modifi�.
	 */	
	public AbstractAtom message(AbstractAtom selector) {


		//Va chercher les attributs
		ListAtom members =  classReference.getAllAttributes();

		//V�rifie si c'est un attribut
		int pos = members.find(selector);



		if (pos != -1) {
			//C'est un attribut
			return values.get(pos);


		} else {
			AbstractAtom res = classReference.findMethod(selector);
			if (res != null) {
				//C'est une m�thode
				return res;
			} else {
				//Erreur
				throw new IllegalArgumentException("ObjectAtom: no such method or attribute: "+selector.makeKey());
			}
		}
	}

	private AbstractAtom findMethod(AbstractAtom selector) {
		// pas un attribut...
		// Va chercher les m�thodes
		DictionnaryAtom methods = (DictionnaryAtom) values
				.get(METHOD_FIELD);

		// Cherche dans le dictionnaire
		AbstractAtom res = methods.get(selector.makeKey());
		//super...?
		if (res == null) {
			ListAtom superRefList = (ListAtom) values.get(SUPER_FIELD);
			// if the list is empty, we have no superclass
			if (superRefList != null && !superRefList.isEmpty()) {
				ObjectAtom superRef = (ObjectAtom) superRefList.get(0); // Extract the superclass from the ListAtom
				if (superRef != null) {
					res = superRef.findMethod(selector);
				}
			}
		}

		return res;
	}


	public void setClass(ObjectAtom theClass) {
		classReference = theClass;
	}
	
	
	public ListAtom getAllAttributes() {
	    ListAtom allAttributes = new ListAtom();
	    
	    if (values.size() > SUPER_FIELD) {
	        AbstractAtom superField = values.get(SUPER_FIELD);
	        if (superField instanceof ListAtom) {  
	            ListAtom superClasses = (ListAtom) superField;
	            if (!superClasses.isEmpty()) {
	                AbstractAtom superClassAtom = superClasses.get(0);
	                if (superClassAtom instanceof ObjectAtom && !superClassAtom.equals(ji.getEnvironment().get("Object"))) {
	                    ObjectAtom superClass = (ObjectAtom) superClassAtom;
	                    ListAtom superAttributes = superClass.getAllAttributes();
	                    for (int j = 0; j < superAttributes.size(); j++) {
	                        AbstractAtom superAttr = superAttributes.get(j);
	                        boolean exists = false;
	                        for (int k = 0; k < allAttributes.size(); k++) {
	                            if (allAttributes.get(k).makeKey().equals(superAttr.makeKey())) {
	                                exists = true;
	                                break;
	                            }
	                        }
	                        if (!exists) {
	                            allAttributes.add(superAttr);
	                        }
	                    }
	                }
	            }
	        }
	    }
	    
	    AbstractAtom attributesField = values.get(ATTRIBUTE_FIELD);
	    if (attributesField instanceof ListAtom) { 
	        ListAtom currentAttributes = (ListAtom) attributesField;
	        for (int i = 0; i < currentAttributes.size(); i++) {
	            AbstractAtom currentAttr = currentAttributes.get(i);
	            int existingIndex = -1;
	            for (int k = 0; k < allAttributes.size(); k++) {
	                if (allAttributes.get(k).makeKey().equals(currentAttr.makeKey())) {
	                    existingIndex = k;
	                    break;
	                }
	            }
	            if (existingIndex != -1) {
	                allAttributes.set(existingIndex, currentAttr); 
	            } else {
	                allAttributes.add(currentAttr);
	            }
	        }
	    }
	    
	    return allAttributes;
	}
	
	//Surtout utile pour l'affichage dans ce cas-ci...
	@Override
	public String makeKey() {
		String s="";
		int i=0;
		
		s += "\""+ji.getEnvironment().reverseLookup(classReference)+"\":";
		
		for (AbstractAtom atom : values) {
			
			s+=" "+((ListAtom)classReference.values.get(0)).get(i).makeKey()+":";
			if(atom instanceof ClosureAtom)
			{
				s+=atom;
			}
			else
			{
				s+=atom.makeKey();
			}
			
			i++;
		}
		
		return s;
	}	
	
	public String findClassName(JarvisInterpreter ji){
		
		return ji.getEnvironment().reverseLookup(classReference);
		
	}

	  public ArrayList<AbstractAtom> getValues() { // Retourner le type concret
	        return values;
	    }

}
