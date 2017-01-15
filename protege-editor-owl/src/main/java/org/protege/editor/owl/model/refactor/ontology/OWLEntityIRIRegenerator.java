package org.protege.editor.owl.model.refactor.ontology;

import com.google.common.base.Optional;
import org.protege.editor.core.Disposable;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.entity.CustomOWLEntityFactory;
import org.protege.editor.owl.model.entity.OWLEntityCreationException;
import org.protege.editor.owl.model.entity.OWLEntityFactory;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Aug 21, 2008<br><br>
 *
 * Takes an IRI and replaces the fragment/last path element with a generated ID
 * If no fragment exists, adds the ID directly
 *
 */
public class OWLEntityIRIRegenerator implements Disposable {

    private OWLEntityFactory fac;

    public OWLEntityIRIRegenerator(OWLModelManager mngr) {
        // regardless of how prefs are set up, we always want to generate an auto ID URI
        this.fac = new CustomOWLEntityFactory(mngr){
            protected boolean isFragmentAutoGenerated() {
                return true;
            }
        };
    }


    public IRI generateNewIRI(OWLEntity entity) {
        if(entity.isBuiltIn()) {
            return entity.getIRI();
        }
        IRI base = getBaseIRI(entity);
        String id = ""; // this is the "user given name" which will not be used
        OWLEntity newEntity = getEntity(entity, id, base);
        return newEntity.getIRI();
    }


    private OWLEntity getEntity(OWLEntity entity, String id, IRI base) {
        try {
            return fac.createOWLEntity(entity.getClass(), id, base).getOWLEntity();
        }
        catch (OWLEntityCreationException e) {
            throw new RuntimeException(e);
        }
    }


    private IRI getBaseIRI(OWLEntity entity) {
        Optional<String> remainder = entity.getIRI().getRemainder();
        if (remainder.isPresent()){
            IRI iri = entity.getIRI();
            int remainderIndex = iri.toString().lastIndexOf(remainder.get());
            if (remainderIndex != -1) {
                return IRI.create(iri.toString().substring(0, remainderIndex));
            }
        }
        return entity.getIRI();
    }


    public void dispose() throws Exception {
    }
}