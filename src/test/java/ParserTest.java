import com.code.challenge.exception.APIException;
import com.code.challenge.model.Problem;
import com.code.challenge.model.Triplet;
import com.code.challenge.service.Parser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParserTest {

    static File twoProblemSingleTripletFile;
    static File invalidContentInputFile;
    static File noEuroSignInputFile;

    @BeforeAll
    static void initialize() {
        twoProblemSingleTripletFile = new File(
                PackerTest.class
                        .getClassLoader()
                        .getResource("two_problems_single_triplet.txt")
                        .getFile());
        invalidContentInputFile = new File(
                PackerTest.class
                        .getClassLoader()
                        .getResource("invalid_content.txt")
                        .getFile());
        noEuroSignInputFile = new File(
                PackerTest.class
                        .getClassLoader()
                        .getResource("cost_value_no_euro_sign.txt")
                        .getFile());
    }

    @Test
    void onlyOnceInstanceCreatedWhenGetInstance(){

        Parser parserFirstInstance =Parser.getInstance();
        Parser parserSecondInstance =Parser.getInstance();

        assertEquals(parserFirstInstance,parserSecondInstance);
    }

    @Test
    void returnListOfProblemWhenMultilineInputFileParsed(){

        int expectedListSize = 2;
        int expectedTripletInEachProblem =1;
        int firstProblemMaxCapacity=81;

        Problem firstProblem= new Problem(firstProblemMaxCapacity);
        firstProblem.getTriplets().add(new Triplet(1,53.38f,45));

        int secondProblemMaxCapacity=8;
        Problem secondProblem= new Problem(secondProblemMaxCapacity);
        secondProblem.getTriplets().add(new Triplet(1,15.3f,34));


        List<Problem> systemUnderTest = Parser.getInstance().parse(twoProblemSingleTripletFile.getAbsolutePath());
        int parsedMaxCapacityInFirstProblem = systemUnderTest.get(0).getMaxCapacity();
        List<Triplet> parsedTripletsInFirstProblem = systemUnderTest.get(0).getTriplets();
        int parsedMaxCapacityInSecondProblem = systemUnderTest.get(1).getMaxCapacity();
        List<Triplet> parsedTripletsInSecondProblem = systemUnderTest.get(1).getTriplets();



        assertEquals(expectedListSize,systemUnderTest.size());
        assertEquals(parsedMaxCapacityInFirstProblem,firstProblemMaxCapacity);
        assertEquals(parsedMaxCapacityInSecondProblem,secondProblemMaxCapacity);
        assertEquals(parsedTripletsInFirstProblem.size(),expectedTripletInEachProblem);
        assertEquals(parsedTripletsInSecondProblem.size(),expectedTripletInEachProblem);
        assertEquals(parsedTripletsInFirstProblem,firstProblem.getTriplets());
        assertEquals(parsedTripletsInSecondProblem,secondProblem.getTriplets());
    }

    @Test
    public void throwApiExceptionWhenNotExistingFileInputPassed(){
        String expectedExceptionMessage = "Invalid parameter: file not exists";


        APIException exception = assertThrows(APIException.class,
                () -> Parser.getInstance().parse("/not/existing/input.file"));

        assertEquals(expectedExceptionMessage, expectedExceptionMessage);
    }

    @Test
    public void throwExceptionWhenNullInputFilePassed() {
        String expectedExceptionMessage = "Invalid parameter: File path required";

        APIException exception = assertThrows(APIException.class,
                () ->Parser.getInstance().parse(null));

        assertEquals(expectedExceptionMessage,exception.getMessage());
    }

    @Test
    public void throwExceptionWhenParsingInvalidLine(){
        String expectedExceptionMessage ="(9,89.95,\u20AC78) : 75";

        APIException exception = assertThrows(APIException.class,
                () ->Parser.getInstance().parse(invalidContentInputFile.getAbsolutePath()));

        assertTrue(exception.getMessage().contains(expectedExceptionMessage));
    }

    @Test
    public void throwExceptionWhenParsingCostWithNoEuroSign(){
        String expectedExceptionMessage = "81 : (1,53.38,45)";

        APIException exception = assertThrows(APIException.class,
                () ->Parser.getInstance().parse(noEuroSignInputFile.getAbsolutePath()));

        assertTrue(exception.getMessage().contains(expectedExceptionMessage));
    }
}
