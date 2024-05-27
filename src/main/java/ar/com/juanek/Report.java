package ar.com.juanek;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class Report {
    private final List<Horse> horses;
    private final PowerZone potenciador;
    @Setter
    private boolean terminated;

    public Report(List<Horse> horses,PowerZone potenciador) {
        this.horses = horses;
        this.potenciador = potenciador;
    }

    public void mostrar() {


        while (!terminated){
           log.info("------------------------------------------");
            for (Horse horse : horses) {
               log.info(horse+" "+horse.getDistance()+" metros");
            }
           log.info("Area pontenciadora [ "+potenciador.getDesde()+" , "+potenciador.getHasta()+" ]");
           log.info("------------------------------------------");
            try {
                Thread.sleep(1000); // Reportar cada segundo
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


       log.info("Fin reporte.");
    }

}
