
// Keypad.java
// Represents the keypad of the ATM
import java.util.Scanner; // program uses Scanner to obtain user input

public class Keypad {
   private Scanner input; // reads data from the command line
   private ATMGUI gui;

   // Constructor that accepts GUI reference
   public Keypad(ATMGUI gui) {
      this.gui = gui;
      if (gui == null) {
         input = new Scanner(System.in);
      }
   } // end Keypad constructor

   // return an integer value entered by user
   public int getInput() {
      if (gui != null) {
         return gui.getNumericInput();
      } else {
         return input.nextInt(); // we assume that user enters an integer
      }
   } // end method getInput
   
   // return an integer value entered by user with password masking
   public int getInputPassword() {
      if (gui != null) {
         return gui.getNumericInput(true);
      } else {
         return input.nextInt(); // we assume that user enters an integer
      }
   } // end method getInputPassword
   
   // return an integer value entered by user with right alignment
   public int getInputRightAlign() {
      if (gui != null) {
         return gui.getNumericInput(false, true, "HK$");
      } else {
         return input.nextInt(); // we assume that user enters an integer
      }
   } // end method getInputRightAlign

   // return a double value entered by user
   public double getInputDouble() {
      if (gui != null) {
         return gui.getDoubleInput();
      } else {
         return input.nextDouble(); // get decimal number input
      }
   } // end method getInputDouble
} // end class Keypad

/**************************************************************************
 * (C) Copyright 1992-2007 by Deitel & Associates, Inc. and *
 * Pearson Education, Inc. All Rights Reserved. *
 * *
 * DISCLAIMER: The authors and publisher of this book have used their *
 * best efforts in preparing the book. These efforts include the *
 * development, research, and testing of the theories and programs *
 * to determine their effectiveness. The authors and publisher make *
 * no warranty of any kind, expressed or implied, with regard to these *
 * programs or to the documentation contained in these books. The authors *
 * and publisher shall not be liable in any event for incidental or *
 * consequential damages in connection with, or arising out of, the *
 * furnishing, performance, or use of these programs. *
 *************************************************************************/