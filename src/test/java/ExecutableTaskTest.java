import com.citizenweb.training.reactivelifecyclemanager.model.ExecutableTask;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
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
        Publisher<?> mono1 = executable1.execute(ExecutableTask.FIRST_TASK_NO_ARGS);
        System.out.println("Executable 1 implemented");

        ExecutableTask<String> executable2 = inputs -> {
            String result = "Result 2";
            return Mono.just(result);
        };
        Publisher<?> mono2 = executable2.execute(ExecutableTask.FIRST_TASK_NO_ARGS);
        System.out.println("Executable 2 implemented");

        Mono<?>[] monoArgs = new Mono[] {((Mono<?>) mono1),((Mono<?>) mono2)};
        System.out.println("Tasks flux implemented");

        ExecutableTask<String> executable3 = inputs -> {
            AtomicReference<String> result = new AtomicReference<>("");
            return Mono.zip( ((Mono<?>) inputs[0]) , ((Mono<?>) inputs[1]) ,(m1, m2) -> {
                result.set(((String) m1).concat((String) m2));
                return result.get();
            });
        };
        Publisher<String> mono3 = executable3.execute(monoArgs);
        System.out.println("Executable 3 implemented");

        Mono.from(mono1).log().subscribe(System.out::println);
        Mono.from(mono2).log().subscribe(System.out::println);
        Mono.from(mono3).log().subscribe(System.out::println);

        StepVerifier.create(mono3)
                .expectNext("Result 1Result 2")
                .expectComplete()
                .verify(Duration.ofMillis(5000));

    }

}
