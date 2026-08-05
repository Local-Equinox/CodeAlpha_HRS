import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class HotelSystemGUI extends JFrame {
    private Hotel hotel;
    private User currentUser;
    private boolean isDarkMode = false;

    // UI Container Cards
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JTabbedPane authTabs;

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
    private String lastAutoCalcDates = "";
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
        applyExactTheme();

    }

    // ==========================================
    // 1. AUTHENTICATION PANEL (LOGIN / REGISTER)
    // ==========================================
    private JPanel createAuthPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        authTabs = new JTabbedPane();
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

        // Demo credentials hint (parity with the web preview login screen)
        JLabel demoHint = new JLabel("<html><center>Demo accounts — Admin: <b>admin / admin123</b> &nbsp;·&nbsp; Guest: <b>john_doe / password</b><br>Or register a new guest account.</center></html>");
        demoHint.setFont(demoHint.getFont().deriveFont(11f));
        demoHint.setHorizontalAlignment(SwingConstants.CENTER);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 1;
        gbc.insets = new Insets(14, 0, 0, 0);
        panel.add(demoHint, gbc);

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
            authTabs.setSelectedIndex(0); // land on the Login tab after registering
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

        // Recompute the total when dates are committed (parity with the web preview)
        checkInField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                recalcIfDatesChanged();
            }
        });
        checkOutField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                recalcIfDatesChanged();
            }
        });

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
        userRoomsTable.getColumnModel().getColumn(4).setCellRenderer(new StatusBadgeRenderer());
        tabs.addTab("Available Rooms Directory", new JScrollPane(userRoomsTable));

        userResTableModel = new DefaultTableModel(new String[]{"Reservation ID", "Room Number", "Check-In", "Check-Out", "Amount ($)", "Status"}, 0);
        userResTable = new JTable(userResTableModel);
        userResTable.getColumnModel().getColumn(5).setCellRenderer(new StatusBadgeRenderer());

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
        lastAutoCalcDates = checkInField.getText() + "|" + checkOutField.getText();
        Room r = (Room) userRoomDropdown.getSelectedItem();
        if (r != null) {
            int nights = calculateNights(checkInField.getText(), checkOutField.getText());
            totalAmountField.setText(String.format(Locale.US, "%.2f", r.getPricePerNight() * nights));
        } else {
            totalAmountField.setText("");
        }
    }

    // Only recompute when the dates actually changed, so a manually
    // entered total is not clobbered on unrelated focus loss.
    private void recalcIfDatesChanged() {
        String key = checkInField.getText() + "|" + checkOutField.getText();
        if (!key.equals(lastAutoCalcDates)) {
            autoCalculateUserAmount();
        }
    }

    // Date-aware night count (parity with the web preview): falls back to the
    // original fixed 4-night behavior when dates are missing or invalid.
    private int calculateNights(String checkIn, String checkOut) {
        try {
            LocalDate inDate = LocalDate.parse(checkIn.trim());
            LocalDate outDate = LocalDate.parse(checkOut.trim());
            long nights = ChronoUnit.DAYS.between(inDate, outDate);
            if (nights > 0) return (int) nights;
        } catch (Exception ignored) {
            // fall through to the default
        }
        return 4;
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
        adminRoomsTable.getColumnModel().getColumn(4).setCellRenderer(new StatusBadgeRenderer());

        roomMgmtPanel.add(addRoomBox, BorderLayout.NORTH);
        roomMgmtPanel.add(new JScrollPane(adminRoomsTable), BorderLayout.CENTER);
        tabs.addTab("Manage Rooms Inventory", roomMgmtPanel);

        // Admin Tab 2: User Account Control
        JPanel usersPanel = new JPanel(new BorderLayout());
        adminUsersModel = new DefaultTableModel(new String[]{"Guest ID", "Username", "Role"}, 0);
        adminUsersTable = new JTable(adminUsersModel);
        adminUsersTable.getColumnModel().getColumn(2).setCellRenderer(new StatusBadgeRenderer());

        JButton deleteUserBtn = new JButton("Delete Selected User Account");
        deleteUserBtn.setForeground(Color.RED.darker());
        deleteUserBtn.addActionListener(e -> handleAdminDeleteUser());

        usersPanel.add(new JScrollPane(adminUsersTable), BorderLayout.CENTER);
        usersPanel.add(deleteUserBtn, BorderLayout.SOUTH);
        tabs.addTab("User Accounts", usersPanel);

        // Admin Tab 3: Global Reservations Monitor
        adminAllResModel = new DefaultTableModel(new String[]{"Res ID", "Guest ID", "Room #", "Check-In", "Check-Out", "Total ($)", "Status"}, 0);
        adminAllResTable = new JTable(adminAllResModel);
        adminAllResTable.getColumnModel().getColumn(6).setCellRenderer(new StatusBadgeRenderer());
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
    // EXACT SCREENSHOT COLOR PALETTE ENGINE
    // ==========================================
    private void toggleTheme() {
        isDarkMode = themeToggle.getSelectedIndex() == 1;
        applyExactTheme();
    }

    private void applyExactTheme() {
        // --- 1. LIGHT MODE PALETTE ---
        Color lightBg         = new Color(0xF6, 0xF3, 0xEB); // #F6F3EB Main Cream
        Color lightTopBar     = new Color(0xEF, 0xEC, 0xE3); // #EFECE3 Top Header
        Color lightCard       = new Color(0xFF, 0xFF, 0xFF); // #FFFFFF Pure White
        Color lightInputBg    = new Color(0xF3, 0xEF, 0xE6); // #F3EFE6 Input Fields
        Color lightText       = new Color(0x3C, 0x38, 0x35); // #3C3835 Primary Text
        Color lightBtnBg      = new Color(0xC5, 0x9B, 0x4B); // #C59B4B Gold Button
        Color lightBorder     = new Color(0xE5, 0xDE, 0xC9); // #E5DEC9 Soft Border

        // --- 2. DARK MODE PALETTE ---
        Color darkBg          = new Color(0x0B, 0x0D, 0x17); // #0B0D17 Main Indigo
        Color darkTopBar      = new Color(0x12, 0x15, 0x24); // #121524 Top Header
        Color darkCard        = new Color(0x16, 0x19, 0x28); // #161928 Midnight Card
        Color darkInputBg     = new Color(0x0E, 0x10, 0x1B); // #0E101B Input Fields
        Color darkText        = new Color(0xEC, 0xEF, 0xFC); // #ECEFFC Soft Ice Text
        Color darkBtnBg       = new Color(0x3B, 0x43, 0x78); // #3B4378 Indigo Button
        Color darkBorder      = new Color(0x27, 0x2C, 0x48); // #272C48 Indigo Border

        // --- 3. ACTIVE PALETTE ASSIGNMENT ---
        Color activeBg      = isDarkMode ? darkBg : lightBg;
        Color activeTopBar  = isDarkMode ? darkTopBar : lightTopBar;
        Color activeCard    = isDarkMode ? darkCard : lightCard;
        Color activeInput   = isDarkMode ? darkInputBg : lightInputBg;
        Color activeText    = isDarkMode ? darkText : lightText;
        Color activeBtnBg   = isDarkMode ? darkBtnBg : lightBtnBg;
        Color activeBorder  = isDarkMode ? darkBorder : lightBorder;

        // Apply background to window frame & main card container
        getContentPane().setBackground(activeBg);
        mainPanel.setBackground(activeBg);

        // Style window components recursively
        styleComponents(this, activeBg, activeTopBar, activeCard, activeInput, activeText, activeBtnBg, activeBorder);

        // Force UI tree refresh
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void styleComponents(Component comp, Color bg, Color topBarBg, Color cardBg, Color inputBg, Color textFg, Color btnBg, Color borderClr) {
        
        // Panels & Cards
        if (comp instanceof JPanel) {
            JPanel panel = (JPanel) comp;
            
            // Check if panel is part of the top header bar
            if (panel.getParent() == getContentPane() && panel.getLayout() instanceof BorderLayout) {
                panel.setBackground(topBarBg);
            } else {
                panel.setBackground(cardBg);
            }

            if (panel.getBorder() instanceof javax.swing.border.TitledBorder) {
                javax.swing.border.TitledBorder border = (javax.swing.border.TitledBorder) panel.getBorder();
                border.setTitleColor(textFg);
                border.setBorder(BorderFactory.createLineBorder(borderClr, 1, true));
            }
        } 
        // Labels
        else if (comp instanceof JLabel) {
            comp.setForeground(textFg);
        } 
        // Action Buttons
        else if (comp instanceof JButton) {
            JButton btn = (JButton) comp;
            btn.setBackground(btnBg);
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("SansSerif", Font.BOLD, 13));
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderClr, 1, true),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
            ));
        } 
        // Text Input Fields & Password Fields
        else if (comp instanceof JTextField || comp instanceof JPasswordField) {
            comp.setBackground(inputBg);
            comp.setForeground(textFg);
            comp.setFont(new Font("SansSerif", Font.PLAIN, 13));
            ((JComponent) comp).setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderClr, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
            ));
        } 
        // Dropdown Combo Boxes
        else if (comp instanceof JComboBox) {
            comp.setBackground(inputBg);
            comp.setForeground(textFg);
        } 
        // Data Tables (User Dashboard & Admin Directory)
        else if (comp instanceof JTable) {
            JTable table = (JTable) comp;
            table.setBackground(cardBg);
            table.setForeground(textFg);
            table.setGridColor(borderClr);
            table.setSelectionBackground(btnBg);
            table.setSelectionForeground(Color.WHITE);
            table.setRowHeight(24);
            
            if (table.getTableHeader() != null) {
                table.getTableHeader().setBackground(btnBg);
                table.getTableHeader().setForeground(Color.WHITE);
                table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
            }
        } 
        // Tabbed Views
        else if (comp instanceof JTabbedPane) {
            comp.setBackground(cardBg);
            comp.setForeground(textFg);
        }

        // Process child components
        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                styleComponents(child, bg, topBarBg, cardBg, inputBg, textFg, btnBg, borderClr);
            }
        }
    }

    // ==========================================
    // STATUS BADGE RENDERER — colored pill badges for status columns
    // (parity with the web preview's badge styling, both themes)
    // ==========================================
    private class StatusBadgeRenderer extends JLabel implements TableCellRenderer {
        private Color badgeBg;
        private boolean isBadge;
        private JTable hostTable;
        private boolean rowSelected;

        StatusBadgeRenderer() {
            setOpaque(false);
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(getFont().deriveFont(Font.BOLD, 11f));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            hostTable = table;
            rowSelected = isSelected;
            String text = value == null ? "" : value.toString().trim();
            setText(text);
            isBadge = false;
            setOpaque(false);
            switch (text.toUpperCase()) {
                case "AVAILABLE":
                case "CONFIRMED":
                    badgeBg = isDarkMode ? new Color(23, 48, 31) : new Color(232, 245, 233);
                    setForeground(isDarkMode ? new Color(123, 216, 143) : new Color(46, 125, 50));
                    isBadge = true;
                    break;
                case "BOOKED":
                case "CANCELLED":
                    badgeBg = isDarkMode ? new Color(58, 27, 27) : new Color(253, 236, 234);
                    setForeground(isDarkMode ? new Color(255, 138, 128) : new Color(179, 38, 30));
                    isBadge = true;
                    break;
                case "ADMIN":
                case "USER":
                    badgeBg = isDarkMode ? new Color(51, 38, 15) : new Color(255, 244, 224);
                    setForeground(isDarkMode ? new Color(255, 196, 107) : new Color(178, 106, 0));
                    isBadge = true;
                    break;
                default:
                    setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
                    setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                    setOpaque(true);
            }
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (!isBadge) { super.paintComponent(g); return; }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Dimension d = getSize();
            // Fill the full cell first so the row-selection band stays visible
            g2.setColor(rowSelected ? hostTable.getSelectionBackground() : hostTable.getBackground());
            g2.fillRect(0, 0, d.width, d.height);
            int h = Math.min(d.height - 6, 20);
            int y = (d.height - h) / 2;
            int w = getFontMetrics(getFont()).stringWidth(getText()) + 20;
            int x = (d.width - w) / 2;
            g2.setColor(badgeBg);
            g2.fillRoundRect(x, y, w, h, h, h);
            g2.dispose();
            super.paintComponent(g);
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