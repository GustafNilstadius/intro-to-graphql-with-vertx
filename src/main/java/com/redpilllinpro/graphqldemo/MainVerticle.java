package com.redpilllinpro.graphqldemo;

import com.redpilllinpro.graphqldemo.service.author.AuthorService;
import com.redpilllinpro.graphqldemo.service.author.model.Author;
import com.redpilllinpro.graphqldemo.service.book.BookService;
import com.redpilllinpro.graphqldemo.service.book.model.Book;
import com.redpilllinpro.graphqldemo.service.quote.QuoteService;
import com.redpilllinpro.graphqldemo.service.quote.model.Quote;
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
    bookService = BookService.create(vertx);
    authorService = AuthorService.create(vertx);
    quoteService = QuoteService.create(vertx);

    GraphQLHandlerOptions graphQLHandlerOptions = new GraphQLHandlerOptions()
      .setRequestBatchingEnabled(true);

    GraphQL graphQL = setupGraphQL();
    GraphQLHandler graphQLHandler = GraphQLHandler.create(graphQL, graphQLHandlerOptions);


    // Graphical interface, GraphiQL.
    GraphiQLHandlerOptions options = new GraphiQLHandlerOptions()
      .setEnabled(true);

    Router router = Router.router(vertx);
    router.route().handler(LoggerHandler.create());
    router.post().handler(BodyHandler.create());

    router.route("/graphql").handler(graphQLHandler);
    router.route("/graphiql/*").handler(GraphiQLHandler.create(options));

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
