package Donation;

public class donationInfo {
    String donator;
    double amount;
    String message;

    public donationInfo(String name, double amt, String mess) {
        this.donator = name;
        this.amount = amt;
        this.message = mess;
    }
}
