// ui/BaseMenu.java
package com.etextbook.ui;

import com.etextbook.model.User;
import com.etextbook.util.SessionManager;
import java.util.Scanner;

public abstract class BaseMenu {
    protected final User user;
    protected final String sessionId;
    protected final Scanner scanner;

    public BaseMenu(User user, String sessionId) {
        this.user = user;
        this.sessionId = sessionId;
        this.scanner = new Scanner(System.in);
    }

    public void show() {
        while (true) {
            if (!SessionManager.getUser(sessionId).isPresent()) {
                System.out.println("Session expired. Please login again.");
                break;
            }

            displayMenu();
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (!handleMenuChoice(choice)) {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    protected abstract void displayMenu();
    protected abstract boolean handleMenuChoice(int choice);
}