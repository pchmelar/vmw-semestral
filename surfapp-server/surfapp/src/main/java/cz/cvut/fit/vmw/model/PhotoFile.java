/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.vmw.model;

import com.google.gson.annotations.Expose;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author jan
 */
@Entity
@Table(name = "photo_file")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PhotoFile.findAll", query = "SELECT p FROM PhotoFile p ORDER BY p.id ASC"),
    @NamedQuery(name = "PhotoFile.findById", query = "SELECT p FROM PhotoFile p WHERE p.id = :id"),
    @NamedQuery(name = "PhotoFile.findByCreateDate", query = "SELECT p FROM PhotoFile p WHERE p.createDate = :createDate")})
public class PhotoFile implements Serializable, Comparable<PhotoFile> {

    private static final long serialVersionUID = 1L;
    @Expose
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Column(name = "data")
    private byte[] data;
    @Expose
    @Basic(optional = false)
    @NotNull
    @Column(name = "create_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;
    @Expose
    @Transient
    private Double similarity;

    public PhotoFile() {
    }

    public PhotoFile(Integer id) {
        this.id = id;
    }

    public PhotoFile(Integer id, byte[] data, Date createDate) {
        this.id = id;
        this.data = data;
        this.createDate = createDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(Double similarity) {
        this.similarity = similarity;
    }
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PhotoFile)) {
            return false;
        }
        PhotoFile other = (PhotoFile) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cz.cvut.fit.vmw.model.PhotoFile[ id=" + id + " ]";
    }

    @Override
    public int compareTo(PhotoFile o) {
        return o.getSimilarity().compareTo(this.similarity);
    }
    
}
