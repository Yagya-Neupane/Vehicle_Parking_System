import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class VehicleParkingSystem extends JFrame {
    CardLayout cardLayout;
    JPanel mainPanel;

    ArrayList<Vehicle> parkedVehicles = new ArrayList<>();
    int totalAmountCollected = 0;

    public VehicleParkingSystem() {
        setTitle(" Vehicle Parking System ");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Menubar
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        JMenuItem entryMenu = new JMenuItem("Vehicle Entry");
        JMenuItem exitMenu = new JMenuItem("Vehicle Exit");
        JMenuItem showMenu = new JMenuItem("Show Parked");
        JMenuItem summaryMenu = new JMenuItem("Summary");

        menu.add(entryMenu);
        menu.add(exitMenu);
        menu.add(showMenu);
        menu.add(summaryMenu);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        // Main Panel with CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        JPanel entryPanel = createEntryPanel();
        JPanel exitPanel = createExitPanel();
        JPanel showPanel = createShowPanel();
        JPanel summaryPanel = createSummaryPanel();

        mainPanel.add(entryPanel, "Entry");
        mainPanel.add(exitPanel, "Exit");
        mainPanel.add(showPanel, "Show");
        mainPanel.add(summaryPanel, "Summary");

        add(mainPanel);

        // Menu Actions
        entryMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Entry");
            }
        });

        exitMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Exit");
            }
        });

        showMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Show");
            }
        });

        summaryMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Summary");
            }
        });
     setVisible(true);
    }

    // Placeholder panels for Entry

    private JPanel createEntryPanel() {
    JPanel panel = new JPanel();
    panel.setBackground(new Color(245, 248, 255)); // light blue-gray
    panel.setLayout(new GridLayout(4, 2, 10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

    JLabel lblNumber = new JLabel("Vehicle Number:");
    styleComponent(lblNumber, 14, Color.DARK_GRAY);

    final JTextField txtNumber = new JTextField();

    JLabel lblType = new JLabel("Vehicle Type:");
    styleComponent(lblType, 14, Color.DARK_GRAY);

    String[] types = {"Car", "Bike", "Van", "Truck"};
    final JComboBox<String> cmbType = new JComboBox<>(types);

    JButton btnSubmit = new JButton("Park Vehicle");
    styleComponent(btnSubmit, 14, Color.WHITE);
    btnSubmit.setBackground(new Color(0, 123, 255));

    final JLabel lblMessage = new JLabel("");
    styleComponent(lblMessage, 13, Color.BLUE);

    panel.add(lblNumber);
    panel.add(txtNumber);

    panel.add(lblType);
    panel.add(cmbType);

    panel.add(btnSubmit);
    panel.add(lblMessage);

    // Fill empty cells in grid
    panel.add(new JLabel(""));
    panel.add(new JLabel(""));

    // Action Listener to save vehicle
    btnSubmit.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            String number = txtNumber.getText().trim();
            String type = cmbType.getSelectedItem().toString();

            if (number.isEmpty()) {
                lblMessage.setText("Please enter vehicle number.");
                return;
            }

            Vehicle v = new Vehicle(number, type);
            parkedVehicles.add(v);
            lblMessage.setText(" Vehicle parked successfully!");

            // Clear fields
            txtNumber.setText("");
            cmbType.setSelectedIndex(0);
        }
    });

    return panel;
}

// Placeholder panels for Exit
    private JPanel createExitPanel() {
    JPanel panel = new JPanel();
    panel.setBackground(new Color(245, 248, 255)); // light blue-gray
    panel.setLayout(new GridLayout(4, 1, 10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

    JLabel lblNumber = new JLabel("Enter Vehicle Number:");
    styleComponent(lblNumber, 14, Color.DARK_GRAY);
    final JTextField txtNumber = new JTextField();
    
    JButton btnExit = new JButton("Process Exit");
    styleComponent(btnExit, 14, Color.WHITE);
    btnExit.setBackground(new Color(255, 102, 102));

    final JLabel lblResult = new JLabel("");
    styleComponent(lblResult, 13, Color.RED); // error or result
    
    panel.add(lblNumber);
    panel.add(txtNumber);
    panel.add(btnExit);
    panel.add(lblResult);

    btnExit.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            String number = txtNumber.getText().trim();
            Vehicle found = null;

            for (Vehicle v : parkedVehicles) {
                if (v.vehicleNumber.equalsIgnoreCase(number)) {
                    found = v;
                    break;
                }
            }

            if (found != null) {
                found.exitTime = System.currentTimeMillis();
                long duration = found.exitTime - found.entryTime;
                long minutes = duration / (1000 * 60);
                long hours = (minutes / 60);
                if (minutes % 60 > 0) hours++; // Round up to next hour

                int fee = 20;
                if (hours > 1) {
                    fee += (hours - 1) * 10;
                }
                totalAmountCollected += fee;

                parkedVehicles.remove(found);
                lblResult.setText(" Fee: Rs." + fee + " | Duration: " + hours + " hr(s)");
            } else {
                lblResult.setText(" Vehicle not found.");
            }
        }
    });

    return panel;
}

// Placeholder panels for Show
    private JPanel createShowPanel() {
    final JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    final String[] columns = {"Vehicle Number", "Type", "Entry Time"};
    final String[][] data = {}; // initially empty

    final JTable table = new JTable(data, columns);
    JScrollPane scrollPane = new JScrollPane(table);

    JLabel title = new JLabel("Currently Parked Vehicles", SwingConstants.CENTER);
    styleComponent(title, 16, new Color(20, 40, 90));
    title.setFont(new Font("Arial", Font.BOLD, 16));

    JButton btnRefresh = new JButton("Refresh Table");
    styleComponent(btnRefresh, 14, Color.WHITE);
    btnRefresh.setBackground(new Color(90, 170, 255));

    // 🔄 Action to refresh table
    btnRefresh.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            updateTable(table, columns);
        }
    });

    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.add(title, BorderLayout.CENTER);
    topPanel.add(btnRefresh, BorderLayout.EAST);

    panel.add(topPanel, BorderLayout.NORTH);
    panel.add(scrollPane, BorderLayout.CENTER);

    return panel;
}


// Placeholder panels for Summary
    private JPanel createSummaryPanel() {
    final JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

    final JLabel lblParked = new JLabel("Total Vehicles Parked: ");
    styleComponent(lblParked, 14, Color.DARK_GRAY);
    final JLabel lblCollected = new JLabel("Total Amount Collected: ");
    styleComponent(lblCollected, 14, Color.DARK_GRAY);
    JButton btnUpdate = new JButton("Update Summary");
    styleComponent(btnUpdate, 14, Color.WHITE);
    btnUpdate.setBackground(new Color(0, 150, 100));

    lblParked.setFont(new Font("Arial", Font.PLAIN, 16));
    lblCollected.setFont(new Font("Arial", Font.PLAIN, 16));

    btnUpdate.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            lblParked.setText("Total Vehicles Parked: " + parkedVehicles.size());
            lblCollected.setText("Total Amount Collected: Rs. " + totalAmountCollected);
        }
    });

    panel.add(lblParked);
    panel.add(lblCollected);
    panel.add(btnUpdate);

    return panel;
}
    private void styleComponent(JComponent comp, int fontSize, Color fg) {
        comp.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
        comp.setForeground(fg);
    }


    public static void main(String[] args) {
        new VehicleParkingSystem();
    }

    private void updateTable(JTable table, String[] columns) {
    String[][] data = new String[parkedVehicles.size()][3];
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    for (int i = 0; i < parkedVehicles.size(); i++) {
        Vehicle v = parkedVehicles.get(i);
        data[i][0] = v.vehicleNumber;
        data[i][1] = v.vehicleType;
        data[i][2] = sdf.format(new Date(v.entryTime));
    }

    table.setModel(new javax.swing.table.DefaultTableModel(data, columns));
}

private Component getVisibleCard(Container container) {
    for (Component comp : container.getComponents()) {
        if (comp.isVisible()) return comp;
    }
    return null;
}

    // Simple model
    class Vehicle {
        String vehicleNumber;
        String vehicleType;
        long entryTime;
        long exitTime;

        public Vehicle(String num, String type) {
            this.vehicleNumber = num;
            this.vehicleType = type;
            this.entryTime = System.currentTimeMillis();
        }
    }
}
