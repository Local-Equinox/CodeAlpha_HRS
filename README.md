# CodeAlpha - Hotel Reservation System

A Java-based desktop application designed to search, book, and manage hotel room reservations with simulated payment processing and dynamic inventory management. Built as part of the **CodeAlpha Java Development Internship**.

---

## 📌 Features

* **Room Categorization & Directory:** Search and browse room availability across different room types and categories (Standard, Deluxe, Suite).
* **Reservation & Booking Engine:**
  * Select available rooms dynamically based on category filters.
  * Auto-calculate total booking costs based on room rates.
  * Register guest check-in and check-out stay durations.
* **Reservation Management & Cancellations:** View active booking logs and cancel confirmed reservations to immediately restore room availability.
* **User Authentication:** Role-based access control with USER and ADMIN roles.
* **Tabbed GUI Interface:** Clean Java Swing desktop dashboard featuring dynamic tables for room directory listings and real-time reservation logs.
* **Simulated Payment Processing:** Instant booking confirmation and status tracking.

---

## 🛠️ Tech Stack & Concepts Used

* **Language:** Java (JDK 8+)
* **GUI Framework:** Java Swing (`JFrame`, `JTabbedPane`, `JTable`, `JComboBox`, `DefaultTableModel`)
* **Architecture:** Model-View-Controller (MVC) design pattern using object-oriented entity classes (`Room`, `Reservation`, `Hotel`, `User`)
* **Data Structures:** Object Collections (`ArrayList`, `List`) for state management and dynamic list updates

---

## 📁 Project Structure

```text
CodeAlpha_HotelReservationSystem/
├── README.md
├── Room.java
├── Reservation.java
├── Hotel.java
├── User.java
└── HotelSystemGUI.java
```

## 🚀 Getting Started

**Prerequisites**
- Java Development Kit (JDK 8 or higher) installed
- Terminal or Java IDE (VS Code, IntelliJ IDEA, Eclipse)

**Run the application:**
```bash
javac *.java
java HotelSystemGUI
```

## 🎥 Video Demonstration
LinkedIn Demo Post: (Yet to be made)

## 📄 License & Acknowledgments
Developed during the Java Development Internship at CodeAlpha
