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
    private StringBuilder inputBuffer;
    private boolean waitingForInput;
    private String lastInput;
    private boolean isNumericInput;
    private boolean isPasswordMode;
    private int inputStartPosition; // Track where input starts on screen
    private boolean instantMenuMode; // When true, number buttons auto-submit
    private boolean rightAlignInput; // When true, input is right-aligned
    private String promptPrefix; // Store the prompt prefix (e.g., "HK$")
    private String inputPrefix; // Prefix that sticks with input (e.g., "HK$")
    
    // Color scheme for ATM
    private static final Color ATM_BACKGROUND = new Color(30, 30, 40);
    private static final Color SCREEN_COLOR = new Color(20, 120, 180);
    private static final Color BUTTON_COLOR = new Color(70, 70, 90);
    private static final Color ENTER_BUTTON_COLOR = new Color(50, 150, 50);
    private static final Color CANCEL_BUTTON_COLOR = new Color(180, 50, 50);
    private static final Color CLEAR_BUTTON_COLOR = new Color(200, 150, 50);
    
    public ATMGUI() {
        setTitle("ATM Machine");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 750);
        setLocationRelativeTo(null);
        setResizable(false);
        
        inputBuffer = new StringBuilder();
        waitingForInput = false;
        lastInput = "";
        isNumericInput = true;
        isPasswordMode = false;
        inputStartPosition = 0;
        instantMenuMode = false;
        rightAlignInput = false;
        promptPrefix = "";
        inputPrefix = "";
        
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
        
        // Left buttons: top=5, second=6, third=1(View Balance), bottom=3(Transfer)
        int[] leftOptions = {5, 6, 1, 3};
        for (int i = 0; i < 4; i++) {
            JButton btn = new JButton("<");
            btn.setBackground(BUTTON_COLOR);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setFont(new Font("Arial", Font.BOLD, 16));
            btn.setPreferredSize(new Dimension(60, 40));
            final int option = leftOptions[i];
            btn.addActionListener(e -> handleSideButton(option));
            panel.add(btn);
        }
        
        return panel;
    }
    
    private JPanel createRightSideButtons() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 10));
        panel.setBackground(ATM_BACKGROUND);
        
        // Right buttons: top=7, second=8, third=2(Withdraw), bottom=4(Exit)
        int[] rightOptions = {7, 8, 2, 4};
        for (int i = 0; i < 4; i++) {
            JButton btn = new JButton(">");
            btn.setBackground(BUTTON_COLOR);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setFont(new Font("Arial", Font.BOLD, 16));
            btn.setPreferredSize(new Dimension(60, 40));
            final int option = rightOptions[i];
            btn.addActionListener(e -> handleSideButton(option));
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
    
    private void handleSideButton(int option) {
        if (waitingForInput) {
            lastInput = String.valueOf(option);
            waitingForInput = false;
            synchronized (this) {
                notifyAll();
            }
        }
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
            return -1;
        }
        
        instantMenuMode = false; // Disable instant mode after selection
        
        if ("CANCEL".equals(lastInput) || lastInput.isEmpty()) {
            return -1;
        }
        try {
            return Integer.parseInt(lastInput);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
