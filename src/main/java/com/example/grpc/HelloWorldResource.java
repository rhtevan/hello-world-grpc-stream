package com.example.grpc;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestSseElementType;

import io.quarkus.example.HelloReply;
import io.quarkus.example.HelloRequest;
import io.quarkus.example.MutinyGreeterGrpc.MutinyGreeterStub;
import io.quarkus.grpc.runtime.annotations.GrpcService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

@Path("/hello")
public class HelloWorldResource {

    // Looks like the blocking grpc stub DOES NOT work with Rest-Reactive
    // @GrpcService("hello")
    // GreeterBlockingStub client;

    // The name of the service is used at application.properties for configuration only
    // For example, quarkus.grpc.client.{*}.host=
    @GrpcService("hello")
    MutinyGreeterStub reactClient;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello Nobody!";
    }

    // Using gRPC blocking stub does NOT working with rest-reactive 
    // @GET
    // @Produces(MediaType.TEXT_PLAIN)
    // @Path("/{name}")
    // public String hello(String name) {
    //     return client.sayHello(HelloRequest.newBuilder().setName(name).build()).getMessage();
    // }

    /**
     * By default, react library will use IO threads to process the events/messages.
     * The number of event-loop (aka IO threads) is 2 times of cores.
     * The default maximum length of execution and or blocking on IO is 2 seconds. 
     * 
     * See the following examples, for those using non-blocking gRPC stub there will be no threads being
     * blocked while waiting for downstream respons regardless how long it would return; however, warning message
     * will be thrown for the implementation relying on blocking stub if it waits more than 2 seconds.
     */

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/uni/{name}")
    public Uni<String> asyncHello(String name) {
        return reactClient.sayHello(HelloRequest.newBuilder().setName(name).build())
            .onItem().transform(HelloReply::getMessage)
            .onItem().transform(String::toUpperCase);
    }

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @RestSseElementType(MediaType.APPLICATION_JSON)
    @Path("/multi/{name}")
    public Multi<String> streamHello(String name) {
        return reactClient.sayHelloAgainAndAgain(HelloRequest.newBuilder().setName(name).build())
                          .onItem().transform(HelloReply::getMessage);
    }
}