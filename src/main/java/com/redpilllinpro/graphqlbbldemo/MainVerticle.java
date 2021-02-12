package com.redpilllinpro.graphqlbbldemo;

import com.redpilllinpro.graphqlbbldemo.service.author.AuthorService;
import com.redpilllinpro.graphqlbbldemo.service.author.AuthorServiceVeritcle;
import com.redpilllinpro.graphqlbbldemo.service.author.model.Author;
import com.redpilllinpro.graphqlbbldemo.service.book.BookService;
import com.redpilllinpro.graphqlbbldemo.service.book.BookServiceVerticle;
import com.redpilllinpro.graphqlbbldemo.service.book.model.Book;
import com.redpilllinpro.graphqlbbldemo.service.quote.QuoteService;
import com.redpilllinpro.graphqlbbldemo.service.quote.QuoteServiceVerticle;
import com.redpilllinpro.graphqlbbldemo.service.quote.model.Quote;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.graphql.GraphQLHandler;
import io.vertx.ext.web.handler.graphql.GraphQLHandlerOptions;
import io.vertx.ext.web.handler.graphql.GraphiQLHandler;
import io.vertx.ext.web.handler.graphql.GraphiQLHandlerOptions;
import io.vertx.ext.web.handler.graphql.schema.VertxDataFetcher;

import java.util.List;

import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;
import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

public class MainVerticle extends AbstractVerticle {

  private BookService bookService;
  private AuthorService authorService;
  private QuoteService quoteService;

  @Override
  public void start(Promise<Void> startPromise) {
    vertx.deployVerticle(BookServiceVerticle.class.getName(),
      new DeploymentOptions().setInstances(3).setConfig(config()), event -> System.out.println("Deployment of BookService succeeded? " + event.succeeded()));
    vertx.deployVerticle(QuoteServiceVerticle.class.getName(),
      new DeploymentOptions().setInstances(3).setConfig(config()), event -> System.out.println("Deployment of QuoteService succeeded? " + event.succeeded()));
    vertx.deployVerticle(AuthorServiceVeritcle.class.getName(),
      new DeploymentOptions().setInstances(3).setConfig(config()), event -> System.out.println("Deployment of AuthorService succeeded? " + event.succeeded()));

    // Create proxies for the services.
    bookService = BookService.createProxy(vertx);
    authorService = AuthorService.createProxy(vertx);
    quoteService = QuoteService.createProxy(vertx);

    Router router = Router.router(vertx);


    GraphQLHandlerOptions graphQLHandlerOptions = new GraphQLHandlerOptions()
      .setRequestBatchingEnabled(true);

    GraphQL graphQL = setupGraphQL();
    GraphQLHandler graphQLHandler = null;
    try {
      graphQLHandler = GraphQLHandler.create(graphQL, graphQLHandlerOptions);
    } catch (Exception e) {
      e.printStackTrace();
    }

    // Graphical interface, GraphiQL.
    GraphiQLHandlerOptions options = new GraphiQLHandlerOptions()
      .setEnabled(true);


    router.route().handler(LoggerHandler.create());
    router.post().handler(BodyHandler.create());

    router.route("/graphql").handler(graphQLHandler);
    router.route("/graphiql/*").handler(GraphiQLHandler.create(options));


    router.errorHandler(500, ctx -> {
      ctx.failure().printStackTrace();
      ctx.response().setStatusCode(500).end();
    });

    vertx.createHttpServer().requestHandler(router).listen(8888, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("HTTP server started on port 8888");
      } else {
        startPromise.fail(http.cause());
      }
    });
  }

  private GraphQL setupGraphQL() {
    String schema = vertx.fileSystem().readFileBlocking("schema/schema.graphql").toString();

    SchemaParser schemaParser = new SchemaParser();
    TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(schema);

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

  // Data fetchers
  private VertxDataFetcher<Book> bookByIdDataFetcher() {
    // This is thread safe, one verticle is always executed by the same event thread unless new threads are created.
    return VertxDataFetcher.create((dataFetchingEnvironment, bookPromise) -> bookService.getBookById(dataFetchingEnvironment.getArgument("id"), bookPromise));
  }
  private VertxDataFetcher<List<Book>> booksDataFetcher() {
    return VertxDataFetcher.create((dataFetchingEnvironment, booksPromise) -> bookService.getBooks(booksPromise));
  }
  private VertxDataFetcher<Author> authorDataFetcher() {
    return VertxDataFetcher.create((dataFetchingEnvironment, authorPromise) -> {
      Book book = dataFetchingEnvironment.getSource();
      String authorId = book.getAuthor();
      authorService.getAuthorByID(authorId, authorPromise);
    });
  }
  private VertxDataFetcher<Quote> quoteDataFetcher() {
    return VertxDataFetcher.create((dataFetchingEnvironment, quotePromise) -> {
      Author author = dataFetchingEnvironment.getSource();
      String quoteId = author.getQuote();
      quoteService.getQuote(quoteId, quotePromise);
    });
  }



}
