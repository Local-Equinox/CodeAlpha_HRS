public class User {
    private int guestId;
    private String username;
    private String password;
    private String role; // "USER" or "ADMIN"

    public User(int guestId, String username, String password, String role) {
        this.guestId = guestId;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public int getGuestId() { return guestId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public boolean isAdmin() { return "ADMIN".equalsIgnoreCase(role); }

    @Override
    public String toString() {
        return username + " (Guest ID: #" + guestId + ")";
    }
}