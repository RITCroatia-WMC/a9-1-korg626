import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * Displays data in a table format allowing the user to sort the table by the
 * data in any column by clicking on the column header.
 */
public class Filters extends Application {

    private List<String[]> data;
    private List<List<Label>> labels;  // Corrected variable name

    @Override
    public void start(Stage stage) throws Exception {
        // The filename will be passed through as a command line parameter
        List<String> args = getParameters().getRaw();
        FileReader file = new FileReader(args.get(0));
        BufferedReader fin = new BufferedReader(file);

        // If the data is too big, add scroll bars
        ScrollPane scroller = new ScrollPane();
        scroller.setMaxSize(1000, 600);

        GridPane pane = new GridPane();
        data = new ArrayList<>();
        labels = new ArrayList<>();  // Corrected variable name

        // Use the header to create the first row as buttons.
        String[] header = fin.readLine().trim().split(",");
        int col = 0;
        for (String value : header) {
            Button button = new Button(value);
            final int colIndex = col;
            button.setOnAction(e -> {
                System.out.println("Button " + value + " pressed");
                sortDataByColumn(colIndex);
                update();
            });
            pane.add(button, col, 0);
            col++;
        }

        // Use the rest of the data to fill in the labels.
        Stream<String> lines = fin.lines(); // Change: Using streams to read lines
        lines.forEach(line -> { // Change: Processing each line using streams
            String[] record = line.trim().split(",");
            data.add(record);
            labels.add(new ArrayList<>());
            addLabelsToPane(record, labels.size(), pane); // Method to add labels
        });
        fin.close();

        scroller.setContent(pane);
        Scene scene = new Scene(scroller);
        stage.setScene(scene);
        stage.show();
    }

    private void addLabelsToPane(String[] record, int row, GridPane pane) {
        int col = 0;
        for (String value : record) {
            Label label = new Label(value);
            // Keep track of all the labels so they can be adjusted without
            // having to find them in the Grid which can be a pain.
            labels.get(row - 1).add(label);
            pane.add(label, col, row);
            col++;
        }
    }

    private void sortDataByColumn(int colIndex) {
        data.sort(Comparator.comparing(record -> record[colIndex]));
    }

    /**
     * Helper function used to update all the labels based on the 
     * data. It should be called whenever the data changes.
     */
    private void update() {
        int row = 0;
        for (List<Label> label_row : labels) {  // Corrected variable name
            int col = 0;
            for (Label label : label_row) {
                label.setText(data.get(row)[col]);
                col++;
            }
            row++;
        }
    }

    public static void main(String[] args) {
        // Example of hard coding the args, useful for debugging but
        // should be removed to test using command line arguments.
        args = new String[] {"data/grades_010.csv"};
        launch(args);
    }
}
