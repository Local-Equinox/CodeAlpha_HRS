import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class HotelSystemGUI extends JFrame {
    private Hotel hotel;
    private User currentUser;
    private boolean isDarkMode = false;

    // UI Container Cards
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Theme Selector
    private JComboBox<String> themeToggle;

    // Login/Register Components
    private JTextField loginUserField;
    private JPasswordField loginPassField;
    private JTextField regUserField;
    private JPasswordField regPassField;

    // User Dashboard Components
    private JLabel userWelcomeLabel;
    private JComboBox<String> userCategoryFilter;
    private JComboBox<Room> userRoomDropdown;
    private JTextField checkInField;
    private JTextField checkOutField;
    private JTextField totalAmountField;
    private JTable userRoomsTable;
    private DefaultTableModel userRoomsTableModel;
    private JTable userResTable;
    private DefaultTableModel userResTableModel;

    // Admin Dashboard Components
    private JTextField newRoomNumField, newRoomTypeField, newRoomPriceField;
    private JComboBox<String> newRoomCatBox;
    private JTable adminRoomsTable, adminUsersTable, adminAllResTable;
    private DefaultTableModel adminRoomsModel, adminUsersModel, adminAllResModel;

    public HotelSystemGUI() {
        hotel = new Hotel();

        setTitle("Hotel Reservation System - CodeAlpha");
        setPreferredSize(new Dimension(950, 700));
        setMinimumSize(new Dimension(900, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- TOP GLOBAL BAR (Title + Theme Toggle) ---
        JPanel topBar = new JPanel(new BorderLayout(10, 10));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        JLabel appTitle = new JLabel("🏨 Enterprise Hotel & Reservation Suite");
        appTitle.setFont(new Font("SansSerif", Font.BOLD, 18));

        JPanel rightTopPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightTopPanel.add(new JLabel("Theme:"));
        themeToggle = new JComboBox<>(new String[]{"☀️ Light Mode", "🌙 Dark Mode"});
        themeToggle.addActionListener(e -> toggleTheme());
        rightTopPanel.add(themeToggle);

        topBar.add(appTitle, BorderLayout.WEST);
        topBar.add(rightTopPanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // --- CARD LAYOUT CONTAINER ---
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add Screens to CardLayout
        mainPanel.add(createAuthPanel(), "AUTH");
        mainPanel.add(createUserDashboard(), "USER_DASHBOARD");
        mainPanel.add(createAdminDashboard(), "ADMIN_DASHBOARD");

        add(mainPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        applyTheme();
    }

    // ==========================================
    // 1. AUTHENTICATION PANEL (LOGIN / REGISTER)
    // ==========================================
    private JPanel createAuthPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        JTabbedPane authTabs = new JTabbedPane();
        authTabs.setPreferredSize(new Dimension(380, 280));

        // Login Sub-Tab
        JPanel loginPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        loginPanel.add(new JLabel("Username:"));
        loginUserField = new JTextField();
        loginPanel.add(loginUserField);

        loginPanel.add(new JLabel("Password:"));
        loginPassField = new JPasswordField();
        loginPanel.add(loginPassField);

        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> handleLogin());
        loginPanel.add(new JLabel(""));
        loginPanel.add(loginBtn);

        authTabs.addTab("Login", loginPanel);

        // Register Sub-Tab
        JPanel regPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        regPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        regPanel.add(new JLabel("New Username:"));
        regUserField = new JTextField();
        regPanel.add(regUserField);

        regPanel.add(new JLabel("New Password:"));
        regPassField = new JPasswordField();
        regPanel.add(regPassField);

        JButton regBtn = new JButton("Create Account");
        regBtn.addActionListener(e -> handleRegister());
        regPanel.add(new JLabel(""));
        regPanel.add(regBtn);

        authTabs.addTab("Register New Guest", regPanel);

        panel.add(authTabs);
        return panel;
    }

    private void handleLogin() {
        String user = loginUserField.getText().trim();
        String pass = new String(loginPassField.getPassword()).trim();

        User u = hotel.authenticate(user, pass);
        if (u != null) {
            currentUser = u;
            loginUserField.setText("");
            loginPassField.setText("");

            if (currentUser.isAdmin()) {
                refreshAdminTables();
                cardLayout.show(mainPanel, "ADMIN_DASHBOARD");
            } else {
                userWelcomeLabel.setText("Welcome, " + currentUser.getUsername() + "  |  Unique Guest ID: #" + currentUser.getGuestId());
                refreshUserView();
                cardLayout.show(mainPanel, "USER_DASHBOARD");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Username or Password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRegister() {
        String user = regUserField.getText().trim();
        String pass = new String(regPassField.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fields cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User newUser = hotel.registerUser(user, pass);
        if (newUser != null) {
            JOptionPane.showMessageDialog(this, "Account Created Successfully!\nYour Assigned Guest ID is: #" + newUser.getGuestId(), "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
            regUserField.setText("");
            regPassField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Username already exists. Choose another.", "Registration Failed", JOptionPane.WARNING_MESSAGE);
        }
    }

    // ==========================================
    // 2. USER DASHBOARD
    // ==========================================
    private JPanel createUserDashboard() {
        JPanel dashboard = new JPanel(new BorderLayout(10, 10));
        dashboard.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header Panel
        JPanel header = new JPanel(new BorderLayout());
        userWelcomeLabel = new JLabel("Welcome, Guest");
        userWelcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> logout());
        header.add(userWelcomeLabel, BorderLayout.WEST);
        header.add(logoutBtn, BorderLayout.EAST);
        dashboard.add(header, BorderLayout.NORTH);

        // Booking Controls Panel
        JPanel bookingControls = new JPanel(new GridLayout(3, 1, 5, 5));
        bookingControls.setBorder(BorderFactory.createTitledBorder("Book a Room"));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row1.add(new JLabel("Category:"));
        userCategoryFilter = new JComboBox<>(new String[]{"All", "Standard", "Deluxe", "Suite"});
        userCategoryFilter.addActionListener(e -> updateUserRoomsDropdown());
        row1.add(userCategoryFilter);

        row1.add(new JLabel("Select Room:"));
        userRoomDropdown = new JComboBox<>();
        userRoomDropdown.setPreferredSize(new Dimension(280, 25));
        userRoomDropdown.addActionListener(e -> autoCalculateUserAmount());
        row1.add(userRoomDropdown);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row2.add(new JLabel("Check-In:"));
        checkInField = new JTextField("2026-08-10", 8);
        row2.add(checkInField);

        row2.add(new JLabel("Check-Out:"));
        checkOutField = new JTextField("2026-08-14", 8);
        row2.add(checkOutField);

        row2.add(new JLabel("Total ($):"));
        totalAmountField = new JTextField(6);
        row2.add(totalAmountField);

        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton bookBtn = new JButton("Confirm Booking & Payment");
        bookBtn.addActionListener(e -> handleUserBookRoom());
        row3.add(bookBtn);

        bookingControls.add(row1);
        bookingControls.add(row2);
        bookingControls.add(row3);

        // Tabbed Display
        JTabbedPane tabs = new JTabbedPane();

        userRoomsTableModel = new DefaultTableModel(new String[]{"Room Number", "Type", "Category", "Price/Night", "Status"}, 0);
        userRoomsTable = new JTable(userRoomsTableModel);
        tabs.addTab("Available Rooms Directory", new JScrollPane(userRoomsTable));

        userResTableModel = new DefaultTableModel(new String[]{"Reservation ID", "Room Number", "Check-In", "Check-Out", "Amount ($)", "Status"}, 0);
        userResTable = new JTable(userResTableModel);

        JPanel myResPanel = new JPanel(new BorderLayout());
        myResPanel.add(new JScrollPane(userResTable), BorderLayout.CENTER);
        JButton cancelUserResBtn = new JButton("Cancel Selected Reservation");
        cancelUserResBtn.setForeground(Color.RED.darker());
        cancelUserResBtn.addActionListener(e -> handleUserCancelRes());
        myResPanel.add(cancelUserResBtn, BorderLayout.SOUTH);

        tabs.addTab("My Active Bookings", myResPanel);

        JPanel centerContainer = new JPanel(new BorderLayout(10, 10));
        centerContainer.add(bookingControls, BorderLayout.NORTH);
        centerContainer.add(tabs, BorderLayout.CENTER);

        dashboard.add(centerContainer, BorderLayout.CENTER);
        return dashboard;
    }

    private void updateUserRoomsDropdown() {
        userRoomDropdown.removeAllItems();
        String cat = (String) userCategoryFilter.getSelectedItem();
        for (Room r : hotel.searchAvailableRooms(cat)) {
            userRoomDropdown.addItem(r);
        }
        autoCalculateUserAmount();
    }

    private void autoCalculateUserAmount() {
        Room r = (Room) userRoomDropdown.getSelectedItem();
        if (r != null) {
            totalAmountField.setText(String.valueOf(r.getPricePerNight() * 4));
        } else {
            totalAmountField.setText("");
        }
    }

    private void handleUserBookRoom() {
        Room selected = (Room) userRoomDropdown.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select an available room.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double amount = Double.parseDouble(totalAmountField.getText().trim());
            String in = checkInField.getText().trim();
            String out = checkOutField.getText().trim();

            Reservation res = hotel.makeReservation(currentUser.getGuestId(), selected.getRoomNumber(), in, out, amount);
            if (res != null) {
                JOptionPane.showMessageDialog(this, "Booking Confirmed!\nReservation ID: #" + res.getReservationId(), "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshUserView();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid inputs.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUserCancelRes() {
        int row = userResTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a reservation to cancel.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int resId = (int) userResTableModel.getValueAt(row, 0);
        if (hotel.cancelReservation(resId)) {
            JOptionPane.showMessageDialog(this, "Reservation #" + resId + " cancelled.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
            refreshUserView();
        }
    }

    private void refreshUserView() {
        // Refresh Rooms
        userRoomsTableModel.setRowCount(0);
        for (Room r : hotel.getAllRooms()) {
            userRoomsTableModel.addRow(new Object[]{r.getRoomNumber(), r.getRoomType(), r.getCategory(), String.format("%.2f", r.getPricePerNight()), r.isAvailable() ? "Available" : "Booked"});
        }

        // Refresh User Reservations
        userResTableModel.setRowCount(0);
        for (Reservation res : hotel.getReservationsForGuest(currentUser.getGuestId())) {
            userResTableModel.addRow(new Object[]{res.getReservationId(), res.getRoomId(), res.getCheckInDate(), res.getCheckOutDate(), String.format("%.2f", res.getTotalAmount()), res.getStatus()});
        }

        updateUserRoomsDropdown();
    }

    // ==========================================
    // 3. ADMIN DASHBOARD
    // ==========================================
    private JPanel createAdminDashboard() {
        JPanel dashboard = new JPanel(new BorderLayout(10, 10));
        dashboard.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        JLabel adminTitle = new JLabel("🛡️ Administrator Operations Console");
        adminTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> logout());
        header.add(adminTitle, BorderLayout.WEST);
        header.add(logoutBtn, BorderLayout.EAST);
        dashboard.add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();

        // Admin Tab 1: Add/Manage Rooms
        JPanel roomMgmtPanel = new JPanel(new BorderLayout(10, 10));
        JPanel addRoomBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        addRoomBox.setBorder(BorderFactory.createTitledBorder("Publish New Available Room"));

        addRoomBox.add(new JLabel("Room #:"));
        newRoomNumField = new JTextField(4);
        addRoomBox.add(newRoomNumField);

        addRoomBox.add(new JLabel("Type:"));
        newRoomTypeField = new JTextField(8);
        addRoomBox.add(newRoomTypeField);

        addRoomBox.add(new JLabel("Category:"));
        newRoomCatBox = new JComboBox<>(new String[]{"Standard", "Deluxe", "Suite"});
        addRoomBox.add(newRoomCatBox);

        addRoomBox.add(new JLabel("Price/Night ($):"));
        newRoomPriceField = new JTextField(6);
        addRoomBox.add(newRoomPriceField);

        JButton addRoomBtn = new JButton("Add Room");
        addRoomBtn.addActionListener(e -> handleAdminAddRoom());
        addRoomBox.add(addRoomBtn);

        adminRoomsModel = new DefaultTableModel(new String[]{"Room Number", "Type", "Category", "Price / Night", "Availability"}, 0);
        adminRoomsTable = new JTable(adminRoomsModel);

        roomMgmtPanel.add(addRoomBox, BorderLayout.NORTH);
        roomMgmtPanel.add(new JScrollPane(adminRoomsTable), BorderLayout.CENTER);
        tabs.addTab("Manage Rooms Inventory", roomMgmtPanel);

        // Admin Tab 2: User Account Control
        JPanel usersPanel = new JPanel(new BorderLayout());
        adminUsersModel = new DefaultTableModel(new String[]{"Guest ID", "Username", "Role"}, 0);
        adminUsersTable = new JTable(adminUsersModel);

        JButton deleteUserBtn = new JButton("Delete Selected User Account");
        deleteUserBtn.setForeground(Color.RED.darker());
        deleteUserBtn.addActionListener(e -> handleAdminDeleteUser());

        usersPanel.add(new JScrollPane(adminUsersTable), BorderLayout.CENTER);
        usersPanel.add(deleteUserBtn, BorderLayout.SOUTH);
        tabs.addTab("User Accounts", usersPanel);

        // Admin Tab 3: Global Reservations Monitor
        adminAllResModel = new DefaultTableModel(new String[]{"Res ID", "Guest ID", "Room #", "Check-In", "Check-Out", "Total ($)", "Status"}, 0);
        adminAllResTable = new JTable(adminAllResModel);
        tabs.addTab("Global Booking Monitor", new JScrollPane(adminAllResTable));

        dashboard.add(tabs, BorderLayout.CENTER);
        return dashboard;
    }

    private void handleAdminAddRoom() {
        try {
            int num = Integer.parseInt(newRoomNumField.getText().trim());
            String type = newRoomTypeField.getText().trim();
            String cat = (String) newRoomCatBox.getSelectedItem();
            double price = Double.parseDouble(newRoomPriceField.getText().trim());

            if (hotel.addRoom(num, type, cat, price)) {
                JOptionPane.showMessageDialog(this, "Room " + num + " added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                newRoomNumField.setText("");
                newRoomTypeField.setText("");
                newRoomPriceField.setText("");
                refreshAdminTables();
            } else {
                JOptionPane.showMessageDialog(this, "Room number already exists.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid values for Room # and Price.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleAdminDeleteUser() {
        int row = adminUsersTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a user from the table to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int guestId = (int) adminUsersModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete Guest #" + guestId + "?\nAll active reservations for this user will be cancelled.", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (hotel.deleteUser(guestId)) {
                JOptionPane.showMessageDialog(this, "User deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshAdminTables();
            } else {
                JOptionPane.showMessageDialog(this, "Cannot delete Admin accounts.", "Action Denied", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshAdminTables() {
        // Rooms
        adminRoomsModel.setRowCount(0);
        for (Room r : hotel.getAllRooms()) {
            adminRoomsModel.addRow(new Object[]{r.getRoomNumber(), r.getRoomType(), r.getCategory(), String.format("%.2f", r.getPricePerNight()), r.isAvailable() ? "Available" : "Booked"});
        }

        // Users
        adminUsersModel.setRowCount(0);
        for (User u : hotel.getAllUsers()) {
            adminUsersModel.addRow(new Object[]{u.getGuestId(), u.getUsername(), u.getRole()});
        }

        // Reservations
        adminAllResModel.setRowCount(0);
        for (Reservation res : hotel.getAllReservations()) {
            adminAllResModel.addRow(new Object[]{res.getReservationId(), res.getGuestId(), res.getRoomId(), res.getCheckInDate(), res.getCheckOutDate(), String.format("%.2f", res.getTotalAmount()), res.getStatus()});
        }
    }

    private void logout() {
        currentUser = null;
        cardLayout.show(mainPanel, "AUTH");
    }

    // ==========================================
    // 4. LIGHT / DARK THEME ENGINE
    // ==========================================
    private void toggleTheme() {
        isDarkMode = themeToggle.getSelectedIndex() == 1;
        applyTheme();
    }

    private void applyTheme() {
        Color bg = isDarkMode ? new Color(30, 30, 30) : new Color(245, 245, 247);
        Color panelBg = isDarkMode ? new Color(45, 45, 45) : Color.WHITE;
        Color textFg = isDarkMode ? new Color(220, 220, 220) : Color.BLACK;

        // Set global colors recursively
        updateComponentColors(this, bg, panelBg, textFg);

        SwingUtilities.updateComponentTreeUI(this);
    }

    private void updateComponentColors(Component comp, Color bg, Color panelBg, Color textFg) {
        if (comp instanceof JPanel || comp instanceof JTabbedPane) {
            comp.setBackground(panelBg);
        } else if (comp instanceof JLabel) {
            comp.setForeground(textFg);
        } else if (comp instanceof JTable) {
            comp.setBackground(panelBg);
            comp.setForeground(textFg);
        }

        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                updateComponentColors(child, bg, panelBg, textFg);
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new HotelSystemGUI().setVisible(true));
    }
}