import com.code.challenge.exception.APIException;
import com.code.challenge.packer.Packer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PackerTest {

    static File multiLineInputFile;
    static File singleLineInputFile;

    @BeforeAll
    static void initialize() {
        multiLineInputFile = new File(
                PackerTest.class
                        .getClassLoader()
                        .getResource("multiline_problem.txt")
                        .getFile());
        singleLineInputFile = new File(
                PackerTest.class
                        .getClassLoader()
                        .getResource("single_line_problem.txt")
                        .getFile());
    }

    @Test
    public void throwExceptionWhenNullInputFilePassed() {
        String expectedExceptionMessage = "Invalid parameter: File path required";

        APIException exception = assertThrows(APIException.class, () ->Packer.pack(null));

        assertEquals(expectedExceptionMessage,exception.getMessage());
    }

    @Test
    public void throwApiExceptionWhenNotExistingFileInputPassed() {
        String expectedExceptionMessage = "Invalid parameter: file not exists";


        APIException exception = assertThrows(APIException.class, () ->Packer.pack("/not/existing/input.file"));

        assertEquals(expectedExceptionMessage, expectedExceptionMessage);
    }

    @Test
    public void returnOptimalStringResultsWhenValidSinglelineInputFilePassed() {
        String expectedResult = "7, 2";


        String result = Packer.pack(singleLineInputFile.getAbsolutePath());

        assertEquals(expectedResult, result);
    }

    @Test
    public void returnOptimalStringResultsWhenValidMultilineInputFilePassed() {
        String expectedResult = (new StringBuilder())
                .append("4")
                .append(System.lineSeparator())
                .append("-")
                .append(System.lineSeparator())
                .append("7, 2")
                .append(System.lineSeparator())
                .append("8, 9")
                .toString();


        String result = Packer.pack(multiLineInputFile.getAbsolutePath());

        assertEquals(expectedResult, result);
    }
}
