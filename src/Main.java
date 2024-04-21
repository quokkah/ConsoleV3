import java.io.*;
import java.util.Objects;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import javafx.*;
public class Main {
    //Variables - Scanner
    static Scanner sc = new Scanner(System.in);

    //Variables - Files
    static Path pathCommands = Paths.get("src/commands.txt");
    static Writer writerUsers;
    static Path pathUsers = Paths.get("src/usernames.txt");
    static {
        try {
            writerUsers = Files.newBufferedWriter(pathUsers, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    static Writer writerPass;
    static Path pathPass = Paths.get("src/passwords.txt");
    static {
        try {
            writerPass = Files.newBufferedWriter(pathPass, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    static Writer writerUserBackup;
    static Path pathUserBackup = Paths.get("src/userBackup.txt");
    static {
        try {
            writerUserBackup = Files.newBufferedWriter(pathUserBackup, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    static Writer writerPassBackup;
    static Path pathPassBackup = Paths.get("src/passBackup.txt");
    static {
        try {
            writerPassBackup = Files.newBufferedWriter(pathPassBackup, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    static BufferedReader backupReader;
    static {
        try {
            backupReader = new BufferedReader(new FileReader("src/passwords.txt"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }   //reader to check if files were saved properly
    static Writer writerPassImport;     //imports content from passBackup.txt to passwords.txt
    static {
        try {
            writerPassImport = Files.newBufferedWriter(pathPass, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    static Writer writerUserImport;     //imports content from userBackup.txt to usernames.txt
    static {
        try {
            writerUserImport = Files.newBufferedWriter(pathUsers, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    //Variables - Account Information
    static String[] usernames = new String[128];
    static String uncheckedUsername; //username to be checked if available
    static boolean usernameInUse; //whether the requested username is already in use
    static boolean usernamePositionAvailable = false; //whether the current array-position is occupied
    static int accountNumber; //the indexNumber of the current operating account
    static String[] passwords = new String[usernames.length];
    static String uncheckedPassword; //password to be checked if legal

    //Variables - Console
    static String startString = "Type 'help' to view all commands."; //shown at the start of the console and after long commands
    static String consoleInput; //input as one string
    static String[] consoleInputSplit = new String[3]; //input, split into multiple Strings to allow multi-phrase commands, like password view

    //Variables - Login
    static String loginUsername; //username inputted at the login-screen
    static boolean usernameExists = false; //if the inputted username actually exists
    static String loginPass; //password inputted at the login-screen

    //Variables for commands:
    //password
    static String unconfirmedPassword; //password inputted at password-change screen
    static String confirmPassword;
    //user
    static String unconfirmedUser; //username inputted at username-change screen

    //Start of Program
    public static void main(String[] args) {
        saveCheck();
    }

    //check if saved properly / backups
    static void saveCheck() {
        try {
            if (backupReader.readLine() == null) {
                for (String line : java.nio.file.Files.readAllLines(pathPassBackup)) {
                    writerPassImport.write(line + "\n");
                }
                writerPassImport.close();
                for (String line : java.nio.file.Files.readAllLines(pathUserBackup)) {
                    writerUserImport.write(line + "\n");
                }
                writerUserImport.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        dataToArray();
    }

    //Write user/pass into array
    static void dataToArray() {
        try {
            String pathToFile = "src/usernames.txt";                            //write usernames.txt into the array
            List<String> lines = Files.readAllLines(Paths.get(pathToFile));
            usernames = lines.toArray(new String[usernames.length]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {                                                                   //write passwords.txt into the array
            String pathToFile = "src/passwords.txt";
            List<String> lines = Files.readAllLines(Paths.get(pathToFile));
            passwords = lines.toArray(new String[usernames.length]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int x = 0; x < usernames.length; x++) {                            //replace "null" with empty space, otherwise it would be read as text
            if (Objects.equals(usernames[x], null)) {
                usernames[x] = "";
            }
        }
        signupOrLogin();
    }

    //Login/Signup
    static void signupOrLogin() {
        System.out.println("Do you want to sign up or log in?\n[1] Sign up\n[2] Log in");
        switch (sc.next()) {
            case "1":
                sc.nextLine();
                signupUser();
                break;
            case "2":
                sc.nextLine();
                loginUser();
                break;
            default:
                System.out.println("Please enter a number from 1-2!");
                signupOrLogin();
        }
    }
    static void signupUser() {
        System.out.println("(Type '- back' to get back to the start!)\nCreate your username:");
        uncheckedUsername = sc.nextLine();
        if (Objects.equals(uncheckedUsername, "- back")) {
            signupOrLogin();
        } else {
            for (String username : usernames) {                                     //checks if the inputted username is occupied
                usernameInUse = Objects.equals(uncheckedUsername, username);
                break;
            }
            if (usernameInUse) {
                System.out.println("Username is already in use!");
                signupUser();
            } else {
                for (int x = usernames.length - 1; x >= 0; x--) {                   //checks if there is room for a new account
                    if (Objects.equals(usernames[x], "")) {
                        usernamePositionAvailable = true;
                        accountNumber = x;
                    }
                }
                usernames[accountNumber] = uncheckedUsername;
                if (usernamePositionAvailable) {
                    usernamePositionAvailable = false;
                    signupPass();
                } else {
                    System.out.println("No place for username available!");
                    signupOrLogin();
                }
            }
        }
    }
    static void signupPass() {
        System.out.println("Create your password:");
        uncheckedPassword = sc.nextLine();
        if (Objects.equals(uncheckedPassword, "- back")) {
            usernameInUse = false;
            usernames[accountNumber] = null;
            signupOrLogin();
        } else {
            if (uncheckedPassword.contains(" ") || uncheckedPassword.isEmpty()) {                        //requirements for the passwords
                System.out.println("Your password cannot contain spaces or be empty. Try again!");
                signupPass();
            } else {
                passwords[accountNumber] = uncheckedPassword;
                for (int x = 1; x <= 20; x++) {                                                          //lots of \n go brrrrr
                    System.out.print("\n");
                }
                System.out.println("Welcome!");
                consoleStart();
            }
        }
    }
    static void loginUser() {
        System.out.println("(Type '- back' to get back to the start!)\nEnter your username:");
        loginUsername = sc.nextLine();
        switch (loginUsername) {
            case "- back":
                signupOrLogin();
                break;
            case "":
                loginUser();
                break;
            default:
                for (int x = 0; x < usernames.length; x++) {                //checks if the inputted username exists
                    if (Objects.equals(loginUsername, usernames[x])) {
                        usernameExists = true;
                        accountNumber = x;
                        break;
                    }
                }
                if (usernameExists) {                                       //if the username exists, you go on to input the password, if not show an error
                    loginPass();
                } else {
                    System.out.println("This username does not exist, try again.");
                    loginUser();
                }
        }
    }
    static void loginPass() {
        usernameExists = false;
        System.out.println("Enter your password:");
        loginPass = sc.nextLine();
        if (Objects.equals(loginPass, "- back")) {
            signupOrLogin();
        } else {        //du bist toll!
            if (Objects.equals(loginPass, passwords[accountNumber])) {          //if inputted password matches the correct password, \n go brrrrr
                for (int x = 1; x <= 20; x++) {
                    System.out.print("\n");                                     //lots of \n go brrrrr
                }
                System.out.println("Welcome!");
                consoleStart();
            } else {
                System.out.println("Wrong password!");
                loginPass();
            }
        }
    }

    //Console Hub
    static void consoleStart() {
        System.out.println(startString);
        console();
    }       //send user to consoleStart() if start message (type 'help' for commands) should be shown
    static void console() {
        try {                                                                                  //username file gets cleared, so a duplicate doesn't get pasted on top
            Files.newBufferedWriter(pathUsers , StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {                                                                                  //password file gets cleared, so a duplicate doesn't get pasted on top
            Files.newBufferedWriter(pathPass , StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (String username : usernames) {                                                    //array gets written into usernames.txt
            try {
                writerUsers.write(username + "\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        for (String password : passwords) {                                                    //array gets written into passwords.txt
            try {
                writerPass.write(password + "\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        consoleInput = sc.nextLine();
        if (consoleInput.isBlank()) {
            System.out.println("Unknown Command!");
            console();
        } else {
            consoleInputSplit = consoleInput.split(" ");    //input gets split into multiple parts, split up with a " "
            switch (consoleInputSplit[0]) {
                case "he":
                case "help":
                    help();
                    break;
                case "pa":
                case "password":
                    if (consoleInputSplit.length > 1) {
                        switch (consoleInputSplit[1]) {
                            case "ch":
                            case "change":
                                password.paChange();
                                break;
                            case "vi":
                            case "view":
                                password.paView();
                                break;
                            default:
                                System.out.println("Unknown Command!");
                                console();
                        }
                    } else {
                        password.paDefault();
                    }
                    break;
                case "us":
                case "username":
                case "user":
                    if (consoleInputSplit.length > 1) {
                        switch (consoleInputSplit[1]) {
                            case "ch":
                            case "change":
                                user.usChange();
                                break;
                            case "vi":
                            case "view":
                                user.usView();
                                break;
                            default:
                                System.out.println("Unknown Command!");
                                console();
                        }
                    } else {
                        user.usDefault();
                    }
                    break;
                case "timistderbeste":
                    System.out.println("richtig +1P. :o");
                    console();
                    break;
                case "lo":
                case "log":
                case "log out":
                case "logout":
                    logout();
                    break;
                case "qu":
                case "quit":
                case "le":
                case "leave":
                case "ex":
                case "exit":
                    exit();
                    break;
                default:
                    System.out.println("Unknown Command!");
                    console();
            }
        }
    }            //if no message should be shown, send them to console();

    //Commands
    static void help() {
        try {
            for (String line : Files.readAllLines(pathCommands)) {      //read and print all lines from commands.txt
                System.out.println(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Tip: You only have to type the first two letters of the command!");
        console();
    }

    static Main password = new Main();     //create password object
    public void paView() {
        System.out.println("Your password is: " + passwords[accountNumber]);
        console();              //timistderbeste
    }
    public void paChange() {
        System.out.println("(Type '- back' to go back!)\nCreate your new password:");
        unconfirmedPassword = sc.nextLine();
        if (Objects.equals(unconfirmedPassword, passwords[accountNumber])) {
            System.out.println("Your new password cannot be your old password!");
            paChange();
        } else {
            if (unconfirmedPassword.contains(" ") || unconfirmedPassword.isEmpty()) {       //password requirements
                if (unconfirmedPassword.equals("- back")) {
                    consoleStart();
                } else {
                    System.out.println("Your password cannot contain spaces or be empty. Try again!");
                    password.paChange();
                }
            } else {
                password.paConfirm();
            }
        }
        console();
    }
    public void paConfirm() {
        System.out.println("Confirm your new password:");
        confirmPassword = sc.nextLine();
        if (Objects.equals(confirmPassword, unconfirmedPassword)) {
            passwords[accountNumber] = unconfirmedPassword;
            System.out.println("Your password has been changed!");
            consoleStart();
        } else {
            if (Objects.equals(confirmPassword, "- back")) {
                consoleStart();
            } else {
                System.out.println("The passwords do not match. Try again or type '- back' to go back!");
                paConfirm();
            }
        }
    }  //users will be sent here after entering new password in order to confirm it
    public void paDefault() {
        System.out.println("Do you want to view or change your password?\n[1] View\n[2] Change\n[3] Back");
        switch (sc.nextLine()) {
            case "1":
                password.paView();
                break;
            case "2":
                password.paChange();
                break;
            case "3":
                consoleStart();
                break;
            default:
                System.out.println("Please enter a number from 1-3!");
                paDefault();
        }
        console();
    }  //users will be sent here if no second phrase has been given after 'password'

    static Main user = new Main();         //create username object
    public void usView() {
        System.out.println("Your username is: " + usernames[accountNumber]);
        console();
    }
    public void usChange() {
        System.out.println("(Type '- back' to go back!)\nCreate your new username:");
        unconfirmedUser = sc.nextLine();
        if (Objects.equals(unconfirmedUser, usernames[accountNumber])) {
            System.out.println("Your new username cannot be your old username!");
            usChange();
        } else {
            if (Objects.equals(unconfirmedUser, "- back")) {
                consoleStart();
            } else {
                usConfirm();
            }
        }
    }
    public void usConfirm() {
        System.out.println("Do you want to change your username to: " + unconfirmedUser + "?\n[1] Yes\n[2] No");
        switch (sc.next()) {
            case "1":
                usernames[accountNumber] = unconfirmedUser;
                System.out.println("Your username has been successfully changed!");
                sc.nextLine();
                consoleStart();
                break;
            case "2":
                sc.nextLine();
                user.usChange();
                break;
            default:
                System.out.println("Please enter a number from 1-2!");
                user.usConfirm();
        }
    }
    public void usDefault() {
        System.out.println("Do you want to view or change your username?\n[1] View\n[2] Change\n[3] Back");
        switch (sc.nextLine()) {
            case "1":
                user.usView();
                break;
            case "2":
                user.usChange();
                break;
            case "3":
                consoleStart();
                break;
            default:
                System.out.println("Please enter a number from 1-3!");
                usDefault();
        }
        console();
    }

    static void logout() {
        System.out.println("Are you sure you want to log out?\n[1] Yes, log out\n[2] No, go back");
        switch (sc.nextLine()) {
            case "1":
                for (int x = 1; x <= 20; x++) {             //lots of \n go brrrrr
                    System.out.print("\n");
                }
                signupOrLogin();
                break;
            case "2":
                consoleStart();
                break;
            default:
                System.out.println("Please enter a number from 1-2!");
                logout();
        }
    }

    static void exit() {
        System.out.println("Do you want to close and save the program?\n[1] Yes\n[2] No");
        switch (sc.nextLine()) {
            case "1":
                try {                                                                                  //username file gets cleared, so a duplicate doesn't get pasted on top
                    Files.newBufferedWriter(pathUserBackup , StandardOpenOption.TRUNCATE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {                                                                                  //password file gets cleared, so a duplicate doesn't get pasted on top
                    Files.newBufferedWriter(pathPassBackup , StandardOpenOption.TRUNCATE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                for (String username : usernames) {                                                    //array gets written into usernames.txt
                    try {
                        writerUserBackup.write(username + "\n");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                for (String password : passwords) {                                                    //array gets written into passwords.txt
                    try {
                        writerPassBackup.write(password + "\n");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                try {
                    writerUsers.close();    //written userdata gets saved
                    writerPass.close();
                    writerUserBackup.close();
                    writerPassBackup.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "2":
                consoleStart();
                break;
            default:
                System.out.println("Please enter a number from 1-2!");
        }
    }
}
