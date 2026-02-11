import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileComparator {

    public static void main(String[] args) {
        // Define the paths to the files to be compared
        String filePath1 = "project2_grading_testcases/project2_grading_testcases/outputs/type2_medium_e.txt"; // Replace with your first file path
        String filePath2 = "myout.txt"; // Replace with your second file path

        try {
            // Compare the contents of the two files
            compareFiles(filePath1, filePath2);
        } catch (IOException e) {
            // Handle any I/O exceptions that occur during file reading
            System.out.println("An error occurred while reading the files: " + e.getMessage());
        }
    }

    /**
     * Compares the contents of two files and prints whether they are identical.
     * If they are not identical, prints the first 5 mismatched lines.
     *
     * @param filePath1 the path to the first file
     * @param filePath2 the path to the second file
     * @throws IOException if an I/O error occurs reading from the files
     */
    public static void compareFiles(String filePath1, String filePath2) throws IOException {
        // Read all lines from the first file
        List<String> file1Content = Files.readAllLines(Paths.get(filePath1));
        // Read all lines from the second file
        List<String> file2Content = Files.readAllLines(Paths.get(filePath2));

        // Check if the contents of the two files are identical
        if (file1Content.equals(file2Content)) {
            System.out.println("The files are identical.");
        } else {
            System.out.println("The files are not identical. Showing first 5 mismatches:");

            int maxMismatch = 5;  // Limit to the first 5 mismatches
            int mismatchCount = 0;

            // Determine the maximum number of lines to compare
            int maxLines = Math.max(file1Content.size(), file2Content.size());
            for (int i = 0; i < maxLines; i++) {
                // Get the line from the first file, or an empty string if the line does not exist
                String line1 = (i < file1Content.size()) ? file1Content.get(i) : "";
                // Get the line from the second file, or an empty string if the line does not exist
                String line2 = (i < file2Content.size()) ? file2Content.get(i) : "";

                // Compare the lines from the two files
                if (!line1.equals(line2)) {
                    mismatchCount++;
                    System.out.println("Line " + (i + 1) + ":");
                    System.out.println("Your Output: " + line1);
                    System.out.println("Expected Output: " + line2);
                    System.out.println();

                    // Stop after printing the first 5 mismatches
                    if (mismatchCount >= maxMismatch) {
                        break;
                    }
                }
            }
        }
    }
}