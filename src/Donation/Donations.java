package Donation;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Jared on 21.10.2014.
 */
public class Donations extends Thread implements Runnable {
    String totalDonations = "0";
    int userId = 116306; //Twitch.tv/riccio is the person this was made for so I defaulted it to his ID
    String[] donationURL = {"http://www.extra-life.org/index.cfm?fuseaction=donorDrive.participantDonations&participantID=",
            "http://events.doctorswithoutborders.org/index.cfm?fuseaction=donordrive.personalCampaignDonations&participantID="};
    String url;
    //This is how I tested it without spending money
    //String url = "https://dl.dropboxusercontent.com/u/17647321/extralife.html";
    int refreshTime = 2;
    boolean addToQueue = false;
    String soundFileLocation = "";
    Boolean enableDonationValue = true;
    String donationMessage = "";
    double donationValue = 0;
    String donatorName = "";
    boolean visible = false;
    Controller alertWindow;
    long systemTime = 0;
    int donationSite;
    boolean isDonationPopupActive = false;
    String lastDonatorName = "";
    String lastDonatorMessage = "";
    double lastDonatorAmount = 0;
    //How long to have the alert appear
    int alertShowTime = 5;

    Queue<donationInfo> q = new LinkedList();

    public Donations(int DonationSite, int donationSiteUserID, int timeBetween, String soundFile, Boolean showDonation, double donation, String Mess, String Name, Controller controller) {
        userId = donationSiteUserID;
        donationSite = DonationSite;
        url = donationURL[DonationSite] + userId;
        System.out.println("Donation URL = " + url);
        refreshTime = timeBetween * 1000;
        soundFileLocation = soundFile;
        alertWindow = controller;
        enableDonationValue = showDonation;
        donationValue = donation;
        donationMessage = Mess;
        donatorName = Name;
    }

    /*public void runCheck() {
        long systemTime = 0;
        updateDonations();
        systemTime = System.currentTimeMillis();
        while (true) {
            updateDonations();
            try {
                Thread.sleep(refreshTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }*/
    void playSound(String filename) {
        System.out.println(filename);
        String bip = filename;
        Media hit = new Media("file:///" + bip);
        MediaPlayer mediaPlayer = new MediaPlayer(hit);
        mediaPlayer.play();
    }

    public void updateDonations() {
        switch (donationSite) {
            case 0:
                updateExtraLife();
                break;
            case 1:
                updateDoctorsWithoutBorders();
                break;
        }
    }

    /*
    * Checks if there's an update in the Extra-Life page and if there is, it updates
    * and adds the new donation to a queue
    */
    private void updateExtraLife() {
        String linkText = "";
        String iframeSrc;
        String recentDonatorName;
        String recentDonatorMessage = "";
        double recentDonatorAmount;
        System.out.println("Add to Queue");

        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();

            Element link = null;
            link = doc.select("iframe").first();
            Elements links = null;
            String[] parts;
            links = doc.select("#donors .donor-detail > .block");
            link = links.first();
            recentDonatorName = link.text();
            parts = recentDonatorName.split(" don");
            recentDonatorName = parts[0];
            parts = parts[1].split("ated");
            recentDonatorAmount = Double.parseDouble(parts[1].substring(2));

            links = doc.select("#donors .donor-detail:first-of-type > em");
            link = links.first();

            recentDonatorMessage = link.text();

            System.out.println("");
            if ((!lastDonatorName.equals(recentDonatorName) || !lastDonatorMessage
                    .equals(recentDonatorMessage))) {
                lastDonatorName = recentDonatorName;
                lastDonatorMessage = recentDonatorMessage;
                if (enableDonationValue) {
                    updateDonationNoValue(alertWindow, recentDonatorName,
                            recentDonatorAmount, recentDonatorMessage);

                }
            }
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
    * Checks if there's an update in the Doctors Without Borders page and if there is, it updates
    * and adds the new donation to a queue
    */
    private void updateDoctorsWithoutBorders() {
        String linkText = "";
        String iframeSrc;
        String recentDonatorName;
        String recentDonatorMessage = "";
        double recentDonatorAmount;
        System.out.println("Add to Queue");

        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();

            Element link = null;
            /*
			 * link = doc.select("iframe").first(); System.out.println(link +
			 * "link"); iframeSrc = link.attr("src");
			 */

            link = doc.select(".thermoBox > script").first();

            //System.out.println(link + "link");
            iframeSrc = link.attr("src");

            Elements links = null;
            String[] parts;
            links = doc.select("#donors .donor-detail > .block");
            link = links.first();
            // link = doc.select("strong").first();
            recentDonatorName = link.text();
            parts = recentDonatorName.split(" don");
            recentDonatorName = parts[0];
            parts = parts[1].split("ated");
            recentDonatorAmount = Double.parseDouble(parts[1].substring(2));

            links = doc.select("#donors .donor-detail:first-of-type > em");
            link = links.first();
            recentDonatorMessage = link.text();
            System.out.println("");
            if ((!lastDonatorName.equals(recentDonatorName) || !lastDonatorMessage
                    .equals(recentDonatorMessage))) {
                lastDonatorName = recentDonatorName;
                lastDonatorMessage = recentDonatorMessage;
                if (enableDonationValue) {
                    updateDonationNoValue(alertWindow, recentDonatorName,
                            recentDonatorAmount, recentDonatorMessage);

                }
            }

        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void updateDonationNoValue(Controller myController, String Donator,
                                      double Amount, String Message) {
        if (Message.length() > 92) {
            Message = Message.substring(0, 80) + "...";
        }
        System.out.println("I got to the update part!");
        System.out.println(q.isEmpty());
        donationInfo temp = new donationInfo(Donator, Amount, Message);
        q.add(temp);
    }

    void processQueue() {
        System.out.println("got to processqueue");
        systemTime = System.currentTimeMillis();
        alertWindow.showDonationAlertMoney(q.peek().donator, q.peek().amount,
                q.peek().message);
        System.out.println(q.peek());
        //removes the first object from the queue which is the one we just processed
        q.remove();
        System.out.println(q.peek());
        isDonationPopupActive = true;
    }
    /*
    * Runs the donation checker which looks at the website requested and updates if required
    */
    public void run() {
        System.out.println(System.currentTimeMillis() + " " + systemTime + " ???");
        systemTime = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() - systemTime > alertShowTime * 1000
                    && isDonationPopupActive) {
                systemTime = System.currentTimeMillis();
                alertWindow.testSlider();
                isDonationPopupActive = false;
            }
            System.out.println(isDonationPopupActive + " " + q.isEmpty());
            /*Checks if the queue q (hehehe) is empty and if a donation is currently active. If they queue is not empty
            * it processes the queue,
            */
            if (!q.isEmpty() && !isDonationPopupActive) {
                processQueue();
                isDonationPopupActive = true;
            }
            updateDonations();
            try {
                Thread.sleep(refreshTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
