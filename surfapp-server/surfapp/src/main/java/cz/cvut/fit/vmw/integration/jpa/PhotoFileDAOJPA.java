package cz.cvut.fit.vmw.integration.jpa;

import cz.cvut.fit.vmw.integration.PhotoFileDAO;
import cz.cvut.fit.vmw.model.PhotoFile;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.persistence.TypedQuery;

@Dependent
public class PhotoFileDAOJPA extends AbstractDAOJPA implements PhotoFileDAO {

    @Override
    public PhotoFile find(Integer id) {
        return getEntityManager().find(PhotoFile.class, id);
    }

    @Override
    public List<PhotoFile> findAll() {
        TypedQuery<PhotoFile> q = getEntityManager().createNamedQuery("PhotoFile.findAll", PhotoFile.class);
        return q.getResultList();
    }

    @Override
    public boolean create(PhotoFile photo) {
        getEntityManager().persist(photo);
        getEntityManager().flush();
        return true;
    }

}
