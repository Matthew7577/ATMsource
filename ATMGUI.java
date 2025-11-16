// ATMGUI.java
// Main GUI frame for the ATM system that mimics real ATM interface

import javax.swing.*;
import java.awt.*;

public class ATMGUI extends JFrame {
    private JTextArea displayArea;
    private JPanel keypadPanel;
    private JButton[] numberButtons;
    private JButton clearButton, enterButton, cancelButton;
    private JButton dotButton, doubleZeroButton;
    private JButton[] leftSideButtons;  // L1, L2, L3, L4
    private JButton[] rightSideButtons; // R1, R2, R3, R4
    private String[] activeButtons;  // Track which side buttons are currently active
    private StringBuilder inputBuffer;
    private boolean waitingForInput;
    private String lastInput;
    private boolean isNumericInput;
    private boolean isPasswordMode;
    private int inputStartPosition; // Track where input starts on screen
    private boolean instantMenuMode; // When true, number buttons auto-submit
    private boolean rightAlignInput; // When true, input is right-aligned
    private String inputPrefix; // Prefix that sticks with input (e.g., "HK$")
    private boolean menuSelectionMode; // When true, side buttons can be used
    
    // Color scheme for ATM
    private static final Color ATM_BACKGROUND = new Color(30, 30, 40);
    private static final Color SCREEN_COLOR = new Color(20, 120, 180);
    private static final Color BUTTON_COLOR = new Color(70, 70, 90);
    private static final Color ENTER_BUTTON_COLOR = new Color(50, 150, 50);
    private static final Color CANCEL_BUTTON_COLOR = new Color(180, 50, 50);
    private static final Color CLEAR_BUTTON_COLOR = new Color(200, 150, 50);
    
    // Alert settings
    private static final int ALERT_DISPLAY_SECOND = 2000; // Duration in milliseconds (2000 = 2 seconds)
    
    public ATMGUI() {
        setTitle("ATM Machine");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 750);
        setLocationRelativeTo(null);
        setResizable(false);
        
        inputBuffer = new StringBuilder();
        activeButtons = new String[0];
        waitingForInput = false;
        lastInput = "";
        isNumericInput = true;
        isPasswordMode = false;
        inputStartPosition = 0;
        instantMenuMode = false;
        rightAlignInput = false;
        inputPrefix = "";
        menuSelectionMode = false;
        
        // Main panel with ATM background
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(ATM_BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create display screen
        createDisplayScreen();
        
        // Create number keypad
        createKeypad();
        
        // Assemble the GUI
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(ATM_BACKGROUND);
        
        JPanel displayWithButtons = new JPanel(new BorderLayout());
        displayWithButtons.setBackground(ATM_BACKGROUND);
        displayWithButtons.add(createLeftSideButtons(), BorderLayout.WEST);
        displayWithButtons.add(displayArea, BorderLayout.CENTER);
        displayWithButtons.add(createRightSideButtons(), BorderLayout.EAST);
        
        centerPanel.add(displayWithButtons, BorderLayout.CENTER);
        centerPanel.add(keypadPanel, BorderLayout.SOUTH);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        add(mainPanel);
        setVisible(true);
    }
    
    private void createDisplayScreen() {
        displayArea = new JTextArea(27, 72);
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Consolas", Font.BOLD, 20));
        displayArea.setBackground(SCREEN_COLOR);
        displayArea.setForeground(Color.WHITE);
        displayArea.setLineWrap(false);  // Disable line wrapping
        displayArea.setWrapStyleWord(false);
        displayArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 3),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
    }
    
    private JPanel createLeftSideButtons() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 10));
        panel.setBackground(ATM_BACKGROUND);
        
        leftSideButtons = new JButton[4];
        
        // Left buttons: L1, L2, L3, L4 (top to bottom)
        String[] leftPositions = {"L1", "L2", "L3", "L4"};
        for (int i = 0; i < 4; i++) {
            JButton btn = new JButton("<");
            btn.setBackground(BUTTON_COLOR);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setFont(new Font("Arial", Font.BOLD, 16));
            btn.setPreferredSize(new Dimension(60, 40));
            final String position = leftPositions[i];
            btn.addActionListener(e -> handleSideButton(position));
            leftSideButtons[i] = btn;
            panel.add(btn);
        }
        
        return panel;
    }
    
    private JPanel createRightSideButtons() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 10));
        panel.setBackground(ATM_BACKGROUND);
        
        rightSideButtons = new JButton[4];
        
        // Right buttons: R1, R2, R3, R4 (top to bottom)
        String[] rightPositions = {"R1", "R2", "R3", "R4"};
        for (int i = 0; i < 4; i++) {
            JButton btn = new JButton(">");
            btn.setBackground(BUTTON_COLOR);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setFont(new Font("Arial", Font.BOLD, 16));
            btn.setPreferredSize(new Dimension(60, 40));
            final String position = rightPositions[i];
            btn.addActionListener(e -> handleSideButton(position));
            rightSideButtons[i] = btn;
            panel.add(btn);
        }
        
        return panel;
    }
    
    private void createKeypad() {
        keypadPanel = new JPanel(new BorderLayout(10, 10));
        keypadPanel.setBackground(ATM_BACKGROUND);
        
        // Number pad (1-9, 0)
        JPanel numberPanel = new JPanel(new GridLayout(4, 3, 5, 5));
        numberPanel.setBackground(ATM_BACKGROUND);
        numberButtons = new JButton[10];
        
        // Buttons 1-9
        for (int i = 1; i <= 9; i++) {
            numberButtons[i] = createKeypadButton(String.valueOf(i));
            numberPanel.add(numberButtons[i]);
        }
        
        // Bottom row: 00, 0, dot (.)
        doubleZeroButton = createKeypadButton("00");
        numberPanel.add(doubleZeroButton);
        numberButtons[0] = createKeypadButton("0");
        numberPanel.add(numberButtons[0]);
        dotButton = createKeypadButton(".");
        numberPanel.add(dotButton);
        
        // Control buttons panel
        JPanel controlPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        controlPanel.setBackground(ATM_BACKGROUND);
        
        clearButton = new JButton("CLEAR");
        clearButton.setFont(new Font("Arial", Font.BOLD, 16));
        clearButton.setBackground(CLEAR_BUTTON_COLOR);
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        clearButton.addActionListener(e -> handleClear());
        
        cancelButton = new JButton("CANCEL");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 16));
        cancelButton.setBackground(CANCEL_BUTTON_COLOR);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> handleCancel());
        
        enterButton = new JButton("ENTER");
        enterButton.setFont(new Font("Arial", Font.BOLD, 16));
        enterButton.setBackground(ENTER_BUTTON_COLOR);
        enterButton.setForeground(Color.WHITE);
        enterButton.setFocusPainted(false);
        enterButton.addActionListener(e -> handleEnter());
        
        controlPanel.add(clearButton);
        controlPanel.add(cancelButton);
        controlPanel.add(enterButton);
        
        keypadPanel.add(numberPanel, BorderLayout.CENTER);
        keypadPanel.add(controlPanel, BorderLayout.SOUTH);
    }
    
    private JButton createKeypadButton(String label) {
        JButton button = new JButton(label);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setPreferredSize(new Dimension(80, 60));
        button.setBackground(BUTTON_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.addActionListener(e -> handleNumberButton(label));
        return button;
    }
    
    private void handleNumberButton(String number) {
        if (waitingForInput && isNumericInput) {
            if (instantMenuMode) {
                // In instant menu mode, immediately submit the number
                lastInput = number;
                waitingForInput = false;
                synchronized (this) {
                    notifyAll();
                }
            } else {
                // Normal mode: accumulate input
                inputBuffer.append(number);
                updateInputDisplay();
            }
        }
    }
    
    private String getMaskedInput() {
        if (isPasswordMode) {
            StringBuilder masked = new StringBuilder();
            for (int i = 0; i < inputBuffer.length(); i++) {
                masked.append("*");
            }
            return masked.toString();
        }
        return inputBuffer.toString();
    }
    
    private void handleClear() {
        if (waitingForInput) {
            inputBuffer.setLength(0);
            updateInputDisplay();
        }
    }
    
    private void handleCancel() {
        if (waitingForInput) {
            inputBuffer.setLength(0);
            lastInput = "CANCEL";
            waitingForInput = false;
            synchronized (this) {
                notifyAll();
            }
        }
    }
    
    private void handleEnter() {
        synchronized (this) {
            if (waitingForInput) {
                if (inputBuffer.length() > 0) {
                    lastInput = inputBuffer.toString();
                    inputBuffer.setLength(0);
                }
                waitingForInput = false;
                notifyAll();
            }
        }
    }
    
    private synchronized void handleSideButton(String position) {
        // Only process if button is in the active array and we're waiting for menu input
        if (!waitingForInput || !menuSelectionMode || !isButtonActive(position)) {
            return; // Silently ignore inactive button presses
        }
        
        lastInput = position;
        waitingForInput = false;
        notifyAll();
    }
    
    // Check if a button position is in the active buttons array
    private boolean isButtonActive(String position) {
        for (String active : activeButtons) {
            if (active.equals(position)) {
                return true;
            }
        }
        return false;
    }
    
    // Set which side buttons are currently active (responsive to clicks)
    public void setActiveSideButtons(String... positions) {
        activeButtons = positions;
    }
    
    // Clear all active side buttons
    public void clearActiveSideButtons() {
        activeButtons = new String[0];
    }
    
    private void updateInputDisplay() {
        SwingUtilities.invokeLater(() -> {
            String currentText = displayArea.getText();
            // Only update if we have a valid start position
            if (inputStartPosition <= currentText.length()) {
                String textBeforeInput = currentText.substring(0, inputStartPosition);
                
                if (rightAlignInput) {
                    // Right-align the input on the current line
                    // Find the last newline to get current line start
                    int lastNewline = textBeforeInput.lastIndexOf('\n');
                    String beforeCurrentLine = lastNewline >= 0 ? textBeforeInput.substring(0, lastNewline + 1) : "";
                    
                    // Calculate how much padding needed (72 is display width, accounting for input)
                    String inputText = getMaskedInput();
                    int totalWidth = 72; // Display area width
                    
                    // Combine inputPrefix and input for right alignment
                    String combinedText = inputPrefix + inputText;
                    int combinedLen = combinedText.length();
                    int paddingNeeded = totalWidth - combinedLen;
                    
                    if (paddingNeeded > 0) {
                        String padding = String.format("%" + paddingNeeded + "s", "");
                        displayArea.setText(beforeCurrentLine + padding + combinedText);
                    } else {
                        displayArea.setText(beforeCurrentLine + combinedText);
                    }
                } else {
                    // Normal left-aligned input
                    displayArea.setText(textBeforeInput + getMaskedInput());
                }
                displayArea.setCaretPosition(displayArea.getDocument().getLength());
            }
        });
    }
    
    public void displayMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            displayArea.append(message);
            displayArea.setCaretPosition(displayArea.getDocument().getLength());
        });
    }
    
    public void displayMessageLine(String message) {
        displayMessage(message + "\n");
    }
    
    public void clearScreen() {
        SwingUtilities.invokeLater(() -> displayArea.setText(""));
    }
    
    public void showAlert(String title, String message) {
        // Clear the screen first
        clearScreen();
        
        SwingUtilities.invokeLater(() -> {
            // Calculate padding for centering
            int screenWidth = 72;
            int boxWidth = 60;
            
            // Split message into multiple lines if it contains newlines
            String[] messageLines = message.split("\n");
            
            // Create centered box with alert message
            String topBorder = centerText("╔" + repeatChar('═', boxWidth - 2) + "╗", screenWidth);
            String titleLine = centerText("║ " + centerText(title, boxWidth - 4) + " ║", screenWidth);
            String separator = centerText("║" + repeatChar('─', boxWidth - 2) + "║", screenWidth);
            String bottomBorder = centerText("╚" + repeatChar('═', boxWidth - 2) + "╝", screenWidth);
            
            // Add vertical spacing and display alert (centered vertically too)
            displayArea.append("\n\n\n\n");
            displayArea.append(topBorder + "\n");
            displayArea.append(titleLine + "\n");
            displayArea.append(separator + "\n");
            
            // Display each message line
            for (String line : messageLines) {
                String msgLine = centerText("║ " + centerText(line, boxWidth - 4) + " ║", screenWidth);
                displayArea.append(msgLine + "\n");
            }
            
            displayArea.append(bottomBorder + "\n");
            displayArea.setCaretPosition(displayArea.getDocument().getLength());
        });
        
        // Wait before dismissing
        try {
            Thread.sleep(ALERT_DISPLAY_SECOND);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    // Helper method to repeat a character n times
    private String repeatChar(char c, int count) {
        StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            sb.append(c);
        }
        return sb.toString();
    }
    
    // Helper method to center text within a fixed width
    private String centerText(String text, int width) {
        int padding = (width - text.length()) / 2;
        int paddingRight = width - text.length() - padding;
        return String.format("%" + padding + "s%s%" + paddingRight + "s", "", text, "");
    }
    
    public synchronized void waitForEnter() {
        // Set up a special waiting mode that only responds to ENTER button
        isNumericInput = false;
        isPasswordMode = false;
        inputBuffer.setLength(0);
        lastInput = "";
        waitingForInput = true;
        
        // Capture input start position after any pending display updates
        SwingUtilities.invokeLater(() -> {
            inputStartPosition = displayArea.getText().length();
        });
        
        try {
            while (waitingForInput) {
                wait();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public synchronized String getInput(boolean numeric, boolean passwordMode) {
        return getInput(numeric, passwordMode, false);
    }
    
    public synchronized String getInput(boolean numeric, boolean passwordMode, boolean rightAlign) {
        return getInput(numeric, passwordMode, rightAlign, "");
    }
    
    public synchronized String getInput(boolean numeric, boolean passwordMode, boolean rightAlign, String prefix) {
        isNumericInput = numeric;
        isPasswordMode = passwordMode;
        rightAlignInput = rightAlign;
        inputPrefix = prefix;
        inputBuffer.setLength(0);
        lastInput = "";
        waitingForInput = true;
        
        // Capture input start position after any pending display updates
        SwingUtilities.invokeLater(() -> {
            inputStartPosition = displayArea.getText().length();
            // Show initial prefix if right-aligned
            if (rightAlign && !prefix.isEmpty()) {
                updateInputDisplay();
            }
        });
        
        try {
            while (waitingForInput) {
                wait();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "";
        }
        
        return lastInput;
    }
    
    public int getNumericInput() {
        return getNumericInput(false, false);
    }
    
    public int getNumericInput(boolean passwordMode) {
        return getNumericInput(passwordMode, false);
    }
    
    public int getNumericInput(boolean passwordMode, boolean rightAlign) {
        return getNumericInput(passwordMode, rightAlign, "");
    }
    
    public int getNumericInput(boolean passwordMode, boolean rightAlign, String prefix) {
        String input = getInput(true, passwordMode, rightAlign, prefix);
        if ("CANCEL".equals(input) || input.isEmpty()) {
            return -1;
        }
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    public double getDoubleInput() {
        String input = getInput(true, false);
        if ("CANCEL".equals(input) || input.isEmpty()) {
            return -1;
        }
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    public synchronized int getMenuSelection() {
        // For menu selections, allow both side buttons and keypad
        isNumericInput = true;  // Allow keypad input for menu
        isPasswordMode = false;
        instantMenuMode = true; // Enable instant submission for menu
        menuSelectionMode = true; // Enable side buttons for menu
        inputBuffer.setLength(0);
        lastInput = "";
        waitingForInput = true;
        
        // Enable only the buttons used in main menu (L3, R3, L4, R4)
        setActiveSideButtons("L3", "R3", "L4", "R4");
        
        // Capture input start position after any pending display updates
        SwingUtilities.invokeLater(() -> {
            inputStartPosition = displayArea.getText().length();
        });
        
        try {
            while (waitingForInput) {
                wait();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return -1;
        }
        
        instantMenuMode = false; // Disable instant mode after selection
        menuSelectionMode = false; // Disable side buttons after selection
        clearActiveSideButtons(); // Clear active buttons after menu selection
        
        if ("CANCEL".equals(lastInput) || lastInput.isEmpty()) {
            return -1;
        }
        
        // Map button positions to menu options for main menu
        // Main menu has options at L3=1, R3=2, L4=3, R4=4
        int selection = mapButtonPositionToMainMenuOption(lastInput);
        if (selection != -1) {
            return selection;
        }
        
        // If not a button position, try to parse as direct number input
        try {
            return Integer.parseInt(lastInput);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    // Map button position (L1-L4, R1-R4) to main menu options
    // This method is specifically for the main menu screen layout
    private int mapButtonPositionToMainMenuOption(String position) {
        // Main menu layout: L3=1, R3=2, L4=3, R4=4
        switch (position) {
            case "L3": return 1; // View Balance
            case "R3": return 2; // Withdraw
            case "L4": return 3; // Transfer
            case "R4": return 4; // Exit
            default: return -1;  // Button not mapped to any option
        }
    }
    
    // Get menu selection for withdrawal menu with side buttons
    public synchronized int getWithdrawalMenuSelection() {
        // For withdrawal menu selections, allow both side buttons and keypad
        isNumericInput = true;  // Allow keypad input for menu
        isPasswordMode = false;
        instantMenuMode = true; // Enable instant submission for menu
        menuSelectionMode = true; // Enable side buttons for menu
        inputBuffer.setLength(0);
        lastInput = "";
        waitingForInput = true;
        
        // Enable buttons used in withdrawal menu (L2, R2, L3, R3, L4, R4)
        setActiveSideButtons("L2", "R2", "L3", "R3", "L4", "R4");
        
        // Capture input start position after any pending display updates
        SwingUtilities.invokeLater(() -> {
            inputStartPosition = displayArea.getText().length();
        });
        
        try {
            while (waitingForInput) {
                wait();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return -1;
        }
        
        instantMenuMode = false; // Disable instant mode after selection
        menuSelectionMode = false; // Disable side buttons after selection
        clearActiveSideButtons(); // Clear active buttons after menu selection
        
        if ("CANCEL".equals(lastInput) || lastInput.isEmpty()) {
            return -1;
        }
        
        // Map button positions to withdrawal menu options
        // Withdrawal menu: L2=1, R2=2, L3=3, R3=4, L4=5, R4=6
        int selection = mapButtonPositionToWithdrawalMenuOption(lastInput);
        if (selection != -1) {
            return selection;
        }
        
        // If not a button position, try to parse as direct number input
        try {
            return Integer.parseInt(lastInput);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    // Map button position to withdrawal menu options
    private int mapButtonPositionToWithdrawalMenuOption(String position) {
        // Withdrawal menu layout: L2=1, R2=2, L3=3, R3=4, L4=5, R4=6
        switch (position) {
            case "L2": return 1; // $200
            case "R2": return 2; // $500
            case "L3": return 3; // $1,000
            case "R3": return 4; // $2,000
            case "L4": return 5; // $5,000
            case "R4": return 6; // Other amount
            default: return -1;  // Button not mapped to any option
        }
    }
}
