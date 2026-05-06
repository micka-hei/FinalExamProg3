package com.examen.hei.model;

public class CollectivityLocalStatistics {
    private MemberDescription memberDescription;
    private double earnedAmount;
    private double unpaidAmount;

    public MemberDescription getMemberDescription() {
        return memberDescription;
    }

    public void setMemberDescription(MemberDescription memberDescription) {
        this.memberDescription = memberDescription;
    }

    public double getEarnedAmount() {
        return earnedAmount;
    }

    public void setEarnedAmount(double earnedAmount) {
        this.earnedAmount = earnedAmount;
    }

    public double getUnpaidAmount() {
        return unpaidAmount;
    }

    public void setUnpaidAmount(double unpaidAmount) {
        this.unpaidAmount = unpaidAmount;
    }
}