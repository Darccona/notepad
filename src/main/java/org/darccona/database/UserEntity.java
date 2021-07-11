package org.darccona.database;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class UserEntity {

    @Id

    @Column(name = "NAME")
    private String name;

    @Column(name = "PASSWORD")
    private String password;

    public UserEntity() { }

    public UserEntity(String name, String password) {
        this.name = name;
        this.password = password;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<FolderEntity> folder = new HashSet<>();

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getPassword() {
        return password;
    }

    public void setFolder(Set<FolderEntity> folder) {
        this.folder = folder;
    }
    public Set<FolderEntity> getFolder() {
        return folder;
    }

    public void removeFolder(FolderEntity f) {
        getFolder().remove(f);
    }
}
