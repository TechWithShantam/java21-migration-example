# Copilot Instructions for AI Agents

## Project Overview
- This is a Java project using Maven, with source code in `src/main/java/com/example/test/` and tests in `src/test/java/`.
- The main entry point is likely `Main.java` in the `com.example.test` package.
- Build artifacts are output to the `target/` directory.

## Architecture & Structure
- Follows standard Maven project layout: `src/main/java` for source, `src/test/java` for tests, `src/main/resources` for resources.
- All Java code is under the `com.example.test` package.
- No evidence of custom modules or non-standard structure.

## Developer Workflows
- **Build:** Use `mvn clean install` to build the project and run all tests.
- **Run:** Main class is likely `com.example.test.Main`. Run with Maven: `mvn exec:java -Dexec.mainClass="com.example.test.Main"` (ensure `exec-maven-plugin` is configured in `pom.xml`).
- **Test:** Run all tests with `mvn test`.
- **Debug:** Use your IDE's Java debugging tools. For command-line debugging, use `mvnDebug` or configure remote debugging in your IDE.

## Conventions & Patterns
- Standard Java and Maven conventions are followed.
- No custom code generation, annotation processing, or non-standard build steps detected.
- No project-specific naming or directory conventions beyond Maven defaults.

## Integration & Dependencies
- All dependencies are managed via `pom.xml`.
- No evidence of external service integration or custom build scripts.

## Key Files & Directories
- `pom.xml`: Maven build configuration and dependencies.
- `src/main/java/com/example/test/Main.java`: Likely the main entry point.
- `src/test/java/`: Place for unit and integration tests.
- `target/`: Build output (do not edit files here).

## Example Commands
- Build: `mvn clean install`
- Run: `mvn exec:java -Dexec.mainClass="com.example.test.Main"`
- Test: `mvn test`

## Guidance for AI Agents
- Adhere to Maven and Java best practices unless project-specific instructions are found in `pom.xml` or source files.
- When adding new code, follow the existing package structure and naming conventions.
- Reference `pom.xml` for dependency management and plugin configuration.
- If unsure about entry points or build steps, inspect `pom.xml` for plugins and configuration.
