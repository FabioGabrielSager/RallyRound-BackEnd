package org.fs.rallyroundbackend.repository;

import org.fs.rallyroundbackend.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {

    @Query(
            "SELECT d FROM DocumentEntity d JOIN DocumentContentTypeEntity dct ON d.contentType=dct " +
                    "WHERE d.title=:title AND dct.type=:contentType"
    )
    Optional<DocumentEntity> findByTitleAndContentType(String title, String contentType);
}
