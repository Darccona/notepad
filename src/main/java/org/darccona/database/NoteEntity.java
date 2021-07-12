package org.darccona.database;

import org.springframework.security.core.userdetails.User;

import org.darccona.database.UserEntity;
import javax.persistence.*;
import java.util.Date;

@Entity
public class NoteEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DATE")
    private Date date;

    @Column(name = "RECORD")
    private String record;

    @Column(name = "STANDING")
    private boolean standing;

    public NoteEntity() { }

    public NoteEntity(String name, Date date, String record) {
        this.name = name;
        this.date = date;
        this.record = record;
        this.standing = false;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "folder", nullable = false)
    private FolderEntity folder;

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    public Date getDate() {
        return date;
    }

    public void setRecord(String record) {
        this.record = record;
    }
    public String getRecord() {
        return record;
    }

    public void setStanding(boolean standing) {
        this.standing = standing;
    }
    public boolean getStanding() {
        return standing;
    }

    public void setFolder(FolderEntity folder) {
        this.folder = folder;
    }
    public FolderEntity getFolder() {
        return folder;
    }
}
