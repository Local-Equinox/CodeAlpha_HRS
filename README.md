# CodeAlpha - Hotel Reservation System

A Java-based desktop application designed to search, book, and manage hotel room reservations with simulated payment processing and dynamic inventory management. Built as part of the **CodeAlpha Java Development Internship**.

---

## 📌 Features

* **Room Categorization & Directory:** Search and browse room availability across different room types and categories (Standard, Deluxe, Suite).
* **Reservation & Booking Engine:**
  * Select available rooms dynamically based on category filters.
  * Auto-calculate total booking costs based on room rates.
  * Register guest check-in and check-out stay durations.
* **Reservation Management & Cancellations:** View active booking logs and cancel confirmed reservations to immediately restore room availability[cite: 1].
* **Tabbed GUI Interface:** Clean Java Swing desktop dashboard featuring dynamic tables for room directory listings and real-time reservation logs[cite: 1].
* **Simulated Payment Processing:** Instant booking confirmation and status tracking[cite: 1].

---

## 🛠️ Tech Stack & Concepts Used

* **Language:** Java (JDK 8+)
* **GUI Framework:** Java Swing (`JFrame`, `JTabbedPane`, `JTable`, `JComboBox`, `DefaultTableModel`)[cite: 1]
* **Architecture:** Model-View-Controller (MVC) design pattern using object-oriented entity classes (`Room`, `Reservation`, `Hotel`)[cite: 1]
* **Data Structures:** Object Collections (`ArrayList`, `List`) for state management and dynamic list updates[cite: 1]

---

## 📁 Project Structure

```text
CodeAlpha_HotelReservationSystem/
├── README.md
└── src/
    ├── Room.java
    ├── Reservation.java
    ├── Hotel.java
    └── HotelSystemGUI.java
```

## 🚀 Getting Started
Prerequisites
Java Development Kit (JDK 8 or higher) installed

Terminal or Java IDE (VS Code, IntelliJ IDEA, Eclipse)

## 🎥 Video Demonstration
LinkedIn Demo Post: (Yet to be made)

## 📄 License & Acknowledgments
Developed during the Java Development Internship at CodeAlpha
