# java21-migration.prompt.md
# Java 17 → Java 21 — Spec-Driven Migration Orchestrator
# For GitHub Copilot Agent / Copilot Chat (@workspace)

---

## ► HOW TO START THIS MIGRATION

In GitHub Copilot Chat, type exactly:

```
@workspace #file:java21-migration.prompt.md Begin migration. Start with Spec-01.
```

Copilot will then execute the full loop described below — one spec at a time,
never advancing without your explicit approval.

---

## ═══════════════════════════════════════════════════
## PHASE 0 — BOOTSTRAP (run once before any spec)
## ═══════════════════════════════════════════════════

Before touching a single line of code, Copilot MUST complete all steps below.

### 0-A  Read project conventions
1. Open and fully read `.github/copilot-instructions.md`.
2. Extract and memorise: naming conventions, package structure rules, forbidden
   libraries, required annotations, formatting style, and any Java version pins.
3. These rules OVERRIDE anything in this file. Never violate them.

### 0-B  Read the test contract
1. Open and fully read `.github/test.prompt.md`.
2. This file contains the acceptance criteria and test cases for every spec.
3. After completing each spec's code changes you will invoke the relevant
   section of `test.prompt.md` as the verification gate.

### 0-C  Detect build system
Identify which build system is in use and record it for use in all commands:

| File present          | Build system    | Test command      | Build command    |
|-----------------------|-----------------|-------------------|------------------|
| `pom.xml`             | Maven           | `mvn test`        | `mvn verify`     |
| `build.gradle`        | Gradle (Groovy) | `./gradlew test`  | `./gradlew build`|
| `build.gradle.kts`    | Gradle (Kotlin) | `./gradlew test`  | `./gradlew build`|

### 0-D  Generate the master TODO list
Scan the entire `src/` directory. For each spec below, produce a structured
TODO list in the following format and display it to the user before proceeding:

```
╔══════════════════════════════════════════════════════════════╗
║          JAVA 21 MIGRATION — MASTER TODO LIST                ║
╠══════════════════════════════════════════════════════════════╣
║ SPEC-11 │ CI/CD Pipeline Updates                             ║
║   [ ] .github/workflows/build.yml                            ║
║   [ ] Dockerfile                                             ║
╠══════════════════════════════════════════════════════════════╣
║ SPEC-01 │ Records                                            ║
║   [ ] src/main/java/com/example/Money.java                   ║
║   [ ] src/main/java/com/example/Address.java                 ║
╠══════════════════════════════════════════════════════════════╣
║  ... (one row per candidate file per spec) ...               ║
╚══════════════════════════════════════════════════════════════╝
```

Save this list internally. Update a checkbox to [x] as each file is completed.
Print the updated list at the end of every spec cycle.

### 0-E  Confirm before starting
Display this and WAIT for user input:
```
──────────────────────────────────────────────────────────────
  Master TODO list generated above.
  Ready to begin. Execution order is dependency-aware (see end
  of this file). Spec-11 (CI/CD) runs first.

  Type  GO      → start from Spec-11
  Type  SKIP    → skip a specific spec (e.g. SKIP SPEC-06)
  Type  STATUS  → reprint the full TODO list at any time
──────────────────────────────────────────────────────────────
```

---

## ═══════════════════════════════════════════════════
## THE SPEC EXECUTION LOOP
## ═══════════════════════════════════════════════════

For EVERY spec, repeat these exact phases in order:

```
┌──────────────────────────────────────────────────────────────┐
│  For each SPEC (in execution order):                         │
│                                                              │
│  1. ANNOUNCE   — print spec header & candidate file list     │
│  2. APPLY      — transform files one by one                  │
│  3. TEST GATE  — run test.prompt.md section for this spec    │
│  4. REPORT     — print pass / fail / blocked summary         │
│  5. CONFIRM    — wait for user command before advancing      │
└──────────────────────────────────────────────────────────────┘
```

### Step 1 — ANNOUNCE
```
╔══════════════════════════════════════════════════╗
║  SPEC-XX │ <Name>                                ║
║  Candidates found: <N> files                     ║
║  Reading test contract from test.prompt.md...    ║
╚══════════════════════════════════════════════════╝
```
If zero candidates exist, print "No candidates — skipping." and jump to Step 5.

### Step 2 — APPLY
For each candidate file:
- Apply the transformation defined in this spec's section.
- Follow ALL global migration rules.
- Add `// migrated: SPEC-XX` to the top of each changed class or method block.
- If a file cannot be safely changed, add
  `// TODO(java21-migration): <reason>` and leave original code intact.
- Print after each file: `  ✓ transformed: <filepath>`

### Step 3 — TEST GATE
After ALL files for this spec are transformed:
1. Print: `Running acceptance tests via test.prompt.md [SPEC-XX]...`
2. Execute the `[SPEC-XX]` section from `.github/test.prompt.md`.
3. Run the build test command detected in Phase 0-C.
4. On test failure:
   - Print failing test name and error.
   - Attempt ONE automatic fix.
   - Re-run tests.
   - If still failing: mark file as `⚠ BLOCKED` in TODO list, revert that
     file's change, and continue with remaining files.

### Step 4 — REPORT
```
──────────────────────────────────────────────────────
  SPEC-XX COMPLETE
  ✓ Transformed : N files
  ✓ Tests passed: all
  ⚠ Blocked     : N files (see TODO list for details)

  Updated TODO list:
  [x] File1.java   [x] File2.java   [⚠] File3.java (blocked)
──────────────────────────────────────────────────────
```

### Step 5 — CONFIRM
Display and WAIT — do NOT advance until the user types a command:

```
══════════════════════════════════════════════════════
  Spec-XX done. What would you like to do?

    NEXT    → proceed to the next spec
    RETRY   → re-attempt blocked files only
    SKIP    → skip the next spec
    ABORT   → stop migration and print final report
    STATUS  → reprint full TODO list
══════════════════════════════════════════════════════
```

---

## ═══════════════════════════════════════════════════
## GLOBAL MIGRATION RULES  (apply to every spec)
## ═══════════════════════════════════════════════════

- **Target runtime**: Java 21 LTS (`--release 21`).
- **Scope**: `src/main/java` and `src/test/java` unless the spec says otherwise.
- **Granularity**: one file at a time — never bulk-rewrite.
- **Traceability**: `// migrated: SPEC-XX` at the top of every changed class
  or method block.
- **Safety net**: if a transformation cannot be proven safe, add
  `// TODO(java21-migration): <reason>` and leave original code intact.
- **Logic**: NEVER alter business logic — modernise the Java construct only.
- **Docs**: preserve all Javadoc, annotations, and copyright headers.
- **Commit suggestion**: after each approved spec, suggest:
  `git commit -m "refactor: Java 21 migration [SPEC-XX] — <description>"`

---

## ═══════════════════════════════════════════════════
## SPEC DEFINITIONS
## ═══════════════════════════════════════════════════

---

### SPEC-11 — CI/CD Pipeline Java Version Updates

**Purpose**: Update all pipeline and infrastructure files to Java 21 so that
every subsequent spec is built and tested on the correct runtime from the start.
This spec runs FIRST.

**Trigger** — scan the entire repository for:

| File pattern                  | What to update                                      |
|-------------------------------|-----------------------------------------------------|
| `.github/workflows/*.yml`     | `java-version: '17'` → `'21'`                      |
| `Dockerfile` / `Dockerfile.*` | `FROM eclipse-temurin:17*` → `eclipse-temurin:21-jre-alpine` |
| `docker-compose*.yml`         | `amazoncorretto:17` → `amazoncorretto:21`           |
| `Jenkinsfile`                 | `tool 'jdk-17'` → `'jdk-21'`                       |
| `.travis.yml`                 | `jdk: openjdk17` → `openjdk21`                     |
| `azure-pipelines.yml`         | `jdkVersion: '1.17'` → `'21'`                      |
| `pom.xml`                     | `<java.version>17</java.version>` → `21`; add `<maven.compiler.release>21` |
| `build.gradle` / `.kts`       | `sourceCompatibility = '17'` → `'21'`; `jvmTarget = "17"` → `"21"` |
| `.sdkmanrc`                   | `java=17.*` → latest Temurin 21 build               |
| `Makefile`                    | `JAVA_VERSION=17` → `21`                           |
| `helm/*/values.yaml`          | `javaVersion: 17` or image tag with `17`           |
| `kubernetes/*.yaml`           | Container image tags with Java 17 base images      |
| `bitbucket-pipelines.yml`     | `maven:3.*-openjdk-17` → `maven:3.*-openjdk-21`   |

**Transformation examples**:

```yaml
# .github/workflows/build.yml
# BEFORE
- uses: actions/setup-java@v4
  with:
    java-version: '17'
    distribution: 'temurin'

# AFTER  (migrated: SPEC-11)
- uses: actions/setup-java@v4
  with:
    java-version: '21'
    distribution: 'temurin'
```

```dockerfile
# Dockerfile — multi-stage example
# BEFORE
FROM eclipse-temurin:17-jdk-alpine AS builder
FROM eclipse-temurin:17-jre-alpine

# AFTER  (migrated: SPEC-11)
FROM eclipse-temurin:21-jdk-alpine AS builder
FROM eclipse-temurin:21-jre-alpine
```

```xml
<!-- pom.xml — BEFORE -->
<properties>
    <java.version>17</java.version>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
</properties>

<!-- AFTER  (migrated: SPEC-11) -->
<properties>
    <java.version>21</java.version>
    <maven.compiler.source>21</maven.compiler.source>
    <maven.compiler.target>21</maven.compiler.target>
    <maven.compiler.release>21</maven.compiler.release>
</properties>
```

```kotlin
// build.gradle.kts — BEFORE
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "17" }

// AFTER  (migrated: SPEC-11)
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "21" }
```

**Additional rules**:
- Verify `actions/setup-java` version is v3+ (v4 recommended) for Java 21 support.
- For multi-stage Dockerfiles update ALL stages (builder AND runtime).
- Do NOT change JVM flags (heap size, GC settings) — out of scope.

---

### SPEC-01 — Records

**Purpose**: Replace immutable data-carrier classes with `record`.

**Trigger** — all of the following must be true:
- All fields are `private final`.
- Has a canonical all-args constructor.
- Has only getters (no setters).
- `equals`, `hashCode`, `toString` delegate entirely to fields (or are
  auto-generated by Lombok / IDE).
- NOT annotated with `@Entity`, `@Table`, `@MappedSuperclass`, or any
  JPA/Hibernate annotation.
- NOT a Spring `@Component`, `@Service`, `@Repository`, or `@Controller`.

**Transformation**:
```java
// BEFORE
public final class Money {
    private final BigDecimal amount;
    private final Currency currency;
    public Money(BigDecimal amount, Currency currency) { ... }
    public BigDecimal getAmount() { return amount; }
    public Currency getCurrency() { return currency; }
    @Override public boolean equals(Object o) { ... }
    @Override public int hashCode() { ... }
    @Override public String toString() { ... }
}

// AFTER
// migrated: SPEC-01
public record Money(BigDecimal amount, Currency currency) {}
```

**Additional rules**:
- Update ALL call sites: `obj.getAmount()` → `obj.amount()`.
- Preserve implemented interfaces: `record Money(...) implements Serializable`.
- Move constructor validation into a compact constructor.
- Lombok `@Value` classes are also eligible — remove Lombok after conversion.

---

### SPEC-02 — Sealed Classes

**Purpose**: Restrict inheritance to a known, closed set of subtypes.

**Trigger**:
- An `abstract class` or `interface` has subclasses confined to the same
  package or module.
- Subclass count is 2–12 (assess larger sets carefully).
- No external subclassing is intended.

**Transformation**:
```java
// BEFORE
public abstract class Shape { ... }
public class Circle extends Shape { ... }
public class Rectangle extends Shape { ... }

// AFTER
// migrated: SPEC-02
public sealed class Shape permits Circle, Rectangle { ... }
public final class Circle extends Shape { ... }
public final class Rectangle extends Shape { ... }
```

**Additional rules**:
- Every permitted subclass must be `final`, `sealed`, or `non-sealed`.
- Verify no subclass exists in test source that breaks the seal.
- Flag `instanceof` chains on sealed types for SPEC-04:
  `// SPEC-04-CANDIDATE: convert instanceof chain to switch`

---

### SPEC-09 — Deprecation & Removal Cleanup

**Purpose**: Remove or replace APIs that are removed/deprecated-for-removal
in Java 17–21. Run this early so later specs work on clean code.

**Checklist**:

| API / Pattern | Action |
|---|---|
| `Thread.stop()` | Replace with `Thread.interrupt()` + cooperative loop |
| `Thread.suspend()` / `resume()` | Replace with `wait()`/`notify()` or `LockSupport` |
| `SecurityManager` / `System.setSecurityManager()` | Remove entirely |
| `Applet` / `AppletContext` | Remove; file a migration ticket |
| `protected void finalize()` | Replace with `Cleaner` or try-with-resources |
| `sun.*` / `com.sun.*` internal imports | Add TODO comment; do not remove yet |
| `--illegal-access` in JVM args / scripts | Remove the flag |
| `Runtime.exec(String)` single-arg form | Replace with `ProcessBuilder` |
| `Date`, `Calendar`, `SimpleDateFormat` | Replace with `java.time.*` equivalents |

**Transformation example**:
```java
// BEFORE
protected void finalize() throws Throwable { resource.close(); }

// AFTER
// migrated: SPEC-09
private final Cleaner.Cleanable cleanable =
    Cleaner.create().register(this, () -> resource.close());
```

---

### SPEC-03 — Text Blocks

**Purpose**: Replace multi-line string concatenation with `"""` text blocks.

**Trigger** — a String that:
- Spans more than 2 lines via `+` concatenation, OR
- Contains `\n`, `\t`, or `\"` escape sequences for formatting, OR
- Holds SQL, JSON, XML, HTML, GraphQL, or template content.

**Transformation**:
```java
// BEFORE
String sql = "SELECT u.id, u.name\n" +
             "FROM users u\n" +
             "WHERE u.active = true\n" +
             "ORDER BY u.name";

// AFTER
// migrated: SPEC-03
String sql = """
    SELECT u.id, u.name
    FROM users u
    WHERE u.active = true
    ORDER BY u.name
    """;
```

**Additional rules**:
- Closing `"""` controls indentation stripping — align to desired baseline.
- Replace `+` variable injection with `.formatted()`:
  `""" Hello %s """.formatted(name)`.
- Do NOT convert strings shorter than 60 characters or single-line strings.
- Do NOT convert strings built dynamically in loops.

---

### SPEC-05 — Pattern Matching `instanceof`

**Purpose**: Eliminate redundant casts following an `instanceof` check.

**Trigger**: any `instanceof` check immediately followed by a cast to the same type.

**Transformation**:
```java
// BEFORE
if (obj instanceof String) {
    String s = (String) obj;
    return s.toUpperCase();
}

// AFTER
// migrated: SPEC-05
if (obj instanceof String s) {
    return s.toUpperCase();
}
```

**Additional rules**:
- Negated form: `if (!(obj instanceof String s)) { ... }` — `s` is available
  after the guard.
- Do NOT apply if the cast type differs from the `instanceof` type.
- Do NOT apply if the cast variable is reused outside the `if` scope.

---

### SPEC-04 — Pattern Matching for `switch`

**Purpose**: Replace `instanceof`/cast chains and type-discriminating switches
with pattern matching switch expressions.

**Trigger**:
- `if / else if` chains using `instanceof` followed by an explicit cast.
- `switch` on a type token (enum used as a type discriminator).
- Visitor pattern `visit()` dispatch methods.

**Transformation**:
```java
// BEFORE
if (shape instanceof Circle) {
    Circle c = (Circle) shape;
    return Math.PI * c.radius() * c.radius();
} else if (shape instanceof Rectangle) {
    Rectangle r = (Rectangle) shape;
    return r.width() * r.height();
} else {
    throw new IllegalArgumentException("Unknown: " + shape);
}

// AFTER
// migrated: SPEC-04
return switch (shape) {
    case Circle c    -> Math.PI * c.radius() * c.radius();
    case Rectangle r -> r.width() * r.height();
    default          -> throw new IllegalArgumentException("Unknown: " + shape);
};
```

**Additional rules**:
- If the hierarchy is sealed and exhaustive, REMOVE the `default` branch.
- Nested conditions become guarded patterns:
  `case Circle c when c.radius() > 0 -> ...`
- Add `case null -> ...` if the original checked `!= null` first.

---

### SPEC-08 — `switch` Expressions

**Purpose**: Replace assignment-only `switch` statements with switch expressions.

**Trigger**: a `switch` statement whose sole purpose is to assign one variable.

**Transformation**:
```java
// BEFORE
String label;
switch (status) {
    case ACTIVE:   label = "Active";   break;
    case INACTIVE: label = "Inactive"; break;
    default:       label = "Unknown";
}

// AFTER
// migrated: SPEC-08
String label = switch (status) {
    case ACTIVE   -> "Active";
    case INACTIVE -> "Inactive";
    default       -> "Unknown";
};
```

**Additional rules**:
- Remove `break` (arrow syntax is implicit).
- Fall-through cases use comma-separated labels: `case A, B -> ...`.
- Side-effect-only switches: convert to arrow syntax but leave as statement.

---

### SPEC-06 — Virtual Threads

**Purpose**: Replace platform-thread pools used for I/O-bound work.

**Trigger**:
- `Executors.newFixedThreadPool(N)` / `newCachedThreadPool()` wrapping I/O.
- `new Thread(runnable).start()` for one-off background tasks.
- `CompletableFuture` chains calling blocking APIs on the common pool.

**Transformation**:
```java
// BEFORE
ExecutorService pool = Executors.newFixedThreadPool(200);
new Thread(task).start();

// AFTER
// migrated: SPEC-06
ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor();
Thread.ofVirtual().start(task);
```

**Additional rules**:
- Do NOT apply to CPU-bound work (encryption, image processing, ML).
- If pool size was chosen for back-pressure, do NOT migrate; add:
  `// TODO(java21-migration): use Semaphore for back-pressure with virtual threads`
- **Spring Boot**: prefer `spring.threads.virtual.enabled=true` in
  `application.properties` over manual executor replacement.
- Warn when `synchronized` blocks wrap I/O inside submitted tasks:
  `// TODO(java21-migration): synchronized may pin virtual thread — consider ReentrantLock`

---

### SPEC-07 — Sequenced Collections

**Purpose**: Replace first/last element workarounds with `SequencedCollection` API.

**Trigger**: accessing first or last elements via index hacks or iterator tricks
on ordered collections.

**Transformation**:
```java
// BEFORE
list.get(0)                       // first
list.get(list.size() - 1)         // last
new ArrayList<>(set).get(0)       // set first (hack)

// AFTER  (migrated: SPEC-07)
list.getFirst()
list.getLast()
set.getFirst()    // LinkedHashSet only
```

**Additional rules**:
- Only apply to `ArrayList`, `LinkedList`, `LinkedHashSet`, `LinkedHashMap`.
- Do NOT apply to `HashSet` or `HashMap` (unordered).
- Also replace `addFirst()`, `addLast()`, `removeFirst()`, `removeLast()`.
- `reversed()` view replaces manual reversal loops.

---

### SPEC-10 — String Template Candidates (annotation pass only)

**Purpose**: Mark `String.format` / `MessageFormat` calls as future candidates
for String Templates (JEP 459 — stabilising post-21). No code changes.

**Action**: Add one comment line above each qualifying call:
```java
// JAVA21-TEMPLATE-CANDIDATE: replace with STR."..." when JEP-459 is stable
String msg = String.format("Hello %s, you have %d messages", name, count);
```

**Trigger**: `String.format(...)` / `MessageFormat.format(...)` with 2+ arguments.
**Do NOT annotate**: SLF4J / Log4j parameterised log calls.

---

## ═══════════════════════════════════════════════════
## SPEC EXECUTION ORDER  (dependency-aware)
## ═══════════════════════════════════════════════════

Always execute in this order:

```
 1. SPEC-11  CI/CD Pipeline Updates     ← Java 21 in pipelines first
 2. SPEC-01  Records
 3. SPEC-02  Sealed Classes             ← must precede SPEC-04
 4. SPEC-09  Deprecation Cleanup        ← clean slate before new patterns
 5. SPEC-03  Text Blocks
 6. SPEC-05  Pattern Matching instanceof
 7. SPEC-04  Pattern Matching switch    ← benefits from sealed types (SPEC-02)
 8. SPEC-08  Switch Expressions
 9. SPEC-06  Virtual Threads
10. SPEC-07  Sequenced Collections
11. SPEC-10  Template Candidates        ← annotation only, always last
```

---

## ═══════════════════════════════════════════════════
## PHASE 4 — FINAL REPORT
## ═══════════════════════════════════════════════════

After all specs complete (or user types ABORT):

```
╔══════════════════════════════════════════════════════════════╗
║            JAVA 21 MIGRATION — FINAL REPORT                  ║
╠══════════════════════════════════════════════════════════════╣
║  SPEC-11  CI/CD Updates            ✓ N files                 ║
║  SPEC-01  Records                  ✓ N files  ⚠ N blocked    ║
║  SPEC-02  Sealed Classes           ✓ N files                 ║
║  SPEC-09  Deprecation Cleanup      ✓ N files  ⚠ N TODO       ║
║  SPEC-03  Text Blocks              ✓ N files                 ║
║  SPEC-05  Pattern Matching inst.   ✓ N files                 ║
║  SPEC-04  Pattern Matching switch  ✓ N files                 ║
║  SPEC-08  Switch Expressions       ✓ N files                 ║
║  SPEC-06  Virtual Threads          ✓ N files  ⚠ N TODO       ║
║  SPEC-07  Sequenced Collections    ✓ N files                 ║
║  SPEC-10  Template Candidates      ✓ N files (annotated)     ║
╠══════════════════════════════════════════════════════════════╣
║  All tests passing     : YES / NO                            ║
║  Items needing review  : N  (search TODO(java21-migration))  ║
╠══════════════════════════════════════════════════════════════╣
║  SUGGESTED NEXT STEPS                                        ║
║  1. grep -r "TODO(java21-migration)" src/                    ║
║  2. Run full integration test suite                          ║
║  3. Open PRs for blocked files                               ║
║  4. Review virtual thread pinning warnings (synchronized)    ║
╚══════════════════════════════════════════════════════════════╝

Suggested commit:
  git commit -m "refactor: complete Java 17 → 21 migration [all specs]"
```
