package be.pxl.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class RandomGenerator {

    private static final Random random = new Random();

    public static String randomString() {
        return randomString(1, 20);
    }

    public static String randomString(int maxLength) {
        return randomString(1, maxLength);
    }

    public static String randomString(int minLength, int maxLength) {
        int length = minLength + random.nextInt(maxLength - minLength + 1);
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, length);
    }

    public static int randomInt(int max) {
        return randomInt(1, max);
    }

    public static int randomInt(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    public static List<Long> randomIntList(int count, int min, int max) {
        List<Long> ids = new ArrayList<>();
        while (ids.size() < count) {
            long number = randomInt(min, max);
            if (!ids.contains(number)) {
                ids.add(number);
            }
        }
        return ids.stream().sorted().toList();
    }

    public static double randomDouble() {
        return randomDouble(1, 100);
    }

    public static double randomDouble(double max) {
        return randomDouble(1, max);
    }

    public static double randomDouble(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }

    public static <T extends Enum<T>> T randomFromEnum(Class<T> enumClass) {
        T[] enumConstants = enumClass.getEnumConstants();
        return enumConstants[random.nextInt(enumConstants.length)];
    }
}
