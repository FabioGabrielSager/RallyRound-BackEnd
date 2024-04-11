package org.fs.rallyroundbackend.entity.users.participant;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "email_verification_tokens")
public class EmailVerificationTokenEntity {
    private static final int EXPIRATION = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private int code;

    @OneToOne(targetEntity = ParticipantEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private ParticipantEntity user;

    private Date expiryDate;

    public static Date calculateExpiryDate(Locale locale) {
        Calendar cal = Calendar.getInstance(locale);
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, EmailVerificationTokenEntity.EXPIRATION);
        return new Date(cal.getTime().getTime());
    }

    public boolean isExpired() {
        Date currentDate = new Date();
        return currentDate.after(this.expiryDate);
    }

    public long getTimeUntilExpiration() {
        long currentTimeMillis = System.currentTimeMillis();
        long expiryTimeMillis = expiryDate.getTime();
        long timeUntilExpirationMillis = expiryTimeMillis - currentTimeMillis;
        long timeUntilExpirationMinutes = timeUntilExpirationMillis / (1000 * 60); // Convert milliseconds to minutes
        return timeUntilExpirationMinutes;
    }
}
