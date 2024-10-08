package org.fs.rallyroundbackend.entity.users.participant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fs.rallyroundbackend.entity.events.ActivityEntity;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "participant_favorite_activities")
public class ParticipantFavoriteActivityEntity implements Comparable<ParticipantFavoriteActivityEntity> {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "participant_id")
    private ParticipantEntity participant;

    @ManyToOne
    @JoinColumn(name = "activity_id")
    @Cascade({CascadeType.PERSIST, CascadeType.MERGE})
    private ActivityEntity activity;

    @Column(name = "favorite_order", columnDefinition = "SMALLINT", nullable = false)
    private int favoriteOrder;

    @Override
    public int compareTo(@NotNull ParticipantFavoriteActivityEntity o) {
        return Integer.compare(this.favoriteOrder, o.getFavoriteOrder());
    }
}
