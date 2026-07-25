public class Reservation {
    private int reservationId;
    private int guestId;
    private int roomId;
    private String checkInDate;
    private String checkOutDate;
    private double totalAmount;
    private String status;

    public Reservation(int reservationId, int guestId, int roomId, String checkInDate, String checkOutDate, double totalAmount, String status) {
        this.reservationId = reservationId;
        this.guestId = guestId;
        this.roomId = roomId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    // Getters and Setters
    public int getReservationId() { return reservationId; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }

    public int getGuestId() { return guestId; }
    public void setGuestId(int guestId) { this.guestId = guestId; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public String getCheckInDate() { return checkInDate; }
    public void setCheckInDate(String checkInDate) { this.checkInDate = checkInDate; }

    public String getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(String checkOutDate) { this.checkOutDate = checkOutDate; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Reservation #" + reservationId + " - Guest: " + guestId + ", Room: " + roomId + 
               ", Check-in: " + checkInDate + ", Check-out: " + checkOutDate + ", Total: $" + totalAmount + ", Status: " + status;
    }
}
