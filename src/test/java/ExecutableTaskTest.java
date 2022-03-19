import com.citizenweb.training.reactivelifecyclemanager.model.ExecutableTask;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

public class ExecutableTaskTest {

    @Test
    public void createExecutable() {

        ExecutableTask<String> executable1 = inputs -> {
            String result = "Result 1";
            return Mono.just(result);
        };
        Mono<?> mono1 = executable1.execute(Flux.empty());
        System.out.println("Executable 1 implemented");

        ExecutableTask<String> executable2 = inputs -> {
            String result = "Result 2";
            return Mono.just(result);
        };
        Mono<?> mono2 = executable2.execute(Flux.empty());
        System.out.println("Executable 2 implemented");

        Flux<Mono<?>> stringFlux = Flux.just(mono1,mono2);
        System.out.println("Tasks flux implemented");

        ExecutableTask<String> executable3 = inputs -> {
            AtomicReference<String> result = new AtomicReference<>("");
            inputs.subscribe(mono -> result.set(result.get() + getValue(mono)));
            return Mono.just(result.get());
        };
        Mono<String> mono3 = executable3.execute(stringFlux);
        System.out.println("Executable 3 implemented");

        mono1.log().subscribe(System.out::println);
        mono2.log().subscribe(System.out::println);
        mono3.log().subscribe(System.out::println);

        StepVerifier.create(mono3)
                .expectNext("Result 1Result 2")
                .expectComplete()
                .verify(Duration.ofMillis(5000));

    }

    private <T> T getValue(Mono<T> mono) {
        return mono.block();
    }

}
