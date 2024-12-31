import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class HydroElectricApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}

// Giriş Ekranı
class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("Hidroelektrik Sistem Giriş");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(173, 216, 230));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel emailLabel = new JLabel("E-posta:");
        emailLabel.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(emailLabel, gbc);

        emailField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        JLabel passwordLabel = new JLabel("Şifre:");
        passwordLabel.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        JButton loginButton = new JButton("Giriş Yap");
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(loginButton, gbc);

        loginButton.addActionListener(new LoginActionListener());

        passwordField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginButton.doClick();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        });

        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }

    private class LoginActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hydroelectricbd", "root", "ceyda")) {
                String query = "SELECT role FROM users WHERE email = ? AND password = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, email);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String role = rs.getString("role");
                    dispose();
                    if (role.equals("admin")) {
                        new AdminFrame();
                    } else {
                        new UserFrame();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Geçersiz e-posta veya şifre", "Giriş Hatası", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Veritabanı bağlantı hatası", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

// Admin Paneli
class




AdminFrame extends JFrame {
    public AdminFrame() {
        setTitle("Admin Paneli");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel label = new JLabel("Admin Paneli: Kullanıcı Yönetimi ve Güncelleme Kayıtları", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(new Color(102, 102, 255));
        add(label, BorderLayout.NORTH);

        JPanel adminPanel = new JPanel();
        adminPanel.setLayout(new GridLayout(5, 1, 20, 20));
        adminPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton addUserButton = new JButton("Kullanıcı Ekle");
        addUserButton.addActionListener(e -> openAddUserDialog());
        adminPanel.add(addUserButton);

        JButton viewUsersButton = new JButton("Kullanıcıları Görüntüle");
        viewUsersButton.addActionListener(e -> showUserTable());
        adminPanel.add(viewUsersButton);

        JButton viewUpdatesButton = new JButton("Su ve Enerji Güncellemelerini Görüntüle");
        viewUpdatesButton.addActionListener(e -> showUpdateLogs());
        adminPanel.add(viewUpdatesButton);

        JButton viewAlertsButton = new JButton("Uyarılar");
        viewAlertsButton.addActionListener(e -> showAlerts());
        adminPanel.add(viewAlertsButton);

        JButton logoutButton = new JButton("Çıkış Yap");
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });
        adminPanel.add(logoutButton);

        adminPanel.setBackground(new Color(173, 216, 230));
        add(adminPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void openAddUserDialog() {
        JDialog addUserDialog = new JDialog(this, "Kullanıcı Ekle", true);
        addUserDialog.setSize(400, 300);
        addUserDialog.setLayout(new GridLayout(5, 2));

        JLabel nameLabel = new JLabel("Ad:");
        JTextField nameField = new JTextField();

        JLabel surnameLabel = new JLabel("Soyad:");
        JTextField surnameField = new JTextField();

        JLabel emailLabel = new JLabel("E-posta:");
        JTextField emailField = new JTextField();

        JLabel passwordLabel = new JLabel("Şifre:");
        JPasswordField passwordField = new JPasswordField();

        JButton addButton = new JButton("Ekle");
        addButton.addActionListener(e -> {
            String name = nameField.getText();
            String surname = surnameField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hydroelectricbd", "root", "ceyda")) {
                String query = "INSERT INTO users (name, surname, email, password, role) VALUES (?, ?, ?, ?, 'user')";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, name);
                stmt.setString(2, surname);
                stmt.setString(3, email);
                stmt.setString(4, password);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(addUserDialog, "Kullanıcı başarıyla eklendi!");
                addUserDialog.dispose();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(addUserDialog, "Kullanıcı ekleme hatası.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });

        addUserDialog.add(nameLabel);
        addUserDialog.add(nameField);
        addUserDialog.add(surnameLabel);
        addUserDialog.add(surnameField);
        addUserDialog.add(emailLabel);
        addUserDialog.add(emailField);
        addUserDialog.add(passwordLabel);
        addUserDialog.add(passwordField);
        addUserDialog.add(new JLabel());
        addUserDialog.add(addButton);

        addUserDialog.setVisible(true);
    }

    private void showUserTable() {
        JDialog userTableDialog = new JDialog(this, "Kullanıcılar", true);
        userTableDialog.setSize(800, 400);
        userTableDialog.setLayout(new BorderLayout());

        String[] columnNames = {"ID", "Ad", "Soyad", "E-posta", "Rol", "Sil"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        userTableDialog.add(scrollPane, BorderLayout.CENTER);

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hydroelectricbd", "root", "ceyda")) {
            String query = "SELECT id, name, surname, email, role FROM users";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String surname = rs.getString("surname");
                String email = rs.getString("email");
                String role = rs.getString("role");

                Object[] row = {id, name, surname, email, role, "Sil"};
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        table.getColumn("Sil").setCellRenderer(new ButtonRenderer());
        table.getColumn("Sil").setCellEditor(new ButtonEditor(new JCheckBox()));

        userTableDialog.setVisible(true);
    }

    private void showUpdateLogs() {
        JDialog logsDialog = new JDialog(this, "Su ve Enerji Güncellemeleri", true);
        logsDialog.setSize(800, 600);
        logsDialog.setLayout(new GridLayout(2, 1));

        JPanel waterPanel = new JPanel(new BorderLayout());
        JLabel waterLabel = new JLabel("Su Seviyesi Güncellemeleri", SwingConstants.CENTER);
        waterLabel.setFont(new Font("Arial", Font.BOLD, 16));
        waterPanel.add(waterLabel, BorderLayout.NORTH);

        String[] waterColumns = {"ID", "Seviye (m³)", "Zaman"};
        DefaultTableModel waterModel = new DefaultTableModel(waterColumns, 0);
        JTable waterTable = new JTable(waterModel);
        JScrollPane waterScrollPane = new JScrollPane(waterTable);
        waterPanel.add(waterScrollPane, BorderLayout.CENTER);

        JPanel energyPanel = new JPanel(new BorderLayout());
        JLabel energyLabel = new JLabel("Enerji Seviyesi Güncellemeleri", SwingConstants.CENTER);
        energyLabel.setFont(new Font("Arial", Font.BOLD, 16));
        energyPanel.add(energyLabel, BorderLayout.NORTH);

        String[] energyColumns = {"ID", "Seviye (MW)", "Zaman"};
        DefaultTableModel energyModel = new DefaultTableModel(energyColumns, 0);
        JTable energyTable = new JTable(energyModel);
        JScrollPane energyScrollPane = new JScrollPane(energyTable);
        energyPanel.add(energyScrollPane, BorderLayout.CENTER);

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hydroelectricbd", "root", "ceyda")) {
            Statement stmt = conn.createStatement();

            ResultSet rsWater = stmt.executeQuery("SELECT * FROM water_levels");
            while (rsWater.next()) {
                waterModel.addRow(new Object[]{rsWater.getInt("id"), rsWater.getDouble("level"), rsWater.getTimestamp("timestamp")});
            }

            ResultSet rsEnergy = stmt.executeQuery("SELECT * FROM energy_levels");
            while (rsEnergy.next()) {
                energyModel.addRow(new Object[]{rsEnergy.getInt("id"), rsEnergy.getDouble("level"), rsEnergy.getTimestamp("timestamp")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        logsDialog.add(waterPanel);
        logsDialog.add(energyPanel);
        logsDialog.setVisible(true);
    }

    private void showAlerts() {
        JDialog alertsDialog = new JDialog(this, "Uyarılar", true);
        alertsDialog.setSize(800, 400);
        alertsDialog.setLayout(new BorderLayout());

        String[] columnNames = {"ID", "Mesaj", "Tarih"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        alertsDialog.add(scrollPane, BorderLayout.CENTER);

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hydroelectricbd", "root", "ceyda")) {
            String query = "SELECT * FROM alerts ORDER BY id DESC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                int id = rs.getInt("id");
                String message = rs.getString("message");
                Timestamp date = rs.getTimestamp("date");

                model.addRow(new Object[]{id, message, date});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        alertsDialog.setVisible(true);
    }
}

// Kullanıcı Paneli
class UserFrame extends JFrame {
    public UserFrame() {
        setTitle("Kullanıcı Paneli");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel label = new JLabel("Kullanıcı Paneli: Su/Enerji Seviyelerini Yönet", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(new Color(102, 102, 255));
        add(label, BorderLayout.NORTH);

        JPanel userPanel = new JPanel();
        userPanel.setLayout(new GridLayout(3, 1, 20, 20));
        userPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton adjustWaterButton = new JButton("Su Seviyesini Güncelle");
        adjustWaterButton.addActionListener(e -> adjustLevel("water_levels", "m³", 50_000_000, 100_000_000));
        userPanel.add(adjustWaterButton);

        JButton adjustEnergyButton = new JButton("Enerji Seviyesini Güncelle");
        adjustEnergyButton.addActionListener(e -> adjustLevel("energy_levels", "MW", 10, 50));
        userPanel.add(adjustEnergyButton);

        JButton logoutButton = new JButton("Çıkış Yap");
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });
        userPanel.add(logoutButton);

        add(userPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void adjustLevel(String tableName, String unit, int min, int max) {
        String input = JOptionPane.showInputDialog(this, "Yeni seviye giriniz (" + unit + "):");
        try {
            int value = Integer.parseInt(input);
            int percentage = (int) (((double) value / max) * 100);

            if (value < min) {
                logAlert("Seviye çok düşük: " + value + " " + unit);
            } else if (value > max) {
                logAlert("Seviye çok yüksek: " + value + " " + unit);
            }

            JOptionPane.showMessageDialog(this, "Doluluk oranı: %" + percentage);

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hydroelectricbd", "root", "ceyda")) {
                String query = "INSERT INTO " + tableName + " (level) VALUES (?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, value);
                stmt.executeUpdate();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Geçerli bir sayı giriniz.", "Hata", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Veritabanı hatası.", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logAlert(String message) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hydroelectricbd", "root", "ceyda")) {
            String query = "INSERT INTO alerts (message, date) VALUES (?, NOW())";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, message);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

// Silme Butonu
class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer() {
        setText("Sil");
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return this;
    }
}

class ButtonEditor extends DefaultCellEditor {
    private String label;
    private int row;
    private JTable table;

    public ButtonEditor(JCheckBox checkBox) {
        super(checkBox);
    }

    @Override
    public Object getCellEditorValue() {
        return label;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.table = table;
        this.row = row;
        label = (value == null) ? "" : value.toString();
        JButton button = new JButton(label);
        button.addActionListener(e -> {
            int userId = (int) table.getValueAt(row, 0);
            deleteUser(userId);
        });
        return button;
    }

    private void deleteUser(int userId) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hydroelectricbd", "root", "ceyda")) {
            String deleteQuery = "DELETE FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(deleteQuery);
            stmt.setInt(1, userId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(table, "Kullanıcı başarıyla silindi!");
            ((DefaultTableModel) table.getModel()).removeRow(row);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(table, "Kullanıcı silinirken hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
}