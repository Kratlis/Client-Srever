package work_with_collection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import story.Jail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.Stack;

import static java.lang.System.exit;

/**
 * @author katya
 */
public class CollectionManager {
    private Stack<Jail> jailStack;
    protected Date initDate;
    protected FileManager fileWorker;

    /**
     *Конструктор в входными параметрами.
     * @param collectionFile - файл, из которого будет считываться коллекция.
     * @throws FileNotFoundException Ошибка пояаляется, когда файл с коллекцией не был найден.
     * @see FileManager
     * @see Date
     */
    public CollectionManager(File collectionFile) throws FileNotFoundException {
        fileWorker = new FileManager(collectionFile);
        while (fileWorker.readFile().equals("")){
            System.out.println("Данные в файле записаны неверно. Введите имя файла.");
            fileWorker = new FileManager(new File(new Scanner(System.in).next()));
        }
        while (!checkSource(fileWorker.readFile())){
            System.out.println("Данные в файле записаны неверно. Введите имя файла.");
            fileWorker = new FileManager(new File(new Scanner(System.in).next()));
        }
        this.initDate = new Date();
        jailStack = createStack(fileWorker.readFile());
    }

    /**
     * Конструктор без аргументов.
     */
    CollectionManager() {jailStack = new Stack<>();}

    /**
     * Метод добавляет в коллекцию элемент.
     * @param jailInString - элемент в формате json.
     * @see Stack
     * @see Gson
     */
    void add(String jailInString) {
        try {
            jailStack.push(createJail(jailInString));
            if (!(jailStack.empty())){
                System.out.println("Элемент " + jailStack.peek() + " добавлен");
            }
            else {
                System.out.println("Коллекция пуста.");
            }
        } catch (JsonSyntaxException e) {
            System.out.println("Элемент задан неверно.");
        }
    }

    /**
     * Метод выводит все элементы коллекции.
     * @see Stack
     * @see Gson
     * @see GsonBuilder
     */
    public void show(){
        if (jailStack.empty()){
            System.out.println("Коллекция пуста.");
        } else {
            System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(jailStack));
        }
    }

    /**
     * Метод добавляет в коллекцию элементы из данного файла.
     * @param fileName:(java.io.File) - файл, в котором записаны элементы коллекции в формате json, которые надо добавить в текущую коллекцию.
     * @see File
     * @see FileManager
     * @see Stack
     */
    public void doImport(String fileName) throws FileNotFoundException {
        if (checkSource(new FileManager(new File(fileName)).readFile())){
            Stack<Jail> newStack = createStack(new FileManager(new File(fileName)).readFile());
            if (newStack == null){
                exit(0);
            }
            if (!(newStack.empty())){
                while (!(newStack.empty())) {
                    Jail f = newStack.pop();
                    jailStack.push(f);
                }
                System.out.println("Элементы добавлены.");
            } else System.out.println("Ничего не добавлено: импортируемая коллекция пуста.");
        } else System.out.println("Ничего не добавлено: элементы заданы неверно");
    }

    /**
     * Метод записывает элемент в коллекцию на заданное место.
     * @param place - индек, под которым элемент будет записан в коллекцию.
     * @param str - элемент в формате json.
     * @see Integer
     * @see Stack
     */
    public void insert(String place, String str){
        int index = Integer.parseInt(place);
        Stack<Jail> stack2 = new Stack<>();
        if ((index < jailStack.size())&&(index >= 0)) {
            for (int i = 0; i < index; i++) {
                stack2.add(jailStack.pop());
            }
            jailStack.add(createJail(str));
            System.out.println(jailStack.peek() + " вставлен на место " + index);
            while (stack2.size() != 0){
                jailStack.add(stack2.pop());
            }
        } else System.out.println("Неверный индекс.");
    }

    /**
     * Метод выводит информацию о коллекции.
     */
    public void info() {
            System.out.println("Коллекция имеет тип \"Stack\" и содержит объекты класса \"story.Jail\".");
            System.out.println("Дата инициализации: " + initDate);
            System.out.println("Колличество элементов в коллекции - " + jailStack.size() + ".");
        }

    /**
     * Метод удаляет данный элемент из коллекции.
     * @param str - элемент в формате json.
     * @see Stack
     */
    public void remove(String str) {
            Jail jailForRemove = createJail(str);
            if (jailStack.search(jailForRemove) < 0) {
                System.out.println("Такого элемента в коллекции нет.");
            } else {
                Stack<Jail> jailStack2 = new Stack<>();
                for (int i = 0; i < jailStack.search(jailForRemove); i++) {
                    jailStack2.push(jailStack.pop());
                }
                System.out.println(jailStack.pop() + " удален.");
                for (int i = 0; i < jailStack.search(jailForRemove); i++) {
                    jailStack.add(jailStack2.pop());
                }
            }
    }

    /**
     * Метод перезаписывает коллекцию, используя данные из файла.
     * @param fileName - имя файла, в котором записана коллекция в формате json.
     * @see File
     * @see FileManager
     * @see CollectionManager#createStack(String)
     */
    public void load(String fileName) throws FileNotFoundException, NullPointerException {
        jailStack = createStack(new FileManager(new File(fileName)).readFile());
    }

    /**
     *Метод сохраняет текущую коллекцию в файл с уникальным названием.
     * @throws IOException Ошибка пояаляется, когда файл с коллекцией не был найден или не мог быть использован.
     * @see File
     * @see SimpleDateFormat
     */
    public void save() throws IOException {
//        if (fileWorker != null) {
//            File newFile = new File("saveFile" + new SimpleDateFormat("yyyy.MM.dd.hh.mm.ss").format(new Date()) + ".txt");
//            if (newFile.createNewFile()) {
//                new FileManager(newFile).writeToFile(new GsonBuilder().setPrettyPrinting().create().toJson(jailStack));
//                System.out.println("Коллекция сохранена в файл " + newFile.getAbsolutePath());
//            } else throw new FileNotFoundException();
//        }
        try{
            fileWorker.writeToFile(new GsonBuilder().setPrettyPrinting().create().toJson(jailStack));
        } catch (FileNotFoundException e){
            System.out.println("Файл "+fileWorker+" не найден. Для сохранения коллекции будет создан новый файл.");
            File newFile = new File("saveFile" + new SimpleDateFormat("yyyy.MM.dd.hh.mm.ss").format(new Date()) + ".txt");
            if (newFile.createNewFile()) {
                new FileManager(newFile).writeToFile(new GsonBuilder().setPrettyPrinting().create().toJson(jailStack));
                System.out.println("Коллекция сохранена в файл " + newFile.getAbsolutePath());
            } else throw new FileNotFoundException();
        }
    }

    /**
     * Завершает работу с коллекцией элементов, сохраняя ее в фаил из которого она была считана.
     * Если сохранение в исходный фаил не удалось, то сохранение происходит в фаил с уникальным названием.
     * @see FileManager#writeToFile(String)
     * @see CollectionManager#save()
     */
    public void finishWork() {
            try{
                fileWorker.writeToFile(new GsonBuilder().setPrettyPrinting().create().toJson(jailStack));
            } catch (IOException e) {
                try {
                    save();
                }catch (IOException ex){
                    System.out.println("Сохранение коллекции не удалось.");
                }
            }
    }

    /**
     *
     * @param jails - существующая коллекция
     * @return String -
     */
    public static String createString(Stack<Jail> jails) {
        Gson gson = new Gson();
        return gson.toJson(jails);
    }

    /**
     *
     * @param str
     * @return
     * @throws JsonSyntaxException
     */
    public static Stack<Jail> createStack(String str) throws JsonSyntaxException {
        Gson gson = new Gson();
        Type type = new TypeToken<Stack<Jail>>(){}.getType();
        return gson.fromJson(str, type);
    }

    public static Jail createJail(String str) throws JsonSyntaxException {
        Jail newJail = new Gson().fromJson(str, Jail.class);
        if (newJail.getName() == null){
            newJail.setName("NoName");
        }
        return newJail;
    }

    public static boolean checkSource(String source) {
        try {
            Stack<Jail> testStack = createStack(source);
            return true;
        } catch (JsonSyntaxException e) {
            System.out.println("Содержимое файла не удовлетворяет формату JSON.");
            return false;
        }
    }
}