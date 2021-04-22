import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;

public class Client {
    public static final String PATH_TO_PROPERTIES = "src/main/resources/param.properties";
    public static int portParam;
    public static String hostAddress;
    public static SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");


    public static void main(String[] args) throws IOException {

        //Читаем файл с параметрами и присваиваем их переменным.
        FileInputStream fileInputStream;
        Properties prop = new Properties();
        try {
            fileInputStream = new FileInputStream(PATH_TO_PROPERTIES);
            prop.load(fileInputStream);
            portParam = Integer.parseInt(prop.getProperty("port"));
            hostAddress = prop.getProperty("host");
        } catch (IOException e) {
            System.out.println("Ошибка в программе: файл " + PATH_TO_PROPERTIES + " не обнаружено");
            e.printStackTrace();
        }

        //Подключаемся к серверу
        Socket soc = null;
        try {
            soc = new Socket(hostAddress, portParam);
        } catch (IOException e) {
            System.out.println("Произошла ошибка, ваш сеанс будет завершен");
            System.exit(0);
        }
        System.out.println("Вы подключены.");

        //Для отправки сообщений на сервер
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(soc.getOutputStream()));
        //Для чтения сообщений от сервера
        BufferedReader reader = new BufferedReader(new InputStreamReader(soc.getInputStream()));
        String name;
        String massage;
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите свое имя: ");
        name = scanner.nextLine();

        //Цикл, что бы можно было много сообщений отправлять
        while (true) {
            System.out.print("Введите сообщение: ");
            massage = scanner.nextLine();
            //Формируем JSON для отправки на сервер
            JSONObject massageObject = new JSONObject();
            massageObject.put("Request", new JSONObject()
                    .put("User", new JSONObject().put("Login", name))
                    .put("Massage", new JSONObject()
                            .put("Body", massage)
                            .put("Timestamp", formatForDateNow.format(new Date()))));

            //Отправляем на сервер сообщение
            writer.write(massageObject + "\n");
            writer.flush();
            //Выходим если ввели \exit
            if (massage.contains("\\exit")) {
                break;
            }
            //Получаем ответ от сервера
            massage = reader.readLine();
            System.out.println("Сервер: " + massage);
        }

        System.out.println("Мы отключились от сервера.");
    }

}
