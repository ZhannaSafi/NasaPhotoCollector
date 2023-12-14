import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите месяц (мм): ");
        int month = scanner.nextInt();
        System.out.print("Введите год (гггг): ");
        int year = scanner.nextInt();
        String[] nasa = new String[32];

        int daysInMonth = 0;
        boolean isLeapYear = (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;

        switch (month) {
            case 2: // февраль
                daysInMonth = isLeapYear ? 29 : 28;
                break;
            case 4: // апрель
            case 6: // июнь
            case 9: // сентябрь
            case 11: // ноябрь
                daysInMonth = 30;
                break;
            default:
                daysInMonth = 31;
                break;
        }

        for (int day = 1; day <= daysInMonth; day++) {
            String Str = day < 10 ? "0" + day : String.valueOf(day);
            Thread.sleep(1000);
            String photo = downloadWebPage("https://api.nasa.gov/planetary/apod?api_" +
                    "key=LUvkfAzepYMKulD1G6lracnRWkOTolzRYOGM0chY&date=" + year + "-" + month + "-" + Str);
            int urlBegin = photo.lastIndexOf("url");
            int urlEnd = photo.lastIndexOf("}");
            String url = photo.substring(urlBegin + 6, urlEnd - 1);

            int explanationBegin = photo.lastIndexOf("explanation");
            int explanationEnd = photo.lastIndexOf("hdurl");
            String explanation = photo.substring(explanationBegin + 13, explanationEnd - 2);
            System.out.println(day + " снимок: " + explanation);
            nasa[day - 1] = url;

            File file = new File("NasaPhoto" + day + ".jpg");
            String destinationFile = "/Users/macuser/Desktop/Проекты/NasaPhotoCollector/src/" + file;

            URL url1 = new URL(url);
            InputStream is = url1.openStream();
            OutputStream os = new FileOutputStream(destinationFile);

            byte[] b = new byte[2048];
            int length;

            while ((length = is.read(b)) != -1) {
                os.write(b, 0, length);
            }
            is.close();
            os.close();

            // Генерируем html страницу
            String folderPath = "NasaPhoto/";
            String[] fileNasaPhoto= new File(folderPath).list();
            try {
                String destinationFile2 = "/Users/macuser/Desktop/Проекты/NasaPhotoCollector/src/";
                PrintWriter writer = new PrintWriter(destinationFile2 + "Nasa.html");
                writer.println("<html><head><title>NASA Images</title></head><body>");

                assert fileNasaPhoto != null;

                for (int day1 = 1; day1 <= daysInMonth; day1++) {
                    String Nasa1 = "NasaPhoto" + day1 + ".jpg";
                    writer.println("<img src=\"" + Nasa1 + "\"/>");
                }

                writer.println("</body></html>");
                writer.close();
            } catch (IOException e) {
                System.out.println("Ошибка при генерации HTML-страницы: " + e.getMessage());
            }
        }
        System.out.println("Снимки сохранены. HTML страница сгенерирована.");
    }
    static String downloadWebPage(String url) throws IOException {

        StringBuilder result = new StringBuilder();
        String line;

        URLConnection urlConnection = new URL(url).openConnection();
        urlConnection.addRequestProperty("User-Agent", "Mozilla");
        urlConnection.setReadTimeout(5000);
        urlConnection.setConnectTimeout(5000);

        try (InputStream is = urlConnection.getInputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            while ((line = br.readLine()) != null) {
                result.append(line);
            }
        }
        return result.toString();
    }
}
