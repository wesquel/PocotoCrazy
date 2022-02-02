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
import org.openqa.selenium.remote.http.HttpClient.Factory;


import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;

public class HelloController implements Initializable {

    private volatile Thread webThread;
    private volatile Thread MetaThread;
    private volatile Thread hintThread;
    private boolean threadUp;
    private boolean threadHintUp;
    private final String pathUser = System.getProperty("user.home");
    private WebDriver driver;
    Set<Integer> cavalosClicados = new HashSet<>();
    List<WebElement> horses;
    ChromeOptions options;
    String INPUT_PASSWORD_PATH = "//*[@id=\"password\"]";
    String BUTTON_PASSWORD_META_PATH = "//*[@id=\"app-content\"]/div/div[3]/div/div/button";
    String ACTIVITY_BUTTON_META_PATH = "//*[@id=\"app-content\"]/div/div[3]/div/div/div/div[3]/ul/li[2]/button";
    String PATH_MENSAGEN_HINT_BUTTON = "html/body/div[4]/div/div/div/div[2]/div[3]";
    String ERROR_METAMASK_LOGIN = "//*[@id=\"password-helper-text\"]";
    String PATH_FRAME_GOOGLE = "/html/body/div[3]/div/div/div/div[2]/div[2]/div/div/div/iframe";
    String CAPTCHAR_PATH_CHECKBOX = "//*[@id=\"recaptcha-anchor\"]/div[1]";
    String SOLVED_PATH_FRAME = "/html/body/div[4]/div[4]/iframe";
    String PATH_TO_FILA = "//*[@id=\"app-content\"]/div/div[3]/div/div/div/div[3]/div/div/div/div[1]/div[1]";
    String PATH_TO_UNPROVVER = "//*[@id=\"app-content\"]/div/div[3]/div/div/div/div[3]/div/div/div/div[1]/div[2]";
    String PATH_TO_UNPROVVER_TEXT  = "//*[@id=\"app-content\"]/div/div[3]/div/div/div/div[3]/div/div/div/div[1]/div[2]/div[3]/h3/div";
    String PATH_TO_EDIT_GAS_BUTTON = "//*[@id=\"app-content\"]/div/div[3]/div/div[4]/div[2]/div/div/div/div[1]/button";
    String PATH_TO_MAX_GAS_BUTTON = "//*[@id=\"popover-content\"]/div/div/section/div/div/div[2]/div[1]/div[2]/div[3]/label/div[1]/input";
    String PATH_TO_CONFIRM_META_BUTTON = "//*[@id=\"app-content\"]/div/div[3]/div/div[4]/div[3]/footer/button[2]";
    String PATH_TO_CONFIRM_GAS_BUTTON= "//*[@id=\"popover-content\"]/div/div/section/footer/button";
    String PATH_TO_SpeedUp_Button = "/div/div[2]/div[4]/div/button[1]";
    String PATH_TO_SITE_TEXT_BUTTON = "//*[@id=\"app-content\"]/div/div[3]/div/div/div/div[3]/div/div/div/div[1]/div[2]/div[3]/h3/span";


    int cavaloSelecionado = 0;

    @FXML Button startHintButton;
    @FXML VBox botVbox;
    @FXML VBox metaVbox;
    @FXML Button cancelHintButton;
    @FXML ProgressBar progressBarHint;
    @FXML Button startRunButton;
    @FXML Button cancelRunButton;
    @FXML ProgressBar progressBarRun;
    @FXML ProgressBar progressBarConectMetamask;
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

    public boolean waitElementLoad(String path, String errorPath){
        int i = 0;
        int errorI = 0;
        while(true){
            i =driver.findElements(By.xpath(path)).size();
            if (!Objects.equals(errorPath, "0")){
                errorI = driver.findElements(By.xpath(errorPath)).size();
            }
            if(i >= 1){
                return true;
            }
            else if (errorI >= 1){
                return false;
            }
        }
    }

    public void waitELementDestroy(String path){
        int i = 0;
        //waitElementLoad(path,"0");
        while (true){
//            i =driver.findElements(By.xpath(path)).size();
//            System.out.println(driver.findElements(By.xpath(path)).get(0).getText());
//            System.out.println("Numero de elemntos destroy="+i);
//            if(i < 1){
//                return;
//            }
            driver.switchTo().defaultContent();
            if (hintButtonActivi() || driver.findElements(By.xpath("html/body/div[4]/div/div/div/div[2]/div[3]")).size() > 0){
                return;
            }
            driver.switchTo().frame(driver.findElement(By.xpath(SOLVED_PATH_FRAME)));
        }
    }

    public void initializeMetaCheckThread(){
        threadUp = true;
        if (webThread != null) {
            webThread = null;
        } else {
            webThread = new Thread(this::checkMetaMask);
            webThread.start();
        }
    }

    public void initializeMetaThread(){
        threadUp = true;
        if (webThread != null) {
            webThread = null;
        } else {
            webThread = new Thread(this::connectMeta);
            webThread.start();
        }
    }

    public void visibleBot(){

    }


    public void connectMeta(){
        try{
            this.driver = new ChromeDriver(options.addArguments("user-data-dir="+pathUser+"\\AppData\\Local\\Google\\Chrome\\User Data"));
            progressBarConectMetamask.setVisible(true);
            progressBarConectMetamask.setProgress(0.1);
            this.driver.get("chrome-extension://nkbihfbeogaeaoehlefnkodbefgpgknn/home.html");
            progressBarConectMetamask.setProgress(0.3);
            if (!this.waitElementLoad(INPUT_PASSWORD_PATH,ERROR_METAMASK_LOGIN)){
                throw new ElementClickInterceptedException("Mensagem Disparada Manualmente.");
            }
            progressBarConectMetamask.setProgress(0.4);
            WebElement passwordInput = this.driver.findElement(By.xpath(INPUT_PASSWORD_PATH));
            WebElement buttonPassword = this.driver.findElement(By.xpath(BUTTON_PASSWORD_META_PATH));
            progressBarConectMetamask.setProgress(0.5);
            passwordInput.sendKeys(metaMaskPasswordInput.getText());
            progressBarConectMetamask.setProgress(0.7);
            buttonPassword.click();
//            if (!this.waitElementLoad(ACTIVITY_BUTTON_META_PATH,ERROR_METAMASK_LOGIN)){
//                throw new ElementClickInterceptedException("Mensagem Disparada Manualmente.");
//            }
            progressBarConectMetamask.setProgress(0.9);
//            WebElement activityButton = this.driver.findElement(By.xpath(ACTIVITY_BUTTON_META_PATH));
//            activityButton.click();
            progressBarConectMetamask.setProgress(1);
            metaVbox.setVisible(false);
            botVbox.setVisible(true);
        }catch (SessionNotCreatedException | ElementClickInterceptedException e){
            errorConectMeta();
        }
    }

    public void errorConectMeta(){
        progressBarConectMetamask.setProgress(0);
        driver.quit();
    }

    public void startConnectMeta(){
        initializeMetaThread();
    }

    public void startHint() {
        connectToHint();
        //stableHint();
        initializeHint();
    }

    public void initializeHint(){
        threadUp = true;
        if (hintThread != null) {
            hintThread = null;
        } else {
            hintThread = new Thread(this::stableHint);
            hintThread.start();
        }
    }

    public void stableHint(){
        while(threadUp){
            horses = driver.findElements(By.className("item-pega"));
            while(horses.size() <= 0){
                this.driver.get("https://play.pegaxy.io/renting?tab=share-profit");
                checkMetaMask();
                System.out.println("Stable Hint Load");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                horses = driver.findElements(By.className("item-pega"));
            }
            selectHorse();
        }
    }

    public void connectToHint() {
        this.openNewTab();
        this.driver.switchTo().window(captureTabChrome().get(1));
        this.driver.get("https://play.pegaxy.io/renting?tab=share-profit");
        this.progressBarHint.setProgress(1);
    }

    public void selectHorse(){
        try {
            checkMetaMask();
            int horseSelect = 0;
            int maxPercent = 1;
            int atualPercent = 0;
            int nomeCavalo;

            int increment = 0;
            cavaloSelecionado = 0;
            WebElement horse;
            WebElement horseName;
            for (int i = 0; i < horses.size(); i++) {
                increment = i + 1;
                horseName = driver.findElement(By.xpath("//*[@id=\"__next\"]/div[1]/div[2]/div/div/div[2]/div/div/div[2]/div[" + increment + "]/a/div/div[2]/div/div[1]/div[1]"));
                horse = driver.findElement(By.xpath("//*[@id=\"__next\"]/div[1]/div[2]/div/div/div[2]/div/div/div[2]/div[" + increment + "]/div/div/div/div/div[2]/div/span[2]"));
                nomeCavalo = Integer.parseInt(horseName.getText().replace("#", "").split(" ")[0]);
                System.out.println(nomeCavalo);
                System.out.println("TESTE:"+horse.getText().replace("%", "").split(" ")[0]);
                atualPercent = Integer.parseInt(horse.getText().replace("%", "").split(" ")[0]);
                if (maxPercent < atualPercent && !cavalosClicados.contains(nomeCavalo)) {
                    horseSelect = increment;
                    maxPercent = atualPercent;
                }
                cavalosClicados.add(nomeCavalo);
            }

            if (horseSelect != 0) {
                cavaloSelecionado = horseSelect;
                WebElement buttonHorseSelect = driver.findElement(By.xpath("//*[@id=\"__next\"]/div[1]/div[2]/div/div/div[2]/div/div/div[2]/div[" + horseSelect + "]/div/div/div/div/div[2]"));
                buttonHorseSelect.click();
                System.out.println("Frame Google Load");
                waitElementLoad(PATH_FRAME_GOOGLE,"0");
                startRun();
            }
            checkMetaMask();
            this.driver.get("https://play.pegaxy.io/renting?tab=share-profit");

        }
        catch (NoSuchElementException e){
            System.out.println("Passou");
        }
        catch (RuntimeException ignored){
            System.out.println("Error na atualPercent");
            this.driver.get("https://play.pegaxy.io/renting?tab=share-profit");
        }


    }

    public void cancelHint(){
        threadUp = false;
        progressBarHint.setProgress(0);
    }

    public void checkMetaMask(){

            System.out.println("Entrei no Check Metamask");
            selectTab(0);
            List<WebElement> fila = driver.findElements(By.xpath(PATH_TO_FILA));
            System.out.println("Tamnho da fila=" + fila.size());
            if (fila.size() >= 1) {
                String text = driver.findElement(By.xpath(PATH_TO_UNPROVVER_TEXT)).getText();
                System.out.println(text);
                String text2 = driver.findElement(By.xpath(PATH_TO_SITE_TEXT_BUTTON)).getText();
                System.out.println(text2);
                if (Objects.equals(text, "Unapproved") && Objects.equals(text2, "play.pegaxy.io")) {
                    System.out.println("PATH_TO_UNPROVVER");
                    driver.findElement(By.xpath(PATH_TO_UNPROVVER)).click();
                    System.out.println("PATH_TO_EDIT_GAS_BUTTON");
                    waitElementLoad(PATH_TO_EDIT_GAS_BUTTON, "0");
                    driver.findElement(By.xpath(PATH_TO_EDIT_GAS_BUTTON)).click();
                    System.out.println("PATH_TO_MAX_GAS_BUTTON");
                    waitElementLoad(PATH_TO_MAX_GAS_BUTTON, "0");
                    driver.findElement(By.xpath(PATH_TO_MAX_GAS_BUTTON)).click();

                    System.out.println("PATH_TO_CONFIRM_GAS_BUTTON");
                    waitElementLoad(PATH_TO_CONFIRM_GAS_BUTTON, "0");
                    driver.findElement(By.xpath(PATH_TO_CONFIRM_GAS_BUTTON)).click();

                    System.out.println("PATH_TO_CONFIRM_META_BUTTON");
                    waitElementLoad(PATH_TO_CONFIRM_META_BUTTON, "0");
                    driver.findElement(By.xpath(PATH_TO_CONFIRM_META_BUTTON)).click();
                }
            }
            selectTab(1);
    }

    public void startRun(){
        driver.switchTo().defaultContent();
        driver.switchTo().frame(driver.findElement(By.xpath(PATH_FRAME_GOOGLE)));
        WebElement checkBoxGoogle = driver.findElement(By.xpath(CAPTCHAR_PATH_CHECKBOX));
        WebElement checkBoxGoogle2 = driver.findElement(By.id("recaptcha-accessible-status"));
        checkBoxGoogle.click();
        driver.switchTo().defaultContent();
        driver.switchTo().frame(driver.findElement(By.xpath(SOLVED_PATH_FRAME)));
//        System.out.println("Button Solved Load");
//        waitElementLoad("//*[@id=\"rc-imageselect\"]/div[3]/div[2]/div[1]/div[1]/div[4]","0");
//        WebElement solvedButton = driver.findElement(By.xpath("//*[@id=\"rc-imageselect\"]/div[3]/div[2]/div[1]/div[1]/div[4]"));
//        solvedButton.click();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Wait elemento Destroy");
        waitELementDestroy("//*[@id=\"rc-imageselect\"]/div[3]/div[2]/div[1]/div[1]/div[4]");
        System.out.println("Saiu Wait elemento Destroy");
        if (hintButtonActivi()){
            driver.switchTo().defaultContent();
            System.out.println("TESTE");
            List<WebElement> buttonHint = driver.findElements(By.className("button-game"));
            buttonHint.get(buttonHint.size() - 1).click();
            waitElementLoad("\"html/body/div[4]/div/div/div/div[2]/div[3]\"","0");
            System.out.println("TESTE32");
        }

        progressBarRun.setProgress(1);
    }

    public boolean hintButtonActivi(){
        if (cavaloSelecionado != 0) {
            List<WebElement> e = driver.findElements(By.className("button-game"));
            return e.get(e.size() - 1).getAttribute("class").contains("primary");
        }
        return false;
    }


    public void cancelRun(){
        checkMetaMask();
    }



}