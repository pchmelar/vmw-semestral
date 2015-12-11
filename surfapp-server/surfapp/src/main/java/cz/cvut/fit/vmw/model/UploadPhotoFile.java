package cz.cvut.fit.vmw.model;

import javax.ws.rs.FormParam;
import org.jboss.resteasy.annotations.providers.multipart.PartType;

public class UploadPhotoFile {

    private byte[] data;

    public UploadPhotoFile() {
    }

    public byte[] getData() {
        return data;
    }

    @FormParam("photo")
    @PartType("application/octet-stream")
    public void setData(byte[] data) {
        this.data = data;
    }

}
