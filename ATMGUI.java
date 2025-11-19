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
    private JButton[] leftSideButtons; // L1, L2, L3, L4
    private JButton[] rightSideButtons; // R1, R2, R3, R4
    private JButton cardButton; // Card slot button
    private JButton cashButton; // Cash slot button
    private boolean cardInserted; // Track if card is inserted
    private boolean allowCardRemoval; // Track if card removal is allowed (only on exit)
    private boolean waitingForCashTaken; // Track if waiting for cash to be taken
    private String[] activeButtons; // Track which side buttons are currently active
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
    private Thread alertThread; // Track the alert timer thread
    private volatile boolean alertDismissed; // Track if alert was manually dismissed

    // Color scheme for ATM
    private static final Color ATM_BACKGROUND = new Color(30, 30, 40);
    private static final Color SCREEN_COLOR = new Color(20, 120, 180);
    private static final Color BUTTON_COLOR = new Color(70, 70, 90);
    private static final Color ENTER_BUTTON_COLOR = new Color(50, 150, 50);
    private static final Color CANCEL_BUTTON_COLOR = new Color(180, 50, 50);
    private static final Color CLEAR_BUTTON_COLOR = new Color(200, 150, 50);

    private final static int EMPTY = -2;

    public ATMGUI() {
        // Force cross-platform Look and Feel for consistent appearance
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            // If setting L&F fails, continue with default
        }

        setTitle("ATM Machine");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 750);
        setLocationRelativeTo(null);
        setResizable(false);

        inputBuffer = new StringBuilder();
        activeButtons = new String[0];
        cardInserted = false;
        allowCardRemoval = false;
        waitingForCashTaken = false;
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
        mainPanel.add(createCardAndCashButtons(), BorderLayout.EAST);

        add(mainPanel);
        setVisible(true);
    }

    private void createDisplayScreen() {
        displayArea = new JTextArea(27, 72);
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Consolas", Font.BOLD, 20));
        displayArea.setBackground(SCREEN_COLOR);
        displayArea.setForeground(Color.WHITE);
        displayArea.setLineWrap(false); // Disable line wrapping
        displayArea.setWrapStyleWord(false);
        displayArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 3),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
    }

    private JPanel createLeftSideButtons() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 10));
        panel.setBackground(ATM_BACKGROUND);

        leftSideButtons = new JButton[4];

        // Left buttons: L1, L2, L3, L4 (top to bottom)
        String[] leftPositions = { "L1", "L2", "L3", "L4" };
        for (int i = 0; i < 4; i++) {
            JButton btn = new JButton(">");
            btn.setOpaque(true);
            btn.setBorderPainted(false);
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
        String[] rightPositions = { "R1", "R2", "R3", "R4" };
        for (int i = 0; i < 4; i++) {
            JButton btn = new JButton("<");
            btn.setOpaque(true);
            btn.setBorderPainted(false);
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

    private JPanel createCardAndCashButtons() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ATM_BACKGROUND);

        // Vertical spacing to center the buttons
        mainPanel.add(Box.createVerticalGlue(), BorderLayout.NORTH);
        mainPanel.add(Box.createVerticalGlue(), BorderLayout.SOUTH);

        // Center panel for card and cash buttons
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 5, 15));
        centerPanel.setBackground(ATM_BACKGROUND);

        // Card slot button
        cardButton = new JButton("<html><center>CARD<br>SLOT</center></html>");
        cardButton.setOpaque(true);
        cardButton.setBorderPainted(false);
        cardButton.setBackground(new Color(180, 50, 50)); // Red when no card
        cardButton.setForeground(Color.WHITE);
        cardButton.setFocusPainted(false);
        cardButton.setFont(new Font("Arial", Font.BOLD, 12));
        cardButton.setPreferredSize(new Dimension(70, 60));
        cardButton.addActionListener(e -> handleCardButton());

        // Cash dispenser button
        cashButton = new JButton("<html><center>TAKE<br>CASH</center></html>");
        cashButton.setOpaque(true);
        cashButton.setBorderPainted(false);
        cashButton.setBackground(new Color(180, 120, 50));
        cashButton.setForeground(Color.WHITE);
        cashButton.setFocusPainted(false);
        cashButton.setFont(new Font("Arial", Font.BOLD, 12));
        cashButton.setPreferredSize(new Dimension(70, 60));
        cashButton.setEnabled(false); // Disabled by default
        cashButton.addActionListener(e -> handleCashButton());

        centerPanel.add(cardButton);
        centerPanel.add(cashButton);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private void createKeypad() {
        keypadPanel = new JPanel(new BorderLayout(10, 10));
        keypadPanel.setBackground(ATM_BACKGROUND);

        // 4x4 grid: 3 columns for numpad + 1 column for control buttons
        JPanel gridPanel = new JPanel(new GridLayout(4, 4, 5, 5));
        gridPanel.setBackground(ATM_BACKGROUND);
        numberButtons = new JButton[10];

        // Row 1: 1, 2, 3, CLEAR
        for (int i = 1; i <= 3; i++) {
            numberButtons[i] = createKeypadButton(String.valueOf(i));
            gridPanel.add(numberButtons[i]);
        }
        clearButton = new JButton("CLEAR");
        clearButton.setOpaque(true);
        clearButton.setBorderPainted(false);
        clearButton.setFont(new Font("Arial", Font.BOLD, 14));
        clearButton.setBackground(CLEAR_BUTTON_COLOR);
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        clearButton.setPreferredSize(new Dimension(100, 60));
        clearButton.addActionListener(e -> handleClear());
        gridPanel.add(clearButton);

        // Row 2: 4, 5, 6, CANCEL
        for (int i = 4; i <= 6; i++) {
            numberButtons[i] = createKeypadButton(String.valueOf(i));
            gridPanel.add(numberButtons[i]);
        }
        cancelButton = new JButton("CANCEL");
        cancelButton.setOpaque(true);
        cancelButton.setBorderPainted(false);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.setBackground(CANCEL_BUTTON_COLOR);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setPreferredSize(new Dimension(100, 60));
        cancelButton.addActionListener(e -> handleCancel());
        gridPanel.add(cancelButton);

        // Row 3: 7, 8, 9, ENTER
        for (int i = 7; i <= 9; i++) {
            numberButtons[i] = createKeypadButton(String.valueOf(i));
            gridPanel.add(numberButtons[i]);
        }
        enterButton = new JButton("ENTER");
        enterButton.setOpaque(true);
        enterButton.setBorderPainted(false);
        enterButton.setFont(new Font("Arial", Font.BOLD, 14));
        enterButton.setBackground(ENTER_BUTTON_COLOR);
        enterButton.setForeground(Color.WHITE);
        enterButton.setFocusPainted(false);
        enterButton.setPreferredSize(new Dimension(100, 60));
        enterButton.addActionListener(e -> handleEnter());
        gridPanel.add(enterButton);

        // Row 4: 00, 0, dot, empty
        doubleZeroButton = createKeypadButton("00");
        gridPanel.add(doubleZeroButton);
        numberButtons[0] = createKeypadButton("0");
        gridPanel.add(numberButtons[0]);
        dotButton = createKeypadButton(".");
        gridPanel.add(dotButton);
        JPanel emptyPanel = new JPanel();
        emptyPanel.setBackground(ATM_BACKGROUND);
        gridPanel.add(emptyPanel);

        keypadPanel.add(gridPanel, BorderLayout.CENTER);
    }

    private JButton createKeypadButton(String label) {
        JButton button = new JButton(label);
        button.setOpaque(true);
        button.setBorderPainted(false);
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

    private synchronized void handleCashButton() {
        // Dismiss alert when cash button is clicked
        if (waitingForCashTaken) {
            waitingForCashTaken = false;
            notifyAll();
        }
        alertDismissed = true;
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
        // Only process if button is in the active array and we're waiting for menu
        // input
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
                    int paddingNeeded = totalWidth - combinedLen - 1; // -1 for one space on right

                    if (paddingNeeded > 0) {
                        String padding = String.format("%" + paddingNeeded + "s", "");
                        displayArea.setText(beforeCurrentLine + padding + combinedText + " ");
                    } else {
                        displayArea.setText(beforeCurrentLine + combinedText + " ");
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

    public void showAlert(String title, String message, double seconds) {
        // Clear the screen first
        clearScreen();
        alertDismissed = false;

        // Enable cash button if this is a cash dispensed alert
        if (title.equals("Cash Dispensed")) {
            SwingUtilities.invokeLater(() -> cashButton.setEnabled(true));
        }

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

        // Wait before dismissing (convert seconds to milliseconds)
        // But allow early dismissal via cash button click
        alertThread = new Thread(() -> {
            try {
                long waitTime = (long) (seconds * 1000);
                long startTime = System.currentTimeMillis();
                while (!alertDismissed && (System.currentTimeMillis() - startTime) < waitTime) {
                    Thread.sleep(100); // Check every 100ms
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                // Disable cash button after alert is dismissed
                SwingUtilities.invokeLater(() -> cashButton.setEnabled(false));
            }
        });
        alertThread.start();

        // Wait for alert thread to complete
        try {
            alertThread.join();
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

    public synchronized boolean waitForEnterOrCancel() {
        // Set up a waiting mode that responds to ENTER or CANCEL button
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

        // Return true if ENTER was pressed, false if CANCEL was pressed
        return !lastInput.equals("CANCEL");
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

    // Get integer input
    public int getNumericInput(boolean passwordMode, boolean rightAlign, String prefix) {
        String input = getInput(true, passwordMode, rightAlign, prefix);
        if ("CANCEL".equals(input)) {
            return -1; // CANCEL button pressed
        }
        if (input.isEmpty()) {
            return EMPTY; // Empty input (Enter pressed with no input)
        }
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return EMPTY; // Invalid input
        }
    }

    // Get double input
    public double getDoubleInput(boolean rightAlign, String prefix) {
        String input = getInput(true, false, rightAlign, prefix);
        if ("CANCEL".equals(input)) {
            return -1; // CANCEL button pressed
        }
        if (input.isEmpty()) {
            return EMPTY; // Empty input (Enter pressed with no input)
        }
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            return EMPTY; // Invalid input
        }
    }

    public synchronized int getMenuSelection() {
        // For menu selections, allow both side buttons and keypad
        isNumericInput = true; // Allow keypad input for menu
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

        if ("CANCEL".equals(lastInput)) {
            return -1;
        }

        if (lastInput.isEmpty()) {
            return EMPTY; // Empty input
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
            return EMPTY; // Invalid input
        }
    }

    // Map button position (L1-L4, R1-R4) to main menu options
    // This method is specifically for the main menu screen layout
    private int mapButtonPositionToMainMenuOption(String position) {
        // Main menu layout: L3=1, R3=2, L4=3, R4=4
        switch (position) {
            case "L3":
                return 1; // View Balance
            case "R3":
                return 2; // Withdraw
            case "L4":
                return 3; // Transfer
            case "R4":
                return 4; // Exit
            default:
                return -1; // Button not mapped to any option
        }
    }

    // Get menu selection for withdrawal menu with side buttons
    public synchronized int getWithdrawalMenuSelection() {
        // For withdrawal menu selections, allow both side buttons and keypad
        isNumericInput = true; // Allow keypad input for menu
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

        if ("CANCEL".equals(lastInput)) {
            return -1;
        }

        if (lastInput.isEmpty()) {
            return EMPTY; // Empty input
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
            return EMPTY; // Invalid input
        }
    }

    // Map button position to withdrawal menu options
    private int mapButtonPositionToWithdrawalMenuOption(String position) {
        // Withdrawal menu layout: L2=1, R2=2, L3=3, R3=4, L4=5, R4=6
        switch (position) {
            case "L2":
                return 1; // $200
            case "R2":
                return 2; // $500
            case "L3":
                return 3; // $1,000
            case "R3":
                return 4; // $2,000
            case "L4":
                return 5; // $5,000
            case "R4":
                return 6; // Other amount
            default:
                return -1; // Button not mapped to any option
        }
    }

    // Get menu selection for post-transaction menu with side buttons
    public synchronized int getPostTransactionMenuSelection() {
        // For post-transaction menu selections, allow both side buttons and keypad
        isNumericInput = true; // Allow keypad input for menu
        isPasswordMode = false;
        instantMenuMode = true; // Enable instant submission for menu
        menuSelectionMode = true; // Enable side buttons for menu
        inputBuffer.setLength(0);
        lastInput = "";
        waitingForInput = true;

        // Enable buttons used in post-transaction menu (L4, R4)
        setActiveSideButtons("L4", "R4");

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

        if ("CANCEL".equals(lastInput)) {
            return -1;
        }

        if (lastInput.isEmpty()) {
            return EMPTY; // Empty input
        }

        // Map button positions to post-transaction menu options
        // Post-transaction menu: L4=1, R4=2
        int selection = mapButtonPositionToPostTransactionMenuOption(lastInput);
        if (selection != -1) {
            return selection;
        }

        // If not a button position, try to parse as direct number input
        try {
            return Integer.parseInt(lastInput);
        } catch (NumberFormatException e) {
            return EMPTY; // Invalid input
        }
    }

    // Map button position to post-transaction menu options
    private int mapButtonPositionToPostTransactionMenuOption(String position) {
        // Post-transaction menu layout: L4=1 (Continue), R4=2 (Eject)
        switch (position) {
            case "L4":
                return 1; // Continue Operations
            case "R4":
                return 2; // Eject Card
            default:
                return -1; // Button not mapped to any option
        }
    }

    // Handle card button click
    private synchronized void handleCardButton() {
        if (!cardInserted) {
            // Card insertion
            cardInserted = true;
            SwingUtilities.invokeLater(() -> {
                cardButton.setBackground(new Color(50, 150, 50)); // Green when card inserted
            });
            notifyAll(); // Wake up waiting thread
        } else if (allowCardRemoval) {
            // Card removal (only allowed during exit)
            cardInserted = false;
            allowCardRemoval = false;
            SwingUtilities.invokeLater(() -> {
                cardButton.setBackground(new Color(180, 50, 50)); // Red when card removed
            });
            notifyAll(); // Wake up waiting thread for card removal
        }
        // If card is inserted but removal not allowed, do nothing (button stays green)
    }

    // Display card insertion screen
    public void displayCardInsertionScreen() {
        clearScreen();
        int screenWidth = 72;
        displayMessageLine("\n\n\n\n");
        displayMessageLine(centerText("Welcome to use the ATM", screenWidth));
        displayMessageLine("");
        displayMessageLine(centerText("Please insert your card", screenWidth));
    }

    // Wait for card insertion
    public synchronized boolean waitForCardInsertion() {
        cardInserted = false;
        waitingForInput = false; // Don't allow any input during card insertion

        try {
            while (!cardInserted) {
                wait();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }

        return cardInserted;
    }

    // Reset card state (for logout)
    public synchronized void resetCardState() {
        cardInserted = false;
        SwingUtilities.invokeLater(() -> {
            cardButton.setBackground(new Color(180, 50, 50)); // Red when no card
        });
    }

    // Wait for card to be removed (after exit)
    public synchronized void waitForCardRemoval() {
        allowCardRemoval = true; // Enable card removal
        try {
            while (cardInserted) {
                wait();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        allowCardRemoval = false; // Disable card removal after it's removed
    }

    // Wait for cash to be taken from the slot
    public synchronized void waitForCashTaken() {
        waitingForCashTaken = true;
        SwingUtilities.invokeLater(() -> cashButton.setEnabled(true));

        try {
            while (waitingForCashTaken) {
                wait();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        SwingUtilities.invokeLater(() -> cashButton.setEnabled(false));
    }
}
