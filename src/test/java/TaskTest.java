import com.citizenweb.training.reactivelifecyclemanager.model.ExecutableTask;
import com.citizenweb.training.reactivelifecyclemanager.model.Task;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@Log4j2
public class TaskTest {

    @Test
    public void createTask() {

        ExecutableTask<String> executable1 = inputs -> {
            String result = "Result 1";
            return Mono.just(result);
        };
        Task task1 = new Task("Task 1", executable1, Collections.emptySet());
        System.out.println("Task 1 implemented");

        ExecutableTask<String> executable2 = inputs -> {
            String result = "Result 2";
            return Mono.just(result);
        };
        Task task2 = new Task("Task 2", executable2, Collections.emptySet());
        System.out.println("Task 2 implemented");

        ExecutableTask<String> executable3 = inputs -> {
            AtomicReference<String> result = new AtomicReference<>("");
            inputs.subscribe(mono -> result.set(result.get() + getValue(mono)));
            return Mono.just(result.get());
        };
        Task task3 = new Task("Task 3", executable3, Set.of(task1, task2));
        System.out.println("Task 3 implemented");

        ExecutableTask<String> executable4 = inputs -> {
            AtomicReference<String> result = new AtomicReference<>("");
            inputs.subscribe(mono -> result.set(result.get() + getValue(mono)));
            return Mono.just(result.get());
        };
        Task task4 = new Task("Task 4", executable4, Set.of(task1, task3));
        System.out.println("Task 4 implemented");

        task1.execute().log().subscribe(System.out::println);
        task2.execute().log().subscribe(System.out::println);
        task3.execute().log().subscribe(System.out::println);
        task4.execute().log().subscribe(System.out::println);

    }

    private <T> T getValue(Mono<T> mono) {
        return mono.block();
    }
}