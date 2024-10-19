package retraite.simulateur;

import org.springframework.stereotype.Service;

@Service
public class ServiceRetraite {
    public double calculerEpargneRetraite() {
        return 15000 * 0.5 *1;
    }
}
