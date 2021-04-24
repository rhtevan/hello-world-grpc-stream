package com.example.grpc;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Singleton;

import io.quarkus.example.HelloReply;
import io.quarkus.example.HelloRequest;
import io.quarkus.example.MutinyGreeterGrpc.GreeterImplBase;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

@Singleton
public class ReactHelloWorldService extends GreeterImplBase {

    // Unary gRPC call
    @Override
    public Uni<HelloReply> sayHello(HelloRequest request) {
        return Uni.createFrom().item(() -> 
            HelloReply.newBuilder().setMessage("Hello " + request.getName()).build()
        ); 
    }

    // Server streaming gRPC call
    @Override
    public Multi<HelloReply> sayHelloAgainAndAgain(HelloRequest request) {
       return Multi.createBy()
                   .repeating()
                   .uni(
                       () -> new AtomicInteger(),
                       state -> (Uni<HelloReply>) Uni.createFrom()
                                   .item(() -> HelloReply.newBuilder().setMessage(
                                        "Hello " + request.getName() + "@"
                                                 + state.getAndIncrement()).build())
                                   .onItem().delayIt().by(Duration.ofSeconds(1))) // Wait for 1 seconds to release the reply with out warning
                   .atMost(100); // Say hello at most 100 times
   } 
}
