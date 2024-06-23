package org.fs.rallyroundbackend.dto.participant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReportsByMotiveAndMonthAndYear {
    private int month;
    private int year;
    private long inappropriateBehaviorReportsCount;
    private long spammingReportsCount;
    private long harassmentReportCounts;
    private long offensiveLanguageReportCounts;
    private long fraudReportCounts;
    private long impersonationReportCounts;
    private long inappropriateContentReportCounts;
    private long absenteeismReportCounts;
    private long otherMotivesReportCounts;

    public ReportsByMotiveAndMonthAndYear(Long inappropriateBehaviorReportsCount, Long spammingReportsCount,
                                          Long harassmentReportCounts, Long offensiveLanguageReportCounts,
                                          Long fraudReportCounts, Long impersonationReportCounts,
                                          Long inappropriateContentReportCounts, Long absenteeismReportCounts,
                                          Long otherMotivesReportCounts) {
        this.inappropriateBehaviorReportsCount =
                inappropriateBehaviorReportsCount != null ? inappropriateBehaviorReportsCount : 0;
        this.spammingReportsCount = spammingReportsCount != null ? spammingReportsCount : 0;
        this.harassmentReportCounts = harassmentReportCounts != null ? harassmentReportCounts : 0;
        this.offensiveLanguageReportCounts = offensiveLanguageReportCounts != null ? offensiveLanguageReportCounts : 0;
        this.fraudReportCounts = fraudReportCounts != null ? fraudReportCounts : 0;
        this.impersonationReportCounts = impersonationReportCounts != null ? impersonationReportCounts : 0;
        this.inappropriateContentReportCounts = inappropriateContentReportCounts != null ? inappropriateContentReportCounts : 0;
        this.absenteeismReportCounts = absenteeismReportCounts != null ? absenteeismReportCounts : 0;
        this.otherMotivesReportCounts = otherMotivesReportCounts != null ? otherMotivesReportCounts : 0;
    }
}
