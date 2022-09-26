package com.efimchick.ifmo;

import com.efimchick.ifmo.util.CourseResult;
import com.efimchick.ifmo.util.Person;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.*;

public class Collecting {
    protected static Map<Double, String> scoreAndMarks;

    public int sum(IntStream stream) {
        return stream
                .sum();
    }

    public int production(IntStream stream) {
        return stream
                .reduce((i1, i2) -> i1 * i2).orElse(0);
    }

    public int oddSum(IntStream stream) {
        return stream
                .filter(i -> i % 2 != 0)
                .sum();
    }

    public Map<Integer, Integer> sumByRemainder(Integer divisor, IntStream stream) {
        return stream
                .boxed()
                .collect(Collectors.groupingBy(s -> s % divisor,
                        Collectors.summingInt(x -> x)));
    }

    public Map<Person, Double> totalScores(Stream<CourseResult> stream) {
        List<CourseResult> result = stream.collect(Collectors.toList());
        return result.stream()
                .collect(Collectors.toMap(CourseResult::getPerson,
                        r -> r.getTaskResults()
                                .values()
                                .stream()
                                .mapToInt(v -> v)
                                .sum() / (double)getCountTasks(result)));
    }

    private long getCountTasks (List<CourseResult> courseResults) {
        return courseResults.stream()
                .flatMap(r -> r.getTaskResults()
                        .keySet()
                        .stream())
                .distinct()
                .count();
    }

    public Double averageTotalScore(Stream<CourseResult> stream) {
        return totalScores(stream)
                .values()
                .stream()
                .collect(Collectors.averagingDouble(x -> x));
    }

    public Map<String, Double> averageScoresPerTask(Stream<CourseResult> stream) {
        List<CourseResult> result = stream.collect(Collectors.toList());
        return result.stream()
                .flatMap(r->r.getTaskResults()
                        .entrySet()
                        .stream())
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.summingDouble(value -> value.getValue() / (double)getCountPerson(result))));
    }

    private long getCountPerson(List<CourseResult> result) {
        return result.stream()
                .map(CourseResult::getPerson)
                .distinct()
                .count();
    }

    public Map<Person, String> defineMarks(Stream<CourseResult> stream) {
        List<CourseResult> result = stream.collect(Collectors.toList());
        return result.stream()
                .collect(Collectors.toMap(CourseResult::getPerson,
                        x -> getScore(getAverageScore(x, result))));
    }

    private Double getAverageScore(CourseResult course, List<CourseResult> list) {
        return course.getTaskResults()
                .values()
                .stream()
                .mapToDouble(value -> value)
                .sum() / getCountTasks(list);
    }

    private String getScore(double mark) {
        String result;
        if(mark >= 90)
            result = "A";
        else if(mark >= 83)
            result = "B";
        else if(mark >= 75)
            result = "C";
        else if(mark >= 68)
            result = "D";
        else if(mark >= 60)
            result = "E";
        else
            result = "F";
        return result;
    }

    public String easiestTask(Stream<CourseResult> stream) {
        return averageScoresPerTask(stream)
                .entrySet()
                .stream()
                .max(Comparator.comparingDouble(Map.Entry::getValue))
                .get()
                .getKey();
    }

    public Collector<CourseResult, Table, String> printableStringCollector() {
        return Collector.of(
                Table::new,
                Table::addCourseResult,
                (table, table2) -> {
                    throw new UnsupportedOperationException("Cannot be performed in parallel");
                },
                table -> {
                    StringBuilder builder = new StringBuilder();
                    table.createTable(builder);
                    return builder.toString();
                }
        );
    }
}


