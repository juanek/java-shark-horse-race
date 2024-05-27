package ar.com.juanek;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

import static ar.com.juanek.Race.totalHorsesAtFinish;



@Slf4j
@Data
public class Horse {

    private static final int AVANCE_MAXIMO = 10;
    private static final int ESPERA_MAXIMA = 5;

    private String nombre;

    private int velocidad;

    private int resistencia;

    private int distance;

    public Horse(String nombre, int velocidad, int resistencia) {
        this.nombre = nombre;
        this.velocidad = velocidad;
        this.resistencia = resistencia;
    }

    public void correr(List<Horse> llegada, Semaphore semaphore, PowerZone potenciador) {
        try {

            log.info("run ...");

            Random random = new Random();
            while (distance < Race.RACE_DISTANCE && totalHorsesAtFinish <= 3) {

                int advance = velocidad * (random.nextInt(AVANCE_MAXIMO) + 1); // Avance aleatorio
                distance += advance;
                log.info("{} avanzó {} metros {} metros",nombre,advance,distance);

                // Verificar si el potenciador está activo y este caballo lo ha pisado
                if (distance >= potenciador.getDesde() && distance < potenciador.getHasta()) {
                    synchronized (potenciador) {
                        if (!potenciador.isBloqueado()) {
                            potenciador.setBloqueado(true); // Bloquear el potenciador para otros caballos
                            log.info("{} ha pisado el área potenciadora!",nombre);
                            try {
                                Thread.sleep(7000); // Espera 7 segundos
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            distance += 100; // Avanza 100 metros adicionales
                            log.info( "{} ha avanzado 100 metros extra después de pisar el potenciador.",nombre);
                            potenciador.setBloqueado(false); // Desbloquear el potenciador
                            potenciador.notify(); // Notificar que el potenciador está disponible
                            log.info("{} notify()",nombre);
                        } else {
                            try {
                                log.info(" antes de wait {}",nombre);
                                potenciador.wait(); // Esperar a que el potenciador esté disponible
                                log.info("{} despues de wait",nombre);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }


                int tiempoEspera = random.nextInt(ESPERA_MAXIMA) + 1 - resistencia;
                log.info("==> " + tiempoEspera);
                if (tiempoEspera > 0) {
                    try {
                        Thread.sleep(tiempoEspera * 1000L);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Interrupción del hilo durante la espera", e);
                    }
                }
                //log.info(nombre + " avanzó " + advance + " metros " + distance + " metros");

            }


            // Check if this horse has finished

            semaphore.acquire();
            totalHorsesAtFinish++;
            if (totalHorsesAtFinish > 3) {
                log.info(nombre + " ha llegado a la meta!");

            } else {
                log.info(nombre + " ha llegado a la meta! (" + totalHorsesAtFinish + "/3)");
                llegada.add(this);
            }
            semaphore.release();


        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public String toString() {
        return "Horse{" +
                "nombre='" + nombre + '\'' +
                ", velocidad=" + velocidad +
                ", resistencia=" + resistencia +
                '}';
    }
}

