package ui;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final ChessClient client;
    private final Scanner scanner = new Scanner(System.in);


    public Repl(String serverUrl) {client = new ChessClient(serverUrl);}

    private void run() {

    }

    public void runSignedOut() {
        System.out.println("Welcome to Patrick's Chess Game! Sign in to start");
        client.helpLoggedOut();
        System.out.println(client.getResult().getFirst());

        while (!client.getResult().getFirst().equals("exit")) {
            System.out.print("\n" + ">>> ");
            String line = scanner.nextLine();
            try {
                client.evalSignedOut(line);
                System.out.print(client.getResult().getFirst());
            } catch (Throwable e) {
                String msg = e.toString();
                System.out.println(msg);
            }
            if (client.getResult().get(1).equals(1)) {
                runSignedIn();
            }
        }
    }

    public void runSignedIn() {
        System.out.println("You are Signed in!");
        client.helpLoggedIn();
        System.out.println(client.getResult().getFirst());
        List<Object> result = new ArrayList<Object>();
        while (client.getResult().get(1).equals(1)) {
            System.out.print("\n" + ">>> ");
            String line = scanner.nextLine();
            try {
                client.evalSignedIn(line);
                System.out.print(client.getResult().getFirst());
            } catch (Throwable e) {
                String msg = e.toString();
                System.out.println(msg);
            }
        }
    }
}
