

public class Room {
    private int roomNumber;
    private String roomType;
    private String category;
    private double pricePerNight;
    private boolean isAvailable;
    
    public Room(int roomNumber, String roomType, String category, double pricePerNight) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.category = category;
        this.pricePerNight = pricePerNight;
        this.isAvailable = true;
    }
    
    // Getters and Setters
    public int getRoomNumber() { return roomNumber; }
    public void setRoomNumber(int roomNumber) { this.roomNumber = roomNumber; }
    
    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public double getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(double pricePerNight) { this.pricePerNight = pricePerNight; }
    
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    
    @Override
    public String toString() {
        return "Room " + roomNumber + " (" + roomType + " - " + category + ") - $" + pricePerNight + "/night";
    }

    // Quick helper methods for toggling booking status
    public void bookRoom() {
        this.isAvailable = false;
    }

    public void releaseRoom() {
        this.isAvailable = true;
    }
}
