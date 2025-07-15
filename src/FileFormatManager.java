import java.io.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

/*
Used to:
- Open files
- Generate Window
- Facilitate user interaction
- Save and format input
-   ...
*/
public class FileFormatManager {
    Frame frame;

    public FileFormatManager() {
        frame = new Frame("Nova Text Editor");

        //TEMPORARY START WITH EMPTY TEXT WINDOW
        TextWindow textWindow = newTextWindow();
        frame.add(textWindow);

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setResizable(true);
        frame.setVisible(true);

        // Using WindowListener for closing the window
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
    }

    // Text Interface for writing and displaying formatted Text
    private class TextWindow extends JTextPane{
       public TextWindow(String[] initString, String[] initStyles){
            
        StyledDocument doc = this.getStyledDocument();

            //Load the text pane with styled text.
            try {
               for (int i=0; i < initString.length; i++) {
                    doc.insertString(doc.getLength(), initString[i],
                                    doc.getStyle(initStyles[i]));
                }
            } catch (BadLocationException ble) {
               System.err.println("Couldn't insert initial text into text pane.");
            }

       }
    }

    public TextWindow newTextWindow(){
        String[] defaultText = {};
        String[] defaultStyle = {};
        TextWindow t = new TextWindow(defaultText, defaultStyle);
        return t;
    }

    // GUI for interacting with Files
    private class FileUI {

    }

    // GUI for interacting with the TextWindows Format
    private class FormatingUI {

   }

}
