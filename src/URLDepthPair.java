import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLDepthPair {
    public static final String URL_PREFIX = "http://";
    public static final String URL_HREF = "<a href='";
    public static final int URL_HREF_LENGTH = URL_HREF.length();


    private String urlAddress;
    private int searchDepth;

//    Конструктор
    public URLDepthPair(String url, int i) {
        urlAddress = url;
        searchDepth = i;
    }
//    Возвращение глубины
    public int getDepth() {
        return searchDepth;
    }

// Возвращение url
    public String getURL() {
        return urlAddress;
    }

    //возвращение хост
    // MalformedURLException - адрес указан неверно или заданный в нём ресурс отсутствует
    public String getHost() {
        try {
            URL url = new URL(urlAddress);
            return url.getHost();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    //возвращение путь
    // MalformedURLException - адрес указан неверно или заданный в нём ресурс отсутствует
    public String getPath() {
        try {
            URL url = new URL(urlAddress);
            return url.getPath();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    // вывод url и грубины поиска
    public String toString() {
        return "URL - " + urlAddress + ". Depth - " + searchDepth;
    }

    // проверка на ссылочность
    public static String isLink(String link, String host) {
        int start = link.indexOf(URL_HREF);
        // если URL_HREF не найдено
        if (start == -1) {
            return "";
        }
        start += URL_HREF_LENGTH;
        String result = link.substring(start, link.indexOf("'", start));
        // если ссылка начинаетмя не с http
        if (result.startsWith("https")) {
            return "";
        } else if (!result.startsWith("http") ){
            return "http://" + host + "/" + result;
        }
        return result;
    }
}
