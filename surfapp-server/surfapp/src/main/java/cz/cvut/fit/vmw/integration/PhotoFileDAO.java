package cz.cvut.fit.vmw.integration;

import cz.cvut.fit.vmw.model.PhotoFile;
import java.util.List;

public interface PhotoFileDAO {
    PhotoFile find(Integer id);
    
    List<PhotoFile> findAll();
    
    boolean create(PhotoFile photo);
    
}
