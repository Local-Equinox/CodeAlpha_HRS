# CodeAlpha Hotel Reservation System

A Java-based hotel reservation management system with a Swing GUI interface.

## Features

- **Room Management**: View and manage hotel rooms with different categories (Standard, Deluxe, Suite)
- **Reservation System**: Create and manage guest reservations with booking status tracking
- **User Authentication**: Role-based access control (USER/ADMIN roles)
- **Room Availability**: Filter and search available rooms by category
- **Booking Cancellation**: Cancel reservations with automatic room availability updates
- **GUI Interface**: User-friendly Swing-based interface for all operations

## Project Structure

- `Hotel.java` - Core business logic for hotel operations
- `Reservation.java` - Reservation entity with status tracking (CONFIRMED/CANCELLED)
- `Room.java` - Room entity with availability and pricing
- `User.java` - User entity with role-based authentication
- `HotelSystemGUI.java` - Swing GUI for user interaction

## How to Run

Compile and run the GUI:
```bash
javac *.java
java HotelSystemGUI
```

## Recent Updates

- Added User class with role-based authentication (USER/ADMIN)
- Implemented reservation status tracking (CONFIRMED/CANCELLED)
- Added room availability filtering by category
- Implemented automatic room availability updates on booking/cancellation
- Added comprehensive GUI with tabbed interface for rooms and reservations
- Added total amount calculation for reservations