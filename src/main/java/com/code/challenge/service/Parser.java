package com.code.challenge.service;

import com.code.challenge.exception.APIException;
import com.code.challenge.model.Problem;
import com.code.challenge.model.Triplet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Parse packing input file.
 */
public class Parser {
    private final String tripletRegex = "(?<id>\\d+),(?<weight>\\d+\\.\\d+),\u20AC(?<cost>\\d+)";
    private final String lineRegex = String.format("(\\d+) : ((\\(%s)\\s*\\)+)", tripletRegex);
    private Pattern linePattern = Pattern.compile(lineRegex);
    private Pattern tripletPattern = Pattern.compile(tripletRegex);

    private static Parser parser;

    private Parser() {
    }

    /**
     * Get single parser instance each time requested.
     * @return Parser instance
     */
    public static Parser getInstance() {
        if (parser == null) {
            synchronized (Parser.class) {
                if (parser == null)
                    parser = new Parser();
            }
        }
        return parser;
    }

    /**
     * Parse given input file with problem definition to A list of Problems
     * Problem instances correspond to each line defined in the file.
     *
     * @param filePath path to input problem file.
     * @return List of problem corresponding to what defined in input file.
     * @throws APIException when file content can not be parsed.
     */
    public List<Problem> parse(String filePath) throws APIException {
        this.validateFilePath(filePath);

        List<Problem> problems;
        try {
            problems = Files.lines(Paths.get(filePath))
                    .map(this::lineToProblem)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new APIException(e);
        }

        return problems;
    }

    /**
     * Validate if given file exists.
     * @param filePath Path to input file.
     * @throws APIException when filePath is null or not exists.
     */
    private void validateFilePath(String filePath) throws APIException {

        Optional
                .ofNullable(filePath)
                .orElseThrow(() -> new APIException("Invalid parameter: File path required"));

        boolean fileExists = Paths.get(filePath).toFile().exists();

        if (!fileExists)
            throw new APIException("Invalid parameter: file not exists");
    }

    /**
     * Convert a string line from file to Problem object
     * @param line a line read from input file.
     * @return Problem object corresponding to given line.
     */
    private Problem lineToProblem(String line) {

        if (!this.validateProblemInString(line))
            throw new APIException(String.format("Can not parse line: %s ", line));

        int capacity = getCapacityFromStringProblem(line);
        List<Triplet> triplets = getTuplesFromStringProblem(line);

        return new Problem(capacity, triplets);
    }

    /**
     * Validate a line read from input file with pattern expected.
     * @param line a line read from input file.
     * @return true if line matches with expected pattern, otherwise false.
     */
    private boolean validateProblemInString(String line) {
        Matcher linetMatcher = linePattern.matcher(line);

        if (!linetMatcher.find())
            return false;
        return true;
    }

    /**
     * Parse Capacity value in a line read from a file.
     * @param line a line read from input file.
     * @return integer value parsed as capacity.
     */
    private int getCapacityFromStringProblem(String line) {
        String[] splittedCapacityFromItems = line.split(" : ");
        return Integer.parseInt(splittedCapacityFromItems[0]);
    }

    /**
     * Parse Triplets in a line read from a file.
     * @param line a line read from input file.
     * @return list of triplets corresponding to the line read from a file.
     */
    private List<Triplet> getTuplesFromStringProblem(String line) {
        String stringTriplets = line.split(":")[1];
        Matcher tripletMatcher = tripletPattern.matcher(stringTriplets);
        List<Triplet> triplets = new ArrayList<>();
        while (tripletMatcher.find()) {
            Triplet triplet = new Triplet(
                    Integer.parseInt(tripletMatcher.group("id")),
                    Float.parseFloat(tripletMatcher.group("weight")),
                    Integer.parseInt(tripletMatcher.group("cost")));
            triplets.add(triplet);
        }
        return triplets;
    }
}
