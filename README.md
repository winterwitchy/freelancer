## freelancer, a freelancing platform simulation

**freelancer** is a high-performance Java application designed to simulate a gig economy platform. It efficiently manages user registrations, job matching, and month-to-month simulation updates using custom-built data structures.

The system is designed to handle large datasets of customers and freelancers, processing commands in real-time to match the best-suited freelancers to jobs based on a complex scoring algorithm.

## Features

* **User Management:** Handles registration for Customers and Freelancers with unique IDs.
* **Service Categories:** Supports 10 distinct service types (e.g., Web Dev, Painting, Tutoring), each with specific skill requirements.
* **Smart Matching Algorithm:** Matches freelancers using a weighted **Composite Score** derived from:
    * Skill Proficiency (Technical, Communication, Creativity, Efficiency, Attention to Detail).
    * Customer Ratings.
    * Reliability (Completion vs. Cancellation ratio).
* **Dynamic Skill Evolution:**
    * Skills improve when a job is completed with a high rating (4.0+).
    * Skills degrade and penalties are applied if a freelancer cancels a job.
* **Market Simulation:**
    * **Burnout System:** Freelancers taking too many jobs risk burnout, significantly lowering their ranking.
    * **Loyalty Tiers:** Customers earn statuses (Bronze to Platinum) based on spending, unlocking platform subsidies.
    * **Blacklisting:** Supports both customer-specific blacklists and platform-wide bans for unreliable freelancers.

## Technical Implementation

This project relies on **custom implementation of core data structures** rather than standard Java Collections (except `ArrayList`) to ensure optimized performance for specific operations.

### Data Structures
* **Custom HashTable (`HashTable.java`):**
    * Uses **Open Addressing** with **Quadratic Probing** (`hash + i*i`) for collision resolution.
    * Implements Horner's Method for string hashing.
    * Supports dynamic resizing (finding the next prime number for capacity) to maintain low load factors.
    * Used for O(1) average time complexity user lookups (Customer/Freelancer retrieval).

* **Custom MaxHeap (`MaxHeap.java`):**
    * A binary heap implementation used as a Priority Queue.
    * Each `Service` maintains its own MaxHeap to keep freelancers sorted by their **Composite Score** in real-time.
    * Allows for efficient retrieval of the "Top K" freelancers for any given job request.

### Object-Oriented Design
* **Inheritance:** `Customer` and `Freelancer` classes extend a base `User` abstract class.
* **Command Pattern:** The `Main` class acts as a parser, processing a stream of string commands to execute platform logic.

## Project Structure

* `Main.java`: Entry point. Parses input files, processes commands (Register, Request, Employ, etc.), and manages the simulation loop.
* `HashTable.java`: Generic custom hash table implementation.
* `MaxHeap.java`: Priority queue implementation for ranking freelancers.
* `User.java`: Definitions for `User`, `Customer`, and `Freelancer` classes, including scoring logic.
* `Service.java`: Defines service types and holds the heap for that specific market sector.
* `FileComparator.java`: A utility tool to compare your output against expected test cases.

## Installation & Usage

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/winterwitchy/freelancer.git
    cd freelancer
    ```

2.  **Compile the source code:**
    ```bash
    javac *.java
    ```

3.  **Run the application:**
    The program takes an input file containing commands and an output path for the logs.
    ```bash
    java Main <input_file> <output_file>
    ```

### Example Input Command
```text
register_customer cust1
register_freelancer free1 paint 150 75 60 50 85 90
request_job cust1 paint 2
complete_and_rate free1 5
simulate_month
