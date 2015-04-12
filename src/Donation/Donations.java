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
    boolean isActive = false;
    String lastDonatorName = "";
    String lastDonatorMessage = "";
    double lastDonatorAmount = 0;
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

            // parts = recentDonatorMessage.split(" donated");
            // recentDonatorName = parts[0];
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
            // link = doc.select("strong").first();
            recentDonatorMessage = link.text();
            /*if (lastDonatorMessage.equals(link.text())) {
                System.out.println(links.text());
                recentDonatorMessage = "";
            } else {
                System.out.println("else");
                recentDonatorMessage = link.text();
                //lastDonatorMessage = recentDonatorMessage;
            }*/
            // parts = recentDonatorMessage.split(" donated");
            // recentDonatorName = parts[0];
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
        // isActive = true;
        // Donations queueDonations = new Donations(0, 0, "",
        // true,Amount,Message, Donator, myController);
        System.out.println(q.isEmpty());
        // addToQueue(queueDonations);
        // addToQueueDonation(myController,Donator,Amount,Message);
        donationInfo temp = new donationInfo(Donator, Amount, Message);
        q.add(temp);
        // myController.showDonationAlertMoney(Donator, Amount, Message);
    }

    void processQueue() {
        // q.peek()
        /*
		 * try { if (!soundFileLocation.isEmpty()) {
		 * playSound(soundFileLocation); } } catch (Exception e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */

        System.out.println("got to processqueue");
        systemTime = System.currentTimeMillis();
        alertWindow.showDonationAlertMoney(q.peek().donator, q.peek().amount,
                q.peek().message);
        // updateDonationNoValue(alertWindow, q.peek().donatorName,
        // q.peek().donationValue, q.peek().donationMessage);
        System.out.println(q.peek());
        q.remove();
        System.out.println(q.peek());
        isActive = true;
    }

    void addToQueue(donationInfo donatorInfo) {
        q.add(donatorInfo);
    }

    public void run() {

        System.out.println(System.currentTimeMillis() + " " + systemTime + " wtf?");
        //String url = args[0];
        //checkDonations();
        systemTime = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() - systemTime > alertShowTime * 1000
                    && isActive) {
                // System.out.println("System Time" + System.currentTimeMillis()
                // + " "+ systemTime);
                systemTime = System.currentTimeMillis();
                alertWindow.slideIn();
                isActive = false;
            }

            System.out.println(isActive + " " + q.isEmpty());
            if (!q.isEmpty() && !isActive) {
                processQueue();
                isActive = true;
            }
            //if (checkDonations()) {
            updateDonations();
            //}
            try {
                Thread.sleep(refreshTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
