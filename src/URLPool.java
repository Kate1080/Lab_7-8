import java.util.ArrayList;
import java.util.LinkedList;
// Хранит список всех URL-адресов для поиска, а также глубину поиска
public class URLPool {

    private int maxDepth;
    private LinkedList<URLDepthPair> checkedURL;
    private LinkedList<URLDepthPair> uncheckedURL;
    ArrayList<String> seenURL; //
    private int waiting = 0; // сколько потоков в режиме ожидания


//    конструктор
    public URLPool(int depth) {
        checkedURL = new LinkedList<>();
        uncheckedURL = new LinkedList<>();
        seenURL = new ArrayList<>();
        maxDepth = depth;
    }

    // проверка размера
    public synchronized int size(){
        return uncheckedURL.size();
    }

    // проверка количества ожидающих потоков
    public synchronized int threadsCount(){
        return waiting;
    }


    //для получения пары
    public synchronized URLDepthPair getPair() {
        // потоку нечего делать
        if (uncheckedURL.size() == 0){
            waiting ++;
            try {
                wait();
            } catch (InterruptedException e) {
                System.err.println("InterruptedException" + e.getMessage());
            }
            waiting -= 1; // уменьшаем количество ожидающих потоков
        }

        return uncheckedURL.removeFirst();
    }

    // добавление пары
    public synchronized void addPair(URLDepthPair pair) {
        String pairURL = pair.getURL();
        // ссылочку еще не проверяли
        if (!seenURL.contains(pairURL)) {
            seenURL.add(pairURL);

            // проверка глубины прыжка
            if (pair.getDepth() < maxDepth) {
                uncheckedURL.add(pair); // добавляем пару в список необработанных сайтов
                notify(); // будим поток - появилась работа
            }
            checkedURL.add(pair); // добавляем пару в список обработанных сайтов
        }
    }
    public synchronized void getSites() {
        System.out.println("Result:");
        for (URLDepthPair urlDepthPair : checkedURL) {
            System.out.println(urlDepthPair.toString());
        }
    }
}
