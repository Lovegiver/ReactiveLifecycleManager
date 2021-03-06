import com.citizenweb.training.reactivelifecyclemanager.model.ExecutableTask;
import com.citizenweb.training.reactivelifecyclemanager.model.LifecycleManager;
import com.citizenweb.training.reactivelifecyclemanager.model.Task;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Set;

public class LifecycleTest {

    @Test
    public void buildPaths() {
        LifecycleManager lifecycleManager = buildLifecycle();
        lifecycleManager.execute();
    }

    private LifecycleManager buildLifecycle() {

        ExecutableTask<Integer> et1 = inputs -> Mono.just(1);
        Task t1 = new Task("Task 1", et1, Collections.emptySet());

        ExecutableTask<Integer> et2 = inputs -> Mono.just(2);
        Task t2 = new Task("Task 2", et2, Collections.emptySet());

        ExecutableTask<Integer> et3 = inputs -> Mono.just(3);
        Task t3 = new Task("Task 3", et3, Collections.emptySet());

        ExecutableTask<Integer> et4 = inputs -> Mono.just(4);
        Task t4 = new Task("Task 4", et4, Set.of(t1));

        ExecutableTask<Integer> et5 = inputs -> Mono.just(5);
        Task t5 = new Task("Task 5", et5, Set.of(t1,t2));

        ExecutableTask<Integer> et6 = inputs -> Mono.just(6);
        Task t6 = new Task("Task 6", et6, Set.of(t2));

        ExecutableTask<Integer> et7 = inputs -> Mono.just(7);
        Task t7 = new Task("Task 7", et7, Set.of(t3));

        ExecutableTask<Integer> et8 = inputs -> Mono.just(8);
        Task t8 = new Task("Task 8", et8, Set.of(t2,t3));

        ExecutableTask<Integer> et9 = inputs -> Mono.just(9);
        Task t9 = new Task("Task 9", et9, Set.of(t4));

        ExecutableTask<Integer> et10 = inputs -> Mono.just(10);
        Task t10 = new Task("Task 10", et10, Set.of(t4,t1));

        ExecutableTask<Integer> et11 = inputs -> Mono.just(11);
        Task t11 = new Task("Task 11", et11, Set.of(t5,t8));

        ExecutableTask<Integer> et12 = inputs -> Mono.just(12);
        Task t12 = new Task("Task 12", et12, Set.of(t5,t6));

        ExecutableTask<Integer> et13 = inputs -> Mono.just(13);
        Task t13 = new Task("Task 13", et13, Set.of(t6,t7,t8));

        ExecutableTask<Integer> et14 = inputs -> Mono.just(14);
        Task t14 = new Task("Task 14", et14, Set.of(t7,t8));

        ExecutableTask<Integer> et15 = inputs -> Mono.just(15);
        Task t15 = new Task("Task 15", et15, Set.of(t8));

        Set<Task> allTasks = Set.of(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13,t14,t15);

        return new LifecycleManager("LIFECYCLE", allTasks);

    }

}
