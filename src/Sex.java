import java.util.List;
import java.util.Random;

public enum Sex {
    MAN,
    WOMEN;

    private static final List<Sex> VALUES = List.of(values());
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    public static Sex randomSex()  {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }
}
