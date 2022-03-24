import com.citizenweb.training.reactivelifecyclemanager.model.ExecutableTask;
import com.citizenweb.training.reactivelifecyclemanager.model.Task;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
            return Mono.zip( ((Mono<?>) inputs[0]), ((Mono<?>) inputs[1]),(m1, m2) -> {
                result.set(((String) m1).concat((String) m2));
                return result.get();
            });
        };
        Task task3 = new Task("Task 3", executable3, Set.of(task1, task2));
        System.out.println("Task 3 implemented");

        ExecutableTask<String> executable4 = inputs -> {
            AtomicReference<String> result = new AtomicReference<>("");
            return Mono.zip( ((Mono<?>) inputs[0]), ((Mono<?>) inputs[1]),(m1, m2) -> {
                result.set(((String) m1).concat((String) m2));
                return result.get();
            });
        };
        Task task4 = new Task("Task 4", executable4, Set.of(task1, task3));
        System.out.println("Task 4 implemented");

        var task1Hashcode = task1.hashCode();
        var task1HashcodeFromTask3 = task3.getPredecessors().stream()
                .filter(task -> "Task 1".equals(task.getMonitor().getName()))
                .map(Task::hashCode)
                .findAny()
                .orElseThrow();
        var task1HashcodeFromTask4 = task4.getPredecessors().stream()
                .filter(task -> "Task 1".equals(task.getMonitor().getName()))
                .map(Task::hashCode)
                .findAny()
                .orElseThrow();

        assertEquals(task1Hashcode,task1HashcodeFromTask3);
        assertEquals(task1HashcodeFromTask3,task1HashcodeFromTask4);

        Mono.from(task1.execute()).log().subscribe(System.out::println);
        Mono.from(task2.execute()).log().subscribe(System.out::println);
        Mono.from(task3.execute()).log().subscribe(System.out::println);
        Mono.from(task4.execute()).log().subscribe(System.out::println);

    }

}
