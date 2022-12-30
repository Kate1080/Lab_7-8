public class Crawler {

    public static void main(String[] args) {
        int depth = 0;
        int numOfTreads = 4;

        // ошибки ввода
        if (args.length != 3) {
            // число аргументов больше/меньше трёх
            System.out.println("usage: java Crawler <URL><depth><number of threads>");
            System.exit(1);
        } else {
            // проверка: глубина и число потоков цифра
            try {
                depth = Integer.parseInt(args[1]);
                numOfTreads = Integer.parseInt(args[2]);
            } catch (NumberFormatException notNumber) {
                System.out.println("usage: java Crawler <URL><depth><number of threads>");
                System.exit(1);
            }
        }

        // создание начальной планы
        URLDepthPair currentURLDepthPair = new URLDepthPair(args[0], 0);
        //экземпляр пула
        URLPool pool = new URLPool(depth);
        // начальная пара добавляется в пул
        pool.addPair(currentURLDepthPair);
        // создание указанного количества потоков
        for (int i = 0; i < numOfTreads; i++){
            CrawlerTask task = new CrawlerTask(pool);
            Thread thread = new Thread(task);
            thread.start();
        }
// ожидание выхода из программы
        while (pool.threadsCount() != numOfTreads) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.err.println("InterruptedException " + e.getMessage());
            }
        }
        pool.getSites();
        System.exit(0);
    }
}
