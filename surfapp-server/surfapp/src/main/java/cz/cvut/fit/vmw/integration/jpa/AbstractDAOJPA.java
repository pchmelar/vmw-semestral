package cz.cvut.fit.vmw.integration.jpa;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author jan
 */
public abstract class AbstractDAOJPA {

    @PersistenceContext
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }
}
