package org.acme.wedding;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.jupiter.api.Test;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class WeddingTest {

    @Inject
    @Named("wedding")
    Process<? extends Model> process;


    @Test
    public void testWedding() {
        assertNotNull(process);

        Model m = process.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("weddingPlan", generateWeddingPlan());
        

        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = process.createInstance(m);
        processInstance.start();
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.status()); 
        
        Model result = (Model)processInstance.variables();
        assertEquals(1, result.toMap().size());
        WeddingSolution weddingSolution = (WeddingSolution) result.toMap().get("weddingPlan");

        assertNotNull(weddingSolution);

        weddingSolution.getGuestList().stream().collect(Collectors.groupingBy(Guest::getTable, Collectors.toList()))
                .forEach((table, guests) -> {
                    System.out.println("Table: " + guests.stream().map(Guest::getName).collect(Collectors.joining(", ")));
                });
    }


    private static WeddingSolution generateWeddingPlan() {
        WeddingSolution problem = new WeddingSolution();
        problem.setGuestList(IntStream.range(0, 120).mapToObj(i -> {
            Guest guest = new Guest();
            guest.setName("Guest " + i);
            return guest;
        }).collect(Collectors.toList()));
        problem.setTableList(IntStream.range(0, 12).mapToObj(i -> {
            Table table = new Table();
            table.setCapacity(10);
            return table;
        }).collect(Collectors.toList()));
        return problem;
    }
}
