import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;

public class CrawlerTask implements Runnable {
    private URLPool pool;
    public URLDepthPair pair;

    public CrawlerTask(URLPool p) {
        pool = p;
    }

    @Override
    public void run() {

        URLDepthPair newPair;
        int depth;

        // пока в пуле не останется пар url-depth
        while (true){
            pair = pool.getPair();
            depth = pair.getDepth();
            // ссылки на текущей страницы
            LinkedList<String> currentLinksList;
            // поиск всех ссылок на странице и добавление в каррент
            currentLinksList = CrawlerTask.getAllLinks(pair);

            // забег по всем элементам списка каррент
            for (String myURL : currentLinksList) {
                // в необработанные ссылки
                newPair = new URLDepthPair(myURL, depth + 1);
                pool.addPair(newPair);
            }
        }
    }

    // поиск всех ссылок на странице
    public static LinkedList<String> getAllLinks(URLDepthPair currentDepthPair) {
        int port = 80; // порт по которому мы будем подключаться (для http)
        // создание списка для зранения найденных ссылок
        LinkedList<String> foundURLs = new LinkedList<>();
        // создание сокета
        Socket socket;

        // инициализируем сокет
        try {
            socket = new Socket(currentDepthPair.getHost(), port);
        } catch (UnknownHostException e) {
            // исключение "неизвестный хост"
            System.err.println("UnknownHostException " + e.getMessage());
            return foundURLs;
        } catch (IOException e) {
            // исключение ввода/вывода
            System.err.println("IOException  1" + e.getMessage());
            return foundURLs;
        }

        // устанавка времени ожидания сокета (сколько нужно ждать передачи данных с другой стороны)
        // SocketException = возникновение ошибки на сокете
        try {
            socket.setSoTimeout(1000); // 1 секунда
        } catch (SocketException e) {
            System.err.println("SocketException " + e.getMessage());
            return foundURLs;
        }

        // getOutputStream - отправка данных на другую сторону соединения
        OutputStream outputStream;
        try {
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            // исключение ввода/вывода
            System.err.println("IOException " + e.getMessage());
            return foundURLs;
        }

        // создаём printWriter
        // autoFlush - true = очистка буфера после каждого метода println
        PrintWriter printWriter = new PrintWriter(outputStream, true);
        // отправление на сервер запроса
        printWriter.println("GET " + currentDepthPair.getPath() + " HTTP/1.1\r"); // запрашиваем страницу
        printWriter.println("Host: " + currentDepthPair.getHost()+"\r"); // запрашиваем страницу
        printWriter.println("Connection: close\r");
        printWriter.println("\r");

        //inputStreamReader - для получения информации с другого конца соединения
        InputStreamReader in;
        try {
            in = new InputStreamReader(socket.getInputStream());
        } catch (IOException e) {
            // исключение ввода/вывода
            System.err.println("IOException " + e.getMessage());
            return foundURLs;
        }
        //BufferedReader для чтения целых строк
        BufferedReader reader = new BufferedReader(in);
        // перебираем строки
        while (true) {
            String line;
            try {
                line = reader.readLine();
            } catch (IOException e) {
                // исключение ввода/вывода
                System.err.println("IOException " + e.getMessage());
                return foundURLs;
            }
            // больше строк нет
            if (line == null) {
                break;
            }
            // поиск url
            String newURL = URLDepthPair.isLink(line, currentDepthPair.getHost());
            //запоминаем url, если подошла
            if (!"".equals(newURL)) {
                foundURLs.add(newURL);
            }
        }
        // закрытие сокет
        try {
            socket.close();
        } catch (IOException e) {
            // исключение ввода/вывода
            System.err.println("IOException " + e.getMessage());
            return foundURLs;
        }
        return foundURLs;
    }

}
