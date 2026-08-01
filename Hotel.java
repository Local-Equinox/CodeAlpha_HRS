import java.util.ArrayList;
import java.util.List;

public class Hotel {
    private List<Room> rooms;
    private List<Reservation> reservations;
    private List<User> users;
    
    private int userCounter = 1001;
    private int reservationCounter = 5001;

    public Hotel() {
        rooms = new ArrayList<>();
        reservations = new ArrayList<>();
        users = new ArrayList<>();

        // Default Admin Account
        users.add(new User(9999, "admin", "admin123", "ADMIN"));

        // Default Sample User
        users.add(new User(userCounter++, "john_doe", "password", "USER"));

        // Initial Room Inventory
        rooms.add(new Room(101, "Single Bed", "Standard", 100.0));
        rooms.add(new Room(102, "Double Bed", "Standard", 120.0));
        rooms.add(new Room(201, "King Bed", "Deluxe", 220.0));
        rooms.add(new Room(301, "Executive Suite", "Suite", 450.0));
    }

    // --- Authentication Logic ---
    public User authenticate(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    public User registerUser(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return null; // Username taken
            }
        }
        User newUser = new User(userCounter++, username, password, "USER");
        users.add(newUser);
        return newUser;
    }

    public boolean deleteUser(int guestId) {
        User target = null;
        for (User u : users) {
            if (u.getGuestId() == guestId && !u.isAdmin()) {
                target = u;
                break;
            }
        }

        if (target != null) {
            users.remove(target);
            // Cancel active reservations for this user
            for (Reservation res : reservations) {
                if (res.getGuestId() == guestId && "CONFIRMED".equalsIgnoreCase(res.getStatus())) {
                    cancelReservation(res.getReservationId());
                }
            }
            return true;
        }
        return false;
    }

    // --- Room & Reservation Operations ---
    public boolean addRoom(int roomNumber, String type, String category, double price) {
        for (Room r : rooms) {
            if (r.getRoomNumber() == roomNumber) return false; // Duplicate room number
        }
        rooms.add(new Room(roomNumber, type, category, price));
        return true;
    }

    public List<Room> searchAvailableRooms(String category) {
        List<Room> available = new ArrayList<>();
        for (Room r : rooms) {
            boolean match = category.equalsIgnoreCase("All") || r.getCategory().equalsIgnoreCase(category);
            if (match && r.isAvailable()) {
                available.add(r);
            }
        }
        return available;
    }

    public Reservation makeReservation(int guestId, int roomId, String checkIn, String checkOut, double totalAmount) {
        Room selectedRoom = null;
        for (Room r : rooms) {
            if (r.getRoomNumber() == roomId && r.isAvailable()) {
                selectedRoom = r;
                break;
            }
        }

        if (selectedRoom == null) return null;

        selectedRoom.setAvailable(false);
        Reservation newRes = new Reservation(reservationCounter++, guestId, roomId, checkIn, checkOut, totalAmount, "CONFIRMED");
        reservations.add(newRes);
        return newRes;
    }

    public boolean cancelReservation(int reservationId) {
        for (Reservation res : reservations) {
            if (res.getReservationId() == reservationId && res.getStatus().equalsIgnoreCase("CONFIRMED")) {
                res.setStatus("CANCELLED");
                for (Room r : rooms) {
                    if (r.getRoomNumber() == res.getRoomId()) {
                        r.setAvailable(true);
                        break;
                    }
                }
                return true;
            }
        }
        return false;
    }

    // Getters
    public List<Room> getAllRooms() { return rooms; }
    public List<Reservation> getAllReservations() { return reservations; }
    public List<User> getAllUsers() { return users; }
    
    public List<Reservation> getReservationsForGuest(int guestId) {
        List<Reservation> userRes = new ArrayList<>();
        for (Reservation r : reservations) {
            if (r.getGuestId() == guestId) userRes.add(r);
        }
        return userRes;
    }
}