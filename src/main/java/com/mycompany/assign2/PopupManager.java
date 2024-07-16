package com.mycompany.assign2;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class PopupManager {
    private final JFrame frame;

    public PopupManager(JFrame frame) {
        this.frame = frame;
    }

    public void showWinnerPopup(String winnerName) {
        String message = "Congratulations, " + winnerName + "! You are the winner!\n Do you want to restart the game?";
        int option = JOptionPane.showConfirmDialog(frame, message, "Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

        if (option == JOptionPane.YES_OPTION) {
            // Trigger a game reset event or similar action
        }
    }

    public void showDrawPopup() {
        int option = JOptionPane.showConfirmDialog(frame, "The game is a draw. Do you want to play again?", "Draw", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

        if (option == JOptionPane.YES_OPTION) {
            // Trigger a game reset event or similar action
        }
    }

    public int promptForBoardSize() {
        String[] options = {"8*5", "10*6", "12*7"};
        String selectedOption = (String) JOptionPane.showInputDialog(frame,
                "Choose board size", "Board Size",
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (selectedOption != null) {
            String[] parts = selectedOption.split("\\*");
            if (parts.length == 2) {
                int width = Integer.parseInt(parts[0]);
                int height = Integer.parseInt(parts[1]);
                return width * height;
            }
        }
        return 8 * 5;
    }
}
