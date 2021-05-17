[This article about GraphQL can be read here. https://www.redpill-linpro.com/techblog/2021/05/17/intro-to-graphql-with-vertx.html](https://www.redpill-linpro.com/techblog/2021/05/17/intro-to-graphql-with-vertx.html)

GraphQL is a modern approach to APIs that simplifies integrations. This is an introduction to what GraphQL is, and we build a simple GraphQL service with the help of Vert.X.

## What you'll need to follow along ##
This introduction will barely scratch the surface of the potential and power that GraphQL together with Vert.X offers. Understanding of programming is assumed.
* JDK 8+
* Maven
* IDE
* Familiarity with REST and HTTP

Source code for this post can be found here: [https://github.com/HiPERnx/intro-to-graphql-with-vertx](https://github.com/HiPERnx/intro-to-graphql-with-vertx).

## Background ##
Imagine this, you have a book store with 3 defined data entities; Book, Author and Quote. Where every book has an author and every author has a favorite quote.
If we want to retrieve data from our book store with a traditional REST call in our newly developed SuperMegaAwesomeBookStore<sup>TM</sup> app we have two options.

**Option 1:**
Separate REST GET requests for each entity.
1.  We can make a call to `/books` to get a list of all the books.
2.  A call to `/authors/:authorID` to get the author.
3.  Lastly a call `/quotes/:quoteID` to get the favorite quote of the author.

**Option 2:**
Returning a deep object for one REST GET request.
1.  We can make a call to `/books` to get a list of all the books complete with all the authors and quotes.


**The data**
Independent of the method (option 1 or 2) we end up with the same data exampled bellow.
```json
[
    {
      "id": "ABCD",
      "name": "Awesome book name",
      "page_count": 42,
      "author": {
        "id": "ABC123",
        "firstName": "Author",
        "lastName": "Authorson",
        "favoriteQuote": {
            "id": "123",
            "value": "Not too little, nor too much. Just right."
        }
      }
    }
]
```
Either we risk getting too little data in one call e.g. if we want to display all books with the first name of the author.
Alternatively we risk getting too much data in one call e.g. if we just want to list all the book names.
This is where GraphQL comes in, GraphQL enable the client to get **_lagom_** ("_lagom_", Swedish, "Not too little, nor too much. Just right.") amount of data.

## GraphQL ##
[GraphQL](https://graphql.org/) or Graph Query Language is as it suggests a query language for graph data.
GraphQL is a communication pattern and not an implementation or API in itself. Developed by Facebook back in 2012, GraphQL is today a part of the GraphQL foundation.
GraphQL enables the clients of a GraphQL service to get just the right amount of data.

How does GraphQL solve the problems previously described and always serves **_lagom_** amount of data to its clients?
It's actually rather simple, the client tells the backend exactly what it wants, and the backend will provide.

But how does it actually work? First we need to describe our data, in a so called schema. Then from the schema we can construct queries. With a schema we can then query the GraphQL service.
1. Describe your data.
2. Query.
3. Get what you asked for.

### Describe your data. ###
To make your book store data queryable we first have to define it. In GraphQL this is called a schema and can either be defined programmatically depending on the implementation or in a `.graphql` SDL (Schema Definition Language) file.
In this example we'll use the SDL approach. It looks like JSON, but it's not.

So let's start describing our data.
_Note: "scalar" is what a data type is called in GraphQL._
#### Books ####
```
type Book {
  id: ID
  name: String
  pageCount: Int
  author: Author
}
```
Looking closer at the definition for book. `Book` is the object we are defining.
A `Book` has four members, `id` of the scalar type `ID` that signals to GraphQL that the field is a unique identifier and not always human readable.
`name` of the scalar `String`, `pageCount` of scalar `Int` and lastly `author` of type `Author`.
#### Author ####
The definition of author continues in the same fashion as for books.
```
type Author {
  id: ID
  firstName: String
  lastName: String
  favoriteQuote: Quote
}
```

#### Quote ####
```
type Quote {
  id: ID
  value: String
}
```

#### Schema ####
Now with all the objects defined, let's put it all together in a schema. A GraphQL service is required to have a `query` type defined and may have a `mutation` type. `mutation` can be equaled to POST in REST, but is out of scope for this introduction.
`schema.graphql`
```
type Query {
  bookById(id: ID): Book
  getBooks: [Book]
}

type Book {
  id: ID
  name: String
  pageCount: Int
  author: Author
}

type Author {
  id: ID
  firstName: String
  lastName: String
  favoriteQuote: Quote
}

type Quote {
  id: ID
  value: String
}
```
The obligatory `Query` object defines two queries, these queries will later be exposed to our clients.

**bookById(id: ID): Book**
As the name suggests, it's a query to find a book by id. It takes an argument of scalar `ID`, and it returns an object of type `Book`.

**getBooks: [Book]**
Returns an array of type `Book`.

We have now defined a GraphQL schema, the schema can be updated at any time, this makes versioning in a GraphQL service easy.
As long as no fields are removed, it can be updated without breaking the clients.

### Query ###
Let's construct our first query. We want to get all books, and for each book we want to get the number of pages and the author's first name.
```
query {
  getBooks {
    pageCount,
    author {
      firstName
    }
  }
}
```
_Note: operation type `query` can be omitted in most cases. Added for clarity and best practices_
Example result for above query:
```json
{
  "data": {
    "getBooks": [
      {
        "pageCount": 276,
        "author": {
          "firstName": "William"
        }
      },
      {
        "pageCount": 635,
        "author": {
          "firstName": "Herman"
        }
      }
    ]
  }
}
```
Not that exactly what was requested eas also returned. Nothing more, nothing less.

We not have a client that wants to get the favorite quote of each author in addition to the above data, how would we change the query to do that?
Just ask for it! By adding the `favoriteQuote` element to the author we can get the favorite quote for each of the authors.

```
query {
  getBooks {
    pageCount,
    author {
      firstName
      favoriteQuote {
        value
      }
    }
  }
}
```

Example result:
```json
{
  "data": {
    "getBooks": [
      {
        "pageCount": 276,
        "author": {
          "firstName": "William",
          "favoriteQuote": {
            "value": "Chuck Norris calculated the square root of negative one while eating a bowl full of rusty fishhooks."
          }
        }
      },
      {
        "pageCount": 635,
        "author": {
          "firstName": "Herman",
          "favoriteQuote": {
            "value": "The only way to make saturday every day is to kill Chuck Norris. Which is 99999999999999999999999999999999% impossible."
          }
        }
      }
    ]
  }
}
```

## Vert.X ##
[Vert.X](https://vertx.io) describes itself as follows;
> Vert.x is a tool-kit for building reactive applications on the JVM. Reactive applications are both scalable as workloads grow, and resilient when failures arise. A reactive application is responsive as it keeps latency under control by making efficient usage of system resources, and by protecting itself from errors.

Vert.X can best be described as a modern and asynchronous alternative to Spring-boot or a good version of NodeJS. I can go in depth about Vert.X, but it's out of scope for this introduction.
The golden rule for Vert.X is to never block the event loop. Every module in Vert.X runs inside what's called a `verticle`.
```java
public class MainVerticle extends AbstractVerticle {

  private BookService bookService;
  private AuthorService authorService;
  private QuoteService quoteService;

  @Override
  public void start(Promise<Void> startPromise) {
      /* Do stuff, like init GraphQL, deploy microservices and start HTTP servers  */
      /* Don't forget to complete the startPromise */
  }
}
```

## GraphQL with microservices in Vert.X (Java) ##
Due to the highly asynchronous nature of Vert.X and GraphQL they are a great match. Let's put together what we learnt about GraphQL and make a practical demo in Vert.X.
Remember that GraphQL is not an implementation in itself, therefore we are using the implementation [GraphQL-Java](https://www.graphql-java.com) in this example.

### Init GraphQL-Java ###
Let's initiate our GraphQL instance.
`src/main/java/com/redpilllinpro/graphqldemo/MainVerticle.java`
```java
private GraphQL setupGraphQL() {
    /* Read the schema file from the file system. */
    String schema = vertx.fileSystem().readFileBlocking("schema/schema.graphql").toString();

    /* (1) Parse  schema and create a TypeDefinitionRegistry */
    SchemaParser schemaParser = new SchemaParser();
    TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(schema);

    /* (2) RuntimeWiring linking our schema/TypeDefinitionRegistry to our services */
    RuntimeWiring runtimeWiring = newRuntimeWiring()
      .type(newTypeWiring("Query")
        .dataFetcher("bookById", bookByIdDataFetcher())
        .dataFetcher("getBooks", booksDataFetcher()))
      .type(newTypeWiring("Book")
        .dataFetcher("author", authorDataFetcher()))
      .type(newTypeWiring("Author")
        .dataFetcher("favoriteQuote", quoteDataFetcher()))
      .build();

    SchemaGenerator schemaGenerator = new SchemaGenerator();
    GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);

    return GraphQL.newGraphQL(graphQLSchema).build();
}
```

1. **TypeDefinitionRegistry** The TypeDefinitionRegistry is the GraphQL-Java representation of our schema file.
2. **RuntimeWiring** links our TypeDefinitionRegistry (schema) to our DataFetchers for each of the objects.

The rest of the initiation inside the Vert.X verticle starter.
`src/main/java/com/redpilllinpro/graphqldemo/MainVerticle.java`
```java
  public void start(Promise<Void> startPromise) {
    /* Omitted, data service initiation */
    GraphQLHandlerOptions graphQLHandlerOptions = new GraphQLHandlerOptions()
      .setRequestBatchingEnabled(true);

    GraphQL graphQL = setupGraphQL();
    GraphQLHandler graphQLHandler = GraphQLHandler.create(graphQL, graphQLHandlerOptions);

    /* Omitted */
  }
```

### Start HTTP server in Vert.X ###
`src/main/java/com/redpilllinpro/graphqldemo/MainVerticle.java`
```java
  public void start(Promise<Void> startPromise) {
    /* Omitted, data service initiation */

    /* GraphQL-Java stuff */

    /* GraphQL graphical interface, GraphiQL. */
    GraphiQLHandlerOptions options = new GraphiQLHandlerOptions()
      .setEnabled(true);

    /* Creating a HTTP router */
    Router router = Router.router(vertx);
    router.route().handler(LoggerHandler.create());
    router.post().handler(BodyHandler.create());

    /* Binding our GraphQL handler to a endpoint, can be any endpoint. One server can have muliple GraphQL endpoints. */
    router.route("/graphql").handler(graphQLHandler);

    /* Binding the GraphiQL interface to a endpoint */
    router.route("/graphiql/*").handler(GraphiQLHandler.create(options));


    /* Error handling */
    router.errorHandler(500, ctx -> {
      ctx.failure().printStackTrace();
      ctx.response().setStatusCode(500).end();
    });

    /* Strat server on port 8888 */
    vertx.createHttpServer().requestHandler(router).listen(8888, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("HTTP server started on port 8888");
      } else {
        startPromise.fail(http.cause());
      }
    });
  }
```
### DataFetchers ###
`src/main/java/com/redpilllinpro/graphqldemo/MainVerticle.java`
```java
public void start(Promise<Void> startPromise) {
    bookService = BookService.create(vertx);
    authorService = AuthorService.create(vertx);
    quoteService = QuoteService.create(vertx);
}
```
See sources for details on the data services. QuoteService is a little extra interesting.

## Build the code and try our GraphQL service ##
Build and run our Vert.X service.
```shell script
mvn clean package
java -jar target/graphqldemo-1.0.0-SNAPSHOT-fat.jar
```

```shell script
HTTP server started on port 8888
```

Head over to [http://localhost:8888/graphiql/index.html](http://localhost:8888/graphiql/index.html) and try out our service!

GraphiQL is a great tool to try out a GraphQL service, a good starting point is to try the query we wrote before.

This will get all books and some select data members of every book.
```
query {
  getBooks {
    pageCount,
    author {
      firstName
      favoriteQuote {
        value
      }
    }
  }
}
```
