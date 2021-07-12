package org.darccona.database;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

public interface NoteRepository extends CrudRepository<NoteEntity, Long> {

    NoteEntity findById(long id);
    List<NoteEntity> findByFolder_UserIs(UserEntity user);
    List<NoteEntity> findByNameContaining(String title);
    List<NoteEntity> findByRecordContaining(String title);

    List<NoteEntity> findByNameContainingAndFolder_UserIs(String title, UserEntity user);
    List<NoteEntity> findByRecordContainingAndFolder_UserIs(String title, UserEntity user);

    NoteEntity findByStandingAndFolder_UserIs(boolean standing, UserEntity user);
}