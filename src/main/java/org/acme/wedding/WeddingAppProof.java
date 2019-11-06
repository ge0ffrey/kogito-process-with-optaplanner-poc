/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.acme.wedding;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.acme.wedding.constraints.WeddingConstraintProvider;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

public class WeddingAppProof {

    public static void main(String[] args) {
        SolverFactory<WeddingSolution> solverFactory = SolverFactory.createEmpty();
        SolverConfig solverConfig = solverFactory.getSolverConfig();

        solverConfig.withSolutionClass(WeddingSolution.class)
                .withEntityClassList(Arrays.asList(Guest.class))
                .withScoreDirectorFactory(
                        new ScoreDirectorFactoryConfig().withConstraintProviderClass(WeddingConstraintProvider.class)
                )
                .withTerminationConfig(new TerminationConfig()
                        .withSecondsSpentLimit(5L));

        Solver<WeddingSolution> solver = solverFactory.buildSolver();

        WeddingSolution problem = generateProblem();
        WeddingSolution solution = solver.solve(problem);
        solution.getGuestList().stream().collect(Collectors.groupingBy(Guest::getTable, Collectors.toList()))
                .forEach((table, guests) -> {
                    System.out.println("Table");
                    for (Guest guest : guests) {
                        System.out.println("  " + guest.getName());
                    }
                });
    }

    private static WeddingSolution generateProblem() {
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
