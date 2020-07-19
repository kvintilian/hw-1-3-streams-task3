import java.util.*;

public class Main {

    public static void main(String[] args) {

        List<People> peoples = generatePeoples(12_000_000L);

//        processStream(peoples);
        processParallelStream(peoples);

        // Важно: запускать по одному за запуск прграммы, иначе результаты получаются такие
        // что parallelStream после прогона по stream выполнятся в 2 раза(!) быстрее
        // даже если перед каждым жейтсвие генерировать новую базу людей
        // если вызовы поменять местами (processParallelStream первым),
        // то затраченное время на оба процесса увеличивается в среднем на 0,06 секунды
        // Для расчетов будем использовать только запуск с одним процессом поочередно коментирую их!
    }

    public static List<People> generatePeoples(Long maxPeoples) {
        List<String> names = Arrays.asList("Иванов", "Петров", "Сидоров", "Пыжалов", "Бояров", "Ерофеев", "Кулибин",
                "Толстой", "Ямилова", "Курпатова", "Дятлова", "Кимаск", "Дудинова", "Гагарина", "Короткина", "Ткачёва");
        List<People> peoples = new ArrayList<>();
        for (int i = 0; i < maxPeoples; i++) {
            peoples.add(new People(names.get(
                    new Random().nextInt(names.size())),
                    new Random().nextInt(100),
                    Sex.randomSex()));
        }
        return peoples;
    }

    public static void processStream(List<People> peoples) {
        System.out.println("Для stream():");
        long startTime = System.nanoTime();

        System.out.println("Военнообязанных мужчин: "
                + peoples.stream()
                .filter(value -> value.getSex() == Sex.MAN)
                .filter(value -> 18 <= value.getAge() && value.getAge() < 35)
                .count());

        OptionalDouble aDouble = peoples.stream()
                .filter(value -> value.getSex() == Sex.MAN)
                .mapToInt(People::getAge)
                .average();
        if (aDouble.isPresent()) {
            System.out.println("Средний возраст мужчин: " + aDouble.getAsDouble());
        } else {
            System.out.println("Мужчин в выборке нет");
        }

        System.out.println("Количество потенциальных работоспособных людей: "
                + peoples.stream()
                .filter(value -> (value.getAge() >= 18) && (value.getSex() == Sex.MAN ? value.getAge() < 65 : value.getAge() < 60))
                .count());

        long stopTime = System.nanoTime();
        double processTime = (double) (stopTime - startTime) / 1_000_000_000.0;
        System.out.println("Process time: " + processTime + " s");
    }

    public static void processParallelStream(List<People> peoples) {
        System.out.println("Для parallelStream():");
        long startTime = System.nanoTime();

        System.out.println("Военнообязанных мужчин: "
                + peoples.parallelStream()
                .filter(value -> value.getSex() == Sex.MAN)
                .filter(value -> 18 <= value.getAge() && value.getAge() < 35)
                .count());

        OptionalDouble aDouble = peoples.parallelStream()
                .filter(value -> value.getSex() == Sex.MAN)
                .mapToInt(People::getAge)
                .average();
        if (aDouble.isPresent()) {
            System.out.println("Средний возраст мужчин: " + aDouble.getAsDouble());
        } else {
            System.out.println("Мужчин в выборке нет");
        }

        System.out.println("Количество потенциальных работоспособных людей: "
                + peoples.parallelStream()
                .filter(value -> (value.getAge() >= 18) && (value.getSex() == Sex.MAN ? value.getAge() < 65 : value.getAge() < 60))
                .count());

        long stopTime = System.nanoTime();
        double processTime = (double) (stopTime - startTime) / 1_000_000_000.0;
        System.out.println("Process time: " + processTime + " s");
    }

    /*
       РЕЗУЛЬТАТЫ

       Для stream():
        Военнообязанных мужчин: 1022331
        Средний возраст мужчин: 49.47587607541943
        Количество потенциальных работоспособных людей: 5341306
        Process time: 0.456290947 s

       Для parallelStream():
        Военнообязанных мужчин: 1018712
        Средний возраст мужчин: 49.51913772700075
        Количество потенциальных работоспособных людей: 5336702
        Process time: 0.139606006 s

       Оценка эффективности parallelStream() перед stream() при различном количестве:
        Процессор Intel(R) Core(TM) i9-9880H CPU @ 2.30GHz

       Количество  | Время stream() | parallelStream() | Результат
       ------------------------------------------------------------------------
               100 |  0.031166218   |  0.035083992     | медленнее на 12.571 %
             1_000 |  0.022159785   |  0.028068731     | медленнее на 26.665 %
            10_000 |  0.032815325   |  0.02773522      | быстрее на 15.481 %
           100_000 |  0.028828541   |  0.035148663     | медленнее на 21.923 %
         1_000_000 |  0.070576205   |  0.073165662     | медленнее на 3.669 %
        10_000_000 |  0.384716492   |  0.167886637     | быстрее на 56,362 %
       100_000_000 |  3.322397219   |  0.57628942      | быстрее на 82.654 %

     */
}
