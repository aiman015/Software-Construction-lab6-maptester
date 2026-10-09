# Software Construction Lab 6 – MapTester

A Java phone book application demonstrating the use of `HashMap` for storing and managing contact information.

## Features

- Add contacts using `put()`.
- Search for phone numbers using `get()`.
- Find contacts associated with a phone number.
- Delete contacts using `remove()`.
- Display contact names in alphabetical order.
- Demonstrate duplicate key and duplicate value behavior.
- Provide a graphical user interface using Java Swing.
- Display phone book statistics.

## Technologies Used

- Java
- HashMap and Java Collections Framework
- Java Swing and AWT
- Eclipse IDE

## Project Structure

```text
Lab6/
├── src/
│   ├── MapTester.java
│   └── Maptestergui.java
├── README.md
└── .gitignore
```

## How to Run

1. Open the project in Eclipse.
2. Open `MapTester.java` and run its `main()` method to see the HashMap demonstration.
3. Open `Maptestergui.java` and run its `main()` method to launch the graphical phone book.

## HashMap Demonstration

### Duplicate Keys

```java
phoneBook.put("Alice", "111-1111");
phoneBook.put("Alice", "222-2222");
```

The second value replaces the first because HashMap keys must be unique.

### Duplicate Values

```java
phoneBook.put("Bob", "555-5555");
phoneBook.put("Charlie", "555-5555");
```

Both contacts are stored because HashMap allows duplicate values.

## Learning Objectives

- Understand key-value pairs.
- Practice Java collection methods.
- Learn how HashMap handles duplicate keys and values.
- Develop a basic GUI application.

**Course:** Software Construction  
**Lab:** Lab 6 – Task 1: MapTester
