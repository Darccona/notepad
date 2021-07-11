package org.darccona.database;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

@Entity
public class FolderEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "STANDING")
    private boolean standing;

    public FolderEntity() { }

    public FolderEntity(String name) {
        this.name = name;
        this.standing = false;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<NoteEntity> note = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user", nullable = false)
    private UserEntity user;

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setStanding(boolean standing) {
        this.standing = standing;
    }
    public boolean getStanding() {
        return standing;
    }

    public void setNote(Set<NoteEntity> note) {
        this.note = note;
    }
    public Set<NoteEntity> getNote() {
        return note;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
    public UserEntity getUser() {
        return user;
    }

    public static Comparator<FolderEntity> COMPARE_BY_NAME = new Comparator<FolderEntity>() {
        public int compare(FolderEntity one, FolderEntity other) {
            return one.name.compareTo(other.name);
        }
    };

    public void removeNote(NoteEntity n) {
        getNote().remove(n);
    }
}
