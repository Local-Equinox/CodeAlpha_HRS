import java.util.ArrayList;
import java.util.List;

public class Hotel {
    private ArrayList<Room> rooms;
    private ArrayList<Reservation> reservations;
    private int reservationCounter = 1001;

    public Hotel() {
        rooms = new ArrayList<>();
        reservations = new ArrayList<>();
        rooms.add(new Room(101, "Standard", "Standard", 100.0));
        rooms.add(new Room(102, "Standard", "Standard", 100.0));
        rooms.add(new Room(201, "Deluxe", "Deluxe", 200.0));
        rooms.add(new Room(301, "Suite", "Suite", 350.0));
    }
    
    // Getters and Setters
    public ArrayList<Room> getRooms() { return rooms; }
    public void setRooms(ArrayList<Room> rooms) { this.rooms = rooms; }
    
    public ArrayList<Reservation> getReservations() { return reservations; }
    public void setReservations(ArrayList<Reservation> reservations) { this.reservations = reservations; }
    
    @Override
    public String toString() {
        return "Hotel with " + rooms.size() + " rooms, " + " and " + reservations.size() + " reservations";
    }

    public List<Room> searchAvailableRooms(String category) {
        List<Room> availableRooms = new ArrayList<>();
        for (Room room : rooms) {
            // If category is "All" or matches room category AND room is available
            boolean categoryMatch = category.equalsIgnoreCase("All") || room.getCategory().equalsIgnoreCase(category);
            if (categoryMatch && room.isAvailable()) {
                availableRooms.add(room);
            }
        }
        return availableRooms;
    }

    public Reservation makeReservation(int guestId, int roomId, String checkInDate, String checkOutDate, double totalAmount) {
        // Find the room
        Room selectedRoom = null;
        for (Room r : rooms) {
            if (r.getRoomNumber() == roomId && r.isAvailable()) {
                selectedRoom = r;
                break;
            }
        }

        if (selectedRoom == null) {
            return null; // Room not available or doesn't exist
        }

        // Mark room as booked and create reservation
        selectedRoom.setAvailable(false);
        Reservation newReservation = new Reservation(
            reservationCounter++, guestId, roomId, checkInDate, checkOutDate, totalAmount, "CONFIRMED"
        );
        reservations.add(newReservation);
        return newReservation;
    }

    public boolean cancelReservation(int reservationId) {
for (Reservation res : reservations) {
            if (res.getReservationId() == reservationId && res.getStatus().equalsIgnoreCase("CONFIRMED")) {
                res.setStatus("CANCELLED");

                // Free up the room
                for (Room r : rooms) {
                    if (r.getRoomNumber() == res.getRoomId()) {
                        r.setAvailable(true);
                        break;
                    }
                }
                return true; // Cancelled successfully
            }
        }
        return false; // Reservation not found
    }

    // Getters for GUI binding
    public List<Room> getAllRooms() { return rooms; }
    public List<Reservation> getAllReservations() { return reservations; }
}
