package org.darccona.database;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FolderRepository extends CrudRepository<FolderEntity, Long> {

    FolderEntity findById(long id);
    List<FolderEntity> findByUser(UserEntity user);
    FolderEntity findByUserAndStanding(UserEntity user, boolean standing);
    List<FolderEntity> findByUserAndNameContaining(UserEntity user, String title);
}