# CodeAlpha - Hotel Reservation System

A Java-based desktop GUI application designed to search, book, and manage hotel room reservations with simulated payment processing and dynamic inventory management — now shipping with a matching **live web preview** (`HotelSystemWeb.html`) you can open directly from the repo. Developed as part of the **CodeAlpha Java Development Internship**.

---

## 📌 Features

* **Room Categorization & Directory:** Search and browse room availability across different room types and categories (Standard, Deluxe, Suite).
* **Reservation & Booking Engine:**
  * Select available rooms dynamically based on category filters.
  * **Date-aware auto-pricing:** booking totals are calculated from the actual number of nights between check-in and check-out (falling back to a fixed 4-night estimate when dates are missing or invalid), and refresh automatically whenever the dates change.
  * Register guest check-in and check-out stay durations.
* **Reservation Management & Cancellations:** View active booking logs and cancel confirmed reservations to immediately restore room availability.
* **User Authentication:** Role-based access control with USER and ADMIN roles. New accounts land back on the Login tab after registration, and demo credentials are shown right on the login screen.
* **Status Badge Tables:** Room availability, reservation statuses, and user roles render as color-coded pill badges that adapt automatically to the active theme.
* **Dual-Theme Engine:** Warm cream & gold Light Mode and deep indigo & neon-accent Dark Mode, applied recursively across every panel, table, field, and header.
* **Tabbed GUI Interface:** Clean Java Swing desktop dashboard featuring dynamic tables for room directory listings and real-time reservation logs.
* **Simulated Payment Processing:** Instant booking confirmation and status tracking.

---

## 🛠️ Tech Stack & Concepts Used

* **Language:** Java (JDK 8+)
* **GUI Framework:** Java Swing (`JFrame`, `JTabbedPane`, `JTable`, `JComboBox`, `DefaultTableModel`, custom `TableCellRenderer`)
* **Web Preview:** Vanilla HTML/CSS/JS in a single dependency-free file (no build step, no server required)
* **Architecture:** Model-View-Controller (MVC) design pattern using object-oriented entity classes (`Room`, `Reservation`, `Hotel`, `User`)
* **Data Structures:** Object Collections (`ArrayList`, `List`) for state management and dynamic list updates

---

## 📁 Project Structure

```text
CodeAlpha_HRS/
├── README.md
├── Room.java
├── Reservation.java
├── Hotel.java
├── User.java
├── HotelSystemGUI.java
└── HotelSystemWeb.html      ← dependency-free browser preview of the desktop app
```

---

## 🚀 Getting Started

**Prerequisites**
- Java Development Kit (JDK 8 or higher) installed
- Terminal or Java IDE (VS Code, IntelliJ IDEA, Eclipse)

**Run the desktop application:**
```bash
javac *.java
java HotelSystemGUI
```

### 🔑 Demo Accounts

| Role  | Username  | Password   |
|-------|-----------|------------|
| Admin | `admin`   | `admin123` |
| Guest | `john_doe`| `password` |

New guest accounts can also be registered from the Login screen (guest IDs are assigned automatically).

---

## 🌐 Web Preview

`HotelSystemWeb.html` is a faithful, single-file web port of the desktop app — same business engine, seed data, validation messages, and light/dark color palettes. It works fully client-side (state resets on page reload).

**Open it in any browser:**
- Double-click `HotelSystemWeb.html`, or
- Serve the folder with a static server, e.g.:

```bash
python3 -m http.server 8000
# then visit http://localhost:8000/HotelSystemWeb.html
```

---

## 🎥 Video Demonstration
LinkedIn Demo Post: (Yet to be made)

## 📄 License & Acknowledgments
Developed during the Java Development Internship at CodeAlpha
