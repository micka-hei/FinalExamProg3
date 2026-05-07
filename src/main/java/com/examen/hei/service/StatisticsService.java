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

            // Calcul du taux d'assiduité
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

    // Calcul du taux d'assiduité d'un membre (version corrigée avec logs)
    private double calculateMemberAssiduity(String memberId, LocalDate from, LocalDate to) {
        System.out.println("=== calculateMemberAssiduity for " + memberId + " ===");
        System.out.println("Period: " + from + " to " + to);

        // Récupérer le membre
        Member member = db.findMemberById(memberId).orElse(null);
        if (member == null) {
            System.out.println("ERROR: Member not found");
            return 0.0;
        }
        System.out.println("Member occupation: " + member.getOccupation());

        // Récupérer la collectivité du membre
        String collectivityId = null;
        for (Collectivity c : db.findAllCollectivities()) {
            if (c.getMembers().stream().anyMatch(m -> m.getId().getId().equals(memberId))) {
                collectivityId = c.getId();
                break;
            }
        }
        if (collectivityId == null) {
            System.out.println("ERROR: Collectivity not found for member");
            return 0.0;
        }
        System.out.println("Collectivity ID: " + collectivityId);

        // Récupérer les activités de la collectivité
        List<CollectivityActivity> activities = db.findActivitiesByCollectivityId(collectivityId);
        System.out.println("Activities found in collectivity: " + activities.size());

        int totalConcerned = 0;
        for (CollectivityActivity activity : activities) {
            System.out.println("  Activity: " + activity.getId() + ", executiveDate=" + activity.getExecutiveDate() + ", recurrenceRule=" + activity.getRecurrenceRule());

            // Vérifier si l'activité est dans la période
            LocalDate activityDate = activity.getExecutiveDate();
            if (activityDate == null && activity.getRecurrenceRule() != null) {
                activityDate = from;
            }
            if (activityDate == null || activityDate.isBefore(from) || activityDate.isAfter(to)) {
                System.out.println("    -> Activity not in period");
                continue;
            }

            // Vérifier si le membre est concerné par cette activité
            List<MemberOccupation> concernedOccupations = activity.getMemberOccupationConcerned();
            System.out.println("    Concerned occupations: " + concernedOccupations);

            if (concernedOccupations != null && concernedOccupations.contains(member.getOccupation())) {
                totalConcerned++;
                System.out.println("    -> Member IS CONCERNED for this activity (totalConcerned=" + totalConcerned + ")");
            } else {
                System.out.println("    -> Member NOT concerned for this activity");
            }
        }
        System.out.println("Total concerned activities: " + totalConcerned);

        if (totalConcerned == 0) {
            System.out.println("No concerned activities, returning 0.0");
            return 0.0;
        }

        // Compter le nombre de présences du membre
        List<ActivityMemberAttendance> attendances = db.findAttendanceByMemberId(memberId);
        System.out.println("Attendances found for member: " + attendances.size());

        int presentCount = 0;
        for (ActivityMemberAttendance attendance : attendances) {
            System.out.println("  Attendance: activityId=" + attendance.getActivityId() + ", status=" + attendance.getAttendanceStatus());
            if ("ATTENDED".equals(attendance.getAttendanceStatus())) {
                CollectivityActivity activity = db.findActivityById(attendance.getActivityId()).orElse(null);
                if (activity != null) {
                    LocalDate activityDate = activity.getExecutiveDate();
                    if (activityDate != null && !activityDate.isBefore(from) && !activityDate.isAfter(to)) {
                        presentCount++;
                        System.out.println("    -> Counted as present! (totalPresent=" + presentCount + ")");
                    } else {
                        System.out.println("    -> Activity date " + activityDate + " not in period");
                    }
                }
            }
        }
        System.out.println("Total present count: " + presentCount);

        double result = (double) presentCount / totalConcerned * 100.0;
        System.out.println("Assiduity percentage for " + memberId + ": " + result + "%");
        System.out.println("=========================================");

        return result;
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

            // Calcul du taux d'assiduité global de la collectivité
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