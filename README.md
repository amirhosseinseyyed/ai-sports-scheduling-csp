# AI Sports Scheduling using CSP

Artificial Intelligence course project implemented in Java.

## Description

This project solves the weekly sports scheduling problem as a **Constraint Satisfaction Problem (CSP)**.

The scheduler assigns matches to available days, time slots, and stadiums while satisfying all scheduling constraints.

## Features

The solver implements:

- Backtracking Search
- Forward Checking
- Minimum Remaining Values (MRV) heuristic
- Least Constraining Value (LCV) heuristic

## Constraints

The scheduler enforces the following constraints:

- A stadium can host only one match in the same time slot.
- A team cannot play more than one match on the same day.
- At most one important (sensitive) match can be scheduled per day.

## Language

- Java

## Project Structure

- `Test.java` – Reads the input, validates it, and prints the final schedule.
- `Scheduler.java` – Implements the CSP solver, Backtracking, Forward Checking, MRV, and LCV heuristics.
- `Match.java` – Represents a match and its domain of possible assignments.
- `Slot.java` – Represents a scheduling slot (day, hour, stadium).

## Output

The program prints:

- The generated schedule
- Number of backtracks
- Execution time
