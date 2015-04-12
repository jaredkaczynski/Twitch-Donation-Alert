package Donation;

import javafx.animation.TranslateTransition;
import javafx.animation.TranslateTransitionBuilder;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.Timer;
import java.util.TimerTask;

import java.util.concurrent.atomic.AtomicInteger;

public class Controller extends Thread implements Runnable {
    Main newMain = new Main();
    @FXML
    private Text actiontarget;
    @FXML
    private TextField extraLifeID = null;
    @FXML
    private TextField timeDelay;
    @FXML
    private TextField donationSound;
    @FXML
    private CheckBox donationShown;
    @FXML
    private TextField DonatorName = null;
    @FXML
    private TextArea Message = null;
    @FXML
    public Pane FollowerPane;
    @FXML
    private Button startNotifications;
    @FXML
    private Button testSlider;
    @FXML
    private Tab Options;
    @FXML
    private CheckBox showDonationBar;
    @FXML
    private TextField alertShowTime;

    TranslateTransition slideNotificationOut;
    TranslateTransition SlideNotificationin;

    //    Thread DonationChecker;
    // private boolean runOnce=false;

    /*public Controller() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("sample.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }*/
    boolean isSlided = false;
    public void testSlider() {
    	System.out.println(FollowerPane.getTranslateY());
        if(FollowerPane.getTranslateY() <= 0){
            TranslateTransition slideOut = new TranslateTransition(Duration.seconds(.15), FollowerPane);
            slideOut.setByY(62);
            slideOut.play();
            isSlided= true;
        }else if(FollowerPane.getTranslateY() ==62){
            TranslateTransition slideIn = new TranslateTransition(Duration.seconds(.15), FollowerPane);
            slideIn.setByY(-62);
            slideIn.play();
            isSlided= false;
        }
    }
    public void slideIn(){
    	if(FollowerPane.getTranslateY() == 62){
            TranslateTransition slideIn = new TranslateTransition(Duration.seconds(.15), FollowerPane);
            slideIn.setByY(-62);
            slideIn.play();
            isSlided= false;
    }
    	/*
        FollowerPane.setVisible(!FollowerPane.isVisible());
        if(testSlider.getText()=="Hide Panel"){
            testSlider.setText("Show Panel");
        }
        testSlider.setText("Hide Panel");*/
    }


    public void removeScrollBar() {
        ScrollBar scrollBarv = (ScrollBar) Message.lookup(".scroll-bar:vertical");
        scrollBarv.setDisable(true);
    }
    @FXML
    void runCheck(ActionEvent event) {
        //initialize();
        removeScrollBar();
        checkValid();
    }

    public void setHidden() {
        new Thread() {
            public void run() {
                //Do some stuff in another thread
                Platform.runLater(new Runnable() {
                    public void run() {
                        System.out.println("setHidden");
                        FollowerPane.setVisible(false);
                    }
                });
            }
        }.start();

    }
    public void runSoundCheck(){
    	checkSound();
    }
    public void checkSound()
    {
    	String filename;
        filename = donationSound.getText();
        System.out.println(filename);
        String bip = filename;
        Media hit = new Media("file:///"+bip);
        MediaPlayer mediaPlayer = new MediaPlayer(hit);
        mediaPlayer.play();
    }

    private void updateDonorName(String Donator, double Amount){
        //System.out.println(String.format( "%.2f", Amount ) + "string format");
    	//String test = Donator + " $" + String.format( "%.2f", Amount );
        DonatorName.setText(Donator + " $" + String.format( "%.2f", Amount ));
    }
    public void showDonationAlertMoney(final String Donator,final double Amount,final String Mess){
        System.out.println("Trying to show the alert money");
        System.out.println(Thread.currentThread().getId());
        System.out.println(Donator + Mess);
        System.out.println(Thread.currentThread() + " thread");
        Platform.runLater(
                new Runnable(){
                    public void run(){
                        System.out.println(Thread.currentThread() + " thread" + Mess + Message);
                        updateDonorName(Donator,Amount);
                        Message.setText(Mess);
                        FollowerPane.setVisible(true);
                        testSlider();
                    }
                }
        );


        System.out.println("Set Visible");
    }



    boolean threadStarted = false;
    boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    void checkValid() {
        boolean problem = false;
        System.out.println(donationShown.selectedProperty().getValue());
        if (extraLifeID.getText().isEmpty() || (extraLifeID.getText().length() < 5) || !isInteger(extraLifeID.getText())) {
            extraLifeID.clear();
            System.out.println(Thread.currentThread() + " thread update" + extraLifeID);
            extraLifeID.setPromptText("Please insert your ID properly. The 6? digit number.");
            problem = true;
        }
        if (!isInteger(timeDelay.getText())) {
            timeDelay.clear();
            System.out.println(Thread.currentThread() + " thread update" + Message);
            timeDelay.setPromptText("Please insert a time delay in seconds or default to 10");
            problem = true;
            System.out.println("Proble,");
        }
        if (problem == false) {
            //  FollowerPane.setVisible(false);
            startNotifications.setText("running");
            startNotifications.setDisable(true);
            startThread();
        }


    }
    
    void startDonationCheck(){
        //extraLifeID,timeDelay,donationSound,donationShown
        //System.out.println(extraLifeID.getText());
        threadStarted=true;
        int timeDelaySend = 10;
        String donateSound = "";
        if (!timeDelay.getText().isEmpty()) {
            timeDelaySend = Integer.valueOf(timeDelay.getText());
        }
        if(!donationSound.getText().isEmpty()){
            donateSound=donationSound.getText();
        }
        donationSound.getText();
        donationShown.selectedProperty();
       /* Donations DonationChecker = new Donations(Integer.valueOf(extraLifeID.getText()),
                timeDelaySend,
                donateSound,
                donationShown.selectedProperty().getValue(),0,"test","test");*/
        Runnable r = new Donations(Integer.valueOf(extraLifeID.getText()),
                timeDelaySend,
                donateSound,
                donationShown.selectedProperty().getValue(),0,"test","test",Controller.this);
        newThread(r).start();
        //DonationChecker.runCheck();
        /*new Donations(Integer.valueOf(extraLifeID.getText()),
                timeDelaySend,
                donateSound,
                donationShown.selectedProperty().getValue()).start();*/
    }

    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    }
    void startThread() {
        //if(threadStarted == true){
        //newThread().interrupt();
        // Donations.currentThread().isInterrupted();

        // System.out.println(Donations.currentThread().isInterrupted()+"interrupted");
        //startDonationCheck();
        //System.out.println("success with what is done");
        //System.out.println(Donations.activeCount());
        //}else{
        System.out.println("???");
        System.out.println(Donations.activeCount());
        //FollowerPane.setVisible(true);
        startDonationCheck();

        //DonatorName.setVisible(false);
        // Message.setVisible(false);

        //}

    }


}
