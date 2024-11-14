package retraite.simulateur;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.Date;

@Service
public class ServiceRetraite {
// DONE max 1 pour la fraction des trimestres
// DONE décote du taux

    //TODO Rajouter date de retraite souhaitée
    // TODO taux plein automatique à 67 ans
    // TODO surcote (trimestres travaillés après l'age de départ à la retraite)
    // TODO enfants
    // TODO Si vous avez eu au moins 3 enfants, le montant de votre pension de retraite de l'Assurance retraite est majoré de 10%
    // TODO gérer exceptions et valeurs manquantes
    public double calculerEpargneRetraite(Adherent adherent) {
        int nbTrimestresManquants = calculerTrimestresManquants(adherent.getDateNaissance(), adherent.getTrimValide());
        double taux = calculerTaux(nbTrimestresManquants);
        double fractionTrim =  calculerFractionTrimestres(adherent);
        double epargneBrute = adherent.getSAM() * taux * fractionTrim;
        BigDecimal epargneArrondie = BigDecimal.valueOf(epargneBrute).setScale(2, RoundingMode.HALF_UP); // arrondi 2 chiffre après la virgule
        return epargneArrondie.doubleValue();
    }

    public int calculerTrimestresRequis(Date dateNaissance) {
        // Convertir Date en LocalDate pour faciliter les comparaisons
        LocalDate dateNaissanceLocal = new java.sql.Date(dateNaissance.getTime()).toLocalDate();

        if (dateNaissanceLocal.isBefore(LocalDate.of(1961, 1, 1))) {
            return 167; // 1960 et avant
        } else if (dateNaissanceLocal.isBefore(LocalDate.of(1961, 9, 1))) {
            return 168; // 1er janvier au 31 août 1961
        } else if (dateNaissanceLocal.isBefore(LocalDate.of(1962, 1, 1))) {
            return 169; // 1er septembre au 31 décembre 1961
        } else if (dateNaissanceLocal.isBefore(LocalDate.of(1963, 1, 1))) {
            return 169; // 1962
        } else if (dateNaissanceLocal.isBefore(LocalDate.of(1964, 1, 1))) {
            return 170; // 1963
        } else if (dateNaissanceLocal.isBefore(LocalDate.of(1965, 1, 1))) {
            return 171; // 1964
        } else {
            return 172; // 1965 et au-delà
        }
    }

    public LocalDate calculerAgeDepart(Date dateNaissance) {
        LocalDate dateNaissanceLocal = new java.sql.Date(dateNaissance.getTime()).toLocalDate();

        if (dateNaissanceLocal.isBefore(LocalDate.of(1958, 1, 1))) {
            return dateNaissanceLocal.plusYears(62); // 62 ans pour 1955 - 1957
        } else if (dateNaissanceLocal.isBefore(LocalDate.of(1961, 1, 1))) {
            return dateNaissanceLocal.plusYears(62); // 62 ans pour 1958 - 1960
        } else if (dateNaissanceLocal.isBefore(LocalDate.of(1961, 9, 1))) {
            return dateNaissanceLocal.plusYears(62); // 62 ans pour 1er janvier au 31 août 1961
        } else if (dateNaissanceLocal.isBefore(LocalDate.of(1962, 1, 1))) {
            return dateNaissanceLocal.plusYears(62).plusMonths(3); // 62 ans et 3 mois pour 1er septembre - 31 décembre 1961
        } else if (dateNaissanceLocal.isBefore(LocalDate.of(1963, 1, 1))) {
            return dateNaissanceLocal.plusYears(62).plusMonths(6); // 62 ans et 6 mois pour 1962
        } else if (dateNaissanceLocal.isBefore(LocalDate.of(1964, 1, 1))) {
            return dateNaissanceLocal.plusYears(62).plusMonths(9); // 62 ans et 9 mois pour 1963
        } else if (dateNaissanceLocal.isBefore(LocalDate.of(1965, 1, 1))) {
            return dateNaissanceLocal.plusYears(63); // 63 ans pour 1964
        } else if (dateNaissanceLocal.isBefore(LocalDate.of(1966, 1, 1))) {
            return dateNaissanceLocal.plusYears(63).plusMonths(3); // 63 ans et 3 mois pour 1965
        } else if (dateNaissanceLocal.isBefore(LocalDate.of(1967, 1, 1))) {
            return dateNaissanceLocal.plusYears(63).plusMonths(6); // 63 ans et 6 mois pour 1966
        } else if (dateNaissanceLocal.isBefore(LocalDate.of(1968, 1, 1))) {
            return dateNaissanceLocal.plusYears(63).plusMonths(9); // 63 ans et 9 mois pour 1967
        } else {
            return dateNaissanceLocal.plusYears(64); // 64 ans pour 1968 et après
        }
    }

    public int calculerTrimestresManquants(Date dateNaissance, int nbTrimValide) {
        int trimestresRequis = calculerTrimestresRequis(dateNaissance);
        return Math.max(trimestresRequis - nbTrimValide, 0);
    }

    public double calculerTaux(int nbTrimestresManquants) {
        double tauxPlein = 0.5; // Taux plein à 50%
        double tauxDecoteParTrimestre = 0.625; // 0.625 de décote du taux par trimestre manquant
        double tauxMinimum = 0.375; // Taux minimum fixé à 37,5%
        int nbTrimestresManquantsMax = 20;
        nbTrimestresManquants = Math.min(nbTrimestresManquantsMax, nbTrimestresManquants);

        // Calcul de la réduction totale en fonction des trimestres manquants
        double taux = tauxPlein - ((tauxDecoteParTrimestre * nbTrimestresManquants)/100);

        // S'assurer que le taux ne descend pas en dessous du taux minimum
        return Math.max(taux, tauxMinimum);
    }

    public double calculerFractionTrimestres(Adherent adherent){
        int nbTrimestresRequis = calculerTrimestresRequis(adherent.getDateNaissance());
        int nbTrimestresValides = adherent.getTrimValide();
        double fraction = (double) nbTrimestresValides /nbTrimestresRequis;
        fraction = Math.min(1, fraction);
        return fraction;
    }
}
