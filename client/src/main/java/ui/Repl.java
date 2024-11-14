package ui;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final ChessClient client;

    public Repl(String serverUrl) {client = new ChessClient(serverUrl);}

    public void runSignedOut() {
        System.out.println("Welcome to Patrick's Chess Game! Sign in to start");
//        System.out.println(client.help(0));
        Scanner scanner = new Scanner(System.in);
        List<Object> result = new ArrayList<Object>();
        result.add("");
        result.add(0);
        while (!result.getFirst().equals("exit")) {
            System.out.print("\n" + ">>> ");
            String line = scanner.nextLine();

            try {
                result.set(0,client.evalSignedOut(line));
                System.out.print(result.getFirst());
            } catch (Throwable e) {
                String msg = e.toString();
                System.out.println(msg);
            }
            if (result.get(1).equals(1)) {
                runSignedIn();
            }
        }
    }

    public void runSignedIn() {

    }
}
