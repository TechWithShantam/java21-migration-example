# test.prompt.md
# Java 21 Migration — Test Contract
# Called automatically by java21-migration.prompt.md after each spec completes.
# Each section is keyed to a SPEC-ID. Copilot reads the matching section,
# runs all checks listed, and reports pass / fail before asking user to advance.

---

## HOW THIS FILE IS USED

This file is NOT run manually. The migration orchestrator calls it like this:

```
After transforming all SPEC-XX candidates:
  → Read section [SPEC-XX] from this file
  → Execute every CHECK in that section
  → Run the build test command (mvn test OR ./gradlew test)
  → Report results in the REPORT step of the loop
```

If all checks pass AND all tests pass → spec is APPROVED.
If any check fails → attempt one auto-fix, re-run, then mark file BLOCKED.

---

## ═══════════════════════════════════════════════════
## [SPEC-11]  CI/CD Pipeline Updates — Test Contract
## ═══════════════════════════════════════════════════

### Pre-conditions
- All pipeline files listed in the SPEC-11 TODO have been updated.

### Structural Checks (Copilot performs these without running code)

- [ ] **CHECK-11-01**: No file in `.github/workflows/` contains the string
  `java-version: '17'` (or `"17"`).
- [ ] **CHECK-11-02**: No `Dockerfile` or `docker-compose` file references a
  Java 17 base image tag (e.g., `temurin:17`, `openjdk:17`, `amazoncorretto:17`).
- [ ] **CHECK-11-03**: `pom.xml` (if present) has `<java.version>21</java.version>`
  AND `<maven.compiler.release>21</maven.compiler.release>`.
- [ ] **CHECK-11-04**: `build.gradle` or `build.gradle.kts` (if present) has
  `JavaVersion.VERSION_21` and/or `jvmTarget = "21"`.
- [ ] **CHECK-11-05**: Multi-stage Dockerfiles have Java 21 in EVERY stage
  (builder and runtime).
- [ ] **CHECK-11-06**: `.sdkmanrc` (if present) references a Java 21 distribution.
- [ ] **CHECK-11-07**: No Jenkinsfile, Travis config, or Azure Pipelines file
  still references `jdk-17` or `openjdk17`.

### Build Verification
Run the following and assert exit code 0:
```bash
# Maven
mvn compile -q

# Gradle
./gradlew compileJava
```

### Expected outcome
Build succeeds with Java 21 compiler. No `source/target value 17` warnings.

---

## ═══════════════════════════════════════════════════
## [SPEC-01]  Records — Test Contract
## ═══════════════════════════════════════════════════

### Pre-conditions
- All candidate data-carrier classes have been converted to `record`.

### Structural Checks

- [ ] **CHECK-01-01**: Each converted class is now declared with the `record`
  keyword — grep for the original `class <Name>` and assert it no longer exists
  as a plain class.
- [ ] **CHECK-01-02**: No converted record retains explicit `equals`,
  `hashCode`, or `toString` methods (unless custom logic was present).
- [ ] **CHECK-01-03**: All call sites updated — no usages of the old getter
  form `getX()` remain for converted records (search for `get` + field name
  combinations).
- [ ] **CHECK-01-04**: If the original class implemented interfaces, the record
  declaration includes `implements <Interface>`.
- [ ] **CHECK-01-05**: Compact constructors are present wherever the original
  constructor had validation logic (assert, Objects.requireNonNull, etc.).
- [ ] **CHECK-01-06**: No JPA-annotated class (`@Entity`, `@Table`) was
  converted — verify none of the new records carry these annotations.

### Automated Test Cases (Copilot generates and runs these if no existing test covers them)

```java
// TC-01-A: record components are accessible via accessor methods
var money = new Money(new BigDecimal("9.99"), Currency.getInstance("USD"));
assert money.amount().equals(new BigDecimal("9.99"));
assert money.currency().equals(Currency.getInstance("USD"));

// TC-01-B: structural equality
var a = new Money(new BigDecimal("1.00"), Currency.getInstance("USD"));
var b = new Money(new BigDecimal("1.00"), Currency.getInstance("USD"));
assert a.equals(b);
assert a.hashCode() == b.hashCode();

// TC-01-C: toString contains field values
assert money.toString().contains("9.99");

// TC-01-D: compact constructor validation still fires
try {
    new Money(null, Currency.getInstance("USD"));
    assert false : "Expected NullPointerException";
} catch (NullPointerException | IllegalArgumentException e) {
    // expected
}
```

### Build + Test Gate
```bash
mvn test -pl . -Dtest="*RecordTest,*MoneyTest,*AddressTest" -q
# OR
./gradlew test --tests "*RecordTest" --tests "*MoneyTest"
```
All existing tests must pass. No new compilation errors.

---

## ═══════════════════════════════════════════════════
## [SPEC-02]  Sealed Classes — Test Contract
## ═══════════════════════════════════════════════════

### Structural Checks

- [ ] **CHECK-02-01**: Each sealed class/interface has a `permits` clause
  listing all known subclasses.
- [ ] **CHECK-02-02**: Every permitted subclass is marked `final`, `sealed`,
  or `non-sealed` — no plain non-final permitted subclass exists.
- [ ] **CHECK-02-03**: No subclass of a sealed type exists outside the
  permitted list (search test source too).
- [ ] **CHECK-02-04**: Files that were flagged `// SPEC-04-CANDIDATE` exist
  in the SPEC-04 TODO list.

### Compilation Check
```bash
mvn compile -q
# A missing `final`/`sealed`/`non-sealed` on a permitted subclass is a
# compile error — a clean compile proves the structure is valid.
```

### Automated Test Cases

```java
// TC-02-A: exhaustive switch compiles without default branch
// (compile-time proof of exhaustiveness — if this compiles, the seal is valid)
double area = switch (shape) {
    case Circle c    -> Math.PI * c.radius() * c.radius();
    case Rectangle r -> r.width() * r.height();
    // no default — compiler would reject if seal is broken
};

// TC-02-B: attempting to extend a sealed class outside permits fails at compile time
// (this is a negative test — Copilot should assert the class is final/sealed,
//  not attempt to subclass it at runtime)
assert Modifier.isFinal(Circle.class.getModifiers())
    || Circle.class.isSealed();
```

### Build + Test Gate
```bash
mvn test -q
./gradlew test
```

---

## ═══════════════════════════════════════════════════
## [SPEC-09]  Deprecation & Removal Cleanup — Test Contract
## ═══════════════════════════════════════════════════

### Structural Checks

- [ ] **CHECK-09-01**: No call to `Thread.stop()`, `Thread.suspend()`, or
  `Thread.resume()` exists in `src/main/java`.
- [ ] **CHECK-09-02**: No import or reference to `SecurityManager`,
  `System.setSecurityManager`, or `System.getSecurityManager`.
- [ ] **CHECK-09-03**: No `protected void finalize()` method exists in
  `src/main/java`.
- [ ] **CHECK-09-04**: No `import sun.*` or `import com.sun.*` statements
  exist WITHOUT a corresponding `// TODO(java21-migration)` comment on the
  same or preceding line.
- [ ] **CHECK-09-05**: `--illegal-access` does not appear in any startup
  script, Makefile, or Dockerfile `ENTRYPOINT` / `CMD`.
- [ ] **CHECK-09-06**: `new Date()`, `Calendar.getInstance()`, and
  `new SimpleDateFormat()` no longer appear in main source (only in legacy
  code covered by TODO comments).

### Regression Test Gate
```bash
mvn test -q
# All pre-existing tests must pass — this spec only removes deprecated calls,
# so no test should start failing unless it depended on removed behaviour.
```

---

## ═══════════════════════════════════════════════════
## [SPEC-03]  Text Blocks — Test Contract
## ═══════════════════════════════════════════════════

### Structural Checks

- [ ] **CHECK-03-01**: No multi-line string concatenation (3+ lines joined
  with `+`) remains in files listed in the SPEC-03 TODO.
- [ ] **CHECK-03-02**: No `\n` or `\t` escape sequences appear inside strings
  that are clearly multi-line content (SQL, JSON, etc.).
- [ ] **CHECK-03-03**: Every text block's closing `"""` is on its own line
  (correct indentation stripping).
- [ ] **CHECK-03-04**: No single-line string or string under 60 characters was
  converted to a text block.

### Automated Test Cases

```java
// TC-03-A: text block content equals original string value
String expected = "SELECT u.id, u.name\nFROM users u\nWHERE u.active = true\nORDER BY u.name\n";
String actual = """
    SELECT u.id, u.name
    FROM users u
    WHERE u.active = true
    ORDER BY u.name
    """;
assert expected.equals(actual) : "Text block content mismatch";

// TC-03-B: .formatted() injection works correctly
String name = "Alice";
String msg = """
    Hello %s, welcome!
    """.formatted(name);
assert msg.contains("Alice");
```

### Build + Test Gate
```bash
mvn test -q
./gradlew test
```

---

## ═══════════════════════════════════════════════════
## [SPEC-05]  Pattern Matching instanceof — Test Contract
## ═══════════════════════════════════════════════════

### Structural Checks

- [ ] **CHECK-05-01**: No `instanceof` check immediately followed by an
  explicit cast to the same type exists in converted files.
- [ ] **CHECK-05-02**: Negated patterns (`!(obj instanceof X x)`) are used
  where the original code negated the `instanceof`.
- [ ] **CHECK-05-03**: No binding variable is used outside the scope of the
  `if` block it was declared in.

### Automated Test Cases

```java
// TC-05-A: pattern binding works
Object obj = "hello";
if (obj instanceof String s) {
    assert s.length() == 5;
} else {
    assert false : "Should have matched String";
}

// TC-05-B: negated pattern — binding available after guard
Object num = 42;
if (!(num instanceof Integer i)) {
    assert false : "Should have been Integer";
}
// i is NOT in scope here — test that the code compiles and runs correctly

// TC-05-C: non-matching type
Object other = 3.14;
if (other instanceof String s) {
    assert false : "Double should not match String";
}
```

### Build + Test Gate
```bash
mvn test -q
```

---

## ═══════════════════════════════════════════════════
## [SPEC-04]  Pattern Matching switch — Test Contract
## ═══════════════════════════════════════════════════

### Structural Checks

- [ ] **CHECK-04-01**: No `if / else if` chain using `instanceof` + explicit
  cast remains in converted files.
- [ ] **CHECK-04-02**: Exhaustive sealed hierarchies have NO `default` branch
  in the converted switch.
- [ ] **CHECK-04-03**: Non-exhaustive hierarchies retain a `default` branch.
- [ ] **CHECK-04-04**: Guarded patterns (`case X x when x.field() > 0`) are
  used where the original had nested `if` conditions inside an `instanceof`
  block.
- [ ] **CHECK-04-05**: `case null` branch exists wherever the original code
  had a preceding `!= null` guard.

### Automated Test Cases

```java
// TC-04-A: correct dispatch
Shape circle = new Circle(5.0);
double area = switch (circle) {
    case Circle c    -> Math.PI * c.radius() * c.radius();
    case Rectangle r -> r.width() * r.height();
};
assert Math.abs(area - Math.PI * 25) < 0.001;

// TC-04-B: null handling
Shape nullShape = null;
String result = switch (nullShape) {
    case null        -> "null shape";
    case Circle c    -> "circle";
    case Rectangle r -> "rectangle";
};
assert result.equals("null shape");

// TC-04-C: guarded pattern
Shape smallCircle = new Circle(0.5);
String size = switch (smallCircle) {
    case Circle c when c.radius() >= 1.0 -> "large";
    case Circle c                         -> "small";
    default                               -> "other";
};
assert size.equals("small");
```

### Build + Test Gate
```bash
mvn test -q
./gradlew test
```

---

## ═══════════════════════════════════════════════════
## [SPEC-08]  switch Expressions — Test Contract
## ═══════════════════════════════════════════════════

### Structural Checks

- [ ] **CHECK-08-01**: No `switch` statement that exists solely to assign a
  single variable remains in converted files — it must now be a switch
  expression on the right-hand side of an assignment.
- [ ] **CHECK-08-02**: No `break` statements remain inside converted arrow
  switch arms.
- [ ] **CHECK-08-03**: Fall-through cases use comma-separated labels
  (`case A, B -> ...`), not two consecutive `case` statements with no body.

### Automated Test Cases

```java
// TC-08-A: switch expression returns correct value
enum Status { ACTIVE, INACTIVE, PENDING }
Status status = Status.ACTIVE;
String label = switch (status) {
    case ACTIVE   -> "Active";
    case INACTIVE -> "Inactive";
    default       -> "Unknown";
};
assert label.equals("Active");

// TC-08-B: comma-separated case labels
Status s = Status.INACTIVE;
String group = switch (s) {
    case ACTIVE, PENDING -> "live";
    case INACTIVE        -> "archived";
};
assert group.equals("archived");
```

### Build + Test Gate
```bash
mvn test -q
```

---

## ═══════════════════════════════════════════════════
## [SPEC-06]  Virtual Threads — Test Contract
## ═══════════════════════════════════════════════════

### Structural Checks

- [ ] **CHECK-06-01**: No `Executors.newFixedThreadPool` or
  `newCachedThreadPool` call wrapping I/O-bound tasks remains without a
  `// TODO(java21-migration)` comment justifying the exception.
- [ ] **CHECK-06-02**: No bare `new Thread(runnable).start()` exists for
  one-off background tasks (replaced with `Thread.ofVirtual().start()`).
- [ ] **CHECK-06-03**: Every `synchronized` block wrapping I/O inside a
  submitted task has a pinning warning comment.
- [ ] **CHECK-06-04**: Spring Boot `application.properties` contains
  `spring.threads.virtual.enabled=true` (Spring Boot projects only).
- [ ] **CHECK-06-05**: CPU-bound thread pools were NOT converted (verify by
  checking TODO comments left in place for those).

### Automated Test Cases

```java
// TC-06-A: virtual thread is indeed virtual
Thread vt = Thread.ofVirtual().start(() -> {});
assert vt.isVirtual() : "Thread must be virtual";

// TC-06-B: executor produces virtual threads
try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
    Future<?> f = exec.submit(() -> {
        assert Thread.currentThread().isVirtual();
    });
    f.get();
}

// TC-06-C: basic I/O-bound task completes without deadlock
// (run a simple blocking task and assert it completes within 2 seconds)
try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
    Future<String> f = exec.submit(() -> {
        Thread.sleep(10);
        return "done";
    });
    assert f.get(2, TimeUnit.SECONDS).equals("done");
}
```

### Build + Test Gate
```bash
mvn test -q
# Additionally, if a load test profile exists:
mvn verify -Pload-test -q
```

---

## ═══════════════════════════════════════════════════
## [SPEC-07]  Sequenced Collections — Test Contract
## ═══════════════════════════════════════════════════

### Structural Checks

- [ ] **CHECK-07-01**: No `list.get(0)` call remains where `getFirst()` was
  applicable (i.e., on `ArrayList` or `LinkedList`).
- [ ] **CHECK-07-02**: No `list.get(list.size() - 1)` pattern remains.
- [ ] **CHECK-07-03**: No `new ArrayList<>(set).get(0)` hack exists for
  `LinkedHashSet`.
- [ ] **CHECK-07-04**: `HashSet` and `HashMap` accesses were NOT converted
  (unordered — verify no `getFirst()` call on these types).

### Automated Test Cases

```java
// TC-07-A: getFirst / getLast on ArrayList
var list = new ArrayList<>(List.of("a", "b", "c"));
assert list.getFirst().equals("a");
assert list.getLast().equals("c");

// TC-07-B: getFirst on LinkedHashSet (sequenced)
var set = new LinkedHashSet<>(List.of("x", "y", "z"));
assert set.getFirst().equals("x");
assert set.getLast().equals("z");

// TC-07-C: reversed view
var reversed = list.reversed();
assert reversed.getFirst().equals("c");

// TC-07-D: addFirst / addLast
list.addFirst("zero");
assert list.getFirst().equals("zero");
list.addLast("end");
assert list.getLast().equals("end");
```

### Build + Test Gate
```bash
mvn test -q
```

---

## ═══════════════════════════════════════════════════
## [SPEC-10]  String Template Candidates — Test Contract
## ═══════════════════════════════════════════════════

### Structural Checks (annotation pass — no code changes)

- [ ] **CHECK-10-01**: Every `String.format(...)` call with 2+ arguments has
  a `// JAVA21-TEMPLATE-CANDIDATE:` comment on the preceding line.
- [ ] **CHECK-10-02**: No SLF4J / Log4j log call was annotated.
- [ ] **CHECK-10-03**: No existing code was modified — only comments were added.

### Build + Test Gate
```bash
# Compile only — no logic changed, so tests are implicitly passing
mvn compile -q
./gradlew compileJava
```

---

## ═══════════════════════════════════════════════════
## GLOBAL TEST RULES
## ═══════════════════════════════════════════════════

These apply after EVERY spec:

1. **Full test suite must pass**: run `mvn test` or `./gradlew test`. Zero
   regressions are acceptable.
2. **No new compiler warnings**: run with `-Xlint:all` if possible and assert
   no new warnings were introduced by the migration.
3. **No orphaned imports**: converted files must not retain unused imports from
   removed boilerplate (e.g., Lombok imports after record conversion).
4. **Checkstyle / PMD / SpotBugs**: if these tools are configured, run them
   and assert no new violations.

```bash
# Maven with static analysis
mvn verify -Pstatic-analysis -q

# Gradle
./gradlew check
```

5. **TODO audit**: after each spec, count and report the number of
   `// TODO(java21-migration)` comments introduced. These are not failures
   but must be visible in the REPORT step.