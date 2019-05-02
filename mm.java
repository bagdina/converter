
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.application.Application.launch;
import javafx.scene.layout.HBox;

public class mm extends Application implements EventHandler<ActionEvent> {

    private BufferedReader reader = null;
    private int lineCount = 0;

    private Button buttonSelectFile;
    private Button showcode;
    private Button buttonReadLine;
    private Label labelFileName;
    private Text textLineFields;
    private Text code;

    private static final int WINDOW_WIDTH = 500;
    private static final int WINDOW_HEIGHT = 300;
    private FileWriter fstream;
    private FileReader rstream;
    private BufferedWriter out;
    private final int c = 0;
    private int status = 0;
    private int prev = 0;
    private int brackets = 2;
    private final ArrayList<String> svalues = new ArrayList<String>();
    private final ArrayList<String> ivalues = new ArrayList<String>();
    private final ArrayList<String> codes = new ArrayList<String>();

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("File Reader");
        primaryStage.setResizable(true);

        buttonSelectFile = new Button("Select Input Text File");
        buttonSelectFile.setOnAction(this);

        showcode = new Button("Show output file");
        showcode.setOnAction(this);
        showcode.setDisable(true);

        buttonReadLine = new Button("Read Line");
        buttonReadLine.setOnAction(this);
        buttonReadLine.setDisable(true); //User cannot read line until a file has been opened.

        labelFileName = new Label();
        textLineFields = new Text();
        code = new Text();

       

        buttonSelectFile.setMaxWidth(WINDOW_WIDTH / 2);
        showcode.setMaxWidth(WINDOW_WIDTH / 2);
        labelFileName.setMaxWidth(WINDOW_WIDTH / 2);
        buttonReadLine.setMaxWidth(WINDOW_WIDTH / 2);
        textLineFields.setWrappingWidth(WINDOW_WIDTH - 20);
        code.setWrappingWidth(350);
        VBox buttonBox = new VBox();
        buttonBox.setPadding(new Insets(10, 10, 10, 10));  //Sets the space around the buttonBox.
        buttonBox.setSpacing(10);  //Sets the vertical space in pixels between buttons within the box.
        VBox codeBox = new VBox();
        codeBox.setPadding(new Insets(10, 10, 10, 10));  //Sets the space around the buttonBox.
        codeBox.setSpacing(10);
        
        HBox hbox = new HBox();
        buttonBox.getChildren().addAll(
                buttonSelectFile,
                labelFileName,
                buttonReadLine,
                textLineFields,
                showcode
        );
        codeBox.getChildren().add(code);
        hbox.getChildren().addAll(buttonBox, codeBox);
        StackPane root = new StackPane();
        root.getChildren().add(hbox);
        primaryStage.setScene(new Scene(root, 1000, 400));
        
        primaryStage.show();

        try {
            fstream = new FileWriter("out.java", true);
            out = new BufferedWriter(fstream);
            out.write("\nimport java.util.*;");
            out.write("\npublic class out {");
            out.write("\n\tpublic static void main(String[] args) {\n");
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }

    }

    private boolean openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Text File");
        fileChooser.setInitialDirectory(new File("."));
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text Files", "*.txt", "*.py"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile == null) {
            String fileName = "Fox.txt";
            try {
                reader = new BufferedReader(new FileReader("data/" + fileName));
            } catch (IOException e) {
                return false;
            }
            labelFileName.setText(fileName);
            buttonReadLine.setDisable(false);
        } else {
            try {
                reader = new BufferedReader(new FileReader(selectedFile));
                buttonReadLine.setDisable(false);
            } catch (IOException e) {
                showErrorDialog("IO Exception: " + e.getMessage());
                return false;
            }
            labelFileName.setText(selectedFile.getName());
        }

        textLineFields.setText("");
        lineCount = 0;
        return true;
    }

    private String[] readLineIntoFields(char delimiter) {
        String str = null;
        try {
            str = reader.readLine();
        } catch (IOException e) {
            showErrorDialog("readWordsOnLine(): IO Exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(0);
        }

        if (str == null) {
            return null;
        }
        lineCount++;
        return str.split(String.valueOf(delimiter));
    }

    @Override
    public void handle(ActionEvent event) {
        Object source = event.getSource();

        if (source == buttonSelectFile) {
            openFile();
        } else if (source == buttonReadLine) {

            displayNextRecord();
            if (textLineFields.getText() == "END OF FILE") {
                try {
                    fstream = new FileWriter("out.java", true);
                    out = new BufferedWriter(fstream);
                    out.write("\n}}");
                    out.close();
                } catch (Exception e) {//Catch exception if any
                    System.err.println("Error: " + e.getMessage());
                }
            }
        } else if (source == showcode) {
            try {
                code.setText(readFile("out.java"));
            } catch (Exception e) {//Catch exception if any
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private void displayNextRecord() {
        char delimiter = ' ';
        String selectedDelimiter = "Tab";
        if (selectedDelimiter=="Tab") {
            delimiter = '\n';
        }

        String[] fieldList = readLineIntoFields(delimiter);

        if (fieldList == null) {
            textLineFields.setText("END OF FILE");
            buttonReadLine.setDisable(true);
            showcode.setDisable(false);
            return;
        }
        try {
            fstream = new FileWriter("out.java", true);
            out = new BufferedWriter(fstream);
            String s = textLineFields.getText();
            String r = "";
            String br = "";

/////////////removing tab//////////////////
            if (s.contains("\t")) {
                s = s.replace("\t", "");
                status = 1;
                if (prev < status) {
                    prev = 1;

                }

            }
            if (!s.contains("\t")) {
                if (status != 0) {
                    status = 0;
                }
                if (prev > status) {
                    if (brackets > 2) {
                        br += "}\n";
                        brackets -= 1;
                        prev = 0;

                    }

                }

            }

/////////////removing tab//////////////////
            String word[] = s.split(" ");

////////////if...else//////////////////////
            if (s.contains("if") && !s.contains("elif")) {
                String su = s.substring(3, s.length() - 1);
                r += "if(" + su + "){\n";
                brackets += 1;
            }
            if (s.contains("elif")) {
                String su = s.substring(5, s.length() - 1);
                r += "else if(" + su + "){\n";
                brackets += 1;

            }
            if (s.contains("else")) {
                r += "else{\n";
                brackets += 1;

            }
////////////if...else//////////////////////

///////////////for loop////////////////////////////
            if (word[0].equals("for") && word[3].equals("range") && s.contains(",")) {
                r += "for(int " + word[1] + "=" + word[4].substring(1, word[4].length() - 1) + ";" + word[1] + "<" + word[5].substring(0, word[4].length() - 2) + ";" + word[1] + "++){\n";
                brackets += 1;
            } else if (word[0].equals("for") && word[3].equals("range") && !s.contains(",")) {

                r += "for(int " + word[1] + "= 0;" + word[1] + "<" + word[4].substring(1, word[4].length() - 2) + ";" + word[1] + "++){\n";
                brackets += 1;
            }

///////////////for loop////////////////////////////
///////////////data types and simple equations////////////////////////////
            for (int i = 0; i < word.length; i++) {
                if (word[i].equals("=") && !s.contains("+")
                        && !s.contains("-") && !s.contains("*") && !s.contains("/")
                        && !s.contains("%")) {
                    if (svalues.contains(word[i - 1]) || ivalues.contains(word[i - 1])) {
                        r += s + ";\n";

                    } else {
                        try {
                            Integer.parseInt(word[i + 1]);
                            r += "int " + s + ";\n";
                            ivalues.add(word[i - 1]);
                        } catch (NumberFormatException e) {
                            r += "String " + s + ";\n";
                            svalues.add(word[i - 1]);
                        }
                    }
                } else if (word[i].equals("=") && (s.contains("+")
                        || s.contains("-") || s.contains("*") || s.contains("/")
                        || s.contains("%"))) {
                    if (svalues.contains(word[i - 1]) || ivalues.contains(word[i - 1])) {
                        r += s + ";\n";

                    } else if (ivalues.contains(word[i + 1])) {
                        r += "int " + s + ";\n";
                        ivalues.add(word[i - 1]);
                    } else if (!ivalues.contains(word[i + 1])) {
                        try {
                            Integer.parseInt(word[i + 1]);
                            r += "int " + s + ";\n";
                            ivalues.add(word[i - 1]);
                        } catch (NumberFormatException e) {
                            r += "String " + s + ";\n";
                            svalues.add(word[i - 1]);
                        }

                    }

                }
            }
///////////////data types and simple equations////////////////////////////

/////////////////PRINT/////////////////////////////
            if (s.contains("print")) {
                String su = s.substring(6, s.length() - 1);
                if (su.contains(",")) {
                    String a[] = su.split(",");
                    for (int i = 0; i < a.length; i++) {
                        r += "System.out.println(" + a[i] + ");\n";
                    }
                } else {
                    r += "System.out.println(" + su + ");\n";
                }
            }
/////////////////PRINT/////////////////////////////
            r += br;
// String[] parts = s.split(" ");
            out.write(r);
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
        String msg = "";
        for (int i = 0; i < fieldList.length; i++) {
            msg = msg + fieldList[i];
        }
        textLineFields.setText(msg);

    }

    private void showErrorDialog(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(msg);
        alert.showAndWait();
    }
    
    private String readFile(String file) {
        StringBuilder stringBuffer = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {

            bufferedReader = new BufferedReader(new FileReader(file));

            String text;
            while ((text = bufferedReader.readLine()) != null) {
                stringBuffer.append(text+"\n");
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(mm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(mm.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException ex) {
                Logger.getLogger(mm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return stringBuffer.toString();
        
    }

    public static void main(String[] args) {
        launch(args);
    }

}
