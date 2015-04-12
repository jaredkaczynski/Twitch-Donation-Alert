package Donation;

import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Jared Kaczynski on 21.10.2014.
 */
public class Donations extends Thread implements Runnable {
    String totalDonations = "0";
    int userId = 116306; //Twitch.tv/riccio is the person this was made for so I defaulted it to his ID
    String url = "http://www.extra-life.org/index.cfm?fuseaction=donorDrive.participantDonations&participantID=" + userId;
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
    boolean isActive = false;


    Queue<donationInfo> q = new LinkedList();

    public Donations(int donationSiteID, int timeBetween, String soundFile, Boolean showDonation, double donation, String Mess, String Name, Controller controller) {

        userId = donationSiteID;
        refreshTime = timeBetween * 1000;
        soundFileLocation = soundFile;
        alertWindow = controller;
        enableDonationValue = showDonation;
        donationValue = donation;
        donationMessage = Mess;
        donatorName = Name;
    }

    public void runCheck() {
        long systemTime = 0;
        checkDonations();
        systemTime = System.currentTimeMillis();
        while (true) {
            if (checkDonations()) {
                updateDonations();
            }
            try {
                Thread.sleep(refreshTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /*
        void playSound(String musicFile) {
            musicFile = "C:/Users/jared/Desktop/hidey_ho.wav";
            InputStream in = null;
            try {
                in = new FileInputStream(musicFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            // create an audiostream from the inputstream
            AudioStream audioStream = null;
            try {
                audioStream = new AudioStream(in);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // play the audio clip with the audioplayer class
            AudioPlayer.player.start(audioStream);
        }
    */
    void playSound(String filename) {
        System.out.println(filename);
        String bip = filename;
        Media hit = new Media("file:///" + bip);
        MediaPlayer mediaPlayer = new MediaPlayer(hit);
        mediaPlayer.play();
    }

    public void updateDonations() {
        String linkText = "";
        String iframeSrc;
        String recentDonatorName;
        String recentDonatorMessage;
        double recentDonatorAmount;


        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Element link = null;
        link = doc.select("iframe").first();
        iframeSrc = link.attr("src");
        //parts = recentDonatorMessage.split(" donated");
        //recentDonatorName = parts[0];

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


        links = doc.select("#donors .donor-detail > em");
        link = links.first();
        // link = doc.select("strong").first();
        recentDonatorMessage = link.text();
        //parts = recentDonatorMessage.split(" donated");
        //recentDonatorName = parts[0];
        System.out.println("");
        if (enableDonationValue) {
            if (!soundFileLocation.isEmpty()) {
                playSound(soundFileLocation);
            }
            System.out.println(recentDonatorAmount);
            System.out.println(recentDonatorName);
            System.out.println(recentDonatorMessage);
            System.out.println(Thread.currentThread().getId());
            updateDonationNoValue(alertWindow, recentDonatorName, recentDonatorAmount, recentDonatorMessage);
            visible = true;
            systemTime = System.currentTimeMillis();
        }
        System.out.println(iframeSrc + "test");
        System.out.println(String.format("%.2f", recentDonatorAmount));
        System.out.println(recentDonatorAmount);
        System.out.println(recentDonatorName);
        System.out.println(recentDonatorMessage);
        System.out.println(totalDonations);
    }

    /* public void updateDonationNoValue(Controller myController, String Donator, double Amount, String Message) {
         if (Message.length() > 92) {
             Message = Message.substring(0, 80) + "...";
         }
         System.out.println("I got to the update part!");
         addToQueue = true;
         Donations queueDonations = new Donations(0, 0, "", true,Amount,Message, Donator, myController);
         System.out.println(q.isEmpty());
         addToQueue(queueDonations);
         System.out.println(q.isEmpty());
         //myController.showDonationAlertMoney(Donator, Amount, Message);
     }
     */
    public void updateDonationNoValue(Controller myController, String Donator, double Amount, String Message) {
        if (Message.length() > 92) {
            Message = Message.substring(0, 80) + "...";
        }
        System.out.println("I got to the update part!");
        addToQueue = true;
        //Donations queueDonations = new Donations(0, 0, "", true,Amount,Message, Donator, myController);
        System.out.println(q.isEmpty());
        //addToQueue(queueDonations);
        addToQueueDonation(myController, Donator, Amount, Message);
        System.out.println(q.isEmpty());
        myController.showDonationAlertMoney(Donator, Amount, Message);
    }

    public void addToQueueDonation(Controller myController, String Donator, double Amount, String Message) {
        if (Message.length() > 92) {
            Message = Message.substring(0, 80) + "...";
        }
        System.out.println("I got to the update part!");
        addToQueue = true;
        //Donations queueDonations = new Donations(0, 0, "", true,Amount,Message, Donator, myController);
        donationInfo temp = new donationInfo(Donator, Amount, Message);
        System.out.println(q.isEmpty());
        q.add(temp);
        myController.showDonationAlertMoney(Donator, Amount, Message);
        System.out.println(q.isEmpty());
    }

    boolean checkDonations() {
        String iframeSrc = null;
        Document doc = null;
        String currentDonations = "";
        boolean donationUpdate = false;
        try {
            doc = Jsoup.connect(url).get();


            Element link = null;
            link = doc.select("iframe").first();
            iframeSrc = link.attr("src");
            //parts = recentDonatorMessage.split(" donated");
            //recentDonatorName = parts[0];
            currentDonations = iframeSrc.split("&goalLabel")[0];
            currentDonations = currentDonations.split("actualAmount=")[1];
            donationUpdate = (Double.valueOf(currentDonations) > (Double.valueOf(totalDonations)));
            System.out.println(currentDonations + " space " + totalDonations + " " + donationUpdate);
            if (donationUpdate) {
                totalDonations = currentDonations;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (donationUpdate);
    }

    void addToQueue(donationInfo donatorInfo) {
        q.add(donatorInfo);
    }

    void processQueue() {
        //q.peek()
        System.out.println("got to processqueue");
        new Thread() {
            public void run() {
                //Do some stuff in another thread
                Platform.runLater(new Runnable() {
                    public void run() {
                        System.out.println(alertWindow + q.peek().donator + q.peek().amount + q.peek().message);
                        updateDonationNoValue(alertWindow, q.peek().donator, q.peek().amount, q.peek().message);
                    }
                });
            }
        }.start();
        //updateDonationNoValue(alertWindow, q.peek().donatorName, q.peek().donationValue, q.peek().donationMessage);
        System.out.println(q.peek());
        q.remove();
        System.out.println(q.peek());
        isActive = true;
    }

    public void run() {

        System.out.println(System.currentTimeMillis() + " " + systemTime + " wtf?");

        //String url = args[0];
        //checkDonations();
        systemTime = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() - systemTime > 15000 && visible) {
                //System.out.println("System Time");
                systemTime = System.currentTimeMillis();
                alertWindow.switchTab();
                alertWindow.slideIn();
                visible = false;
                addToQueue = false;
                System.out.println("Here");
            }
            try {
                Thread.sleep(refreshTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!q.isEmpty()) {
                processQueue();
            }
            if (q.isEmpty()) {
                isActive = false;
            }
            if (checkDonations()) {
                new Thread() {
                    public void run() {
                        //Do some stuff in another thread
                        Platform.runLater(new Runnable() {
                            public void run() {
                                updateDonations();
                            }
                        });
                    }
                }.start();
                //updateDonations();
            }
            //System.out.println("checkthreadct");
           /* try {
                Thread.sleep(refreshTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        }
    }


}
