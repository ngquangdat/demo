package com.example.demo.grpc;

import com.example.demo.dummy.StockRepository;
import com.example.demo.ws.proto.HelloRequest;
import com.example.demo.ws.proto.HelloResponse;
import com.example.demo.ws.proto.HelloServiceGrpc;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@GrpcService
public class HelloGrpcService extends HelloServiceGrpc.HelloServiceImplBase {

    private final List<StreamObserver<HelloResponse>> observerList = new ArrayList<>();


    @Override
    public void hello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        HelloResponse response = HelloResponse.newBuilder()
                .setMessage("Hello ==> " + request.getMessage())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<HelloRequest> bidirectionalStreamingHello(StreamObserver<HelloResponse> responseObserver) {
        var serverCallStreamObserver = ((ServerCallStreamObserver<HelloResponse>) responseObserver);
        serverCallStreamObserver.setOnCancelHandler(() -> {
            log.info("onCancelHandler");
            observerList.remove(responseObserver);
        });
        return new StreamObserver<HelloRequest>() {
            @Override
            public void onNext(HelloRequest helloRequest) {
                observerList.add(responseObserver);
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {

            }
        };
    }

    @Scheduled(fixedRate = 3000)
    public void sendMessage() {
        var random = ThreadLocalRandom.current();
        var stocks = StockRepository.INSTANCE.getStocks();
        var randomStock = stocks.stream().skip(random.nextInt(stocks.size())).findFirst().orElseThrow();
        var newPrice = randomStock.getPrice() + random.nextInt(-5, 5);
        randomStock.updatePrice(newPrice);

        observerList.forEach(observer -> {
            HelloResponse response = HelloResponse.newBuilder()
                    .setMessage("Hello Scheduled ==> " + StockRepository.INSTANCE.getStocks())
                    .build();
            observer.onNext(response);
        });
    }
}
