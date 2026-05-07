package com.examen.hei.service;

import com.examen.hei.model.*;
import com.examen.hei.enums.ActivityStatus;
import com.examen.hei.repository.DatabaseSimulator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class StatisticsService {

    private final DatabaseSimulator db;

    public StatisticsService(DatabaseSimulator db) {
        this.db = db;
    }

    public List<CollectivityLocalStatistics> getLocalStatistics(String collectivityId, LocalDate from, LocalDate to) {
        Collectivity collectivity = db.findCollectivityById(collectivityId)
                .orElseThrow(() -> new IllegalArgumentException("Collectivity not found: " + collectivityId));

        if (from == null || to == null) {
            throw new IllegalArgumentException("Both 'from' and 'to' dates are required");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("'from' date cannot be after 'to' date");
        }

        List<CollectivityLocalStatistics> statistics = new ArrayList<>();
        List<Member> members = collectivity.getMembers();

        List<MembershipFee> activeFees = db.getActiveMembershipFees();

        Map<String, Double> paymentsByMember = db.getMemberPaymentsByPeriod(collectivityId, from, to);

        for (Member member : members) {
            CollectivityLocalStatistics stat = new CollectivityLocalStatistics();

            MemberDescription memberDesc = new MemberDescription();
            memberDesc.setId(member.getId().getId());
            memberDesc.setFirstName(member.getFirstName());
            memberDesc.setLastName(member.getLastName());
            memberDesc.setEmail(member.getEmail());
            memberDesc.setOccupation(member.getOccupation() != null ? member.getOccupation().name() : null);
            stat.setMemberDescription(memberDesc);

            double earnedAmount = paymentsByMember.getOrDefault(member.getId().getId(), 0.0);
            stat.setEarnedAmount(earnedAmount);

            double unpaidAmount = calculateUnpaidAmount(member, activeFees, from, to);
            stat.setUnpaidAmount(unpaidAmount);

            //Calcul du taux d'assiduité
            double assiduityPercentage = calculateMemberAssiduity(member.getId().getId(), from, to);
            stat.setAssiduityPercentage(assiduityPercentage);

            statistics.add(stat);
        }

        return statistics;
    }

    private double calculateUnpaidAmount(Member member, List<MembershipFee> activeFees, LocalDate from, LocalDate to) {
        double unpaidAmount = 0.0;

        for (MembershipFee fee : activeFees) {
            if (fee.getEligibleFrom() != null && fee.getEligibleFrom().isAfter(to)) {
                continue;
            }

            int paymentsCount = db.getMemberPaymentsCountForFee(member.getId().getId(), fee.getId(), from, to);

            if (paymentsCount == 0) {
                unpaidAmount += fee.getAmount();
            }
        }

        return unpaidAmount;
    }

    //Calcul du taux d'assiduité d'un membre
    private double calculateMemberAssiduity(String memberId, LocalDate from, LocalDate to) {
        // Récupérer le membre
        Member member = db.findMemberById(memberId).orElse(null);
        if (member == null) {
            return 0.0;
        }

        // Récupérer la collectivité du membre
        String collectivityId = null;
        for (Collectivity c : db.findAllCollectivities()) {
            if (c.getMembers().stream().anyMatch(m -> m.getId().getId().equals(memberId))) {
                collectivityId = c.getId();
                break;
            }
        }
        if (collectivityId == null) {
            return 0.0;
        }

        // Compter le nombre d'activités où le membre était concerné (occupation match)
        int totalConcerned = 0;
        List<CollectivityActivity> activities = db.findActivitiesByCollectivityId(collectivityId);

        for (CollectivityActivity activity : activities) {
            // Vérifier si l'activité est dans la période
            LocalDate activityDate = activity.getExecutiveDate();
            if (activityDate == null && activity.getRecurrenceRule() != null) {
                activityDate = from;
            }
            if (activityDate == null || activityDate.isBefore(from) || activityDate.isAfter(to)) {
                continue;
            }

            // Vérifier si le membre est concerné par cette activité
            List<MemberOccupation> concernedOccupations = activity.getMemberOccupationConcerned();
            if (concernedOccupations != null && concernedOccupations.contains(member.getOccupation())) {
                totalConcerned++;
            }
        }

        if (totalConcerned == 0) {
            return 0.0;
        }

        // Compter le nombre de présences du membre
        int presentCount = 0;
        List<ActivityMemberAttendance> attendances = db.findAttendanceByMemberId(memberId);
        for (ActivityMemberAttendance attendance : attendances) {
            if ("ATTENDED".equals(attendance.getAttendanceStatus())) {
                // Vérifier que l'activité est dans la période
                CollectivityActivity activity = db.findActivityById(attendance.getActivityId()).orElse(null);
                if (activity != null) {
                    LocalDate activityDate = activity.getExecutiveDate();
                    if (activityDate != null && !activityDate.isBefore(from) && !activityDate.isAfter(to)) {
                        presentCount++;
                    }
                }
            }
        }

        return (double) presentCount / totalConcerned * 100.0;
    }

    public List<CollectivityOverallStatistics> getOverallStatistics(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Both 'from' and 'to' dates are required");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("'from' date cannot be after 'to' date");
        }

        List<Collectivity> collectivities = db.findAllCollectivities();
        List<MembershipFee> activeFees = db.getActiveMembershipFees();
        List<CollectivityOverallStatistics> statistics = new ArrayList<>();

        for (Collectivity collectivity : collectivities) {
            CollectivityOverallStatistics stat = new CollectivityOverallStatistics();

            CollectivityInformation info = new CollectivityInformation();
            info.setNumber(collectivity.getOfficialNumber());
            info.setName(collectivity.getOfficialName());
            stat.setCollectivityInformation(info);

            int newMembersNumber = db.getNewMembersCount(collectivity.getId(), from, to);
            stat.setNewMembersNumber(newMembersNumber);

            double currentDuePercentage = calculateCurrentDuePercentage(collectivity, activeFees, from, to);
            stat.setOverallMemberCurrentDuePercentage(currentDuePercentage);

            //Calcul du taux d'assiduité global de la collectivité
            double overallAssiduity = calculateOverallAssiduity(collectivity, from, to);
            stat.setOverallMemberAssiduityPercentage(overallAssiduity);

            statistics.add(stat);
        }

        return statistics;
    }

    private double calculateCurrentDuePercentage(Collectivity collectivity, List<MembershipFee> activeFees, LocalDate from, LocalDate to) {
        List<Member> members = collectivity.getMembers();
        if (members.isEmpty()) {
            return 0.0;
        }

        int membersUpToDate = 0;

        for (Member member : members) {
            boolean isUpToDate = true;

            for (MembershipFee fee : activeFees) {
                if (fee.getEligibleFrom() != null && fee.getEligibleFrom().isAfter(to)) {
                    continue;
                }

                int paymentsCount = db.getMemberPaymentsCountForFee(member.getId().getId(), fee.getId(), from, to);

                if (paymentsCount == 0) {
                    isUpToDate = false;
                    break;
                }
            }

            if (isUpToDate) {
                membersUpToDate++;
            }
        }

        return (double) membersUpToDate / members.size() * 100.0;
    }

    // Calcul du taux d'assiduité global de la collectivité
    private double calculateOverallAssiduity(Collectivity collectivity, LocalDate from, LocalDate to) {
        List<Member> members = collectivity.getMembers();
        if (members.isEmpty()) {
            return 0.0;
        }

        double totalAssiduity = 0.0;
        for (Member member : members) {
            totalAssiduity += calculateMemberAssiduity(member.getId().getId(), from, to);
        }
        return totalAssiduity / members.size();
    }
}