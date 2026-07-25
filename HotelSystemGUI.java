import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class HotelSystemGUI extends JFrame {
    private Hotel hotel;

    // GUI Components
    private JComboBox<String> categoryFilterBox;
    private JComboBox<Room> availableRoomsBox;
    private JTextField guestIdField;
    private JTextField checkInField;
    private JTextField checkOutField;
    private JTextField amountField;

    private JTable roomsTable;
    private DefaultTableModel roomsTableModel;

    private JTable reservationsTable;
    private DefaultTableModel reservationsTableModel;

    public HotelSystemGUI() {
        hotel = new Hotel(); // Initialize backend business logic

        setTitle("Hotel Reservation System - CodeAlpha");
        setPreferredSize(new Dimension(850, 650));
        setMinimumSize(new Dimension(800, 550));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ---------------- TOP BOOKING PANEL ----------------
        JPanel bookingPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        bookingPanel.setBorder(BorderFactory.createTitledBorder("Make a Reservation"));

        // Row 1: Filter & Select Room
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row1.add(new JLabel("Category Filter:"));
        categoryFilterBox = new JComboBox<>(new String[]{"All", "Standard", "Deluxe", "Suite"});
        row1.add(categoryFilterBox);

        row1.add(new JLabel("Select Available Room:"));
        availableRoomsBox = new JComboBox<>();
        availableRoomsBox.setPreferredSize(new Dimension(280, 25));
        row1.add(availableRoomsBox);

        // Row 2: Booking Inputs
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row2.add(new JLabel("Guest ID:"));
        guestIdField = new JTextField(6);
        row2.add(guestIdField);

        row2.add(new JLabel("Check-In:"));
        checkInField = new JTextField("2026-08-01", 8);
        row2.add(checkInField);

        row2.add(new JLabel("Check-Out:"));
        checkOutField = new JTextField("2026-08-05", 8);
        row2.add(checkOutField);

        row2.add(new JLabel("Total ($):"));
        amountField = new JTextField(6);
        row2.add(amountField);

        // Row 3: Action Buttons
        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton bookButton = new JButton("Confirm Booking & Payment");
        JButton cancelButton = new JButton("Cancel Selected Reservation");
        cancelButton.setForeground(Color.RED.darker());

        row3.add(bookButton);
        row3.add(cancelButton);

        bookingPanel.add(row1);
        bookingPanel.add(row2);
        bookingPanel.add(row3);
        add(bookingPanel, BorderLayout.NORTH);

        // ---------------- CENTER TABBED PANELS ----------------
        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab 1: Rooms Table
        String[] roomCols = {"Room Number", "Type", "Category", "Price / Night", "Availability"};
        roomsTableModel = new DefaultTableModel(roomCols, 0);
        roomsTable = new JTable(roomsTableModel);
        tabbedPane.addTab("Room Directory", new JScrollPane(roomsTable));

        // Tab 2: Reservations Table
        String[] resCols = {"Reservation ID", "Guest ID", "Room Number", "Check-In", "Check-Out", "Amount ($)", "Status"};
        reservationsTableModel = new DefaultTableModel(resCols, 0);
        reservationsTable = new JTable(reservationsTableModel);
        tabbedPane.addTab("Reservations Log", new JScrollPane(reservationsTable));

        add(tabbedPane, BorderLayout.CENTER);

        // ---------------- ACTION LISTENERS ----------------
        categoryFilterBox.addActionListener(e -> updateAvailableRoomsDropdown());
        availableRoomsBox.addActionListener(e -> autoCalculateAmount());
        bookButton.addActionListener(e -> handleBooking());
        cancelButton.addActionListener(e -> handleCancellation());

        // Initial Data Load
        refreshTables();
        updateAvailableRoomsDropdown();

        pack();
        setLocationRelativeTo(null);
    }

    private void updateAvailableRoomsDropdown() {
        availableRoomsBox.removeAllItems();
        String selectedCat = (String) categoryFilterBox.getSelectedItem();
        List<Room> available = hotel.searchAvailableRooms(selectedCat);

        for (Room r : available) {
            availableRoomsBox.addItem(r);
        }
        autoCalculateAmount();
    }

    private void autoCalculateAmount() {
        Room selectedRoom = (Room) availableRoomsBox.getSelectedItem();
        if (selectedRoom != null) {
            // Default calculation (4 nights sample estimate)
            amountField.setText(String.valueOf(selectedRoom.getPricePerNight() * 4));
        } else {
            amountField.setText("");
        }
    }

    private void handleBooking() {
        Room selectedRoom = (Room) availableRoomsBox.getSelectedItem();
        if (selectedRoom == null) {
            JOptionPane.showMessageDialog(this, "Please select an available room.", "Booking Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int guestId = Integer.parseInt(guestIdField.getText().trim());
            String checkIn = checkInField.getText().trim();
            String checkOut = checkOutField.getText().trim();
            double total = Double.parseDouble(amountField.getText().trim());

            Reservation res = hotel.makeReservation(guestId, selectedRoom.getRoomNumber(), checkIn, checkOut, total);

            if (res != null) {
                JOptionPane.showMessageDialog(this, "Booking Successful!\nReservation ID: " + res.getReservationId(), "Success", JOptionPane.INFORMATION_MESSAGE);
                guestIdField.setText("");
                refreshTables();
                updateAvailableRoomsDropdown();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to book room.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for Guest ID and Amount.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCancellation() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reservation row from the Reservations Log tab to cancel.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int reservationId = (int) reservationsTableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel Reservation #" + reservationId + "?", "Confirm Cancel", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = hotel.cancelReservation(reservationId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Reservation #" + reservationId + " has been cancelled.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTables();
                updateAvailableRoomsDropdown();
            } else {
                JOptionPane.showMessageDialog(this, "Unable to cancel reservation (might already be cancelled).", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshTables() {
        // Refresh Rooms Table
        roomsTableModel.setRowCount(0);
        for (Room r : hotel.getAllRooms()) {
            roomsTableModel.addRow(new Object[]{
                r.getRoomNumber(), r.getRoomType(), r.getCategory(), String.format("%.2f", r.getPricePerNight()), r.isAvailable() ? "Available" : "Booked"
            });
        }

        // Refresh Reservations Table
        reservationsTableModel.setRowCount(0);
        for (Reservation res : hotel.getAllReservations()) {
            reservationsTableModel.addRow(new Object[]{
                res.getReservationId(), res.getGuestId(), res.getRoomId(), res.getCheckInDate(), res.getCheckOutDate(), String.format("%.2f", res.getTotalAmount()), res.getStatus()
            });
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