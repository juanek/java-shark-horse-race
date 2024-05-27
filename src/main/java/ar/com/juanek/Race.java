package ar.com.juanek;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;


@Slf4j
public class Race {
    public static final int RACE_DISTANCE = 1000;
    public static int totalHorsesAtFinish = 0;

    public static void main(String[] args) {
        // Determinar si usar Threads tradicionales o Virtual Threads
        int numProcessors = Runtime.getRuntime().availableProcessors();
        log.info("numProcessors {}", numProcessors);

        boolean useVirtualThreads = numProcessors > 1; // Si hay más de un procesador, usar Virtual Threads
        int numHorses = (args[0] == null) ? 8 : Integer.parseInt(args[0]);
        List<Horse> horseList = crearHorses(numHorses);
        log.info("Horses Información:");
        for (Horse horse : horseList) {
            log.info(" {}", horse);
        }
        log.info("==> Iniciar!!");

        // Crear una lista compartida para almacenar la posición de llegada de cada caballo
        List<Horse> llegada = new ArrayList<>(3);

        Semaphore semaphore = new Semaphore(3); // Semaphore para controlar la finalización de la carrera
        PowerZone powerZone = new PowerZone();
        Thread potenciadorThread = new Thread(powerZone);
        potenciadorThread.setDaemon(false); // No es un daemon thread
        potenciadorThread.start();
        log.info("pontenciador");
        List<Thread> horseThreads = new ArrayList<>();

        // Iniciar hilos para cada caballo
        if (useVirtualThreads) {
            for (Horse horse : horseList) {
                Thread horseThread = Thread.startVirtualThread(() -> horse.correr(llegada, semaphore, powerZone));
                horseThreads.add(horseThread);
            }
        } else {
            for (Horse horse : horseList) {
                Thread horseThread = new Thread(() -> horse.correr(llegada, semaphore, powerZone));
                horseThreads.add(horseThread);
                horseThread.start();
            }
        }

        Report reporte = new Report(horseList, powerZone);
        Thread threadReporte = new Thread(reporte::mostrar);
        threadReporte.start();

        // Esperar a que todos los hilos hayan terminado
        try {
            for (Thread horseThread : horseThreads) {
                horseThread.join();
            }
            reporte.setTerminated(true);
            threadReporte.join();
            powerZone.setTerminated(true);
            potenciadorThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Mostrar resultados
        log.info("Resultados:");
        for (int i = 0; i < llegada.size(); i++) {
            Horse horse = llegada.get(i);
            log.info("Posición  {} :  {} ", (i + 1), horse.getNombre());
        }
    }

    private static List<Horse> crearHorses(int numHorses) {
        List<Horse> horses = new ArrayList<>();
        Random random = new Random();
        for (int i = 1; i <= numHorses; i++) {
            String name = "Horse " + i;
            int speed = random.nextInt(3) + 1;
            int endurance = random.nextInt(3) + 1;
            horses.add(new Horse(name, speed, endurance));
        }
        return horses;
    }
}
