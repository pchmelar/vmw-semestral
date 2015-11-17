package cz.cvut.fit.mdw.fileserver.support;

import java.util.Date;

public class AppFile {
    private String name;
    private String ext;
    private Long size;
    private Date date;

    public AppFile() {
    }

    public AppFile(String name, String ext, Long size, Date date) {
        this.name = name;
        this.ext = ext;
        this.size = size;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
    
}
