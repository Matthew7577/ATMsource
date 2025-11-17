// ATMCaseStudy.java
// Driver program for the ATM case study

public class ATMCaseStudy {
   // main method creates and runs the ATM
   public static void main(String[] args) {
      ATMGUI gui = new ATMGUI();
      ATM theATM = new ATM(gui);
      theATM.run();
   } // end main
} // end class ATMCaseStudy
