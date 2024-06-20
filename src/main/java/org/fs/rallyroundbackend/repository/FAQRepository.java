package org.fs.rallyroundbackend.repository;

import org.fs.rallyroundbackend.entity.FAQEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FAQRepository extends JpaRepository<FAQEntity, Long> {
}
