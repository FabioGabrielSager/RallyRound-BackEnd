package org.fs.rallyroundbackend.repository;

import org.fs.rallyroundbackend.RallyRoundBackEndApplication;
import org.fs.rallyroundbackend.entity.events.ActivityEntity;
import org.fs.rallyroundbackend.repository.ActivityRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ActivityRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ActivityRepository activityRepository;

    @Test
    public void getMatches_matchingTheStartOfTheActivityName() {
        entityManager.persist(ActivityEntity.builder().name("futbol").build());
        entityManager.persist(ActivityEntity.builder().name("básquet").build());
        entityManager.flush();

        List<ActivityEntity> result = activityRepository.findMatchesByName("fut");

        assertEquals(1, result.size());
        assertEquals("futbol", result.get(0).getName());
    }

    @Test
    public void getMatches_matchingTheEndOfTheActivityName() {
        entityManager.persist(ActivityEntity.builder().name("futbol").build());
        entityManager.persist(ActivityEntity.builder().name("básquet").build());
        entityManager.flush();

        List<ActivityEntity> result = activityRepository.findMatchesByName("bol");

        assertEquals(1, result.size());
        assertEquals("futbol", result.get(0).getName());
    }

    @Test
    public void getMatches_matchingTheContentOfTheActivityName() {
        entityManager.persist(ActivityEntity.builder().name("futbol").build());
        entityManager.persist(ActivityEntity.builder().name("básquet").build());
        entityManager.flush();

        List<ActivityEntity> result = activityRepository.findMatchesByName("utb");

        assertEquals(1, result.size());
        assertEquals("futbol", result.get(0).getName());
    }

    @Test
    public void getMatches_matchingWithMoreThanOneAcitivity() {
        entityManager.persist(ActivityEntity.builder().name("futbol").build());
        entityManager.persist(ActivityEntity.builder().name("básquet").build());
        entityManager.flush();

        List<ActivityEntity> result = activityRepository.findMatchesByName("u");

        assertEquals(2, result.size());
    }
}
