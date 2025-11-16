// ATMCaseStudyGUI.java
// Driver program for the ATM case study with GUI

public class ATMCaseStudyGUI {
   // main method creates and runs the ATM with GUI
   public static void main(String[] args) {
      ATMGUI gui = new ATMGUI();
      ATM theATM = new ATM(gui);
      theATM.run();
   } // end main
} // end class ATMCaseStudyGUI
