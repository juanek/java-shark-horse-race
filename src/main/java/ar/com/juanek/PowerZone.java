package ar.com.juanek;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Data
public class PowerZone implements Runnable {

    private static final int POTENCIADOR_DISTANCIA = 50;
    private static final int POTENCIADOR_INTERVALO = 10000;

    private int desde;
    private int hasta;
    private boolean bloqueado;

    private boolean terminated;

    public PowerZone() {
        this.desde = -1;// Inicialmente fuera de la pista
        this.hasta = -1;
        this.bloqueado = false;
    }

    @Override
    public void run() {
        while (!terminated) {
            try {
                generarPosicion(); // Genera una nueva posición para el potenciador
                Thread.sleep(POTENCIADOR_INTERVALO); // El área se refresca cada 15 segundos

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void generarPosicion() {
        // Generar una posición aleatoria en la pista
        desde = new Random().nextInt(Race.RACE_DISTANCE - POTENCIADOR_DISTANCIA);
        hasta = desde + POTENCIADOR_DISTANCIA;
       log.info("¡Área potenciadora activada en la posición [{} , {}!",desde,hasta);
       log.info(" {}",LocalDateTime.now());
    }
}

