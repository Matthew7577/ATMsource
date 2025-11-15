// ATMCaseStudyGUI.java
// Driver program for the ATM case study with GUI

import javax.swing.SwingUtilities;

public class ATMCaseStudyGUI {
   // main method creates and runs the ATM with GUI
   public static void main(String[] args) {
      // Create GUI on the Event Dispatch Thread
      SwingUtilities.invokeLater(() -> {
         ATMGUI gui = new ATMGUI();
         ATM theATM = new ATM(gui);
         
         // Run ATM in a separate thread so GUI remains responsive
         new Thread(() -> theATM.run()).start();
      });
   } // end main
} // end class ATMCaseStudyGUI
