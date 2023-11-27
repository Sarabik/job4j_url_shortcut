package ru.job4j.util;
import java.security.SecureRandom;

/* Generates random sequence of letters and numbers */
public class RandomGenerator {
    public static String generateSequence(int len) {
        SecureRandom random = new SecureRandom();
        return random.ints(48, 123)
                .filter(i -> Character.isAlphabetic(i) || Character.isDigit(i))
                .limit(len)
                .collect(StringBuilder::new,
                        StringBuilder::appendCodePoint,
                        StringBuilder::append)
                .toString();
    }
}
