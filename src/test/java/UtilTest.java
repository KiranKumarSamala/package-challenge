import com.code.challenge.utility.Util;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UtilTest {
    @Test
    public void returnDefaultValueWhenStringIsEmpty() {
        String expectedResult = "default";
        String result = Util.defaultIfEmpty("", expectedResult);

        assertEquals(expectedResult, result);
    }

    @Test
    public void returnStringValueWhenStringIsNotEmpty() {
        String expectedResult = "Value";
        String result = Util.defaultIfEmpty(expectedResult, "default");

        assertEquals(expectedResult, result);
    }

    @Test
    public void returnFloatScaledTo2PrecisionWhenMoreThan2DecimalPoints() {
        float expectedResult = 14.57f;
        float result = Util.round(14.5699999999f);

        assertEquals(expectedResult, result);
    }
}
