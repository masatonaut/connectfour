package com.mycompany.assign2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/**
 *
 * @author emaoits
 */
public class ConnectFourUI extends javax.swing.JFrame {

    private boolean currentPlayer = true;
    private int boardSize;
    private JLabel[][] labels;
    private Color[][] originalColors;
    private Timer timer;
    private int currentRow;
    private int currentCol;
    private PopupManager popupManager;

    
    public ConnectFourUI() {
        popupManager = new PopupManager(this);
        initComponents();
        boardSize = popupManager.promptForBoardSize();
        //promptForBoardSize();
        createGridLabels();
        adjustGuiSize();
        initializeTimer();
        startFallingAnimation();
    }

    private void createGridLabels() {
        jPanel1.setLayout(new GridLayout(boardSize / 8, 8));
        labels = new JLabel[boardSize / 8][8];
        originalColors = new Color[boardSize / 8][8];

        for (int i = 0; i < boardSize / 8; i++) {
            for (int j = 0; j < 8; j++) {
                labels[i][j] = new JLabel("", SwingConstants.CENTER);
                labels[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                labels[i][j].setOpaque(true);

                originalColors[i][j] = Color.GRAY;

                labels[i][j].setBackground(originalColors[i][j]);
                labels[i][j].setPreferredSize(new Dimension(50, 50));
                jPanel1.add(labels[i][j]);
            }
        }
    }
    
    private void adjustGuiSize() {
        int preferredSize = 12 * 100;
        int margin = 100;
        int totalSize = preferredSize + margin;

        setSize((int) (totalSize*0.6), totalSize/2);
    }
    
    private void initializeTimer() {
        int delay = 1000;
        timer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveCircleDown();
            }
        });
    }

    private void startFallingAnimation() {
        currentRow = 0;
        currentCol = 0;

        labels[currentRow][currentCol].setIcon(createPlayerIcon());
        placeDisc(currentRow, currentCol);
        timer.start();
    }

    private void moveCircleDown() {
        if (currentRow + 1 < boardSize / 8 && labels[currentRow + 1][currentCol].getIcon() == null && originalColors[currentRow + 1][currentCol].equals(Color.GRAY)) {
            labels[currentRow][currentCol].setIcon(null);
            removeDisc(currentRow, currentCol);

            currentRow++;

            labels[currentRow][currentCol].setIcon(createPlayerIcon());
            placeDisc(currentRow, currentCol);

        } else {
            labels[currentRow][currentCol].setIcon(createPlayerIcon());
            placeDisc(currentRow, currentCol);
            checkChain(currentRow, currentCol);
            if(checkForDraw()){
                showDrawPopup();
            }
            
            currentRow = 0;
            
            currentPlayer = !currentPlayer;

            labels[currentRow][currentCol].setIcon(createPlayerIcon()); 
            placeDisc(currentRow, currentCol);
        }
    }

    private void resetGame() {
        for (int row = 0; row < labels.length; row++) {
            for (int col = 0; col < labels[row].length; col++) {
                labels[row][col].setIcon(null);
                labels[row][col].setBackground(Color.GRAY);
            }
        }

        currentPlayer = true;
        startFallingAnimation();
    }


    private void showWinnerPopup(String winnerName) {
        popupManager.showWinnerPopup(winnerName); // Updated to use PopupManager
        resetGame();
    }


    private void placeDisc(int row, int col) {
        labels[row][col].setOpaque(true);
        labels[row][col].setBackground(currentPlayer ? Color.RED : Color.BLUE);
    }
    
    private void removeDisc(int row, int col){
        labels[row][col].setOpaque(true);
        labels[row][col].setBackground(Color.GRAY);
    }


    private ImageIcon createPlayerIcon() {
        int diameter = 50;
        String text = currentPlayer ? "X" : "O";
        Color color = currentPlayer ? Color.WHITE : Color.BLACK;

        BufferedImage image = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color);
        g2d.setFont(new Font("Arial", Font.BOLD, 40));

        FontMetrics fm = g2d.getFontMetrics();
        int x = (diameter - fm.stringWidth(text)) / 2;
        int y = (fm.getAscent() + (diameter - (fm.getAscent() + fm.getDescent())) / 2);
        g2d.drawString(text, x, y);

        g2d.dispose();
        return new ImageIcon(image);
    }

    
    private void moveCircleLeft() {
        
        if(labels[currentRow][currentCol-1].getIcon() == null){
            labels[currentRow][currentCol].setIcon(null);
            removeDisc(currentRow, currentCol);

            currentCol--;

            if (currentCol < 0) {
                currentCol = 7;
            }

            labels[currentRow][currentCol].setIcon(createPlayerIcon());
            placeDisc(currentRow, currentCol);
            
        }
    }

    private void moveCircleRight() {
        
        if(labels[currentRow][currentCol + 1].getIcon() == null){
            labels[currentRow][currentCol].setIcon(null);
            removeDisc(currentRow, currentCol);

            currentCol++;

            if (currentCol > 7) {
                currentCol = 0;
            }

            labels[currentRow][currentCol].setIcon(createPlayerIcon());
            placeDisc(currentRow, currentCol);
        }
    }
    
    
    private void checkChain(int row, int col) {
        if (checkWin(row, col)) {
            String winnerName = currentPlayer ? "Player 1" : "Player 2";
            showWinnerPopup(winnerName);
            resetGame();
        }
    }
    
    private boolean checkWin(int row, int col) {
        Color color = labels[row][col].getBackground();
        if (checkHorizontal(row, col, color)) return true;
        if (checkDirection(row, col, 0, 1, color)) return true;
        if (checkDirection(row, col, 1, 0, color)) return true;
        if (checkDiagonal1(row, col, color)) return true;
        if (checkDiagonal2(row, col, color)) return true;
        return false;
    }
    
    private boolean checkHorizontal(int row, int col, Color color){
        int count = checkRightDirection(row, col, color) + checkLeftDirection(row, col, color);
        return count >= 4;
    }
    
    private boolean checkDiagonal1(int row, int col, Color color){
        int count = checkRightLower(row, col, color) + checkLeftUpper(row, col, color);
        return count >= 4;
    }
    private boolean checkDiagonal2(int row, int col, Color color){
        int count = checkLeftLower(row, col, color) + checkRightUpper(row, col, color);
        return count >= 4;
    }
 
    private int checkRightLower(int row, int col, Color color){
        int count = 1;
        int currentRow = row + 1;
        int currentCol = col + 1;
        while (isValidCell(currentRow, currentCol) && labels[currentRow][currentCol].getBackground().equals(color)) {
            count++;
            currentRow += 1;
            currentCol += 1;
        }
        return count;
    }
    private int checkLeftUpper(int row, int col, Color color){
        int count = 0;
        int currentRow = row - 1;
        int currentCol = col - 1;
        while (isValidCell(currentRow, currentCol) && labels[currentRow][currentCol].getBackground().equals(color)) {
            count++;
            currentRow -= 1;
            currentCol -= 1;
        }
        return count;
    }
    private int checkRightUpper(int row, int col, Color color){
        int count = 1;
        int currentRow = row - 1;
        int currentCol = col + 1;
        while (isValidCell(currentRow, currentCol) && labels[currentRow][currentCol].getBackground().equals(color)) {
            count++;
            currentRow -= 1;
            currentCol += 1;
        }
        return count;
    }
    private int checkLeftLower(int row, int col, Color color){
        int count = 0;
        int currentRow = row + 1;
        int currentCol = col - 1;
        while (isValidCell(currentRow, currentCol) && labels[currentRow][currentCol].getBackground().equals(color)) {
            count++;
            currentRow += 1;
            currentCol -= 1;
        }
        return count;
    }
    
    
    private int checkRightDirection(int row, int col, Color color){
        int count = 1;
        int currentCol = col + 1;
        while (isValidCell(currentRow, currentCol) && labels[currentRow][currentCol].getBackground().equals(color)) {
            count++;
            currentCol += 1;
        }
        return count;
    }
    
    private int checkLeftDirection(int row, int col, Color color){
        int count = 0;
        int currentCol = col - 1;
        while (isValidCell(currentRow, currentCol) && labels[currentRow][currentCol].getBackground().equals(color)) {
            count++;
            currentCol -= 1;
        }
        return count;
    }

    private boolean checkDirection(int row, int col, int rowChange, int colChange, Color color) {
        int count = 1;
        int currentRow = row + rowChange;
        int currentCol = col + colChange;

        while (isValidCell(currentRow, currentCol) && labels[currentRow][currentCol].getBackground().equals(color)) {
            count++;
            currentRow += rowChange;
            currentCol += colChange;
        }

        return count >= 4;
    }
    
    private boolean containsCell(List<int[]> coordinates, int[] cell) {
        for (int[] c : coordinates) {
            if (Arrays.equals(c, cell)) {
                return true;
            }
        }
        return false;
    }
    
    
    private boolean isValidCell(int row, int col) {
        return row >= 0 && row < boardSize / 8 && col >= 0 && col < 8;
    }
    
    private boolean checkForDraw() {
        for (int row = 1; row < labels.length; row++) {
            for (int col = 0; col < labels[row].length; col++) {
                if (labels[row][col].getBackground().equals(Color.GRAY)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void showDrawPopup() {
        popupManager.showDrawPopup(); // Updated to use PopupManager
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 228, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 378, Short.MAX_VALUE)
        );

        jButton1.setText("jButton1");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("jButton2");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(91, 91, 91)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 58, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addGap(33, 33, 33))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(200, 200, 200)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1)
                            .addComponent(jButton2))))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        moveCircleRight();  
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        moveCircleLeft();
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ConnectFourUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ConnectFourUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ConnectFourUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ConnectFourUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ConnectFourUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
