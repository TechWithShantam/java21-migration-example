# Java 21 Migration Demo

This project demonstrates how to migrate a Java application from an older version (e.g., Java 17) to Java 21.

## Overview
- The codebase starts with Java 17-compatible features and is incrementally enhanced to use Java 21 features.
- The main example is a seat booking application, showcasing OOP, concurrency, and analytics.
- Migration steps include adopting new Java language features, updating build tools, and modernizing code patterns.

## Key Features
- **Seat Booking Service:** Thread-safe booking, cancellation, and group booking logic.
- **OOP Design:** Uses User, Booking, and Seat classes for clear separation of concerns.
- **Analytics:** AnalyticsService provides booking statistics and downloadable reports.
- **Concurrent Simulation:** Demonstrates safe concurrent seat booking.
- **CI Pipeline:** GitHub Actions workflow for Maven build and test.

## Migration Guidance
- Start with Java 17 code (no records, sealed types, or pattern matching in switch).
- Gradually refactor to use Java 21 features (records, sealed interfaces, pattern matching, virtual threads, etc.).
- Update `pom.xml` and CI to target Java 21.

## How to Use
1. Clone the repo and open in your IDE.
2. Build and run with Java 17 (initial state).
3. Follow migration prompts or scripts to refactor for Java 21.
4. Update your build and CI to use Java 21.

## How to Run the Java 21 Migration Prompts

To start the migration process using GitHub Copilot Chat:

1. Open this repository in VS Code with Copilot Chat enabled.
2. Open `.github/prompts/java21-migration.prompt.md`.
3. In Copilot Chat, type:

	```
	@workspace #file:java21-migration.prompt.md Begin migration. Start with Spec-01.
	```

4. Copilot will walk you through each migration spec, one at a time, and wait for your approval before advancing.

You can always type `STATUS` in Copilot Chat to see the current migration TODO list.

## Author & Connect

- Medium: https://medium.com/@techwithshantam
- LinkedIn: https://www.linkedin.com/in/shantam-sultania-737084175/

---

*Made with love by Shantam Sultania. Happy coding! Always open to collaboration and connecting on LinkedIn.*