package MinEditDistance;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;
import java.util.Scanner;

public class MinEditDistance {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create the frame and set the layout
            JFrame frame = new JFrame("Min Edit Distance");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);

            // Create a JTabbedPane for switching between Part 1 and Part 2
            JTabbedPane tabbedPane = new JTabbedPane();

            // Add Part 1 and Part 2 panels
            tabbedPane.addTab("Part 1", createPart1Panel());
            tabbedPane.addTab("Part 2", createPart2Panel());

            frame.add(tabbedPane);
            frame.setVisible(true);
        });
    }

    private static JPanel createPart1Panel() {
        long startTime = System.nanoTime(); // Measure start time
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Input panel with text fields and button
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(1, 1));

        JButton loadFileButton = new JButton("Load Word List File");
        JTextArea resultArea = new JTextArea(10, 30);
        resultArea.setEditable(false);

        JLabel timeLabel = new JLabel("Total Running Time: "); // Label for time display

        // Action for load file button
        loadFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                List<String> wordList = loadWordsFromFile(file);

                String target = JOptionPane.showInputDialog("Enter the target word:");
                if (target == null || target.isEmpty()) return;

                Map<String, Integer> wordMap = new HashMap<>();

                for (String word : wordList) {
                    int value = minEditDistance(word, target, 1, resultArea);
                    wordMap.put(word, value);
                }

                long endTime = System.nanoTime(); // Measure end time
                long duration = endTime - startTime; // Calculate duration
                double seconds = duration / 1_000_000_000.0;

                // Display the closest matches
                StringBuilder resultText = new StringBuilder();
                wordMap.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue())
                        .limit(50)
                        .forEach(entry -> resultText.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n"));

                resultArea.setText(resultText.toString());
                timeLabel.setText("Total Running Time: " + seconds + " seconds");
            }
        });

        inputPanel.add(loadFileButton);
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        panel.add(timeLabel, BorderLayout.SOUTH); // Add time label to bottom
        return panel;
    }

    // Panel for Part 2 - Edit Distance with Operations
    private static JPanel createPart2Panel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Input panel with text fields and button
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2));
        long startTime = System.nanoTime(); // Start time
        JLabel sourceLabel = new JLabel("Enter Source Word:");
        JTextField sourceField = new JTextField();
        JLabel targetLabel = new JLabel("Enter Target Word:");
        JTextField targetField = new JTextField();
        JButton calculateButton = new JButton("Calculate Operations");

        inputPanel.add(sourceLabel);
        inputPanel.add(sourceField);
        inputPanel.add(targetLabel);
        inputPanel.add(targetField);
        inputPanel.add(calculateButton);

        // Result area for displaying the operations
        JTextArea resultArea = new JTextArea(10, 30);
        resultArea.setEditable(false);
        JLabel timeLabel = new JLabel("Execution Time: "); // Label for time display

        // Action for calculate button
        calculateButton.addActionListener(e -> {
            String source = sourceField.getText().trim();
            String target = targetField.getText().trim();
            if (source.isEmpty() || target.isEmpty()) return;

            source = source.toLowerCase();
            target = target.toLowerCase();

            minEditDistance(source, target, 2, resultArea);
            long endTime = System.nanoTime(); // End time

            long duration = endTime - startTime; // Calculate duration
            double seconds = duration / 1_000_000_000.0;
            timeLabel.setText("Execution Time: " + seconds + " seconds");
        });

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        panel.add(timeLabel, BorderLayout.SOUTH); // Add time label to bottom
        return panel;
    }

    // Function to load words from a file
    public static List<String> loadWordsFromFile(File file) {
        List<String> wordList = new ArrayList<>();
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                wordList.add(sc.nextLine().trim());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return wordList;
    }

    public static int minEditDistance(String source, String target, int part, JTextArea resultArea) {
        resultArea.setText(""); // Sonuç alanýný temizle
        int m = source.length();
        int n = target.length();

        // DP tablosu
        int[][] distance = new int[m + 1][n + 1];

        // DP tablosunun baþlangýç durumunu ayarla (baz durumlar)
        for (int i = 0; i <= m; i++) {
            distance[i][0] = i;
        }
        for (int j = 0; j <= n; j++) {
            distance[0][j] = j;
        }

        // MED deðeri hesaplamak için ayrý bir tablo
        int[][] medCostTable = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++) {
            medCostTable[i][0] = i;
        }
        for (int j = 0; j <= n; j++) {
            medCostTable[0][j] = j;
        }

        // Tablo baþlýklarýný yazdýr (part == 2 durumunda)
        if (part == 2) {
            resultArea.append("\t#\t");
            for (int j = 0; j < target.length(); j++) {
                resultArea.append(target.charAt(j) + "\t");
            }
            resultArea.append("\n");
        }

        // DP tablosunu doldur ve yazdýr
        for (int i = 0; i <= m; i++) {
            if (part == 2) {
                resultArea.append(i == 0 ? "#\t" : source.charAt(i - 1) + "\t");
            }

            for (int j = 0; j <= n; j++) {
                if (i > 0 && j > 0) {
                    // Normal tablo için maliyetler (her biri +1 artýþ)
                    int insertCost = distance[i][j - 1] + 1;  // Ekleme maliyeti
                    int deleteCost = distance[i - 1][j] + 1;  // Silme maliyeti
                    int replaceCost = distance[i - 1][j - 1] + (source.charAt(i - 1) != target.charAt(j - 1) ? 1 : 0); // Yer deðiþtirme

                    // Minimum maliyeti bul ve tabloyu güncelle
                    distance[i][j] = Math.min(insertCost, Math.min(deleteCost, replaceCost));

                    // MED maliyet tablosu için yer deðiþtirme maliyetini 2 olarak hesapla
                    int medInsertCost = medCostTable[i][j - 1] + 1;
                    int medDeleteCost = medCostTable[i - 1][j] + 1;
                    int medReplaceCost = medCostTable[i - 1][j - 1] + (source.charAt(i - 1) != target.charAt(j - 1) ? 2 : 0);

                    medCostTable[i][j] = Math.min(medInsertCost, Math.min(medDeleteCost, medReplaceCost));
                }

                // Tablo deðerlerini yazdýr
                if (part == 2) {
                    resultArea.append(distance[i][j] + "\t");
                }
            }

            // Yeni satýr ekle (sadece yazdýrma için)
            if (part == 2) {
                resultArea.append("\n");
            }
        }

        // Part 2 için iþlemleri yazdýr
        if (part == 2) {
            resultArea.append("\nOperations:\n");
            List<String> operations = backtrackOperations(medCostTable, source, target);
            for (String operation : operations) {
                resultArea.append(operation + "\n");
            }
        }

        resultArea.append("MED Value: " + medCostTable[m][n] + "\n");
        return medCostTable[m][n];
    }

    private static List<String> backtrackOperations(int[][] distance, String source, String target) {
        List<String> operations = new ArrayList<>();
        int i = source.length();
        int j = target.length();

        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && distance[i][j] == distance[i - 1][j - 1] + (source.charAt(i - 1) != target.charAt(j - 1) ? 2 : 0)) {
                // Yer deðiþtirme iþlemi
                if (source.charAt(i - 1) != target.charAt(j - 1)) {
                    operations.add("Replace '" + source.charAt(i - 1) + "' with '" + target.charAt(j - 1) + "'");
                }
                i--;
                j--;
            } else if (i > 0 && distance[i][j] == distance[i - 1][j] + 1) {
                // Silme iþlemi
                operations.add("Delete '" + source.charAt(i - 1) + "'");
                i--;
            } else if (j > 0 && distance[i][j] == distance[i][j - 1] + 1) {
                // Ekleme iþlemi
                operations.add("Insert '" + target.charAt(j - 1) + "'");
                j--;
            }
        }

        Collections.reverse(operations);
        return operations;
    }

}
