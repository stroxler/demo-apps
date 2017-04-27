# spring-jdbc-simple

This is a simple Spring app showing how to set up JdbcTemplate support for
SQL queries. Along the way I also demonstrate using AspectJ to log around
function calls, since that kind of logging is a useful thing to have whenever
exploring unfamiliar tools in languages that support it.

## Why use Spring Jdbc?

The Spring `JdbcTemplate` provides a generic wrapper around `jdbc` connections
that cuts down on boilerplate for running simple queries. There's also a
`JdbcTransactionTemplate` that makes handling transactions in a standardized
way simpler than it would be using raw jdbc connections.

Spring's Jdbc tooling lies midway between dealing with low-level connections
and an ORM: you still do the following:
  - instantiate the `DataSource` that the `JdbcTemplate` wraps
  - write sql queries
  - convert sql `ResultSet` objects (basically one record of a result) into
    your domain objects
But Spring handles a lot of boilerplate for you:
  - handling thread-safety (once a `JdbcTemplate` is set up, it should be
    thread-save, that is you can use a single instance in all your workers)
  - opening connections, executing statements, and looping over results
  - converting the typed SQLException into one of several unchecked types,
    where the type depends on the specific error
  - making transactions easier to set up (e.g. by annotating a method)

In this demo app I'm using the Spring Framework and Beans to set up my app,
but (as far as I know - I haven't tried it) it's possible to use Spring
`JdbcTemplate` without using the Spring framework, if you find that it
provides the right level of abstraction for your app but you don't
want to use the Inversion of Control (IOC) container.

## Show me the code!

### The core SQL code

The `com.stroxler.Dao` class has all the core SQL functionality. It uses a
`customers` table and implements CRUD for customers; the entity it uses is in
`com.stroxler.entity.Customer`.

There's a `com.stroxler.SqlScriptRunner` with static methods that I've found
very useful in integration tests, which takes a `JdbcTemplate` and a path
to a script in `resources` as input, and runs that SQL script.

Typically `SpringJdbc` demos implement `RowMapper` classes to convert
SQL results to domain objects; since this is a lightweight, minimal example
I decided to use lambdas instead in my `findCustomer` and `findAllCustomers`
methods. If I add a part 2, more complex demo I'll demonstrate how the
code changes when you implement a `RowMapper` class.

Finally, in `com.stroxler.DaoTest` there's a full integration test suite
for the CRUD operations implemented in `Dao`; the tests use a `clear_db.sql`
script and the `SqlScriptRunner` to get a clean state for each test.

Since my app is a Spring app, I'm using the Spring-JUnit integration to
wire up beans in that test class, although this isn't necessary, and if you
were to use `JdbcTemplates` without using Spring IOC, you would probably
skip this.

### Other code

There is some non-SQL related code in this repo:
  - `Main`: this is both the entrypoint and the Spring config manager
    (with all the `@Bean` methods) for the app
  - `logging.LogCall` and `logging.LoggingInterceptor`: I'll try to make
    a demo that dives into how these work later; for the purposes of this
    demo just think of them as magic, where putting a `@LogCall` annotation
    on a method causes a log of the call and return to show up in Stdout;
    this can help with debugging, and I'm using it here to prove that the
    unit tests actually run


#### Spring setup

The `Main` class is both the entry point and the configuration driver of
our code; all of the Spring beans are obtained via `@Bean`-annotated methods
of `Main`. Using `@Bean` methods isn't the only annotation-driven way of
wiring a Spring app, but I personally find it the most useful for three
reasons:
  - it keeps all the Spring wiring in one place, which makes it easier
    to debug wiring errors since you need only look at one file.
  - related to the above, it decouples most of your application from
    Spring (unlike if you put Spring annotations such as `@Component` directly
    onto all of your classes), which *might* make it easier to use the same
    tools outside of a Spring app
  - making methods for the beans gives an obvious place to add runtime
    complexity - e.g. if you wanted to load config files and/or inspect
    environment variables and set fields of your beans based on the results,
    it's much more obvious how to do so when using the `@Bean` annotations
    on regular methods, without needing to figure out Spring-specific magic,
    since the methods are just java code.

#### Logging setup

The `@LogCall` annotation causes the `LoggingInterceptor`, an aspect-oriented
decorator for method calls, to log the method called, the arguments used,
and the return value. I've found this kind of method tracing really handy
for debugging, so it's nice to have it even in a simple demo project.

As currently configured, the `LoggingInterceptor` logs seem to be going to
standard out rather than standard error; I'm not yet sure why.

A note about the `build.gradle`:
  - I added `logback` along with `slf4j`, which means the `slf4j` interfaces
    are being implemented via `logback`
  - Without the `'org.springframework:spring-aspects:4.2.5.RELEASE'` compile
    dependency, my logging tooling was compiling without error (since it
    only depends on `AspectJ`, but throwing runtime errors during Spring
    wiring. Apparently Spring needs to be made aware of your aspect-oriented
    components in order to use them properly

### Some notes on the gradle build config

Since I had some trouble with my `build.gradle` at a few points, I want
to highlight a few things:
  - In the `test` block, I'm setting the `DB_PATH` environment variable
    explicitly, which affects the path `Main` uses when instantaiating
    my Sqlite `DataSource`. I like controlling behavior via environment
    varaibles, and this is a nice demo of how you can use it to toggle
    test-only settings for integration tests.
  - Also in the `test` block I've added an `afterTest` handler so that
    I see basic test results logged out on the command line.
  - I decided to use `logback` rather than `slf4-simple` for my logging
    implementation. Note that I include `slf4j-api` but not `slf4j-simple`.
    And I need both `logback-core` *and* `logback-classic`, because Java
    is confusing like that.
  - Originally I tried to do my `@LogCall` magic using just `aspectj-lib`
    and `aspectj-rt`, but to make it work with the Spring IOC setup
    it seems like I need `spring-aspects` as well.
  - I was unable to get `junit-jupiter-core` to work as a test runner,
    so I'm using plain old junit as my test runner (which gets pulled
    in by `spring-boot-starter-test`, and only pulling in `jupiter-api`
    and `jupiter-engine`. It's unclear to me how I'm really supposed to do
    this, but it seems to work.
   
## Part 2?

I consider this demo program complete, up to possibly adding more discussion
and/or links to the README to make it more beginner friendly (right now
it's more like a reference for experienced devs).

I think the code right now is in a sweet spot where you could read it all in
less than a half hour and understand everything, and easily copy-paste snippets
into a new app to help bootstrap.

But there's a fair amount of core, basice Spring jdbc functionality
I haven't shown:
  - how to create methods that access multiple tables
  - how to create `RowMapper` and `BatchUpdater` classes for conversion
    to and from domain objects (the lambdas I am using are nice for a
    dead-simple app, but usually you'll have a bunch of methods that need
    to convert the same objects, at which point you want to pull that into
    classes)
  - how to make methods transactional

Hopefully I'll get a chance to make a part 2 sometime soon, where I'll
use the same schema but extend the functionality to include some joins,
transatictions, and serialization classes.
