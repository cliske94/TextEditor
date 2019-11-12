import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.io.PrintWriter;


public class SwingGUI extends JFrame implements ActionListener {
   private final JScrollPane scrollPane;       // Container adds scroll bar
   private final JTextArea outputArea;         // Holds file output
   private final JLabel selectedFileLabel;     // Label for file name
   private final JLabel outputLabel;           // Label for file contents
   private final JTextField selectedFileField; // Holds name of file
   private final JFileChooser fileChooser;     // Enables user to select file
   private final JButton openFileButton;       // Trigger file open
   private final JButton saveFileButton;
   File readFile;
   /* Constructor creates GUI components and adds GUI components
      using a GridBagLayout. */
   SwingGUI() {
      GridBagConstraints layoutConst; // GUI component layout

      // Set frame's title
      super.setTitle("File reader");

      outputLabel = new JLabel("File contents:");
      selectedFileLabel = new JLabel("Selected file:");

      selectedFileField = new JTextField(20);
      selectedFileField.setEditable(false);
      selectedFileField.setText("...");

      outputArea = new JTextArea(10, 25);
      scrollPane = new JScrollPane(outputArea);
      outputArea.setEditable(false);

      openFileButton = new JButton("Open file");
      openFileButton.addActionListener(this);
      openFileButton.addActionListener(new ButtonClickListener());
      
      saveFileButton = new JButton("Save file");
      saveFileButton.setActionCommand("SAVE");
      ButtonClickListener buttonListener = new ButtonClickListener();
      saveFileButton.addActionListener(buttonListener);

      // Create file chooser. It's not added to this frame.
      fileChooser = new JFileChooser();

      // Add components using GridBagLayout
      super.setLayout(new GridBagLayout());

      layoutConst = new GridBagConstraints();
      layoutConst.insets = new Insets(10, 10, 5, 5);
      layoutConst.fill = GridBagConstraints.HORIZONTAL;
      layoutConst.gridx = 0;
      layoutConst.gridy = 0;
      super.add(openFileButton, layoutConst);
      
      layoutConst = new GridBagConstraints();
      layoutConst.insets = new Insets(10, 10, 5, 5);
      layoutConst.gridx = 1;
      layoutConst.fill = GridBagConstraints.HORIZONTAL;
      layoutConst.gridy = 0;
      super.add(saveFileButton, layoutConst);

      layoutConst = new GridBagConstraints();
      layoutConst.insets = new Insets(10, 5, 5, 1);
      layoutConst.anchor = GridBagConstraints.LINE_END;
      layoutConst.gridx = 2;
      layoutConst.gridy = 0;
      super.add(selectedFileLabel, layoutConst);

      layoutConst = new GridBagConstraints();
      layoutConst.insets = new Insets(10, 1, 5, 10);
      layoutConst.fill = GridBagConstraints.HORIZONTAL;
      layoutConst.gridx = 2;
      layoutConst.gridy = 0;
      layoutConst.gridwidth = 2;
      layoutConst.gridheight = 1;
      super.add(selectedFileField, layoutConst);

      layoutConst = new GridBagConstraints();
      layoutConst.insets = new Insets(5, 10, 0, 0);
      layoutConst.fill = GridBagConstraints.HORIZONTAL;
      layoutConst.gridx = 0;
      layoutConst.gridy = 1;
      super.add(outputLabel, layoutConst);

      layoutConst = new GridBagConstraints();
      layoutConst.insets = new Insets(1, 10, 10, 10);
      layoutConst.fill = GridBagConstraints.HORIZONTAL;
      layoutConst.gridx = 0;
      layoutConst.gridy = 2;
      layoutConst.gridheight = 2;
      layoutConst.gridwidth = 4;
      super.add(scrollPane, layoutConst);
   }

   /* Called when openFileButton is pressed. */
   @Override
   public void actionPerformed(ActionEvent event) {
      FileInputStream fileByteStream; // File input stream
      Scanner inFS;                   // Scanner object
      String readLine;                       // Input from file
      readFile = null;                  // Input file
      int fileChooserVal;                    // File chooser

      // Open file chooser dialog and get the file to open
      fileChooserVal = fileChooser.showOpenDialog(this);

      // Check if file was selected
      if (fileChooserVal == JFileChooser.APPROVE_OPTION) {
         readFile = fileChooser.getSelectedFile();

         // Update selected file field
         selectedFileField.setText(readFile.getName());

         // Ensure file is valid
         if (readFile.canRead()) {
            try {
               fileByteStream = new FileInputStream(readFile);
               inFS = new Scanner(fileByteStream);

               // Clear output area
               outputArea.setText(""); 

               // Read until end-of-file
               while (inFS.hasNext()) {
                  readLine = inFS.nextLine();
                  outputArea.append(readLine + "\n");
               }
               outputArea.setEditable(true);
               inFS.close();

            } catch (IOException e) {
               outputArea.append("\n\nError occurred while creating file stream! " + e.getMessage());
            }
         }
         else { // Can't read file
            // Show failure dialog
            JOptionPane.showMessageDialog(this, "Can't read file!");
         }
      }
   }
   
       private class ButtonClickListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            String command = e.getActionCommand();
            
            switch (command) {
                case "SAVE":
                    if (!outputArea.getText().isEmpty()) 
                    {
                        try
                        {
                            createWinBatchFile(readFile.getAbsolutePath() + "saveFile.bat");
                        } catch (IOException exception) {
                            JOptionPane.showMessageDialog(SwingGUI.this, "Couldn't create batch file!");
                        }
                        
                        runWinBatchFile(System.getProperty("user.dir") + "\\saveFile.bat");
                    }
                    else
                    {
                        outputArea.setText("You haven't opened a file yet!");
                    }   break;
                case "RENAME":
                    break;
                default:
                    outputArea.setText("Function not defined yet");
                    break;
            }
        }
    };
       
       public void runWinBatchFile(String fileFullName)
       {
           try 
           {
               Runtime.getRuntime().exec("cmd /c start " + fileFullName);
               
           } catch (IOException exception)
           {
               JOptionPane.showMessageDialog(this, "Can't execute command!");
           }
       }

       public void createWinBatchFile(String name) throws IOException
       {
           //ArrayList<String> words = new ArrayList<String>();
           try
           {
               File file = new File(name);
               PrintWriter printer = new PrintWriter(file);
               printer.write("mkdir saveFileAux" + '\n');
               printer.print("type nul > " + readFile.getAbsolutePath() + '\n');
               printer.print("rem Saved in " + readFile.getAbsolutePath() + '\n');
//               printer.print("@echo off" + '\n');
               String[] words = outputArea.getText().split("\n");
               for (String word : words) 
               {
                   System.out.println(word);
                   printer.print("@echo " + word + " >> " + readFile.getAbsolutePath() + '\n');
               }
               printer.print("exit");
               printer.flush(); 
           } catch (FileNotFoundException exception)
           {
               JOptionPane.showMessageDialog(SwingGUI.this, "Can't open file!");
           }
           
       }
   /* Creates a FileReadFrame and makes it visible */
   public static void main(String[] args) {
      // Creates FileReadFrame and its components
      SwingGUI myFrame = new SwingGUI();

      myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      myFrame.pack();
      myFrame.setVisible(true);
   }
}