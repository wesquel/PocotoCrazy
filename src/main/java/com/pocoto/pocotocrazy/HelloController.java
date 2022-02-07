package com.pocoto.pocotocrazy;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;


import java.io.*;
import java.net.URL;
import java.util.*;

public class HelloController implements Initializable {

    private boolean updatePageActive = false;
    private volatile Thread updatePageThread;


    List<String> cavalosJaSelecionados = new ArrayList<>();
    private volatile Thread webThread;
    private boolean threadUp;
    private final String pathUser = System.getProperty("user.home");
    private WebDriver driver;
    List<WebElement> buttonsHint;
    List<WebElement> horses;
    ChromeOptions options;
    List<WebElement> botoesCavalos;

    String PATH_BUTTON_ACELERATE = "//*[@id=\"app-content\"]/div/div[3]/div/div/div/div[3]/div/div/div/div[1]/div[2]/div[4]/div/button[1]";
    String PATH_BUTTON_ACTIVITY = "//*[@id=\"app-content\"]/div/div[3]/div/div/div/div[3]/ul/li[2]/button";
    String PATH_FRAME_GOOGLE = "/html/body/div[3]/div/div/div/div[2]/div[2]/div/div/div/iframe";
    String CAPTCHAR_PATH_CHECKBOX = "//*[@id=\"recaptcha-anchor\"]/div[1]";
    String SOLVED_PATH_FRAME = "/html/body/div[4]/div[4]/iframe";
    String PATH_GAS_VALUE = "//*[@id=\"popover-content\"]/div/div/section/div/div/div[2]/div[1]/div[1]/h1";
    String PATH_INPUT_GAS_LIMIT = "//*[@id=\"popover-content\"]/div/div/section/div/div/div[2]/div[1]/div[2]/div[1]/label/div[2]/input";
    String SOLVED_BUTTON_PATH1 = "/html/body/div/div/div[8]/div[2]/div[1]/div[1]/div[4]";
    String SOLVED_BUTTON_PATH2 = "//*[@id=\"rc-imageselect\"]/div[3]/div[2]/div[1]/div[1]/div[4]";
    String PATH_TO_FILA = "//*[@id=\"app-content\"]/div/div[3]/div/div/div/div[3]/div/div/div/div[1]/div[1]";
    String PATH_TO_UNPROVVER = "//*[@id=\"app-content\"]/div/div[3]/div/div/div/div[3]/div/div/div/div[1]/div[2]";
    String PATH_TO_UNPROVVER_TEXT  = "//*[@id=\"app-content\"]/div/div[3]/div/div/div/div[3]/div/div/div/div[1]/div[2]/div[3]/h3/div";
    String PATH_TO_EDIT_GAS_BUTTON = "//*[@id=\"app-content\"]/div/div[3]/div/div[4]/div[2]/div/div/div/div[1]/button";
    String PATH_TO_EDIT_MAX_GAS_BUTTON = "//*[@id=\"popover-content\"]/div/div/section/div/div/div[2]/div[1]/button";
    String PATH_TO_MAX_GAS_BUTTON = "//*[@id=\"popover-content\"]/div/div/section/div/div/div[2]/div[1]/div[2]/div[3]/label/div[1]/input";
    String PATH_TO_CONFIRM_META_BUTTON = "//*[@id=\"app-content\"]/div/div[3]/div/div[4]/div[3]/footer/button[2]";
    String PATH_TO_CONFIRM_GAS_BUTTON= "//*[@id=\"popover-content\"]/div/div/section/footer/button";
    String PATH_TO_SITE_TEXT_BUTTON = "//*[@id=\"app-content\"]/div/div[3]/div/div/div/div[3]/div/div/div/div[1]/div[2]/div[3]/h3/span";

    @FXML VBox botVbox;
    @FXML VBox metaVbox;
    @FXML Button cancelHintButton;
    @FXML ProgressBar progressBarHint;
    @FXML Button startRunButton;
    @FXML Button cancelRunButton;
    @FXML ProgressBar progressBarHorse;
    @FXML ProgressBar progressBarStart;
    @FXML PasswordField metaMaskPasswordInput;
    @FXML Label labelMeta;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.setProperty("webdriver.chrome.driver", new File("libs").getAbsolutePath() + "\\driver\\chromedriver.exe");
        options = new ChromeOptions();
    }

    public ArrayList<String> captureTabChrome(){
        return new ArrayList<String>(driver.getWindowHandles());
    }

    public void selectTab(int n){
        this.driver.switchTo().window(captureTabChrome().get(n));
    }

    public void openNewTab(){
        ((JavascriptExecutor) driver).executeScript("window.open()");
    }

//    public void initializeMetaThread(){
//        threadUp = true;
//        if (webThread != null) {
//            webThread = null;
//        } else {
//            webThread = new Thread(this::connectMeta);
//            webThread.start();
//        }
//    }

    public void connectMeta(){
        try{
            createDriverWeb();
            progressBarStart.setProgress(0.35);
            this.driver.get("chrome-extension://nkbihfbeogaeaoehlefnkodbefgpgknn/home.html");
            progressBarStart.setProgress(0.50);
        }catch (SessionNotCreatedException | ElementClickInterceptedException e){
            errorConnectMeta();
        }
    }

    public void errorConnectMeta(){
        driver.quit();
    }

//    public void startConnectMeta(){
//        initializeMetaThread();
//    }

    public void startHint() {
    }

    public void connectToHint() {
        this.openNewTab();
        progressBarStart.setProgress(0.60);
        this.driver.switchTo().window(captureTabChrome().get(1));
        progressBarStart.setProgress(0.75);
        this.driver.get("https://play.pegaxy.io/renting?tab=share-profit");
        progressBarStart.setProgress(0.85);
    }


    public void cancelHint(){
        threadUp = false;
        progressBarHint.setProgress(0);
    }


    public boolean waitQueueMetaMask(){
        long start = System.currentTimeMillis();
        long end = start + 4*1000;
//        driver.switchTo().defaultContent();
        while (System.currentTimeMillis() < end){
            List<WebElement> fila = driver.findElements(By.className("transaction-list__header"));
            if (fila.size() >= 1){
                String text = driver.findElement(By.xpath(PATH_TO_UNPROVVER_TEXT)).getText();
                String text2 = driver.findElement(By.xpath(PATH_TO_SITE_TEXT_BUTTON)).getText();
                if ((Objects.equals(text, "Unapproved") || Objects.equals(text, "Desaprovado")) &&
                        Objects.equals(text2, "play.pegaxy.io")){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkMetaMask(){
        selectTab(0);
        try{
            driver.findElement(By.xpath(PATH_BUTTON_ACTIVITY)).click();
        }
        catch (NoSuchElementException ignored){
        }
        progressBarHorse.setProgress(0.80);
        if (waitQueueMetaMask()) {
            progressBarHorse.setProgress(0.85);
            if (waitELementExistXpath((PATH_TO_UNPROVVER),2)){
                progressBarHorse.setProgress(0.90);
                driver.findElement(By.xpath(PATH_TO_UNPROVVER)).click();
                //if (editGas()){
                    if (waitELementExistXpath((PATH_TO_CONFIRM_META_BUTTON), 4)) {
                        progressBarHorse.setProgress(0.95);
                        while (driver.findElements(By.xpath(PATH_TO_CONFIRM_META_BUTTON)).size() >= 1) {
                            try {
                                driver.findElement(By.xpath(PATH_TO_CONFIRM_META_BUTTON)).click();
                            } catch (ElementClickInterceptedException | NoSuchElementException | StaleElementReferenceException i) {
                                System.out.println("Error Elemento: PATH_TO_CONFIRM_META_BUTTON");
                            }
                        }
                        try {
                            driver.findElement(By.xpath(PATH_BUTTON_ACTIVITY)).click();
                        } catch (NoSuchElementException ignored) {

                        }
                        progressBarHorse.setProgress(1);
                        selectTab(1);
                        return true;
                    }
                //}
            }
        }
        selectTab(1);
        return false;
    }

    public boolean editGas () {
        if (waitELementExistXpath(PATH_TO_EDIT_GAS_BUTTON, 2)) {
            driver.findElement(By.xpath(PATH_TO_EDIT_GAS_BUTTON)).click();
            if (waitELementExistXpath((PATH_INPUT_GAS_LIMIT), 2)) {
                driver.findElement(By.xpath(PATH_INPUT_GAS_LIMIT)).clear();
                driver.findElement(By.xpath(PATH_INPUT_GAS_LIMIT)).sendKeys("3000000");
                if (waitELementExistXpath(PATH_TO_EDIT_MAX_GAS_BUTTON, 2)) {
                    driver.findElement(By.xpath(PATH_TO_EDIT_MAX_GAS_BUTTON)).click();
                    if (waitELementExistXpath(PATH_TO_MAX_GAS_BUTTON, 2)) {
                        driver.findElement(By.xpath(PATH_TO_MAX_GAS_BUTTON)).click();
                        if (waitELementExistXpath((PATH_TO_CONFIRM_GAS_BUTTON), 4)) {
                            while (driver.findElements(By.xpath(PATH_TO_CONFIRM_GAS_BUTTON)).size() >= 1) {
                                try {
                                    driver.findElement(By.xpath(PATH_TO_CONFIRM_GAS_BUTTON)).click();
                                } catch (ElementClickInterceptedException ignored) {
                                    System.out.println("Error Elemento: PATH_TO_CONFIRM_GAS_BUTTON");
                                }
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean speedButton(){
        if (waitELementExistXpath(PATH_BUTTON_ACELERATE, 2)) {
            driver.findElement(By.xpath(PATH_BUTTON_ACELERATE)).click();
            if (waitELementExistXpath(PATH_TO_EDIT_MAX_GAS_BUTTON, 2)) {
                driver.findElement(By.xpath(PATH_TO_EDIT_MAX_GAS_BUTTON)).click();
                if (waitELementExistXpath(PATH_TO_MAX_GAS_BUTTON, 2)) {
                    driver.findElement(By.xpath(PATH_TO_MAX_GAS_BUTTON)).click();
                    if (waitELementExistXpath((PATH_TO_CONFIRM_GAS_BUTTON), 2)) {
                        while (driver.findElements(By.xpath(PATH_TO_CONFIRM_GAS_BUTTON)).size() >= 1) {
                            try {
                                driver.findElement(By.xpath(PATH_TO_CONFIRM_GAS_BUTTON)).click();
                            } catch (ElementClickInterceptedException ignored) {
                                System.out.println("Error Elemento: PATH_TO_CONFIRM_GAS_BUTTON");
                            }
                        }
                        selectTab(1);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void InitializerUpdatePage(){
        if (updatePageActive || updatePageThread != null) {
            updatePageThread = null;
            updatePageActive = false;
        } else {
            updatePageActive = true;
            updatePageThread = new Thread(this::startUpdatePage);
            updatePageThread.start();
        }
    }



    public void createDriverWeb(){
        if (driver == null){
            this.driver = new ChromeDriver(options.addArguments("user-data-dir="+pathUser+"\\AppData\\Local\\Google\\Chrome\\User Data"));
        }
    }

//    public boolean clickCaptcharCheckBox(){
//        driver.switchTo().defaultContent();
//        progressBarHorse.setProgress(0.45);
//        try {
//            int checkPartOfElement = waitELementFrameOrHint((PATH_FRAME_GOOGLE),2);
//            if(checkPartOfElement == 1){
//                progressBarHorse.setProgress(0.65);
//                System.out.println("to aqui2");
//                return true;
//            }
//            else if( checkPartOfElement == 0){
//                progressBarHorse.setProgress(0.50);
//                driver.switchTo().frame(driver.findElement(By.xpath(PATH_FRAME_GOOGLE)));
//                if(waitELementExistXpath((CAPTCHAR_PATH_CHECKBOX),2)){
//                    WebElement checkBoxGoogle = driver.findElement(By.xpath(CAPTCHAR_PATH_CHECKBOX));
//                    checkBoxGoogle.click();
//                    driver.switchTo().defaultContent();
//                    progressBarHorse.setProgress(0.55);
//                    if(waitELementExistXpath((SOLVED_PATH_FRAME),2)){
//                        progressBarHorse.setProgress(0.60);
//                        driver.switchTo().frame(driver.findElement(By.xpath(SOLVED_PATH_FRAME)));
//                        int checkSolvedButton = waitSolvedExistXpath(2);
//                        if (checkSolvedButton == 0){
//                            WebElement buttonSolved = driver.findElement(By.xpath(SOLVED_BUTTON_PATH1));
//                            buttonSolved.click();
//                        }else if (checkSolvedButton == 1){
//                            WebElement buttonSolved = driver.findElement(By.xpath(SOLVED_BUTTON_PATH2));
//                            buttonSolved.click();
//                        }
//                    }
//                }
//            }
//        }catch (StaleElementReferenceException | NoSuchElementException e){
//            System.out.println("Error StaleElementReferenceException | NoSuchElementException e: linha 204");
//            return false;
//        }
//        progressBarHorse.setProgress(0.65);
//        System.out.println("to aqui");
//        return true;
//    }

    public boolean clickCaptcharCheckBox() {
        driver.switchTo().defaultContent();
        progressBarHorse.setProgress(0.45);

        if (waitELementExistXpath(PATH_FRAME_GOOGLE, 2)) {
            progressBarHorse.setProgress(0.50);
            driver.switchTo().frame(driver.findElement(By.xpath(PATH_FRAME_GOOGLE)));
            if (waitELementExistXpath((CAPTCHAR_PATH_CHECKBOX), 2)) {
                WebElement checkBoxGoogle = driver.findElement(By.xpath(CAPTCHAR_PATH_CHECKBOX));
                checkBoxGoogle.click();
                return true;
            }
        }
        return false;
    }

    public int hintOrSolvedClick() {
        try {
            long start = System.currentTimeMillis();
            long end = start + 3 * 1000;
            driver.switchTo().defaultContent();
            while (System.currentTimeMillis() < end) {
                progressBarHorse.setProgress(0.70);
                buttonsHint = driver.findElements(By.className("button-game"));
                int size = driver.findElements(By.xpath(SOLVED_PATH_FRAME)).size();
                if (buttonsHint.get(buttonsHint.size() - 1).getAttribute("class").contains("primary")) {
//                    buttonsHint.get(buttonsHint.size() - 1).click();
//                    progressBarHorse.setProgress(0.75);
                    return 1;
                }
                else if(size >= 1){
                    driver.switchTo().frame(driver.findElement(By.xpath(SOLVED_PATH_FRAME)));
                    int checkSolvedButton = waitSolvedExistXpath(2);
                    if (checkSolvedButton == 0){
                        WebElement buttonSolved = driver.findElement(By.xpath(SOLVED_BUTTON_PATH1));
                        buttonSolved.click();
                        progressBarHorse.setProgress(0.75);
                        return 1;
                    }else if (checkSolvedButton == 1){
                        WebElement buttonSolved = driver.findElement(By.xpath(SOLVED_BUTTON_PATH2));
                        buttonSolved.click();
                        progressBarHorse.setProgress(0.75);
                        return 1;
                    }

                }
            }
        }catch (StaleElementReferenceException | NoSuchElementException e){
            System.out.println("Error linha 246");
        }
        return -1;
    }

    public void startUpdatePage(){
        while (updatePageActive){
            selectTab(1);
            driver.get("https://play.pegaxy.io/renting?tab=share-profit");
            progressBarHorse.setProgress(0);
            if(waitELementExist(("item-pega"), 2)){
                progressBarHorse.setProgress(0.1);
                if(selectGoodHorse()){
                    progressBarHorse.setProgress(0.40);
                    if (clickCaptcharCheckBox()){
                        int checkClickWhere = hintOrSolvedClick();
                        if(checkClickWhere == 0){
                            if (checkMetaMask()){

                            }
                        }else if (checkClickWhere == 1){
                            if(waitButtonActiveAndClick()){
                                if (checkMetaMask()){

                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void initializeAppThread(){
        threadUp = true;
        if (webThread != null) {
            webThread = null;
        } else {
            webThread = new Thread(this::conectWeb);
            webThread.start();
        }
    }

    public void conectWeb(){
        progressBarStart.setVisible(true);
        progressBarStart.setProgress(0.25);
        connectMeta();
        connectToHint();
        progressBarStart.setProgress(1);
        metaVbox.setVisible(false);
        botVbox.setVisible(true);
    }


    public void startRun(){
        initializeAppThread();
    }

    public void startHintHorse(){
        InitializerUpdatePage();
    }

    public boolean selectGoodHorse(){
        try {
            progressBarHorse.setProgress(0.25);
            List<WebElement> cavalos_na_lista = driver.findElements(By.className("item-info-title")); // nome do cavalo
            botoesCavalos = driver.findElements(By.className("item-link"));// botão dos cavalos
            String idCavalo;
            int porcentagemMaxima = 0; // alterar para minina porcentagem possivel para um cavalo
            int cont = 0;
            int porcentagemAtualCavalo = 0;
            int idCavaloMaximo = 0;
            progressBarHorse.setProgress(0.30);
            for (int i = 0; i < cavalos_na_lista.size(); i++){
                cont += 1;
                idCavalo = cavalos_na_lista.get(i).getText().replace("#","").split(" ")[0];
                porcentagemAtualCavalo = Integer.parseInt(botoesCavalos.get(i).getText().replace("%"," ")
                        .split(" ")[0]);
                if (!cavalosJaSelecionados.contains(idCavalo)){
                    cavalosJaSelecionados.add(idCavalo);
                    if (porcentagemAtualCavalo > porcentagemMaxima){
                        porcentagemMaxima = porcentagemAtualCavalo;
                        idCavaloMaximo = i;
                    }
                }
            }
            progressBarHorse.setProgress(0.35);
            if (porcentagemMaxima != 0) {
                botoesCavalos.get(idCavaloMaximo).click();
                return true;
            }
        }catch (ElementClickInterceptedException | StaleElementReferenceException e){
            return false;
        }
        return false;
    }

    public boolean waitButtonActiveAndClick(){
        try {
            long start = System.currentTimeMillis();
            long end = start + 3 * 1000;
            driver.switchTo().defaultContent();
            while (System.currentTimeMillis() < end) {
                progressBarHorse.setProgress(0.70);
                buttonsHint = driver.findElements(By.className("button-game"));
                if (buttonsHint.get(buttonsHint.size() - 1).getAttribute("class").contains("primary")) {
                    buttonsHint.get(buttonsHint.size() - 1).click();
                    progressBarHorse.setProgress(0.75);
                    return true;
                }
            }
            return false;
        }catch (StaleElementReferenceException | NoSuchElementException e){
            System.out.println("Error linha 246");
            return false;
        }
    }

    public boolean waitELementExistXpath(String path, int seconds){
        long start = System.currentTimeMillis();
        long end = start + seconds*1000;
        int elementSize;
        while (System.currentTimeMillis() < end){
            elementSize = driver.findElements(By.xpath(path)).size();
            if (elementSize >= 1){
                return true;
            }
        }
        return false;
    }

    public int waitSolvedExistXpath(int seconds){
        long start = System.currentTimeMillis();
        long end = start + seconds*1000;
        int elementSize1;
        int elementSize2;
        while (System.currentTimeMillis() < end){
            elementSize1 = driver.findElements(By.xpath(SOLVED_BUTTON_PATH1)).size();
            elementSize2 = driver.findElements(By.xpath(SOLVED_BUTTON_PATH2)).size();
            if (elementSize1 >= 1){
                return 0;
            }else if (elementSize2 >= 1){
                return 1;
            }
        }
        return -1;
    }

    public int waitELementFrameOrHint(String path, int seconds){
        long start = System.currentTimeMillis();
        long end = start + seconds*1000;
        int elementSize;
        while (System.currentTimeMillis() < end){
            elementSize = driver.findElements(By.xpath(path)).size();
            buttonsHint = driver.findElements(By.className("button-game"));
            System.out.println("to aqui1");
            if (buttonsHint.get(buttonsHint.size() - 1).getAttribute("class").contains("primary")){
                buttonsHint.get(buttonsHint.size() - 1).click();
                System.out.println("entrei nesse");
                return 1;
            }
            else if (elementSize >= 1){
                System.out.println("entrei no outro");
                return 0;
            }
        }
        System.out.println("não entrei em nem um");
        return -1;
    }

    public boolean waitELementExist(String path, int seconds){
        long start = System.currentTimeMillis();
        long end = start + seconds*1000;
        int elementSize;
        while (System.currentTimeMillis() < end){
            elementSize = driver.findElements(By.className(path)).size();
            if (elementSize >= 1){
                return true;
            }
        }
        return false;
    }

}