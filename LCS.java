import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class LCS extends JFrame {

    JTextField str1, str2;
    JTable dpTable, dirTable;
    DefaultTableModel dpModel, dirModel;
    JTextArea result;

    int[][] dp;
    char[][] dir;

    String s1, s2;

    public LCS() {

        setTitle("LCS Visualizer");
        setSize(950, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        str1 = new JTextField();
        str2 = new JTextField();
        result = new JTextArea(3, 20);

        JButton runBtn = new JButton("Compute LCS");
        runBtn.addActionListener(e -> startLCS());

        JPanel top = new JPanel(new GridLayout(5, 1));
        top.add(new JLabel("Enter String 1:"));
        top.add(str1);
        top.add(new JLabel("Enter String 2:"));
        top.add(str2);
        top.add(runBtn);

        // DP TABLE
        dpModel = new DefaultTableModel();
        dpTable = new JTable(dpModel);
        dpTable.setRowHeight(30);

        // DIRECTION TABLE
        dirModel = new DefaultTableModel();
        dirTable = new JTable(dirModel);
        dirTable.setRowHeight(30);
        dirTable.setFont(new Font("Segoe UI Symbol", Font.BOLD, 18));

        // Color renderer
        dirTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

                c.setBackground(Color.WHITE);

                if (value != null) {
                    String v = value.toString();

                    if (v.equals("↖")) c.setBackground(Color.GREEN);
                    else if (v.equals("↑")) c.setBackground(Color.CYAN);
                    else if (v.equals("←")) c.setBackground(Color.ORANGE);
                }
                return c;
            }
        });

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(dpTable),
                new JScrollPane(dirTable));

        split.setDividerLocation(300);

        add(top, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
        add(result, BorderLayout.SOUTH);
    }

    void startLCS() {

        s1 = str1.getText();
        s2 = str2.getText();

        int n = s1.length();
        int m = s2.length();

        dp = new int[n + 1][m + 1];
        dir = new char[n + 1][m + 1];

        // Column headers
        String[] cols = new String[m + 1];
        cols[0] = "0";
        for (int j = 1; j <= m; j++)
            cols[j] = s2.charAt(j - 1) + "";

        dpModel.setColumnIdentifiers(cols);
        dirModel.setColumnIdentifiers(cols);

        dpModel.setRowCount(n + 1);
        dirModel.setRowCount(n + 1);

        // Initialize first row & column with 0
        for (int i = 0; i <= n; i++) {
            dp[i][0] = 0;
            dpModel.setValueAt(0, i, 0);
            dirModel.setValueAt("0", i, 0);
        }

        for (int j = 0; j <= m; j++) {
            dp[0][j] = 0;
            dpModel.setValueAt(0, 0, j);
            dirModel.setValueAt("0", 0, j);
        }

        // Row labels
        for (int i = 1; i <= n; i++) {
            dpModel.setValueAt(s1.charAt(i - 1), i, 0);
            dirModel.setValueAt(s1.charAt(i - 1), i, 0);
        }

        new Thread(this::fillDP).start();
    }

    void fillDP() {
        try {
            for (int i = 1; i <= s1.length(); i++) {
                for (int j = 1; j <= s2.length(); j++) {

                    // LCS logic
                    if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                        dp[i][j] = dp[i - 1][j - 1] + 1;
                        dir[i][j] = 'D'; // diagonal
                    } else if (dp[i - 1][j] >= dp[i][j - 1]) {
                        dp[i][j] = dp[i - 1][j];
                        dir[i][j] = 'U'; // up
                    } else {
                        dp[i][j] = dp[i][j - 1];
                        dir[i][j] = 'L'; // left
                    }

                    final int r = i, c = j;

                    String arrow = "";
                    if (dir[r][c] == 'D') arrow = "↖";
                    else if (dir[r][c] == 'U') arrow = "↑";
                    else arrow = "←";

                    final String finalArrow = arrow;

                    SwingUtilities.invokeLater(() -> {
                        dpModel.setValueAt(dp[r][c], r, c);
                        dirModel.setValueAt(finalArrow, r, c);
                    });

                    Thread.sleep(120);
                }
            }

            backtrack();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void backtrack() {

        int i = s1.length();
        int j = s2.length();
        String lcs = "";

        try {
            while (i > 0 && j > 0) {

                if (dir[i][j] == 'D') {
                    lcs = s1.charAt(i - 1) + lcs;
                    i--;
                    j--;
                } else if (dir[i][j] == 'U') {
                    i--;
                } else {
                    j--;
                }
            }

            result.setText("LCS: " + lcs + "\nLength: " + lcs.length());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new LCS().setVisible(true);
    }
}