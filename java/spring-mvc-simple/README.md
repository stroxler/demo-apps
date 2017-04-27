# spring-mvc-simple

This is a simple SpringMVC application demo, using `jackson` for `json`
serialization, Spring Boot with embedded `jetty`, and using the apache
`HttpClient` for tests.

## SpringMVC functionality

The SpringMVC functionality is all determined by the `jackson` annotations
in the `TreeNode` class, which determine how it serializes to/from Json, plus
the `TreeController` class.

Hopefully the behavior is fairly self-evident:
 * the jackson annotations indicate the field names and how they correspond to
   constructor / getter methods.
 * the Spring annotations register the controller as a component for handling
   api requests, and its methods for handling specific routes. The
   `@RequestBody`, `@RequestParam`, and `@ResponseBody` annotations indicate
   how java objects correspond to the body of a PUT request (the same approach
   works for POST), the url params of a request, or the json output.

## The tests - using SpringBoot and HttpClient to test a website

The `TreeNodeTest` is a straightforward JUnit test, and shouldn't have any
surprises.

The `ApiTest` is a Spring Boot test, where we load up the same components that
Spring loads when the service starts. When testing Spring Boot tends to use
a different port than at runtime (assuming 8080, which is what the server
runs on when you do `gradle run`, did *not* work), so the nonsense
with `webEnvironment = SpringBoottest.WebEnvironment.RANDOM_PORT` and
`@LocalServerPport` is needed to tell the tests what port to hit.

The content of the tests involve a lot of work with the apache HttpClient.
There are a lot of fiddly details in making it work (of course, it's java!)
but the code should all speak for itself.
