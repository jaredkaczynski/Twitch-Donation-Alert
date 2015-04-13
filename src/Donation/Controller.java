package Donation;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Controller extends Thread implements Runnable {
    @FXML
    public Pane FollowerPane;
    Main newMain = new Main();
    TranslateTransition slideNotificationOut;
    TranslateTransition SlideNotificationin;
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
    boolean threadStarted = false;
    @FXML
    private Text actiontarget;
    @FXML
    private TextField participantID = null;
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
    private Button startNotifications;
    @FXML
    private Button testSlider;
    @FXML
    private Tab Options;
    @FXML
    private TabPane TabPaneMain;
    @FXML
    private Tab NotificationTab;
    @FXML
    private ChoiceBox dropDownSelector;
    @FXML
    private CheckBox showDonationBar;
    @FXML
    private TextField alertShowTime;

    /*
    * Slides the donation graphic in and out so it appears when needed like common Twitch popups
    */
    public void testSlider() {
        System.out.println(FollowerPane.getTranslateY());
        if (FollowerPane.getTranslateY() <= 0) {
            TranslateTransition slideOut = new TranslateTransition(Duration.seconds(.15), FollowerPane);
            slideOut.setByY(62);
            slideOut.play();
            isSlided = true;
        } else if (FollowerPane.getTranslateY() == 62) {
            TranslateTransition slideIn = new TranslateTransition(Duration.seconds(.15), FollowerPane);
            slideIn.setByY(-62);
            slideIn.play();
            isSlided = false;
        }
    }

    /*
    * Removes a scroll bar that by default shows on the message box and is unsightly
    */
    public void removeScrollBar() {
        ScrollBar scrollBarv = (ScrollBar) Message.lookup(".scroll-bar:vertical");
        scrollBarv.setDisable(true);
    }

    @FXML
    void runCheck(ActionEvent event) {
        checkValid();
    }

    /*
    * Does nothing, kept for legacy use or future changes
    */
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

    /*
    * Again a simple function that can be removed but kept for logic sake
    */
    public void runSoundCheck() {
        checkSound();
    }

    /*
    * Tests a sound file
    */
    public void checkSound() {
        String filename;
        filename = donationSound.getText();
        System.out.println(filename);
        String bip = filename;
        Media hit = new Media("file:///" + bip);
        MediaPlayer mediaPlayer = new MediaPlayer(hit);
        mediaPlayer.play();
    }

    /*
    * Adds the donator name and amount to the left box on the GUI
    */
    private void updateDonorName(String Donator, double Amount) {
        DonatorName.setText(Donator + " $" + String.format("%.2f", Amount));
    }

    /*
    * On GUI start this propagates? the dropdown selector as well as removes the scroll bar from the message box
    */
    public void setData() {
        dropDownSelector.getItems().addAll("ExtraLife", "Doctors Without Borders");
        dropDownSelector.getSelectionModel().selectFirst();
        removeScrollBar();
    }

    /*
    * This function sends a request to the GUI thread with the updated information of who donated
    * Contains all information required to update thread
    */
    public void showDonationAlertMoney(final String Donator, final double Amount, final String Mess) {
        System.out.println("Trying to show the alert money");
        System.out.println(Thread.currentThread().getId());
        System.out.println(Donator + Mess);
        System.out.println(Thread.currentThread() + " thread");

        Platform.runLater(
                new Runnable() {
                    public void run() {
                        //This is so a sound can play
                        try {
                            if (!Controller.this.donationSound.getText().isEmpty()) {
                                //Controller.this.checkSoundWav();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.out.println(Thread.currentThread() + " thread" + Mess + Controller.this.Message);
                        Controller.this.updateDonorName(Donator, Amount);
                        Controller.this.Message.setText(Mess);
                        Controller.this.FollowerPane.setVisible(true);
                        Controller.this.testSlider();
                    }
                });
        System.out.println("Set Visible");
    }

    /*
    * Checks if a string is an integer for the user entry
    */
    boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    /*
    * checkValid() Reads the input data given by the user and attempts a simple validation
    * Checks that the user ID is of proper length and that the time delay is proper
    */
    void checkValid() {
        boolean problem = false;
        System.out.println(donationShown.selectedProperty().getValue());
        if (participantID.getText().isEmpty() || (participantID.getText().length() < 4) || !isInteger(participantID.getText())) {
            participantID.clear();
            System.out.println(Thread.currentThread() + " thread update" + participantID);
            participantID.setPromptText("Please insert your ID properly. The 6? digit number.");
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

    /*
    * Starts a program thread to run the donation checking
    * Passes the required donation information to the thread as to run with required settings
    */
    void startDonationCheck() {
        threadStarted = true;
        int timeDelaySend = 10;
        String donateSound = "";
        if (!timeDelay.getText().isEmpty()) {
            timeDelaySend = Integer.valueOf(timeDelay.getText());
        }
        if (!donationSound.getText().isEmpty()) {
            donateSound = donationSound.getText();
        }
        donationSound.getText();
        donationShown.selectedProperty();

        Runnable r = new Donations(dropDownSelector.getSelectionModel().getSelectedIndex(), Integer.valueOf(participantID.getText()),
                timeDelaySend,
                donateSound,
                donationShown.selectedProperty().getValue(), 0, "test", "test", Controller.this);
        newThread(r).start();
    }

    /*
    * Starts a new thread
    */
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    }

    /*
    * Simple function for debugging, can be removed and startDonationCheck() directly called
    * but this looks like a more logical progression
    */
    void startThread() {
        System.out.println("???");
        System.out.println(Donations.activeCount());
        startDonationCheck();
    }


}
